package ru.kafpin.kr_android

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import java.io.IOException
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class AddProvidedServiceActivity:Activity() {

    val REQUEST_CHOOSE_THIEF = 0
    var automobile_id = -1
    var service_name = ""
    var service_id = -1
    var allowDate: Date = Date()
    var enteredDate: Date = Date()

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.add_providedservice)

        automobile_id = intent.getIntExtra("automobile_id",-1)
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


        val bAddProvidedService : Button = findViewById(R.id.bAddProvidedService)

        val tiService : TextInputEditText = findViewById(R.id.tiService)
        val tiDate : TextInputEditText = findViewById(R.id.tiDate)


        bAddProvidedService.setBackgroundColor(resources.getColor(R.color.dark_blue))
        bAddProvidedService.setOnClickListener{
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            enteredDate = dateFormat.parse(tiDate.text.toString())
            if(validProvidedServiceInsert(service_id,enteredDate)){
                // Создаем объект ContentValues для хранения новых данных
                val formattedStartDate : String = dateFormat.format(enteredDate)
                val values = ContentValues()
                values.put("service_id", service_id)
                values.put("automobile_id", automobile_id)
                values.put("date_of_provide", formattedStartDate)
                values.put("status", "В ожидании")
                mDb.insert("provided_services", null, values);
                val answerIntent = Intent()
                setResult(RESULT_OK, answerIntent)
                finish()
            }
        }

        tiDate.setOnClickListener{
            val calendar = Calendar.getInstance()
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH]
            val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]

            val datePickerDialog = DatePickerDialog(
                this,
                { view, year, month, dayOfMonth -> // Получите выбранную дату и обновите ваш EditText
                    val selectedDate = dayOfMonth.toString() + "/" + (month + 1) + "/" + year
                    tiDate.setText(selectedDate)
                },
                year, month, dayOfMonth
            )
            datePickerDialog.show()
        }

        tiService.setOnClickListener{
            val questionIntent = Intent(this@AddProvidedServiceActivity,
                ChoiceServiceActivity::class.java)
            startActivityForResult(questionIntent, REQUEST_CHOOSE_THIEF)
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val tiService : TextInputEditText = findViewById(R.id.tiService)
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CHOOSE_THIEF -> {
                    if (data != null) {
                        service_name = data.getStringExtra("name").toString()
                        service_id = data.getIntExtra("service_id",-1)
                        tiService.setText(service_name)
                    }
                }
            }

        }
    }
    fun validProvidedServiceInsert(service_id:Int,dateOfProvide:Date): Boolean {
        if(service_id==-1){
            Toast.makeText(this@AddProvidedServiceActivity,getString(R.string.need_choice_service),Toast.LENGTH_LONG).show()
            return false
        }
        if(enteredDate.before(allowDate)){
            Toast.makeText(this@AddProvidedServiceActivity,getString(R.string.need_change_date),Toast.LENGTH_LONG).show()
            return false
        }
        else
            return true
    }

}