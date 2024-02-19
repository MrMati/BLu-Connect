package pl.mati.blu.ble.data

import android.bluetooth.BluetoothDevice

class SensorReading: NodeMsgCallback() {
    var state: Float = 0.0F

    override fun onSensorStateChanged(device: BluetoothDevice, rawReading: Int, scaledReading: Float) {
        state = scaledReading
    }
}