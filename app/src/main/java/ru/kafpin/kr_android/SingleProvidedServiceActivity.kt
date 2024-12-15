package ru.kafpin.kr_android

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class SingleProvidedServiceActivity:Activity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.providedservice_details)

        val tvName : TextView = findViewById(R.id.tvName)
        val tvDescription : TextView = findViewById(R.id.tvDescription)
        val tvPrice : TextView = findViewById(R.id.tvPrice)
        val tvProvidedServiceDate : TextView = findViewById(R.id.tvProvidedServiceDate)

        tvName.text = intent.getStringExtra("name")
        tvDescription.text = intent.getStringExtra("description")
        tvPrice.text = "Стоимость "+intent.getIntExtra("price",0).toString()+" рублей"
        tvProvidedServiceDate.text = intent.getStringExtra("date_of_provide")
    }
}