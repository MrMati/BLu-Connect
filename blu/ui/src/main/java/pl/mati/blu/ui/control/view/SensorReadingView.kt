package pl.mati.blu.ui.control.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.compose.style.currentChartStyle
import com.patrykandpatrick.vico.core.chart.copy
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.LineCartesianLayerModel
import com.patrykandpatrick.vico.core.model.lineSeries
import no.nordicsemi.android.common.theme.NordicTheme
import pl.mati.blu.ui.R

@Composable
internal fun SensorReadingView(
    currentSensorVal: Float,
    chartModelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    imageVector = Icons.Default.RadioButtonChecked,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 16.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                )
                Text(
                    text = stringResource(R.string.blu_sensor),
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.blu_sensor_descr),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = currentSensorVal.toString(),
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                
                val defaultLines = currentChartStyle.lineLayer.lines
                CartesianChartHost(
                    chart =
                    rememberCartesianChart(
                        rememberLineCartesianLayer(
                            remember(defaultLines) { defaultLines.map { it.copy(backgroundShader = null) } },
                        ),
                    ),
                    modelProducer = chartModelProducer,

                    runInitialAnimation = false,
                )


            }
        }
    }
}


@Composable
@Preview
private fun SensorReadingViewPreview() {
    NordicTheme {
        SensorReadingView(
            currentSensorVal = 18766f,
            chartModelProducer = CartesianChartModelProducer.build {
                add(
                    LineCartesianLayerModel.partial { series((0..4).toList())})
            },
            modifier = Modifier.padding(16.dp),
        )
    }
}