package pl.mati.blu

import android.os.Bundle
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import pl.mati.blu.ui.control.BLuControlNav
import pl.mati.blu.ui.scanner.ScannerDestination
import no.nordicsemi.android.common.navigation.NavigationView
import no.nordicsemi.android.common.theme.NordicActivity
import no.nordicsemi.android.common.theme.NordicTheme

@AndroidEntryPoint
class MainActivity: NordicActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NordicTheme {
                NavigationView(ScannerDestination + BLuControlNav)
            }
        }
    }
}