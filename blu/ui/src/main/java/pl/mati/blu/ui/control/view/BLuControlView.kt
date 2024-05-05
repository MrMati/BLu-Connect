package pl.mati.blu.ui.control.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.LineCartesianLayerModel
import no.nordicsemi.android.common.theme.NordicTheme

@Composable
internal fun BLuControlView(
    ledState: Boolean,
    currentSensorVal: Float,
    sensorChartProducer: CartesianChartModelProducer,
    onStateChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        LedControlView(
            state = ledState,
            onStateChanged = onStateChanged,
        )

        SensorReadingView(
            currentSensorVal = currentSensorVal,
            chartModelProducer = sensorChartProducer
        )
    }
}

@Preview
@Composable
private fun BLuControlViewPreview() {
    NordicTheme {
        BLuControlView(
            ledState = true,
            currentSensorVal = 18766f,
            sensorChartProducer = CartesianChartModelProducer.build {
                add(
                    LineCartesianLayerModel.partial { series((0..4).toList())})
            },
            onStateChanged = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}