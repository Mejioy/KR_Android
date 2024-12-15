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


class UsersActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

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
        // Список пользователей
        val users = ArrayList<HashMap<String, Any?>>()
        // Список параметров услуги
        var user: HashMap<String, Any?>

        // Отправляем запрос в БД
        val cursor: Cursor = mDb.rawQuery("SELECT * FROM users", null)
        cursor.moveToFirst()

        // Пробегаем по всем пользователям
        while (!cursor.isAfterLast) {
            user = HashMap()

            // Заполняем пользователя
            user["id"] = cursor.getInt(0)
            user["name"] = cursor.getString(1)
            user["phone"] = cursor.getString(2)

            // Закидываем пользователя в список пользователей
            users.add(user)

            // Переходим к следующему пользователю
            cursor.moveToNext()
        }
        cursor.close()


        // Какие параметры пользователя мы будем отображать в соответствующих
        // элементах из разметки adapter_item.xml
        val from = arrayOf("name","phone")
        val to = intArrayOf(R.id.tvUname,R.id.tvPhone)


        // Создаем адаптер
        val adapter = SimpleAdapter(this, users, R.layout.adapter_user_item, from, to)
        val listView = findViewById<View>(R.id.lvUsers) as ListView
        listView.adapter = adapter


        //Устанавливаем слушатель событий на ListView
        listView.onItemClickListener =
            OnItemClickListener { parent, view, position, id -> // Получаем данные о выбранном элементе
                val selectedItem = parent.getItemAtPosition(position) as HashMap<String, Any?>
                println(selectedItem)

                val user_id = selectedItem["id"] as Int

                 // Создаем Intent для перехода на новый экран (замените на ваш класс и экран)
                val intent: Intent = Intent(this@UsersActivity,SingleUserActivity::class.java)

                // Передаем user_id в новый экран
                intent.putExtra("id", user_id)

                // Запускаем новый экран
                startActivity(intent)
            }
    }
}