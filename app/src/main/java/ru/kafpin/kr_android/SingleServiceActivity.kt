package ru.kafpin.kr_android

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class SingleServiceActivity:Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.service_details)

        val tvName : TextView = findViewById(R.id.tvName)
        val tvDescription : TextView = findViewById(R.id.tvDescription)
        val tvPrice : TextView = findViewById(R.id.tvPrice)

        tvName.text = intent.extras!!.getString("name")
        tvDescription.text = intent.extras!!.getString("description")
        tvPrice.text = intent.extras!!.getString("price")
    }
}