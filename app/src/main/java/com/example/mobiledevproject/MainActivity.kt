package com.example.mobiledevproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiledevproject.adapter.FoodAdapter
import com.example.mobiledevproject.model.FoodEntry
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FoodAdapter
    private val foodList = mutableListOf<FoodEntry>()

    private val addFoodLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val newFood = data?.getSerializableExtra("newFood") as? FoodEntry
            newFood?.let {
                foodList.add(it)
                adapter.notifyItemInserted(foodList.size - 1)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerViewItems)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = FoodAdapter(foodList)
        recyclerView.adapter = adapter

        val fabAddFood = findViewById<FloatingActionButton>(R.id.fabAddFood)
        fabAddFood.setOnClickListener {
            val intent = Intent(this, FoodEntryActivity::class.java)
            addFoodLauncher.launch(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        val prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("loggedIn", false)
        if (!isLoggedIn) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}