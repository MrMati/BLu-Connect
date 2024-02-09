package pl.mati.blu.ui.control

import android.bluetooth.BluetoothDevice
import android.os.Parcelable
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.parcelize.Parcelize
import pl.mati.blu.ui.control.view.BLuControlScreen
import no.nordicsemi.android.common.navigation.createDestination
import no.nordicsemi.android.common.navigation.defineDestination
import no.nordicsemi.android.common.navigation.viewmodel.SimpleNavigationViewModel

val BLuControlDest = createDestination<BLuDevice, Unit>("BLuControl")

@Parcelize
data class BLuDevice(
    val device: BluetoothDevice,
    val name: String?,
): Parcelable

val BLuControlNav = defineDestination(BLuControlDest) {
    val viewModel: SimpleNavigationViewModel = hiltViewModel()

    BLuControlScreen(
        onNavigateUp = { viewModel.navigateUp() }
    )
}
