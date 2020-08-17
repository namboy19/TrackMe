package com.namboy.trackme.modules.record

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.namboy.trackme.MainActivity
import com.namboy.trackme.TrackMeApplication
import com.namboy.trackme.model.TrackLocation
import com.namboy.trackme.model.TrackSession
import com.namboy.trackme.utils.AppPreferences
import java.util.*


class TrackService : Service() {

    private val locationList = mutableListOf<TrackLocation>()
    private var counter = 0
    private val client: FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(this) }

    var locationUpdateCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location: Location = locationResult.getLastLocation()
            if (location != null) {

                var trackLocation = TrackLocation(location.latitude, location.longitude, Calendar.getInstance().timeInMillis)
                locationList.add(trackLocation)
                Log.d("Location Service", "location update $location")

                val intent = Intent(INTENT_TRACK_SERVICE)
                intent.putExtra(KEY_NEW_LOCATION_UPDATE, trackLocation)
                LocalBroadcastManager.getInstance(this@TrackService).sendBroadcast(intent)
            }
        }
    }

    companion object {
        const val CHANNEL_ID = "sample.channel.trackme"
        const val CHANNEL_NAME = "Sample Notification"
        const val NOTI_SERVICE_ID = 999

        var mCurrentCommand: String = ""

        val KEY_NEW_LOCATION_UPDATE = "KEY_NEW_LOCATION_UPDATE"
        val KEY_TIME_COUNTER = "KEY_TIME_COUNTER"
        val KEY_TRACK_SESSION = "KEY_TRACK_SESSION"

        val INTENT_TRACK_SERVICE = "INTENT_TRACK_SERVICE"

        val ACTION_START_SERVICE = "ACTION_START_SERVICE"
        val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
        val ACTION_RESUME_SERVICE = "ACTION_RESUME_SERVICE"
        val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"

        fun startService(context: Context) {
            val startIntent = Intent(context, TrackService::class.java)
            startIntent.action = ACTION_START_SERVICE

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(startIntent)
            } else {
                context.startService(startIntent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, TrackService::class.java)
            intent.action = ACTION_STOP_SERVICE
            context.startService(intent)
        }

        fun pauseService(context: Context) {
            val pauseIntent = Intent(context, TrackService::class.java)
            pauseIntent.action = ACTION_PAUSE_SERVICE
            context.startService(pauseIntent)
        }

        fun resumeService(context: Context) {
            val intent = Intent(context, TrackService::class.java)
            intent.action = ACTION_RESUME_SERVICE
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        TrackMeApplication.isTrackServiceRunning=true
    }

    fun createChannel(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableVibration(true)
            notificationChannel.setShowBadge(true)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.parseColor("#e8334a")
            notificationChannel.description = "notification channel description"
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    private fun getNotification(context: Context) {
        //Create Channel
        createChannel(context)

        val notifyIntent = Intent(context, MainActivity::class.java)
        notifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        var mNotification: Notification

        val pendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            mNotification = Notification.Builder(context, CHANNEL_ID)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(com.namboy.trackme.R.drawable.ic_baseline_pause_24)
                    .setAutoCancel(true)
                    .setContentTitle("TrackMe")
                    .setStyle(Notification.BigTextStyle()
                            .bigText("TrackMe is running"))
                    .setContentText("TrackMe is running").build()
        } else {
            mNotification = Notification.Builder(context)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(com.namboy.trackme.R.drawable.ic_baseline_pause_24)
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentTitle("TrackMe")
                    .setStyle(Notification.BigTextStyle()
                            .bigText("TrackMe is running"))
                    .setContentText("TrackMe is running").build()

        }

        startForeground(NOTI_SERVICE_ID, mNotification)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent?.action== ACTION_START_SERVICE){
            val intent = Intent(INTENT_TRACK_SERVICE)
            intent.putExtra(KEY_TRACK_SESSION, TrackSession(Gson().toJson(locationList),counter))
            LocalBroadcastManager.getInstance(this@TrackService).sendBroadcast(intent)
        }

        if (mCurrentCommand!=intent?.action){
            when (intent?.action) {
                ACTION_START_SERVICE -> {
                    startTimer()
                    getNotification(this)
                    requestLocationUpdates()
                }
                ACTION_PAUSE_SERVICE -> {
                    stoptimertask()
                    client.removeLocationUpdates(locationUpdateCallback)
                }
                ACTION_RESUME_SERVICE -> {
                    startTimer()
                    requestLocationUpdates()
                }
                ACTION_STOP_SERVICE->{
                    stoptimertask()
                    client.removeLocationUpdates(locationUpdateCallback)
                    stopSelf()
                }
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        TrackMeApplication.isTrackServiceRunning=false
        stoptimertask()
        client.removeLocationUpdates(locationUpdateCallback)

        if (mCurrentCommand!=ACTION_STOP_SERVICE){
            TrackMeApplication.appPreferences.saveString(AppPreferences.PREFS_CURRENT_SESSION,
                Gson().toJson(TrackSession(Gson().toJson(locationList),counter)))
        }

        /*val broadcastIntent = Intent()
        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(this, RestartTrackService::class.java)
        this.sendBroadcast(broadcastIntent)*/
    }


    fun startTimer() {
        mTimerHandler.postDelayed(mTimerCallback, 1000)
    }

    fun stoptimertask() {
        mTimerHandler.removeCallbacks(mTimerCallback)
    }

    private var mTimerHandler = Handler()

    private var mTimerCallback = Runnable {

        counter++
        val intent = Intent(INTENT_TRACK_SERVICE)
        intent.putExtra(KEY_TIME_COUNTER, counter)
        LocalBroadcastManager.getInstance(this@TrackService).sendBroadcast(intent)

        stoptimertask()
        startTimer()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun requestLocationUpdates() {
        val request = LocationRequest()
        request.setInterval(2000)
        request.setFastestInterval(2000)
        request.setSmallestDisplacement(1f)
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission == PackageManager.PERMISSION_GRANTED) { // Request location updates and when an update is
            // received, store the location in Firebase
            client.requestLocationUpdates(request, locationUpdateCallback, null)
        }
    }
}