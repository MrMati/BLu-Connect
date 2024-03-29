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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.mati.blu.scanner.main.DeviceListItem
import pl.mati.blu.scanner.main.DevicesListView
import pl.mati.blu.scanner.main.viewmodel.ScannerViewModel
import pl.mati.blu.scanner.model.DiscoveredBluetoothDevice
import pl.mati.blu.scanner.repository.ScanningState
import pl.mati.blu.scanner.view.internal.FilterView
import no.nordicsemi.android.common.permissions.ble.RequireBluetooth
import no.nordicsemi.android.common.permissions.ble.RequireLocation
import no.nordicsemi.android.common.theme.R

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScannerView(
    uuid: ParcelUuid?,
    onScanningStateChanged: (Boolean) -> Unit = {},
    onResult: (DiscoveredBluetoothDevice) -> Unit,
    deviceItem: @Composable (DiscoveredBluetoothDevice) -> Unit = {
        DeviceListItem(it.displayName, it.address)
    }
) {
    RequireBluetooth(
        onChanged = { onScanningStateChanged(it) }
    ) {

        // https://github.com/NordicPlayground/Android-Common-Libraries/blob/9510ca21d4973a0f6ce5925c0b8135defb7ddc2d/permissions-ble/src/main/java/no/nordicsemi/android/common/permissions/ble/RequireLocation.kt#L60
        // short blink of LocationRequired screen can be fixed by changing default value
        // from NOT_AVAILABLE to DISABLED here:
        // https://github.com/NordicPlayground/Android-Common-Libraries/blob/9510ca21d4973a0f6ce5925c0b8135defb7ddc2d/permissions-ble/src/main/java/no/nordicsemi/android/common/permissions/ble/viewmodel/PermissionViewModel.kt#L63
        RequireLocation(
            onChanged = { onScanningStateChanged(it) }
        ) { isLocationRequiredAndDisabled ->
            val viewModel = hiltViewModel<ScannerViewModel>()
                .apply { setFilterUuid(uuid) }

            val state by viewModel.state.collectAsStateWithLifecycle(ScanningState.Loading)
            val config by viewModel.filterConfig.collectAsStateWithLifecycle()
            var refreshing by remember { mutableStateOf(false) }

            val scope = rememberCoroutineScope()
            fun refresh() = scope.launch {
                refreshing = true
                viewModel.refresh()
                delay(400) // TODO remove this delay and refreshing variable after updating material dependency
                refreshing = false
            }

            Column(modifier = Modifier.fillMaxSize()) {
                FilterView(
                    config = config,
                    onChanged = { viewModel.setFilter(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorResource(id = R.color.appBarColor))
                        .padding(horizontal = 16.dp),
                )

                val pullRefreshState  = rememberPullRefreshState(
                    refreshing = refreshing,
                    onRefresh = { refresh() },
                )

                Box(modifier = Modifier.pullRefresh(pullRefreshState).clipToBounds()) {
                    DevicesListView(
                        isLocationRequiredAndDisabled = isLocationRequiredAndDisabled,
                        state = state,
                        modifier = Modifier.fillMaxSize(),
                        onClick = { onResult(it) },
                        deviceItem = deviceItem,
                    )

                    PullRefreshIndicator(
                        refreshing = refreshing,
                        state = pullRefreshState,
                        Modifier.align(Alignment.TopCenter)
                    )
                }
            }
        }
    }
}