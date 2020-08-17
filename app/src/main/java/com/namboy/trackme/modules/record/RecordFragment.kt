package com.namboy.trackme.modules.record

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.namboy.trackme.R
import com.namboy.trackme.base.BaseLifecycleFragment
import com.namboy.trackme.model.TrackLocation
import com.namboy.trackme.model.TrackSession
import com.namboy.trackme.utils.DialogFactory
import com.namboy.trackme.utils.FileUtils
import com.namboy.trackme.utils.Util
import kotlinx.android.synthetic.main.fragment_record.*
import java.text.DecimalFormat


class RecordFragment : BaseLifecycleFragment<RecordViewModel>() {

    override val viewModelClass: Class<RecordViewModel>
        get() = RecordViewModel::class.java


    private lateinit var mGoogleMap: GoogleMap
    private var mPolyline: Polyline? = null
    private var mCurrLocationMarker: Marker? = null
    private var mIsResume = false

    private val mMapCallback = OnMapReadyCallback { googleMap ->

        mGoogleMap = googleMap
        //mGoogleMap.uiSettings.setAllGesturesEnabled(false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission()
        } else {
            startLocationService()
        }
    }

    private val mUpdateLocationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            intent.getParcelableExtra<TrackLocation>(TrackService.KEY_NEW_LOCATION_UPDATE)
                ?.let { data ->
                    viewModel.addTrackLocation(data)
                }

            intent.getIntExtra(TrackService.KEY_TIME_COUNTER, -1)?.takeIf { it >= 0 }?.let { data ->
                viewModel.time.value = data
            }

            intent.getParcelableExtra<TrackSession>(TrackService.KEY_TRACK_SESSION)?.let { data ->
                viewModel.locationList.value = data.getTrackLocationList(Gson())
                viewModel.time.value = data.timeSecond
            }
        }
    }

    companion object {
        fun newInstance(isResume: Boolean = false) = RecordFragment().apply {
            this.mIsResume = isResume
        }
    }

    fun handleDisplayLocation() {

        viewModel.locationList.value?.let {
            if (mCurrLocationMarker == null && it.size > 0) {
                //Place current location marker
                val latLng = LatLng(it.get(0)?.lat, it.get(0).lng)
                val markerOptions = MarkerOptions()
                markerOptions.position(latLng)
                markerOptions.title("Current Position")
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions)

                //move map camera
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0F))
            }
        }

        rawPolyline(viewModel.locationList.value?.map { it.getLatLng() } ?: mutableListOf())
        viewModel.calculateDistanceAndSpeed()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(mMapCallback)

        fr_imv_pause.setOnClickListener {
            TrackService.pauseService(requireContext())
            fr_imv_pause.visibility = GONE
            fr_imv_refresh.visibility = VISIBLE
            //stopListenService()
        }

        fr_imv_refresh.setOnClickListener {
            TrackService.resumeService(requireContext())
            fr_imv_pause.visibility = VISIBLE
            fr_imv_refresh.visibility = GONE
            //startListenService()
        }

        fr_imv_stop.setOnClickListener {
            snapshot()
        }
    }

    fun snapshot() {
        var POLYGON_PADDING_PREFERENCE = 350
        var latLngBounds = getPolygonLatLngBounds(viewModel.locationList.value?.map { it.getLatLng() } ?: mutableListOf())
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, POLYGON_PADDING_PREFERENCE))

        showLoading(true)
        Handler().postDelayed({
            showLoading(false)

            mGoogleMap?.snapshot { bitmap ->
                FileUtils.saveBitmap(requireContext(), bitmap) {
                    it?.let {
                        TrackService.stopService(requireContext())
                        viewModel.saveSession(it) {
                            goBack()
                        }
                    }
                }
            }
        },500)
    }

    fun startLocationService() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mGoogleMap.setMyLocationEnabled(true)
            TrackService.startService(requireContext())
        }
    }

    override fun observeLiveData() {
        super.observeLiveData()

        viewModel.distance.observe(this, Observer {
            fr_tv_distance.text =
                getString(R.string.distance_number, DecimalFormat("##.##").format(it))
        })

        viewModel.speed.observe(this, Observer {
            fr_tv_speed.text = getString(R.string.speed_number, it)
        })

        viewModel.time.observe(this, Observer {
            fr_tv_time.text = Util.formatTime(it)
        })

        viewModel.locationList.observe(this, Observer {
            handleDisplayLocation()
        })
    }

    private fun rawPolyline(list: List<LatLng>) {
        if (mPolyline != null) {
            mPolyline?.remove()
        }
        val options = PolylineOptions().width(8f).color(Color.BLUE).geodesic(true)
        options.addAll(list)
        mPolyline = mGoogleMap.addPolyline(options)
    }

    private fun checkLocationPermission() {
        Dexter.withActivity(activity).withPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
        )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        startLocationService()
                    }
                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        DialogFactory.createSimpleOkDialog(
                            requireContext(), getString(R.string.alert),
                            getString(R.string.location_permission_require), getString(R.string.ok)
                        ).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).withErrorListener {
                Toast.makeText(
                    context,
                    getString(R.string.location_permission_require),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
            .onSameThread()
            .check()
    }

    private fun getPolygonLatLngBounds(polygon: List<LatLng>): LatLngBounds {
        val centerBuilder = LatLngBounds.builder()
        for (point in polygon) {
            centerBuilder.include(point)
        }
        return centerBuilder.build()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        startListenService()
    }

    override fun onDetach() {
        super.onDetach()
        stopListenService()
    }

    fun startListenService() {
        LocalBroadcastManager.getInstance(activity!!.applicationContext)
            .registerReceiver(
                mUpdateLocationReceiver,
                IntentFilter(TrackService.INTENT_TRACK_SERVICE)
            )
    }

    fun stopListenService() {
        LocalBroadcastManager.getInstance(activity!!.applicationContext)
            .unregisterReceiver(mUpdateLocationReceiver)
    }
}