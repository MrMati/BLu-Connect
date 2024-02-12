package pl.mati.blu.serialization

import java.io.ByteArrayOutputStream


object Serializer {

    fun serializeHostMsg(msg: MainHostMsg): ByteArray {
        val outputStream = ByteArrayOutputStream()
        msg.writeTo(outputStream)

        return outputStream.toByteArray()
    }

}