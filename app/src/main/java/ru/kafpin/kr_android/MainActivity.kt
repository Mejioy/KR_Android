package ru.kafpin.kr_android

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val bSerrives : Button = findViewById(R.id.bShowServices)
        val bMyAccount : Button = findViewById(R.id.bAddAccount)

        bSerrives.setOnClickListener{
            val intent = Intent(this@MainActivity, ServicesActivity::class.java)
            startActivity(intent)
        }

        bMyAccount.setOnClickListener{

        }
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
    }
}