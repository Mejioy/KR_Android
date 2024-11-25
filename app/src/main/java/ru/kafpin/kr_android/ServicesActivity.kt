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
        // Список клиентов
        val services = ArrayList<HashMap<String, Any?>>()
        // Список параметров конкретного клиента
        var service: HashMap<String, Any?>

        // Отправляем запрос в БД
        val cursor: Cursor = mDb.rawQuery("SELECT * FROM services", null)
        cursor.moveToFirst()

        // Пробегаем по всем клиентам
        while (!cursor.isAfterLast) {
            service = HashMap()

            // Заполняем клиента
            service["id"] = cursor.getInt(0)
            service["name"] = cursor.getString(1)
            service["description"] = cursor.getString(2)
            service["price"] = cursor.getInt(3)

            // Закидываем клиента в список клиентов
            services.add(service)

            // Переходим к следующему клиенту
            cursor.moveToNext()
        }
        cursor.close()


        // Какие параметры клиента мы будем отображать в соответствующих
        // элементах из разметки adapter_item.xml
        val from = arrayOf("name")
        val to = intArrayOf(R.id.tvServiceName)


        // Создаем адаптер
        val adapter = SimpleAdapter(this, services, R.layout.adapter_service_item, from, to)
        val listView = findViewById<View>(R.id.lvServices) as ListView
        listView.adapter = adapter
        listView.setOnItemClickListener(OnItemClickListener { parent, v, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as service
            val intent = Intent(this@ServicesActivity, SingleServiceActivity::class.java)
            intent.putExtra("name", listView.selectedItem.javaClass.getField("name").toString())
            intent.putExtra("description", listView.selectedItem.javaClass.getField("description").toString())
            intent.putExtra("price", listView.selectedItem.javaClass.getField("price").toString())
            startActivity(intent)
            }

//        countriesList.setOnItemClickListener(OnItemClickListener { parent, v, position, id -> // получаем выбранный элемент
//            val selectedItem = parent.getItemAtPosition(position) as String
//            // установка текста элемента TextView
//            selection.setText(selectedItem)
//        })
    }
}