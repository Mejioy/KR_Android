package ru.kafpin.kr_android

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.SimpleAdapter
import java.io.IOException
import java.sql.SQLException


class ServicesActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_services)
        updateDB()
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
    fun updateList(mDb : SQLiteDatabase)
    {
        // Список услуг
        val services = ArrayList<HashMap<String, Any?>>()
        // Список параметров услуги
        var service: HashMap<String, Any?>

        // Отправляем запрос в БД
        val cursor: Cursor = mDb.rawQuery("SELECT * FROM services", null)
        cursor.moveToFirst()

        // Пробегаем по всем услугам
        while (!cursor.isAfterLast) {
            service = HashMap()

            // Заполняем услугу
            service["id"] = cursor.getInt(0)
            service["name"] = cursor.getString(1)
            service["description"] = cursor.getString(2)
            service["price"] = cursor.getInt(3)

            service["displayed_price"] = service["price"].toString()+" рублей"
            // Закидываем услугу в список услуг
            services.add(service)

            // Переходим к следующей услуге
            cursor.moveToNext()
        }
        cursor.close()

        // Какие параметры услуги мы будем отображать в соответствующих
        // элементах из разметки adapter_item.xml
        val from = arrayOf("name","displayed_price")
        val to = intArrayOf(R.id.tvServiceName,R.id.tvServicePrice)

        // Создаем адаптер
        val adapter = SimpleAdapter(this, services, R.layout.adapter_service_item, from, to)
        val listView = findViewById<View>(R.id.lvServices) as ListView
        listView.adapter = adapter

        //Устанавливаем слушатель событий на ListView
        listView.onItemClickListener =
            OnItemClickListener { parent, view, position, id -> // Получаем данные о выбранном элементе

                val selectedItem = parent.getItemAtPosition(position) as HashMap<String, Any?>
                println(selectedItem)

                val name = selectedItem["name"] as String
                var description : String = ""
                if(selectedItem["description"]!=null)
                    description = selectedItem["description"] as String
                val displayed_price = selectedItem["displayed_price"] as String

                 // Создаем Intent для перехода на новый экран (замените на ваш класс и экран)
                val intent: Intent = Intent(this@ServicesActivity,SingleServiceActivity::class.java)

                // Передаем данные в новый экран
                intent.putExtra("name", name)
                intent.putExtra("displayed_price", displayed_price)
                if(selectedItem["description"]!=null)
                    intent.putExtra("description", description)

                // Запускаем новый экран
                startActivity(intent)
            }
    }
}