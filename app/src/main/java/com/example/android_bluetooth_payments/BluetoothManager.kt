package com.example.android_bluetooth_payments

import android.Manifest
import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.ParcelUuid
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton



@Singleton
class BluetoothManager @Inject constructor(@ApplicationContext private val context: Context) {

    interface DiscoveryCallback {
        fun onDeviceDiscovered(device: BluetoothDevice)
        fun onDiscoveryFinished()
    }

    interface ConnectionStateCallback {
        fun onDeviceConnected(device: BluetoothDevice)
        fun onDeviceDisconnected(device: BluetoothDevice)
    }


    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var discoveryCallback: DiscoveryCallback? = null
    private var connectionStateCallback: ConnectionStateCallback? = null

    fun registerConnectionStateCallback(callback: ConnectionStateCallback?) {
        this.connectionStateCallback = callback
        // Register for Bluetooth connection state changes here.
        // Depending on your needs, you might start monitoring connection state changes,
        // e.g., by registering a BroadcastReceiver for ACTION_ACL_CONNECTED and ACTION_ACL_DISCONNECTED.
    }

    fun unregisterConnectionStateCallback() {
        this.connectionStateCallback = null
        // Unregister the BroadcastReceiver here if you have registered one.
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    connectionStateCallback?.onDeviceConnected(device)
                }
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    connectionStateCallback?.onDeviceDisconnected(device)
                }
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    discoveryCallback?.onDeviceDiscovered(device)
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    context.unregisterReceiver(this)
                    discoveryCallback?.onDiscoveryFinished()
                }
                BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED,
                BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED -> {
                    val state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, BluetoothProfile.STATE_DISCONNECTED)
                    when (state) {
                        BluetoothProfile.STATE_CONNECTED -> {
                            val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                            if (ActivityCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.BLUETOOTH_CONNECT
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return
                            }
                            Log.d("BluetoothManager", "Device connected: ${device.name}")
                            // Here, notify your callback or handle the device connected event
                        }
                        BluetoothProfile.STATE_DISCONNECTED -> {
                            val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                            Log.d("BluetoothManager", "Device disconnected: ${device.name}")
                            // Here, notify your callback or handle the device disconnected event
                        }
                    }
                }
            }
        }
    }

    fun registerConnectionReceiver() {
        val filter = IntentFilter().apply {
            addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        }
        context.registerReceiver(receiver, filter)
    }

    fun unregisterConnectionReceiver() {
        context.unregisterReceiver(receiver)
    }

    fun registerDiscoveryCallback(callback: DiscoveryCallback?) {
        this.discoveryCallback = callback
    }

    fun isBluetoothSupported(): Boolean = bluetoothAdapter != null

    fun isBluetoothEnabled(): Boolean = bluetoothAdapter?.isEnabled ?: false

    fun enableBluetooth() {
        if (!isBluetoothEnabled()) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            context.startActivity(enableBtIntent)
        }
    }

    fun startBleAdvertising() {
        val advertiser: BluetoothLeAdvertiser? = bluetoothAdapter?.bluetoothLeAdvertiser
        val serviceUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000") // Replace with your unique UUID

        val advertiseSettings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
            .setConnectable(true)
            .build()

        val advertiseData = AdvertiseData.Builder()
            .addServiceUuid(ParcelUuid(serviceUuid))
            .setIncludeDeviceName(false)
            .build()

        val advertiseCallback = object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
                Log.d("BluetoothManager", "BLE Advertising started successfully")
            }

            override fun onStartFailure(errorCode: Int) {
                Log.e("BluetoothManager", "BLE Advertising failed: $errorCode")
            }
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_ADVERTISE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        advertiser?.startAdvertising(advertiseSettings, advertiseData, advertiseCallback)
    }

    fun makeDiscoverable() {
        startBleAdvertising() // Call the BLE advertising method here
        val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Add this line
        }
        context.startActivity(discoverableIntent)
    }

    fun startDiscovery(): Boolean {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Indicate that permissions need to be requested.
            return true
        } else {
            // Permissions are granted, start discovery.
            // Register the broadcast receiver
            val filter = IntentFilter().apply {
                addAction(BluetoothDevice.ACTION_FOUND)
                addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            }
            context.registerReceiver(receiver, filter)
            bluetoothAdapter?.startDiscovery()
            return false
        }
    }

}
