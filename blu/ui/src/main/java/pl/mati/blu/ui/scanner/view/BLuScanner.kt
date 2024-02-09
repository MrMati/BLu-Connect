package pl.mati.blu.ui.scanner.view

import android.bluetooth.BluetoothDevice
import android.os.ParcelUuid
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import pl.mati.blu.ui.R
import pl.mati.blu.scanner.DeviceSelected
import pl.mati.blu.scanner.ScannerScreen
import pl.mati.blu.spec.BLuSpec

@Composable
fun BLuScanner(
    onDeviceSelected: (BluetoothDevice, String?) -> Unit,
) {
    ScannerScreen(
        title = stringResource(id = R.string.scanner_title),
        uuid = ParcelUuid(BLuSpec.BLU_SERVICE_UUID),
        cancellable = false,
        onResult = { result ->
            when (result) {
                is DeviceSelected -> with(result.device) {
                    onDeviceSelected(device, name)
                }
                else -> {}
            }
        }
    )
}
