package ru.kafpin.kr_android

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.Button
import android.widget.TextView
import java.io.IOException
import java.sql.SQLException

class SingleProvidedServiceActivity:Activity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.providedservice_details)

        val tvName : TextView = findViewById(R.id.tvName)
        val tvPrice : TextView = findViewById(R.id.tvPrice)
        val tvProvidedServiceDate : TextView = findViewById(R.id.tvProvidedServiceDate)

        val tvStatus : TextView = findViewById(R.id.tvStatus)

        val tvEmployer : TextView = findViewById(R.id.tvEmployer)

        tvName.text = intent.getStringExtra("name")
        tvPrice.text = getString(R.string.service_price)+' '+intent.getStringExtra("displayed_price")
        tvProvidedServiceDate.text = getString(R.string.providedservice_date) + ' ' + intent.getStringExtra("date_of_provide")

        val status = intent.getStringExtra("status")

        when (status) {
            "Выполнена" -> {
                val text = SpannableString(getString(R.string.providedservice_status)+' '+status).apply {
                    setSpan(ForegroundColorSpan(Color.GREEN), 8, 8+status.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }.also(tvStatus::setText)
            }
            "В ожидании" -> {
                val text = SpannableString(getString(R.string.providedservice_status)+' '+status).apply {
                    setSpan(ForegroundColorSpan(resources.getColor(R.color.orange)), 8, 8+status.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }.also(tvStatus::setText)
            }
            "Отменена" ->{
                val text = SpannableString(getString(R.string.providedservice_status)+' '+status).apply {
                    setSpan(ForegroundColorSpan(Color.RED), 8, 8+status.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }.also(tvStatus::setText)
            }
        }
        val employer_id = intent.getIntExtra("employer_id",-1)
        val employer = findEmployerFromDB(employer_id)
        if(employer!="")
            tvEmployer.text = getString(R.string.providedservice_employer)+' '+employer
        }
    fun findEmployerFromDB(employer_id:Int):String{

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
        var employer: HashMap<String, Any?> = HashMap()
        employer["displayed_name"]=""
        val cursor: Cursor = mDb.rawQuery("SELECT * " +
                "from employers " +
                "WHERE id = ?", arrayOf(employer_id.toString())
        )
        cursor.moveToFirst()

        // Пробегаем по всем сотрудникам
        while (!cursor.isAfterLast) {


            // Заполняем сотрудника
            employer["id"] = cursor.getInt(0)
            employer["surname"] = cursor.getString(1)
            employer["name"] = cursor.getString(2)
            employer["displayed_name"] = employer["surname"].toString()+' '+employer["name"].toString()
            cursor.moveToNext()
        }
        cursor.close()
        return employer["displayed_name"].toString()
    }
}