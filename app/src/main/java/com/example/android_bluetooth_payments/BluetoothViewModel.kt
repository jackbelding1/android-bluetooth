package com.example.android_bluetooth_payments

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlinx.coroutines.launch
@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothManager: BluetoothManager
) : ViewModel() {
    private val _discoveredDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<BluetoothDevice>> = _discoveredDevices.asStateFlow()

    init {
        // Correctly register the ViewModel as the callback listener for Bluetooth discovery events
        bluetoothManager.registerDiscoveryCallback(object : BluetoothManager.DiscoveryCallback {
            override fun onDeviceDiscovered(device: BluetoothDevice) {
                // Safely update the StateFlow with the new device
                _discoveredDevices.value = _discoveredDevices.value + device
            }
            override fun onDiscoveryFinished() {
                // Discovery finished event can be used to update UI or log, if necessary
            }
        })
    }
    private fun addDiscoveredDevice(device: BluetoothDevice) {
        _discoveredDevices.value = _discoveredDevices.value + listOf(device)
    }

    fun startDiscovery() {
        bluetoothManager.startDiscovery()
    }

    override fun onCleared() {
        super.onCleared()
        // Cleanup, like unregistering the ViewModel as the discovery callback listener
//        bluetoothManager.unregisterDiscoveryCallback()
    }
}
