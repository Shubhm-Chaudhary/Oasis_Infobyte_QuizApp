package com.example.quizy

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView
import com.example.quizy.Activities.SetsActivity

class MainActivity : AppCompatActivity() {

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val history: CardView = findViewById(R.id.history)
        val science: CardView = findViewById(R.id.science)
        history.setOnClickListener{
            val intent = Intent(this@MainActivity, SetsActivity::class.java)
                    startActivity(intent)
        }


    }
}