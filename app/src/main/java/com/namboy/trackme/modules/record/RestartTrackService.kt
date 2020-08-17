package com.namboy.trackme.modules.record

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast

class RestartTrackService : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.i("Broadcast Listened", "Service tried to stop")
        Toast.makeText(context, "Service restarted", Toast.LENGTH_SHORT).show()
        TrackService.startService(context)
    }
}