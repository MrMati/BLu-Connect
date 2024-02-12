package pl.mati.blu.ble.data

import no.nordicsemi.android.ble.data.Data
import pl.mati.blu.serialization.Serializer
import pl.mati.blu.serialization.mainHostMsg
import pl.mati.blu.serialization.setLed


class LedData private constructor() {

    companion object {
        fun from(value: Boolean): Data {
            //return Data.opCode(if (value) 0x01 else 0x00)
            val hostMsg = mainHostMsg {
                setLed = setLed {
                    color = if (value) 0xFFFFFF else 0x01
                }
            }
            val data = Serializer.serializeHostMsg(hostMsg)
            return Data(data)
        }
    }

}