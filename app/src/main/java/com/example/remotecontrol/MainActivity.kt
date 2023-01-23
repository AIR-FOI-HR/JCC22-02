package com.example.remotecontrol
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageButton
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object{
        var myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var btSocket: BluetoothSocket? = null
        lateinit var progresDialog: ProgressDialog
        lateinit var bAdapter: BluetoothAdapter
        var isConnected: Boolean = false
        var address: String? = null
    }
    private var keepLogging = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        address = intent.getStringExtra(SelectDeviceActivity.ADDRESS_OF_DEVICE)
        Log.i("address", address.toString())

        ConnectToDevice(this).execute()
        setListeners()

    }
    private fun setListeners(){
        val btnForward: ImageButton = findViewById<ImageButton>(R.id.btnForward)

        btnForward.setOnTouchListener { _, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    keepLogging = true
                    logtxt("F")
                }
                MotionEvent.ACTION_UP -> keepLogging = false
                else -> {}
            }
            true
        }
        findViewById<Button>(R.id.main_activity_Disconnect_btn).setOnClickListener { disconnect() }
    }
    private fun logtxt(command: String){
        Thread(Runnable {
            while(keepLogging){
                sendCommand(command)
                Thread.sleep(100)
            }
        }).start()
    }
    private fun sendCommand(command: String){
        Log.d("Forward", command)
        if(btSocket != null){
            try {
                btSocket!!.outputStream.write(command.toByteArray())
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    private fun disconnect(){
        if(btSocket != null){
            try {
                btSocket!!.close()
                btSocket = null
                isConnected = false
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
        finish()
    }

    private class ConnectToDevice(con: Context) : AsyncTask<Void, Void, String>() {

        private var connectSuccess: Boolean = true
        private val context: Context

        init{
            this.context = con
        }

        override fun onPreExecute() {
            super.onPreExecute()
            progresDialog = ProgressDialog.show(context, "Trying to connect...", "Please wait")
        }
        override fun doInBackground(vararg p0: Void?): String? {
            try {
                if(btSocket == null || !isConnected){
                    bAdapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
                    val device: BluetoothDevice = bAdapter.getRemoteDevice(address)
                    btSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID)
                    bAdapter.cancelDiscovery()
                    btSocket!!.connect()
                }
            }catch (e: IOException){
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(!connectSuccess){
                Log.i("data", "Couldn't connect to device")
            }else{
                isConnected = true
            }
            progresDialog.dismiss()
        }
    }
}