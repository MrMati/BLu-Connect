package pl.mati.blu.serialization

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProtobufConstructTest {

    @Test
    fun builderTest() {
        val msg = SetLed.newBuilder().setColor(1).build()

        assertEquals(msg.color, 1)
    }


    @Test
    fun nestedBuilderTest() {
        val msg =
            AutoMode.newBuilder().setModeGradient(GradientMode.newBuilder().setLeftColor(3).build())
                .build()

        assertTrue(msg.hasModeGradient())
        assertEquals(msg.modeGradient.leftColor, 3)
    }

    @Test
    fun dslBuilderTest() {
        val dslMsg = setLed { color = 1 }

        assertEquals(dslMsg.color, 1)
        println(dslMsg.toByteArray().toHex())
    }

    @Test
    fun nestedDslBuilderTestProblem() {
        val dslMsg = autoMode {
            modeGradient = gradientMode { leftColor = 2 }
        }


        assertTrue(dslMsg.hasModeGradient())
        assertEquals(dslMsg.modeGradient.leftColor, 2)

    }

    @Test
    fun nestedDslBuilderTestWithoutOneOf() {
        val dslMsg = favPointsMode { points.add(favPoint { point = 1.0F; color = 2 }) }

        assertEquals(dslMsg.pointsCount, 1)
        assertEquals(dslMsg.getPoints(0).point, 1.0F)
        assertEquals(dslMsg.getPoints(0).color, 2)
    }

    @Test
    fun zeroColorTest() {
        val msg = mainHostMsg { setLed =   setLed { color = 0 } }

        println("Zero color: ${msg.toByteArray().toHex()}")
        assertTrue(msg.hasSetLed())
    }


}

fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }