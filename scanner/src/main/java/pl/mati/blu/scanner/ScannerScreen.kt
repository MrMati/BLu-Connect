/*
 * Copyright (c) 2022, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific prior
 * written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package pl.mati.blu.scanner

import android.os.ParcelUuid
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import pl.mati.blu.scanner.main.DeviceListItem
import pl.mati.blu.scanner.model.DiscoveredBluetoothDevice
import pl.mati.blu.scanner.view.ScannerAppBar

@Composable
fun ScannerScreen(
    title: String = stringResource(id = R.string.scanner_screen),
    uuid: ParcelUuid?,
    cancellable: Boolean = true,
    onResult: (ScannerScreenResult) -> Unit,
    deviceItem: @Composable (DiscoveredBluetoothDevice) -> Unit = {
        DeviceListItem(it.displayName, it.address)
    }
) {
    var isScanning by rememberSaveable { mutableStateOf(false) }

    Column {
        if (cancellable) {
            ScannerAppBar(title, isScanning) { onResult(ScanningCancelled) }
        } else {
            ScannerAppBar(title, isScanning)
        }
        ScannerView(
            uuid = uuid,
            onScanningStateChanged = { isScanning = it },
            onResult = { onResult(DeviceSelected(it)) },
            deviceItem = deviceItem,
        )
    }
}