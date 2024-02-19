package pl.mati.blu.ble.data

import android.bluetooth.BluetoothDevice
import no.nordicsemi.android.ble.callback.profile.ProfileReadResponse
import no.nordicsemi.android.ble.data.Data
import pl.mati.blu.serialization.MainNodeMsg

abstract class NodeMsgCallback: ProfileReadResponse() {

    override fun onDataReceived(device: BluetoothDevice, data: Data) {
        val nodeMsg = MainNodeMsg.parseFrom(data.value)
        nodeMsg.contentCase.let { case ->
            when (case) {
                MainNodeMsg.ContentCase.READING_RESPONSE ->
                    nodeMsg.readingResponse.let {
                        onSensorStateChanged(device, it.rawReading, it.scaledReading)
                    }

                MainNodeMsg.ContentCase.CONTENT_NOT_SET, null -> return onInvalidDataReceived(device, data)
            }
        }


    }

    abstract fun onSensorStateChanged(device: BluetoothDevice, rawReading: Int, scaledReading: Float)
}