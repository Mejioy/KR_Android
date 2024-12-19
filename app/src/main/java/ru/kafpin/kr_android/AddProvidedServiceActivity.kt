package ru.kafpin.kr_android

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Person
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.textfield.TextInputEditText
import java.io.IOException
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class AddProvidedServiceActivity:Activity() {

    companion object {
        const val NOTIFICATION_ID = 101
        const val CHANNEL_ID = "channelID"
    }

    val REQUEST_CHOOSE_THIEF = 0
    var automobile_id = -1
    var service_name = ""
    var service_id = -1
    var cal: Calendar = Calendar.getInstance()
    var allowDate: Date = Date()
    var enteredDate: Date = Date()

    var automobile_name = ""
    var user_name = ""

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Создание канала для уведомлений
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel.
            val name = CHANNEL_ID
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }


        cal.add(java.util.Calendar.DATE, -1)
        allowDate = cal.time

        setContentView(R.layout.add_providedservice)

        automobile_id = intent.getIntExtra("automobile_id",-1)
        automobile_name = intent.getStringExtra("automobile_name").toString()
        user_name = intent.getStringExtra("user_name").toString()

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
            val dateOfProvide = tiDate.text.toString()
            if(dateOfProvide.trim().isEmpty()){
                Toast.makeText(this@AddProvidedServiceActivity,getString(R.string.need_choice_date),Toast.LENGTH_LONG).show()
            }
            else{
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
    @SuppressLint("MissingPermission")
    fun validProvidedServiceInsert(service_id:Int, dateOfProvide:Date): Boolean {
        if(service_id==-1){
            Toast.makeText(this@AddProvidedServiceActivity,getString(R.string.need_choice_service),Toast.LENGTH_LONG).show()
            return false
        }
        if(dateOfProvide.before(allowDate)){
            Toast.makeText(this@AddProvidedServiceActivity,getString(R.string.need_change_date),Toast.LENGTH_LONG).show()
            return false
        }
        else {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            val formatedData = dateFormat.format(dateOfProvide)
            val sender = Person.Builder()
                .setName(user_name)
                .build()
            val messagingStyle = Notification.MessagingStyle(sender)
                .addMessage("Подана заявка записи на услугу " + service_name +" для автомобиля "+
                        automobile_name + ". Дата " + formatedData +'.', System.currentTimeMillis(), sender)
            val builder = Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setStyle(messagingStyle)
                .setChannelId(CHANNEL_ID)
            val notificationManager =
                NotificationManagerCompat.from(this@AddProvidedServiceActivity)
            notificationManager.notify(NOTIFICATION_ID, builder.build())
            return true
        }
    }

}