package com.namboy.trackme

import android.Manifest
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.namboy.trackme.base.BaseActivity
import com.namboy.trackme.base.BaseLifecycleActivity
import com.namboy.trackme.modules.history.HistoryFragment
import com.namboy.trackme.modules.record.RecordFragment
import com.namboy.trackme.modules.record.TrackService
import com.namboy.trackme.utils.AppPreferences
import com.namboy.trackme.utils.DialogFactory

class MainActivity : BaseLifecycleActivity<MainViewModel>() {

    override val viewModelClass: Class<MainViewModel>
        get() = MainViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        backStack.addFragment(HistoryFragment())
        checkPermission()
    }

    fun checkSessionExist() {
        if ((getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)?.activeNotifications?.filter { it.id==TrackService.NOTI_SERVICE_ID }?.isNotEmpty()==true){
            backStack.addFragment(RecordFragment.newInstance(true))
        }

        //handle case stop by system os
        if (TrackMeApplication.appPreferences.getString(AppPreferences.PREFS_CURRENT_SESSION)
                ?.isNotEmpty() == true) {

        }
    }

    private fun checkPermission() {
        Dexter.withActivity(this).withPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        checkSessionExist()
                    }
                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        DialogFactory.createSimpleOkDialog(this@MainActivity, getString(R.string.alert),
                            getString(R.string.location_permission_require), getString(R.string.ok)).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                    token.continuePermissionRequest()
                }
            }).withErrorListener {
                Toast.makeText(this, getString(R.string.location_permission_require), Toast.LENGTH_SHORT)
                    .show()
            }
            .onSameThread()
            .check()
    }

    override fun onBackPressed() {
        if (backStack.getFragmentCount()<=1){
            finish()
        }
    }

}