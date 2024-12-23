package ru.kafpin.kr_android

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import java.io.IOException
import java.sql.SQLException

class AddUserActivity:Activity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.add_user)

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


        val bAddAccount : Button = findViewById(R.id.bAddAccount)
        val tiName : TextInputEditText = findViewById(R.id.tiName)
        val tiPhone : TextInputEditText = findViewById(R.id.tiPhone)

        bAddAccount.setBackgroundColor(resources.getColor(R.color.dark_blue))
        bAddAccount.setOnClickListener{
            val name:String = tiName.text.toString()
            val phone:String = tiPhone.text.toString()
            if(validUserInsert(name,phone ,mDb)){
                // Создаем объект ContentValues для хранения новых данных
                val values = ContentValues()
                values.put("name", name)
                values.put("phone", phone)
                mDb.insert("users", null, values);
                val answerIntent = Intent()
                setResult(RESULT_OK, answerIntent)
                finish()
            }
        }
    }
    fun validUserInsert(name:String,phone:String,mDb : SQLiteDatabase): Boolean {
        if(name.trim().isEmpty()||phone.trim().isEmpty()){
            Toast.makeText(this@AddUserActivity,getString(R.string.need_fill_all), Toast.LENGTH_LONG).show()
            return false
        }
        else{
            val pattern : Regex = "^8\\(\\d{3}\\)\\d{3}-\\d{2}-\\d{2}$".toRegex()
            if(!phone.matches(pattern))
            {
                Toast.makeText(this@AddUserActivity,getString(R.string.valid_phone),Toast.LENGTH_LONG).show()
                return false
            }
            return checkExists(phone,mDb)
        }
    }
    fun checkExists(phone:String,mDb : SQLiteDatabase) : Boolean{
        val cursor: Cursor = mDb.rawQuery("SELECT * FROM users WHERE phone = ?", arrayOf(phone))
        val res : Boolean = cursor.moveToFirst()
        cursor.close()
        if(res)
            Toast.makeText(this@AddUserActivity,getString(R.string.user_exist),Toast.LENGTH_LONG).show()
        return !res
    }
}