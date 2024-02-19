package pl.mati.blu.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.ktx.asValidResponseFlow
import no.nordicsemi.android.ble.ktx.getCharacteristic
import no.nordicsemi.android.ble.ktx.state.ConnectionState
import no.nordicsemi.android.ble.ktx.stateAsFlow
import no.nordicsemi.android.ble.ktx.suspend
import pl.mati.blu.ble.data.NodeMsgCallback
import pl.mati.blu.ble.data.SensorReading
import pl.mati.blu.ble.data.MainMsg
import pl.mati.blu.serialization.setLed
import pl.mati.blu.serialization.subscribeReading
import pl.mati.blu.spec.BLuControl
import pl.mati.blu.spec.BLuSpec
import timber.log.Timber

class BLuManager(
    context: Context,
    device: BluetoothDevice
) : BLuControl by BLuManagerImpl(context, device)

private class BLuManagerImpl(
    context: Context,
    private val device: BluetoothDevice,
) : BleManager(context), BLuControl {
    private val scope = CoroutineScope(Dispatchers.IO)

    private var hostOut: BluetoothGattCharacteristic? = null
    private var hostIn: BluetoothGattCharacteristic? = null

    private val _ledState = MutableStateFlow(false)
    override val ledState = _ledState.asStateFlow()

    private val _sensorState = MutableStateFlow(0.0F)
    override val sensorState = _sensorState.asStateFlow()

    override val state = stateAsFlow()
        .map {
            when (it) {
                is ConnectionState.Connecting,
                is ConnectionState.Initializing -> BLuControl.State.LOADING

                is ConnectionState.Ready -> BLuControl.State.READY
                is ConnectionState.Disconnecting,
                is ConnectionState.Disconnected -> BLuControl.State.NOT_AVAILABLE
            }
        }
        .stateIn(scope, SharingStarted.Lazily, BLuControl.State.NOT_AVAILABLE)


    private val dataInCallback by lazy {
        object : NodeMsgCallback() {
            override fun onSensorStateChanged(
                device: BluetoothDevice,
                rawReading: Int,
                scaledReading: Float
            ) {
                _sensorState.tryEmit(scaledReading)
            }
        }
    }


    override suspend fun connect() = connect(device)
        .retry(3, 300)
        .useAutoConnect(false)
        .timeout(3000)
        .suspend()

    override fun release() {
        // Cancel all coroutines.
        scope.cancel()

        val wasConnected = isReady
        // If the device wasn't connected, it means that ConnectRequest was still pending.
        // Cancelling queue will initiate disconnecting automatically.
        cancelQueue()

        // If the device was connected, we have to disconnect manually.
        if (wasConnected) {
            disconnect().enqueue()
        }
    }

    override suspend fun turnLed(state: Boolean) {
        val msg = setLed { color = if (state) 0xFFFFFFFF.toInt() else 0x01 }

        // Write the value to the characteristic.
        writeCharacteristic(
            hostOut,
            MainMsg.hostMsgSerialize(msg),
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        ).suspend()

        // Update the state flow with the new value.
        _ledState.value = state
    }

    override fun log(priority: Int, message: String) {
        Timber.log(priority, message)
    }

    override fun getMinLogPriority(): Int {
        // By default, the library logs only INFO or
        // higher priority messages. You may change it here.
        return Log.VERBOSE
    }

    override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
        gatt.getService(BLuSpec.BLU_SERVICE_UUID)?.apply {

            hostOut = getCharacteristic(
                BLuSpec.BLU_DATA_HOST_OUT_UUID,
                // Mind, that below we pass required properties.
                // If your implementation supports only WRITE_NO_RESPONSE,
                // change the property to BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE.
                BluetoothGattCharacteristic.PROPERTY_WRITE
            )

            hostIn = getCharacteristic(
                BLuSpec.BLU_DATA_HOST_IN_UUID,
                BluetoothGattCharacteristic.PROPERTY_NOTIFY
            )

            // Return true if all required characteristics are supported.
            return hostOut != null && hostIn != null
        }
        return false
    }

    override fun initialize() {
        val flow: Flow<SensorReading> = setNotificationCallback(hostIn)
            //.with(dataInCallback)
            .asValidResponseFlow()

        // Forward the sensor state to the sensorState flow.
        scope.launch {
            flow.map { it.state }.collect { _sensorState.tryEmit(it) }
        }


        enableNotifications(hostIn)
            .enqueue()


        //val reqRead = getReading { numSamples = 1 }
        val reqRead = subscribeReading {
            enable = true
            updateRate = 1
        }

        scope.launch {
            writeCharacteristic(
                hostOut,
                MainMsg.hostMsgSerialize(reqRead),
                BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            ).suspend()
        }
    }

    override fun onServicesInvalidated() {
        hostOut = null
        hostIn = null
    }
}