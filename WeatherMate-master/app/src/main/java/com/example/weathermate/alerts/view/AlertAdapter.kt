package com.example.weathermate.alerts.view

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.weathermate.R
import com.example.weathermate.alerts.model.AlertRepoInterface
import com.example.weathermate.database.RoomAlertPojo
import com.example.weathermate.databinding.CardAlertBinding
import com.example.weathermate.utilities.getDateToAlert
import com.example.weathermate.utilities.getTimeToAlert
import com.google.android.material.snackbar.Snackbar




/*
class AlertAdapter(
    private var alertList: MutableList<RoomAlertPojo>,
    var context: Context,
    var myListener: AlertRepoInterface
) : RecyclerView.Adapter<AlertAdapter.ViewHolder>() {
    private lateinit var binding: CardAlertBinding

    fun setList(list: List<RoomAlertPojo>) {
        alertList.clear()
        alertList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        binding = CardAlertBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = alertList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = alertList[position]

        holder.binding.txtFromDate.text = getDateToAlert(current.dateFrom, "en")
        holder.binding.txtToDate.text = getDateToAlert(current.dateTo, "en")
        holder.binding.txtFromTime.text = getTimeToAlert(current.time, "en")
        holder.binding.txtToTime.text = getTimeToAlert(current.time, "en")
        holder.binding.imageAlertDelete.setOnClickListener {
            val alertDialog = AlertDialog.Builder(context)

            alertDialog.apply {
                setIcon(R.drawable.baseline_delete_24)
                setTitle("Delete")
                setMessage("Are you sure you want to delete this alert?")
                setPositiveButton("OK") { _: DialogInterface?, _: Int ->
                 //   myListener.alertDeleteClick(current)
                    Snackbar.make(
                        binding.root,
                        "The alert deleted successfully",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                setNegativeButton("Cancel") { _, _ ->
                }
            }.create().show()
        }
    }

    inner class ViewHolder(var binding: CardAlertBinding) : RecyclerView.ViewHolder(binding.root)
}

 */




class AlertAdapter(
    private var alertList: MutableList<RoomAlertPojo>,
    var context: Context,
    var myListener: OnAlertListener
) : RecyclerView.Adapter<AlertAdapter.ViewHolder>() {
    private lateinit var binding: CardAlertBinding


    fun setList(list: List<RoomAlertPojo>) {
        alertList = list as MutableList<RoomAlertPojo>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = CardAlertBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = alertList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val current = alertList[position]

        holder.binding.txtFromDate.text = getDateToAlert(current.dateFrom,"en")
        holder.binding.txtToDate.text = getDateToAlert(current.dateTo,"en")
        holder.binding.txtFromTime.text = getTimeToAlert(current.time,"en")
        holder.binding.txtToTime.text = getTimeToAlert(current.time,"en")
        holder.binding.imageAlertDelete.setOnClickListener {
            val alertDialog = AlertDialog.Builder(context)

            alertDialog.apply {
                setIcon(R.drawable.baseline_delete_24)
                setTitle("Delete")
                setMessage("Are you sure you want to delete this alert ?")
                setPositiveButton("OK") { _: DialogInterface?, _: Int ->
                    myListener.alertDeleteClick(current)
                    Snackbar.make(
                        binding.root,
                        "The alert deleted successfully",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                setNegativeButton("Cancel") { _, _ ->
                }
            }.create().show()
        }
    }



    inner class ViewHolder(var binding: CardAlertBinding) : RecyclerView.ViewHolder(binding.root)

}





interface OnAlertListener {
    fun alertCardClick(alertObject: RoomAlertPojo)
    fun alertDeleteClick(alertObject: RoomAlertPojo)
}

