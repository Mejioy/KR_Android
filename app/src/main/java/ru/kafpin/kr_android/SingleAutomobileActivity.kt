package ru.kafpin.kr_android

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView
import java.io.IOException
import java.sql.SQLException


class SingleAutomobileActivity: Activity() {
    var automobile_id : Int = -1

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_providedservices)

        val automobile_name = intent.getStringExtra("name")
        val tbAutoName: TextView = findViewById(R.id.tvAutoName)
        tbAutoName.text = getString(R.string.list_providedservices) + ' ' + automobile_name

        automobile_id = intent.getIntExtra("id",-1)

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

        // Список оказанных услгу
        val provided_services = ArrayList<HashMap<String, Any?>>()
        // Список параметров оказанных услуг
        var provided_service: HashMap<String, Any?>

        // Отправляем запрос в БД
        val cursor: Cursor = mDb.rawQuery("SELECT provided_services.*, services.name, services.price, services.description " +
                "from provided_services " +
                "JOIN services on provided_services.service_id=services.id " +
                "WHERE automobile_id = ?", arrayOf(automobile_id.toString())
        )
        cursor.moveToFirst()

        // Пробегаем по всем автомобилям
        while (!cursor.isAfterLast) {
            provided_service = HashMap()

            // Заполняем автомобиль
            provided_service["id"] = cursor.getInt(0)
            provided_service["service_id"] = cursor.getInt(1)
            provided_service["automobile_id"] = cursor.getInt(2)
            provided_service["date_of_provide"] = cursor.getString(3)
            provided_service["service_name"] = cursor.getString(4)
            provided_service["service_price"] = cursor.getInt(5)
            provided_service["service_description"] = cursor.getString(6)

            // Закидываем автомобиль в список автомобилей
            provided_services.add(provided_service)

            // Переходим к следующему автомобилю
            cursor.moveToNext()
        }
        cursor.close()

        // Какие параметры услуги мы будем отображать в соответствующих
        // элементах из разметки adapter_item.xml
        val from = arrayOf("service_name","date_of_provide")
        val to = intArrayOf(R.id.tvProvidedServiceName,R.id.tvProvidedServiceDate)

        // Создаем адаптер
        val adapter = SimpleAdapter(this, provided_services, R.layout.adapter_providedservice_item, from, to)
        val listView = findViewById<View>(R.id.lvProvidedServices) as ListView
        listView.adapter = adapter

        //Устанавливаем слушатель событий на ListView
        listView.onItemClickListener =
            OnItemClickListener { parent, view, position, id -> // Получаем данные о выбранном элементе

                val selectedItem = parent.getItemAtPosition(position) as HashMap<String, Any?>
                println(selectedItem)

                val name = selectedItem["service_name"] as String
                var description : String = ""
                if(selectedItem["service_description"]!=null)
                    description = selectedItem["service_description"] as String
                val price = selectedItem["service_price"] as Int
                val date = selectedItem["date_of_provide"] as String

                println(selectedItem)
                 // Создаем Intent для перехода на новый экран (замените на ваш класс и экран)
                val intent: Intent = Intent(this@SingleAutomobileActivity,SingleProvidedServiceActivity::class.java)

                // Передаем данные в новый экран
                intent.putExtra("name", name)
                intent.putExtra("price", price)
                if(selectedItem["description"]!=null)
                    intent.putExtra("description", description)
                intent.putExtra("date_of_provide", date)

                // Запускаем новый экран
                startActivity(intent)
            }
    }
}