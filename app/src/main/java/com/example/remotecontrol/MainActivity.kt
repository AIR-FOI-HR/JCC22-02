package com.example.remotecontrol
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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
        lateinit var progressDialog: ProgressDialog
        lateinit var bAdapter: BluetoothAdapter
        var isConnected: Boolean = false
        var address: String? = null
    }
    private var keepLogging = false
    private var twoButtonsClicked = false
    private var left = false
    private var right = false
    private var back = false
    private var forward = false
    private var frontLights = false
    private var rearLights = false
    private var horn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        address = intent.getStringExtra(SelectDeviceActivity.ADDRESS_OF_DEVICE)

        ConnectToDevice(this).execute()
        setListeners()

    }
    private fun setListeners(){
        val btnForward: ImageButton = findViewById(R.id.btnForward)
        val btnBack: ImageButton = findViewById(R.id.btnBack)
        val btnLeft: ImageButton = findViewById(R.id.btnLeft)
        val btnRight: ImageButton = findViewById(R.id.btnRight)
        findViewById<Button>(R.id.main_activity_Disconnect_btn).setOnClickListener { disconnect() }
        findViewById<ImageButton>(R.id.btnHorn).setOnClickListener{sendCommand("V")}
        findViewById<ImageButton>(R.id.btnFrontLights).setOnClickListener{sendCommand("W")}
        findViewById<ImageButton>(R.id.btnRearLights).setOnClickListener{sendCommand("U")}


        btnForward.setOnTouchListener { _, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    forward = true
                    if(!keepLogging) keepLogging = true
                    else twoButtonsClicked = true

                    logtxt("F")
                }
                MotionEvent.ACTION_UP -> {
                    forward = false
                    if(twoButtonsClicked){
                        keepLogging = false
                        keepLogging = true
                        //sendCommand(("S"))
                        twoButtonsClicked = false
                    }else{
                        keepLogging = false
                        sendCommand("S")
                    }

                }
                else -> {}
            }
            true
        }

        btnBack.setOnTouchListener { _, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    back = true
                    if(!keepLogging) keepLogging = true
                    else twoButtonsClicked = true

                    logtxt("B")
                }
                MotionEvent.ACTION_UP -> {
                    back = false
                    if(twoButtonsClicked){
                    keepLogging = false
                    keepLogging = true
                    //sendCommand(("S"))
                    twoButtonsClicked = false
                    }else{
                    keepLogging = false
                    sendCommand("S")
                    }
                }
                else -> {}
            }
            true
        }

        btnLeft.setOnTouchListener { _, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    left = true
                    if(!keepLogging) keepLogging = true
                    else twoButtonsClicked = true

                    logtxt("L")
                }
                MotionEvent.ACTION_UP -> {
                    if(twoButtonsClicked){
                        left = false
                        keepLogging = false
                        keepLogging = true
                        //sendCommand(("S"))
                        twoButtonsClicked = false

                    }else{
                        left = false
                        keepLogging = false
                        sendCommand("S")
                    }

                }
                else -> {}
            }
            true
        }

        btnRight.setOnTouchListener { _, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    right = true
                    if(!keepLogging) keepLogging = true
                    else twoButtonsClicked = true
                    logtxt("R")
                }
                MotionEvent.ACTION_UP -> {
                    if(twoButtonsClicked){
                        right = false
                        keepLogging = false
                        keepLogging = true
                        //sendCommand(("S"))
                        twoButtonsClicked = false
                    }else{
                        right = false
                        keepLogging = false
                        sendCommand("S")
                    }

                }
                else -> {}
            }
            true
        }
    }
    private fun logtxt(command: String){
        Thread(Runnable {
            while(keepLogging){
                sendCommand(command)
                Thread.sleep(50)
            }
        }).start()
    }
    private fun sendCommand(command: String){
        var c = command
        when(command){
            "L" -> {
                if(twoButtonsClicked) return
                if(!left) return
            }
            "R" -> {
                if(twoButtonsClicked) return
                if(!right) return
            }
            "F" -> {
                if(!forward) return
                if(right && twoButtonsClicked) c = "I"
                if(left && twoButtonsClicked) c = "G"
            }
            "B" -> {
                if(!back) return
                if(right && twoButtonsClicked) c = "J"
                if(left && twoButtonsClicked) c = "H"
            }
            "V" -> {
                if(horn){
                    c = "v"
                    horn = false
                } else horn = true
            }
            "W" -> {
                if(frontLights){
                    c = "w"
                    frontLights = false
                } else frontLights = true
            }
            "U" -> {
                if(rearLights){
                    c = "u"
                    rearLights = false
                }else rearLights = true
            }
        }

        Log.d("Command", c)
        if(btSocket != null){
            try {
                btSocket!!.outputStream.write(c.toByteArray())
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
            progressDialog = ProgressDialog.show(context, "Trying to connect...", "Please wait")
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
            progressDialog.dismiss()
        }
    }
}