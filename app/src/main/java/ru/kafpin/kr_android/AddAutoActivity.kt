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

class AddAutoActivity:Activity() {

    var user_id = -1
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.add_auto)

        user_id = intent.getIntExtra("user_id",-1)
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


        val bAddAuto : Button = findViewById(R.id.bAddAuto)
        val tiMark : TextInputEditText = findViewById(R.id.tiMark)
        val tiModel : TextInputEditText = findViewById(R.id.tiModel)
        val tiGosnumber : TextInputEditText = findViewById(R.id.tiGosnumber)


        bAddAuto.setBackgroundColor(resources.getColor(R.color.dark_blue))
        bAddAuto.setOnClickListener{
            val mark:String = tiMark.text.toString()
            val model:String = tiModel.text.toString()
            val gosnumber:String = tiGosnumber.text.toString()
            if(validAutoInsert(mark,model,gosnumber,mDb)){
                // Создаем объект ContentValues для хранения новых данных
                val values = ContentValues()
                values.put("mark", mark)
                values.put("model", model)
                values.put("user_id", user_id)
                values.put("gos_number", gosnumber)
                mDb.insert("automobiles", null, values);
                val answerIntent = Intent()
                setResult(RESULT_OK, answerIntent)
                finish()
            }
        }
    }
    fun validAutoInsert(mark:String,model:String,gosnumber:String,mDb : SQLiteDatabase): Boolean {
        if(mark.trim().isEmpty()||model.trim().isEmpty()||gosnumber.trim().isEmpty()){
            Toast.makeText(this@AddAutoActivity,getString(R.string.need_fill_all),Toast.LENGTH_LONG).show()
            return false
        }
        else{
            val pattern : Regex = "^[ABEKMHOPCTYX]\\d{3}(?<!000)[ABEKMHOPCTYX]{2}\\d{2,3}rus\$".toRegex()
            if(!gosnumber.matches(pattern))
            {
                Toast.makeText(this@AddAutoActivity,getString(R.string.valid_gosnumber),Toast.LENGTH_LONG).show()
                return false
            }
            return checkExists(gosnumber,mDb)
        }
    }
    fun checkExists(gosnumber: String,mDb : SQLiteDatabase) : Boolean{
        val cursor: Cursor = mDb.rawQuery("SELECT * FROM automobiles WHERE gos_number = ?", arrayOf(gosnumber))
        val res : Boolean = cursor.moveToFirst()
        cursor.close()
        if(res)
            Toast.makeText(this@AddAutoActivity,getString(R.string.automobile_exist),Toast.LENGTH_LONG).show()
        return !res
    }
}