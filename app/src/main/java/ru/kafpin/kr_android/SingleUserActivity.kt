package ru.kafpin.kr_android

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.Button
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView
import java.io.IOException
import java.sql.SQLException


class SingleUserActivity: Activity() {

    var user_id : Int = -1

    var user_name = ""
    val REQUEST_CHOOSE_THIEF = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_automobiles)

        user_id = intent.getIntExtra("id",-1)
        user_name = intent.getStringExtra("name").toString()
        val tbUname: TextView = findViewById(R.id.tvUname)
        tbUname.text = getString(R.string.list_automobiles) + ' ' + user_name

        updateDB()
        val bAutoAdd : Button = findViewById(R.id.bAddAuto)

        bAutoAdd.setBackgroundColor(resources.getColor(R.color.dark_blue))
        bAutoAdd.setOnClickListener{
            val questionIntent = Intent(this@SingleUserActivity,
                AddAutoActivity::class.java)
            questionIntent.putExtra("user_id",user_id)
            startActivityForResult(questionIntent, REQUEST_CHOOSE_THIEF)
        }
    }
    fun updateDB(){
        val mDb : SQLiteDatabase
        val mDBHelper: DatabaseHelper = DatabaseHelper(this)

        try {
            mDBHelper.updateDataBase()
        } catch (mIOException: IOException) {
            throw Error("UnableToUpdateDatabase")
        }

        try {
            mDb = mDBHelper.writableDatabase
        } catch (mSQLException: SQLException) {
            throw mSQLException
        }
        updateList(mDb)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CHOOSE_THIEF -> {
                    updateDB()
                }
            }
        }
    }
    fun updateList(mDb : SQLiteDatabase)
    {
        // Список автомобилей
        val automobiles = ArrayList<HashMap<String, Any?>>()
        // Список параметров автомобиля
        var automobile: HashMap<String, Any?>

        // Отправляем запрос в БД
        val cursor: Cursor = mDb.rawQuery("SELECT * FROM automobiles WHERE user_id = ?",
            arrayOf(user_id.toString())
        )
        println(cursor.toString())
        cursor.moveToFirst()

        // Пробегаем по всем автомобилям
        while (!cursor.isAfterLast) {
            automobile = HashMap()

            // Заполняем автомобиль
            automobile["id"] = cursor.getInt(0)
            automobile["mark"] = cursor.getString(1)
            automobile["model"] = cursor.getString(2)
            automobile["user_id"] = cursor.getInt(3)
            automobile["gos_number"] = cursor.getString(4)

            automobile["mark_and_model"] = automobile["mark"].toString() + ' ' + automobile["model"]
            println(automobile)
            // Закидываем автомобиль в список автомобилей
            automobiles.add(automobile)

            // Переходим к следующему автомобилю
            cursor.moveToNext()
        }
        cursor.close()

        // Какие параметры услуги мы будем отображать в соответствующих
        // элементах из разметки adapter_item.xml
        val from = arrayOf("mark_and_model","gos_number")
        val to = intArrayOf(R.id.tvAutoName,R.id.tvAutoNumber)


        // Создаем адаптер
        val adapter = SimpleAdapter(this, automobiles, R.layout.adapter_automobile_item, from, to)
        val listView = findViewById<View>(R.id.lvAutomobiles) as ListView
        listView.adapter = adapter


        //Устанавливаем слушатель событий на ListView
        listView.onItemClickListener =
            OnItemClickListener { parent, view, position, id -> // Получаем данные о выбранном элементе

                val selectedItem = parent.getItemAtPosition(position) as HashMap<String, Any?>
                println(selectedItem)

                val automobile_id = selectedItem["id"] as Int
                val automobile_name = selectedItem["mark_and_model"] as String

                 // Создаем Intent для перехода на новый экран (замените на ваш класс и экран)
                val intent: Intent = Intent(this@SingleUserActivity,SingleAutomobileActivity::class.java)

                // Передаем данные в новый экран
                intent.putExtra("id", automobile_id)
                intent.putExtra("automobile_name", automobile_name)
                intent.putExtra("user_name", user_name)

                // Запускаем новый экран
                startActivity(intent)
            }
    }
}