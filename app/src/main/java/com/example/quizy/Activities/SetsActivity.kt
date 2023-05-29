package com.example.quizy.Activities

import SetsAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizy.R

class SetsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var setsAdapter: SetsAdapter
    private val itemList: MutableList<String> = mutableListOf(
        "Set 1", "Set 2", "Set 3", "Set 4", "Set 5",
        "Set 6", "Set 7", "Set 8", "Set 9", "Set 10")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sets)

        recyclerView = findViewById(R.id.setsRecy)
        recyclerView.layoutManager = LinearLayoutManager(this)
        setsAdapter = SetsAdapter(this, itemList)
        recyclerView.adapter = setsAdapter
    }
}
