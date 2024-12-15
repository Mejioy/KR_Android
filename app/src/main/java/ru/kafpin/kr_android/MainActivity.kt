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

        //Кнопки
        val bSerrives : Button = findViewById(R.id.bShowServices)
        val bMyAccount : Button = findViewById(R.id.bAddAccount)

        //Устанавливаем цвет фона
        bSerrives.setBackgroundColor(resources.getColor(R.color.dark_blue))
        bMyAccount.setBackgroundColor(resources.getColor(R.color.dark_blue))

        //Обработчик кнопки
        bSerrives.setOnClickListener{
            val intent = Intent(this@MainActivity, ServicesActivity::class.java)
            startActivity(intent)
        }

        //Обработчик кнопки
        bMyAccount.setOnClickListener{
            val intent = Intent(this@MainActivity, UsersActivity::class.java)
            startActivity(intent)
        }
    }
}