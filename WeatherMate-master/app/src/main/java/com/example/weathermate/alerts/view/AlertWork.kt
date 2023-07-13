package com.example.weathermate.alerts.view
import android.Manifest.permission.POST_NOTIFICATIONS
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.media.MediaPlayer

import android.media.RingtoneManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters

import com.example.weathermate.R
//import com.example.weathermate.alerts.AlertsDialogFragment
import com.example.weathermate.alerts.model.AlertRepo
import com.example.weathermate.database.ConcreteLocalSource
import com.example.weathermate.database.RoomAlertPojo
import com.example.weathermate.database.WeatherDao

import com.example.weathermate.databinding.FragmentAlertsDialogBinding
import com.example.weathermate.network.ConcreteRemoteSource
import com.example.weathermate.utilities.*

import com.example.weathermate.weather_data_fetcher.WeatherResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import java.util.*

/*
class AlertWork {
}

 */

class  AlertWork(private var appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    val sharedPreferences = appContext.getSharedPreferences(
        Constants.SHARED_PREFERENCE_NAME,
        Context.MODE_PRIVATE
    )

    fun isConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!
            .isConnected
    }

    val lat: Double? = sharedPreferences.getString(Constants.GPS_LAT, "0.0")?.toDouble()
    val lon: Double? = sharedPreferences.getString(Constants.GPS_LON, "0.0")?.toDouble()
    val language: String? = sharedPreferences.getString(Constants.LANGUAGE, "en")
    val units: String? =
        sharedPreferences.getString(Constants.UNITS, Constants.ENUM_UNITS.standard.toString())
    val notification = sharedPreferences.getString(
        Constants.NOTIFICATIONS,
        Constants.ENUM_NOTIFICATIONS.Enabled.toString()
    )
    val alertType = sharedPreferences.getString(
        Constants.ALERT_TYPE,
        Constants.Enum_ALERT.NOTIFICATION.toString()
    )
  //  var _repo = Repository.getInstance(
     var _repo = AlertRepo.getInstance(
      //  ApiClient.getInstance(),
      ConcreteRemoteSource.getInstance(),
        ConcreteLocalSource(appContext)
    )


    companion object {
        const val CHANNEL_ID = "channelID"
    }


    @SuppressLint("SuspiciousIndentation")
    override suspend fun doWork(): Result {
        var systemTime = System.currentTimeMillis()
        systemTime = convertDateToLong(getDateToAlert(systemTime, "en"))
        val alertWorker = inputData.getString("alertWorker")
        var roomAlert = Gson().fromJson(alertWorker, RoomAlertPojo::class.java)

        if (checkTime(roomAlert)) {
            Log.i("TAG", "doWork: checked True ////////////////////////////////")
           // var oneCallResponse: WeatherResponse
            var oneCallResponse: Response<WeatherResponse>? = null

            val apiClient = ConcreteRemoteSource .getInstance()
            if (isConnected(appContext)) {


                Log.i("TAG", "doWork: isConnected : internet isConnected")
                runBlocking {
                    oneCallResponse = lat?.let { latValue ->
                        lon?.let { lonValue ->
                            units?.let { unitsValue ->
                                language?.let { langValue ->
                                    apiClient.getWeatherData(latValue, lonValue, unitsValue, langValue)
                                }
                            }
                        }
                    }


                  //  oneCallResponse = apiClient.getOneCallResponse(lat, lon, units, language)
                   // oneCallResponse = lat?.let { apiClient.getWeatherData(it, lon, units, language) }
                }

             //   var desc: String = oneCallResponse.alerts?.get(0)?.event
              //  var desc: String = oneCallResponse?.body()?.alerts?.get(0)?.event ?: ""
                var desc: String = oneCallResponse?.body()?.alerts?.firstOrNull()?.event ?: ""



                ?: appContext.getString(R.string.no_alert_weather_is_fine)
                if (desc == "") desc = appContext.getString(R.string.no_alert_weather_is_fine)
                if (notification == Constants.ENUM_NOTIFICATIONS.Enabled.toString()) {
                    if (sharedPreferences.getString(
                            Constants.ALERT_TYPE,
                            Constants.Enum_ALERT.NOTIFICATION.toString()
                        ) == Constants.Enum_ALERT.NOTIFICATION.toString()) {
                        sharedPreferences.getString(Constants.COUNTRY_NAME, "")
                            ?.let { setupNotification(it, desc) }
                    } else {
                        Log.i("TAG", "doWork: alertType : $alertType ////////////////////////////////")

                        GlobalScope.launch(Dispatchers.Main) {
                            sharedPreferences.getString(Constants.COUNTRY_NAME, "")
                                ?.let { SetupAlarm(appContext, desc, it).onCreate() }
                        }
                    }
                    runBlocking { _repo.deleteAlert(roomAlert)}
                    WorkManager.getInstance(appContext)
                        .cancelAllWorkByTag(roomAlert.dateFrom.toString() + roomAlert.dateTo.toString())
                }
            }
        } else {
            Log.i("TAG", "doWork: checked false ////////////////////////////////")

            _repo.deleteAlert(roomAlert)
            WorkManager.getInstance(appContext)
                .cancelAllWorkByTag(roomAlert.dateFrom.toString() + roomAlert.dateTo.toString())

        }
        return Result.success()
    }

    private fun setupNotification(timezone: String, description: String) {
        val notificationId = 1
        val channelId = "channelID"
        val channelName = "Channel Name"
        val channelDescription = "Channel Description"

        val notificationSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.baseline_add_alert_24)
            .setContentTitle(timezone)
            .setContentText(description)
            .setSound(notificationSound)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = channelDescription
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    class SetupAlarm(
        private val context: Context,
        private val description: String,
        private val country: String
    ) {
        lateinit var binding:FragmentAlertsDialogBinding //AlarmDialogBinding
        private lateinit var customDialog: View
        private var mediaPlayer: MediaPlayer = MediaPlayer.create(context, R.raw.alarm)


        fun onCreate() {
            mediaPlayer.start()
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            customDialog = inflater.inflate(R.layout.fragment_alerts_dialog, null)
            binding = FragmentAlertsDialogBinding.bind(customDialog)
            initView()
            val LAYOUT_FLAG: Int = Flag()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val layoutParams: WindowManager.LayoutParams = Params(LAYOUT_FLAG)
            windowManager.addView(customDialog, layoutParams)

        }

        private fun Flag(): Int {
            val LAYOUT_FLAG: Int
            LAYOUT_FLAG = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }
            return LAYOUT_FLAG
        }

        private fun Params(LAYOUT_FLAG: Int): WindowManager.LayoutParams {
            val width = (context.resources.displayMetrics.widthPixels * 0.85).toInt()
            return WindowManager.LayoutParams(
                width,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LOCAL_FOCUS_MODE,
                PixelFormat.TRANSLUCENT
            )
        }

        private fun initView() {

         //  binding.txtAlarmDesc.text = description
              binding.txtAlarmDesc.text=description
            binding.txtAlarmCountry.text = country
           // binding.btnOkAlarm.setOnClickListener {
             binding.btnConfrim.setOnClickListener {
                close()
            }
        }
        private fun close() {
            try {
                (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).removeView(
                    customDialog
                )
                customDialog.invalidate()
                (customDialog.parent as ViewGroup).removeAllViews()
            } catch (e: Exception) {
                Log.d("Error", e.toString())
            }
            mediaPlayer.release()
        }
    }

    private fun checkTime(alert: RoomAlertPojo): Boolean {
        val calendar = Calendar.getInstance()
        val currentTime2 = convertTimeToLong(
            getTimeToAlert( calendar.time.time,"en"))
        val currentDateInMillis = convertDateToLong( getDateToAlert( Date().time,"en"))
        Log.i("TAG", "checkTime: alert.dateFrom : ${alert.dateFrom}  ///// \n alert.dateTo : ${alert.dateTo}  \n currentDateInMillis : ${currentDateInMillis} \n   currentTime2: $currentTime2 \n   alert.time: ${alert.time}")
        return (currentDateInMillis >= alert.dateFrom) && (currentDateInMillis <= alert.dateTo + 300000) && (currentTime2 >= alert.time)
    }

}



