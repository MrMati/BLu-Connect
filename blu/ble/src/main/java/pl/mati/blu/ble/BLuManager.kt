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
import pl.mati.blu.ble.data.ButtonCallback
import pl.mati.blu.ble.data.ButtonState
import pl.mati.blu.ble.data.LedCallback
import pl.mati.blu.ble.data.LedData
import pl.mati.blu.spec.BLuControl
import pl.mati.blu.spec.BLuSpec
import timber.log.Timber

class BLuManager(
    context: Context,
    device: BluetoothDevice
): BLuControl by BLuManagerImpl(context, device)

private class BLuManagerImpl(
    context: Context,
    private val device: BluetoothDevice,
): BleManager(context), BLuControl {
    private val scope = CoroutineScope(Dispatchers.IO)

    private var ledCharacteristic: BluetoothGattCharacteristic? = null
    private var buttonCharacteristic: BluetoothGattCharacteristic? = null

    private val _ledState = MutableStateFlow(false)
    override val ledState = _ledState.asStateFlow()

    private val _buttonState = MutableStateFlow(false)
    override val buttonState = _buttonState.asStateFlow()

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


    private val buttonCallback by lazy {
        object : ButtonCallback() {
            override fun onButtonStateChanged(device: BluetoothDevice, state: Boolean) {
                _buttonState.tryEmit(state)
            }
        }
    }

    private val ledCallback by lazy {
        object : LedCallback() {
            override fun onLedStateChanged(device: BluetoothDevice, state: Boolean) {
                _ledState.tryEmit(state)
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
        // Write the value to the characteristic.
        writeCharacteristic(
            ledCharacteristic,
            LedData.from(state),
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
        // Get the LBS Service from the gatt object.
        gatt.getService(BLuSpec.BLU_SERVICE_UUID)?.apply {
            // Get the LED characteristic.
            ledCharacteristic = getCharacteristic(
                BLuSpec.BLU_DATA_HOST_OUT_UUID,
                // Mind, that below we pass required properties.
                // If your implementation supports only WRITE_NO_RESPONSE,
                // change the property to BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE.
                BluetoothGattCharacteristic.PROPERTY_WRITE
            )
            // Get the Button characteristic.
            buttonCharacteristic = getCharacteristic(
                BLuSpec.BLU_DATA_HOST_IN_UUID,
                BluetoothGattCharacteristic.PROPERTY_NOTIFY
            )

            // Return true if all required characteristics are supported.
            return ledCharacteristic != null && buttonCharacteristic != null
        }
        return false
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun initialize() {
        // Enable notifications for the button characteristic.
        val flow: Flow<ButtonState> = setNotificationCallback(buttonCharacteristic)
            .asValidResponseFlow()

        // Forward the button state to the buttonState flow.
        scope.launch {
            flow.map { it.state }.collect { _buttonState.tryEmit(it) }
        }

        enableNotifications(buttonCharacteristic)
            .enqueue()

        // Read the initial value of the button characteristic.
        readCharacteristic(buttonCharacteristic)
            .with(buttonCallback)
            .enqueue()

        // Read the initial value of the LED characteristic.
        readCharacteristic(ledCharacteristic)
            .with(ledCallback)
            .enqueue()
    }

    override fun onServicesInvalidated() {
        ledCharacteristic = null
        buttonCharacteristic = null
    }
}