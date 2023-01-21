package com.example.remotecontrol
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast

class SelectDeviceActivity: AppCompatActivity(){

    private var bAdapter: BluetoothAdapter? = null
    private lateinit var pairedDevices: Set<BluetoothDevice>
    val REQUEST_ENABLE_BLUETOOTH = 1

    companion object {
        val ADDRESS_OF_DEVICE: String = "Device address"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_device_layout)
        bAdapter = getSystemService(BluetoothManager::class.java).adapter
        if(bAdapter == null){
            Toast.makeText(this, "Device does not support bluetooth", Toast.LENGTH_SHORT).show()
            return
        }
        if(!bAdapter!!.isEnabled){
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent, REQUEST_ENABLE_BLUETOOTH)
        }

        findViewById<Button>(R.id.select_device_refresh_btn).setOnClickListener { pairedDeviceList() }
    }

    private fun pairedDeviceList(){
        pairedDevices = bAdapter!!.bondedDevices
        val list: ArrayList<BluetoothDevice> = ArrayList()

        if(pairedDevices.isNotEmpty()){
            for (device: BluetoothDevice in pairedDevices){
                list.add(device)
                Log.i("device", ""+device.name + " - " + device.address)
            }
        }else{
            Toast.makeText(this, "No paired devices found", Toast.LENGTH_SHORT).show()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        val select_device_lv: ListView = findViewById<ListView>(R.id.select_device_list_view)
        select_device_lv.adapter = adapter
        select_device_lv.onItemClickListener = AdapterView.OnItemClickListener{_, _, position, _ ->
            val device: BluetoothDevice = list[position]
            val address: String = device.address
            Toast.makeText(this, "Selected device: " + list[position].name, Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(ADDRESS_OF_DEVICE, address)
            startActivity(intent)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_ENABLE_BLUETOOTH){
            if(resultCode == Activity.RESULT_OK){
                if(bAdapter!!.isEnabled){
                    Toast.makeText(this, "Bluetooth has been enabled", Toast.LENGTH_SHORT).show()
                } else{
                    Toast.makeText(this, "Bluetooth has been disabled", Toast.LENGTH_SHORT).show()
                }
            }else if(resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(this, "Bluetooth enabling has been canceled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}