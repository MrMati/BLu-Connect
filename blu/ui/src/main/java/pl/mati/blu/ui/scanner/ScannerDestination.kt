package pl.mati.blu.ui.scanner

import androidx.hilt.navigation.compose.hiltViewModel
import pl.mati.blu.ui.control.BLuControlDest
import pl.mati.blu.ui.control.BLuDevice
import pl.mati.blu.ui.scanner.view.BLuScanner
import no.nordicsemi.android.common.navigation.createSimpleDestination
import no.nordicsemi.android.common.navigation.defineDestination
import no.nordicsemi.android.common.navigation.viewmodel.SimpleNavigationViewModel

val Scanner = createSimpleDestination("scanner")

val ScannerDestination = defineDestination(Scanner) {
    val viewModel: SimpleNavigationViewModel = hiltViewModel()

    BLuScanner(
        onDeviceSelected = { device, name ->
            viewModel.navigateTo(BLuControlDest, BLuDevice(device, name))
        }
    )
}