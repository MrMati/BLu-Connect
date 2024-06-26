package pl.mati.blu.ui.control.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.lineSeries
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import no.nordicsemi.android.common.logger.LoggerLauncher
import pl.mati.blu.ui.control.repository.BLuRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

/**
 * The view model for the BLu control screen.
 *
 * @param context The application context.
 * @property repository The repository that will be used to interact with the device.
 * @property deviceName The name of the BLu device, as advertised.
 */
@HiltViewModel
class BLuViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val repository: BLuRepository,
    @Named("deviceName") val deviceName: String,
) : AndroidViewModel(context as Application) {
    /** The connection state of the device. */
    val state = repository.state

    /** The LED state. */
    val ledState = repository.loggedLedState
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    /** The sensor state. */
    val sensorState = repository.loggedSensorState
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0F)

    val sensorChartDataProducer = CartesianChartModelProducer.build()
    val sensorData = mutableListOf<Float>(0f)
    var i = 0;

    init {
        // In this sample we want to connect to the device as soon as the view model is created.
        connect()

        viewModelScope.launch() {
            sensorState.collect {
                sensorData.add(it)
                if (sensorData.size > 25) {
                    sensorData.removeAt(0)
                }
                //Timber.i(entries.joinToString(separator = " ") { itt -> "${itt.x}:${itt.y}"  })
                sensorChartDataProducer.tryRunTransaction {
                    lineSeries {
                        series(x = (i..(i + 25)).toList(), y = sensorData)
                        //series(y = listOf(6, 1, 9, 3))
                        //series(x = listOf(1, 2, 3, 4), y = listOf(2, 5, 3, 4))
                    }
                }
                i += 1
            }
        }
    }

    /**
     * Connects to the device.
     */
    fun connect() {
        val exceptionHandler = CoroutineExceptionHandler { _, _ -> }
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            // This method may throw an exception if the connection fails,
            // Bluetooth is disabled, etc.
            // The exception will be caught by the exception handler and will be ignored.
            repository.connect()
        }
    }

    /**
     * Sends a command to the device to toggle the LED state.
     * @param on The new state of the LED.
     */
    fun turnLed(on: Boolean) {
        val exceptionHandler = CoroutineExceptionHandler { _, _ -> }
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            // Just like above, when this method throws an exception, it will be caught by the
            // exception handler and ignored.
            repository.turnLed(on)
        }
    }

    /**
     * Opens nRF Logger app with the log or Google Play if the app is not installed.
     */
    fun openLogger() {
        LoggerLauncher.launch(getApplication(), repository.sessionUri)
    }

    override fun onCleared() {
        super.onCleared()
        repository.release()
    }
}