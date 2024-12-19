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


class SingleAutomobileActivity: Activity() {

    var automobile_id : Int = -1

    val REQUEST_CHOOSE_THIEF = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_providedservices)

        val automobile_name = intent.getStringExtra("name")
        val tbAutoName: TextView = findViewById(R.id.tvAutoName)
        tbAutoName.text = getString(R.string.list_providedservices) + ' ' + automobile_name

        automobile_id = intent.getIntExtra("id",-1)

        updateDB()
        val bAutoAdd : Button = findViewById(R.id.bRequestService)

        bAutoAdd.setBackgroundColor(resources.getColor(R.color.dark_blue))
        bAutoAdd.setOnClickListener{
            val questionIntent = Intent(this@SingleAutomobileActivity,
                AddProvidedServiceActivity::class.java)
            questionIntent.putExtra("automobile_id",automobile_id)
            startActivityForResult(questionIntent, REQUEST_CHOOSE_THIEF)
        }
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
            provided_service["employer_id"] = cursor.getInt(4)
            provided_service["status"] = cursor.getString(5)

            provided_service["service_name"] = cursor.getString(6)
            provided_service["service_price"] = cursor.getInt(7)
            provided_service["service_description"] = cursor.getString(8)

            provided_service["displayed_price"] = provided_service["service_price"].toString()+" рублей"

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

                val displayed_price = selectedItem["displayed_price"] as String
                val date = selectedItem["date_of_provide"] as String

                val status = selectedItem["status"] as String

                val employer_id = selectedItem["employer_id"] as Int
                println(selectedItem)
                 // Создаем Intent для перехода на новый экран (замените на ваш класс и экран)
                val intent: Intent = Intent(this@SingleAutomobileActivity,SingleProvidedServiceActivity::class.java)

                // Передаем данные в новый экран
                intent.putExtra("name", name)
                intent.putExtra("displayed_price", displayed_price)
                intent.putExtra("status", status)
                intent.putExtra("date_of_provide", date)
                if(selectedItem["employer_id"]!=null)
                    intent.putExtra("employer_id", employer_id)


                // Запускаем новый экран
                startActivity(intent)
            }
    }
}