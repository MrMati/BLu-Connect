package pl.mati.blu.ble.data

import com.google.protobuf.MessageLiteOrBuilder
import no.nordicsemi.android.ble.data.Data
import pl.mati.blu.serialization.AutoOptions
import pl.mati.blu.serialization.GetReading
import pl.mati.blu.serialization.SensorOptions
import pl.mati.blu.serialization.Serializer
import pl.mati.blu.serialization.SetLed
import pl.mati.blu.serialization.SubscribeReading
import pl.mati.blu.serialization.mainHostMsg

class MainMsg private constructor() {
    companion object {
        fun <T : MessageLiteOrBuilder> hostMsgSerialize(msg: T): Data {
            val hostMsg = mainHostMsg {
                when (msg) {
                    is SetLed -> setLed = msg
                    is GetReading -> getReading = msg
                    is SensorOptions -> setSensorOptions = msg
                    is AutoOptions -> setAutoOptions = msg
                    is SubscribeReading -> subscribeReading = msg
                }

            }

            val data = Serializer.serializeHostMsg(hostMsg)
            return Data(data)
        }

    }
}