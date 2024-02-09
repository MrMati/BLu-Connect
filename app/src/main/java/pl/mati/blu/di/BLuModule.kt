package pl.mati.blu.di

import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import pl.mati.blu.ble.BLuManager
import pl.mati.blu.ui.control.BLuControlDest
import pl.mati.blu.spec.BLuControl
import pl.mati.blu.spec.R
import no.nordicsemi.android.common.navigation.get
import javax.inject.Named

@Suppress("unused")
@Module
@InstallIn(ViewModelComponent::class)
abstract class BLuModule {

    companion object {

        @Provides
        @ViewModelScoped
        fun provideBluetoothDevice(handle: SavedStateHandle): BluetoothDevice {
            return handle.get(BLuControlDest).device
        }

        @Provides
        @ViewModelScoped
        @Named("deviceName")
        fun provideDeviceName(
            @ApplicationContext context: Context,
            handle: SavedStateHandle,
        ): String {
            return handle.get(BLuControlDest).name ?: context.getString(R.string.unnamed_device)
        }

        @Provides
        @ViewModelScoped
        @Named("deviceId")
        fun provideDeviceId(
            bluetoothDevice: BluetoothDevice
        ): String = bluetoothDevice.address

        @Provides
        @ViewModelScoped
        fun provideBLuManager(
            @ApplicationContext context: Context,
            device: BluetoothDevice,
        ) = BLuManager(context, device)

    }

    @Binds
    abstract fun bindBLu(
        bluManager: BLuManager
    ): BLuControl

}