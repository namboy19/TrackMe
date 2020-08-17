package com.namboy.trackme.modules.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.namboy.trackme.R
import com.namboy.trackme.model.TrackSession
import com.namboy.trackme.utils.Util
import com.namboy.trackme.utils.loadLocalImage
import kotlinx.android.synthetic.main.fragment_record.*
import kotlinx.android.synthetic.main.itemview_track_session.view.*
import java.text.DecimalFormat

class HistoryAdapter(var listTrackSession: MutableList<TrackSession>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.itemview_track_session, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listTrackSession.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        var trackSession = listTrackSession.get(position)
        var context = holder.itemView.its_tv_distance.context

        holder.itemView.its_imv_snapshot.loadLocalImage(trackSession.imagePath ?: "")

        holder.itemView.its_tv_distance.text = context.getString(
            R.string.distance_number,
            DecimalFormat("##.##").format(
                Util.calculateDistance(
                    trackSession.getTrackLocationList(
                        Gson()
                    )
                )
            )
        )

        holder.itemView.its_tv_speed.text = context.getString(
            R.string.average_speed_number,
            Util.calculateSpeed(
                trackSession.getTrackLocationList(Gson()),
                trackSession.getTimeInLong()
            )
        )

        holder.itemView.its_tv_time.text = Util.formatTime(trackSession.timeSecond ?: 0)

    }

    class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }
}