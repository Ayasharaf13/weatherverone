package com.example.weathermate.home_screen.view


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weathermate.R
import com.example.weathermate.databinding.CardDayBinding
import com.example.weathermate.utilities.photos
import com.example.weathermate.utilities.Converter
import com.example.weathermate.weather_data_fetcher.DailyForecast
import java.text.SimpleDateFormat
import java.util.*

class DailyAdapter : RecyclerView.Adapter<DailyAdapter.ViewHolder>() {
    private var dailyForecasts: List<DailyForecast> = emptyList()

    inner class ViewHolder(val cardDayBinding: CardDayBinding) :
        RecyclerView.ViewHolder(cardDayBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val cardDayBinding: CardDayBinding =
            DataBindingUtil.inflate(inflater, R.layout.card_day, parent, false)
        return ViewHolder(cardDayBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentDay = dailyForecasts[position]
        holder.cardDayBinding.dayDate.text = convertToDate(currentDay.time)
        holder.cardDayBinding.currentDay.text = convertToDayName(currentDay.time)
        holder.cardDayBinding.dayStatus.setImageResource(photos[currentDay.weather[0].icon]!!)
        holder.cardDayBinding.maxTemp.text = Converter.convertDoubleToIntString(currentDay.temp.max)
        holder.cardDayBinding.minTemp.text = Converter.convertDoubleToIntString(currentDay.temp.min)
    }

    override fun getItemCount(): Int {
        return dailyForecasts.size
    }

    fun setDailyForecasts(dailyForecasts: List<DailyForecast>) {
        this.dailyForecasts = dailyForecasts
        notifyDataSetChanged()
    }

    private fun convertToDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd-MM", Locale.getDefault())
        val date = Date(timestamp * 1000L)
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }

    private fun convertToDayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEE", Locale.getDefault())
        val date = Date(timestamp * 1000L)
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }
}






/*
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weathermate.R
import com.example.weathermate.databinding.CardDayBinding
import com.example.weathermate.utilities.photos
import com.example.weathermate.utilities.Converter
import com.example.weathermate.weather_data_fetcher.DailyForecast
import java.text.SimpleDateFormat
import java.util.*

class DailyAdapter : RecyclerView.Adapter<DailyAdapter.ViewHolder>() {
    private var dailyForecasts: List<DailyForecast> = emptyList()

    inner class ViewHolder(val cardDayBinding: CardDayBinding) :
        RecyclerView.ViewHolder(cardDayBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val cardDayBinding: CardDayBinding =
            DataBindingUtil.inflate(inflater, R.layout.card_day, parent, false)
        return ViewHolder(cardDayBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentDay = dailyForecasts[position]
        holder.cardDayBinding.dayDate.text = convertToDate(currentDay.time)
        holder.cardDayBinding.currentDay.text = convertToDayName(currentDay.time)
        holder.cardDayBinding.dayStatus.setImageResource(photos[currentDay.weather[0].icon]!!)
        holder.cardDayBinding.maxTemp.text = Converter.convertDoubleToIntString(currentDay.temp.max)
        holder.cardDayBinding.minTemp.text = Converter.convertDoubleToIntString(currentDay.temp.min)
    }

    override fun getItemCount(): Int {
        return dailyForecasts.size
    }

    fun setDailyForecasts(dailyForecasts: List<DailyForecast>) {
        val diffCallback = DiffCallback(this.dailyForecasts, dailyForecasts)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.dailyForecasts = dailyForecasts
        diffResult.dispatchUpdatesTo(this)
    }


fun setDailyForecasts(dailyForecasts: List<DailyForecast>) {
    this.dailyForecasts = dailyForecasts
    notifyDataSetChanged()
}


    private fun convertToDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd-MM", Locale.getDefault())
        val date = Date(timestamp * 1000L)
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }

    private fun convertToDayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEE", Locale.getDefault())
        val date = Date(timestamp * 1000L)
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }

    private class DiffCallback(
        private val oldList: List<DailyForecast>,
        private val newList: List<DailyForecast>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}

 */

/*
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weathermate.R
import com.example.weathermate.databinding.CardDayBinding
import com.example.weathermate.utilities.photos
import com.example.weathermate.utilities.Converter
import com.example.weathermate.weather_data_fetcher.DailyForecast
import java.text.SimpleDateFormat
import java.util.*

class DailyAdapter : ListAdapter<DailyForecast, DailyAdapter.ViewHolder>(DiffUtilDaily()) {

    inner class ViewHolder(var cardDayBinding: CardDayBinding) :
        RecyclerView.ViewHolder(cardDayBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.card_day,
            parent, false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentDay = getItem(position)
        holder.cardDayBinding.dayDate.text = convertToDate(currentDay.time)
        holder.cardDayBinding.currentDay.text = convertToDayName(currentDay.time)
        holder.cardDayBinding.dayStatus.setImageResource(photos.get(currentDay.weather.get(0).icon)!!)
        holder.cardDayBinding.maxTemp.text = Converter.convertDoubleToIntString(currentDay.temp.max)
        holder.cardDayBinding.minTemp.text = Converter.convertDoubleToIntString(currentDay.temp.min)

    }

    private fun convertToDate(timestamp: Long) : String{
        val sdf = SimpleDateFormat("dd-MM", Locale.getDefault())
        val date = Date(timestamp * 1000L)
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }
    private fun convertToDayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEE", Locale.getDefault())
        val date = Date(timestamp * 1000L)
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }

}

 */