package ru.kafpin.kr_android

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class SingleServiceActivity:Activity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.service_details)

        val tvName : TextView = findViewById(R.id.tvName)
        val tvDescription : TextView = findViewById(R.id.tvDescription)
        val tvPrice : TextView = findViewById(R.id.tvPrice)

        tvName.text = intent.getStringExtra("name")
        tvDescription.text = intent.getStringExtra("description")
        tvPrice.text = getString(R.string.service_price)+ ' ' +intent.getStringExtra("displayed_price")
    }
}