package pl.mati.blu.ui.control.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.common.theme.NordicTheme

@Composable
internal fun BLuControlView(
    ledState: Boolean,
    sensorReadingState: Float,
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
            state = sensorReadingState
        )
    }
}

@Preview
@Composable
private fun BLuControlViewPreview() {
    NordicTheme {
        BLuControlView(
            ledState = true,
            sensorReadingState = 1.0F,
            onStateChanged = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}