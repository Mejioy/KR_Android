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

//        val listViewServices: ListView = findViewById(R.id.lvServices)

        val mDBHelper: DatabaseHelper
        val mDb : SQLiteDatabase

        mDBHelper = DatabaseHelper(this)

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

            // Закидываем услугу в список услуг
            services.add(service)

            // Переходим к следующей услуге
            cursor.moveToNext()
        }
        cursor.close()


        // Какие параметры услуги мы будем отображать в соответствующих
        // элементах из разметки adapter_item.xml
        val from = arrayOf("name")
        val to = intArrayOf(R.id.tvServiceName)


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
                val price = selectedItem["price"] as Int
//
//
                 // Создаем Intent для перехода на новый экран (замените на ваш класс и экран)
                val intent: Intent = Intent(this@ServicesActivity,SingleServiceActivity::class.java)


                // Передаем user_id в новый экран
                intent.putExtra("name", name)
                intent.putExtra("price", price)
                if(selectedItem["description"]!=null)
                    intent.putExtra("description", description)

                // Запускаем новый экран
                startActivity(intent)
            }

//        listView.onItemClickListener = object : OnItemClickListener {
//            override fun onItemClick(
//                parent: AdapterView<*>?, itemClicked: View, position: Int,
//                id: Long
//            ) {
//
//                // Получаем данные о выбранном элементе
//                val selectedItem = parent!!.getItemAtPosition(position) as HashMap<String, Any?>
//                val name = selectedItem["name"] as String
//                val price = selectedItem["price"] as Int
//                val description = selectedItem["description"] as String
//
//
//                // Создаем Intent для перехода на новый экран (замените на ваш класс и экран)
//                val intent: Intent = Intent(this@ServicesActivity,SingleServiceActivity::class.java)
//
//
//                // Передаем user_id в новый экран
//                intent.putExtra("name", name)
//                intent.putExtra("price", price)
//                intent.putExtra("description", description)
//
//                // Запускаем новый экран
//                startActivity(intent)
//            }
//        }


//        countriesList.setOnItemClickListener(OnItemClickListener { parent, v, position, id -> // получаем выбранный элемент
//            val selectedItem = parent.getItemAtPosition(position) as String
//            // установка текста элемента TextView
//            selection.setText(selectedItem)
//        })
    }
}