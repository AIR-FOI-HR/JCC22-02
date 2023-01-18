package com.example.remotecontrol

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast

class MainActivity : AppCompatActivity() {
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
    }
}