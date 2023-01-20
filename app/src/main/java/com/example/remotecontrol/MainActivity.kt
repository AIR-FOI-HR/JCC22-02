package com.example.remotecontrol

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_ENABLE_BT:Int = 1
    lateinit var bAdapter : BluetoothAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnFwd: ImageButton = findViewById(R.id.btnForward);
        btnFwd.setOnClickListener{
            Toast.makeText(this, "F", Toast.LENGTH_SHORT).show()
        }
        val btnBck: ImageButton = findViewById(R.id.btnBack);
        btnBck.setOnClickListener{
            Toast.makeText(this, "B", Toast.LENGTH_SHORT).show()
        }
        val btnLft: ImageButton = findViewById(R.id.btnLeft);
        btnLft.setOnClickListener{
            Toast.makeText(this, "L", Toast.LENGTH_SHORT).show()
        }
        val btnRht: ImageButton = findViewById(R.id.btnRight);
        btnRht.setOnClickListener{
            Toast.makeText(this, "R", Toast.LENGTH_SHORT).show()
        }
        bAdapter = getSystemService(BluetoothManager::class.java).adapter
        if(bAdapter.isEnabled == true){
            Toast.makeText(this, "bluetooth ne radi", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "bluetooth radi", Toast.LENGTH_SHORT).show()
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent, REQUEST_CODE_ENABLE_BT)
        }
        val pairedDevices: Set<BluetoothDevice> = bAdapter.bondedDevices
        if (pairedDevices.isEmpty()) {
            println("No paired Bluetooth devices found")
        } else {
            println("Paired Bluetooth devices:")
            for (device in pairedDevices) {
                println("- ${device.name} (${device.address})")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if(requestCode == Activity.RESULT_OK){
                Toast.makeText(this, Activity.RESULT_OK.toString(), Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, Activity.RESULT_CANCELED.toString(), Toast.LENGTH_SHORT).show()
            }
        super.onActivityResult(requestCode, resultCode, data)
    }
}