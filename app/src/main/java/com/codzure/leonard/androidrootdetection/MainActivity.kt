package com.codzure.leonard.androidrootdetection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val text = findViewById<TextView>(R.id.text)
        val isRooted = RootChecker.isRooted(this@MainActivity)
        if (isRooted){
            text.text = "Device is rooted"
        } else {
            text.text = "Device is not rooted"
        }
    }
}