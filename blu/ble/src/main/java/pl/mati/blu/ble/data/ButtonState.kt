package pl.mati.blu.ble.data

import android.bluetooth.BluetoothDevice

class ButtonState: ButtonCallback() {
    var state: Boolean = false

    override fun onButtonStateChanged(device: BluetoothDevice, state: Boolean) {
        this.state = state
    }
}