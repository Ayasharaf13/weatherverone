package com.example.weathermate.alerts.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.sqlite.db.SupportSQLiteCompat.Api16Impl.cancel
import androidx.work.*
import com.example.weathermate.MainActivity
import com.example.weathermate.R
import com.example.weathermate.alerts.model.AlertRepo
import com.example.weathermate.alerts.model.AlertRepoInterface
import com.example.weathermate.alerts.viewmodel.AlertViewModel
import com.example.weathermate.alerts.viewmodel.AlertViewModelFactory
import com.example.weathermate.database.ConcreteLocalSource
import com.example.weathermate.database.RoomAlertPojo
import com.example.weathermate.databinding.FragmentAlertsBinding
import com.example.weathermate.network.ApiState
import com.example.weathermate.network.ConcreteRemoteSource
import com.example.weathermate.network.ResponseState
import com.example.weathermate.utilities.Constants
import com.example.weathermate.utilities.convertTimeToLong
import com.example.weathermate.utilities.getTimeToAlert
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.log










class AlertsFragment : Fragment(), OnAlertListener {
    private lateinit var binding: FragmentAlertsBinding
    private lateinit var sharedPreference: SharedPreferences
    private lateinit var alertAdapter: AlertAdapter
    private lateinit var alertViewModel: AlertViewModel
    private lateinit var alertViewModelFactory: AlertViewModelFactory
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var countryName: String
    private var dateFrom: Long = 0
    private var dateTo: Long = 0
    private var time: Long = 0
    private lateinit var alertType: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAlertsBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getSharedPreferences(
            Constants.SHARED_PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        alertType =
            sharedPreferences.getString(Constants.ALERT_TYPE, Constants.Enum_ALERT.NOTIFICATION.toString())
                .toString()

        return binding.root
    }

    @SuppressLint(
        "SetTextI18n", "SimpleDateFormat", "CutPasteId", "NotifyDataSetChanged",
        "SuspiciousIndentation", "MissingInflatedId"
    )
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (context as AppCompatActivity).supportActionBar?.title = getString(R.string.alerts)
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()

        checkOverlayPermission()

        alertAdapter = AlertAdapter(ArrayList(), requireContext(), this)
        alertViewModelFactory = AlertViewModelFactory(

            // Repository.getInstance(
            AlertRepo.getInstance(
                ConcreteRemoteSource.getInstance(),
                ConcreteLocalSource(requireContext())
            )
        )
        alertViewModel = ViewModelProvider(
            this, alertViewModelFactory
        )[AlertViewModel::class.java]
        binding.recyclerViewAlert.adapter = alertAdapter


        sharedPreference = (context)
            ?.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)!!
        countryName = sharedPreference.getString(Constants.COUNTRY_NAME, "Fayoum").toString()
/*
lifecycleScope.launch {
    alertViewModel.alertResponse.collectLatest { result ->
        when (result) {
            is ApiState.Loading -> {
                binding.alertProgressBar.visibility = View.VISIBLE
                binding.alertLottiAnimation.visibility = View.GONE
                binding.txtNoAlerts.visibility = View.GONE
                binding.recyclerViewAlert.visibility = View.GONE
            }
 */

        lifecycleScope.launch() {
            alertViewModel.alertResponse.collectLatest { result ->
                when (result) {
                    // is ApiState.Loading -> {
                    is ResponseState.Loading->{
                        binding.alertProgressBar.visibility = View.VISIBLE
                        binding.alertLottiAnimation.visibility = View.GONE
                        binding.txtNoAlerts.visibility = View.GONE
                        binding.recyclerViewAlert.visibility = View.GONE
                    }
                    is ResponseState.Success -> {
                        if (result.data.isNotEmpty()) {
                            binding.alertProgressBar.visibility = View.GONE
                            binding.alertLottiAnimation.visibility = View.GONE
                            binding.txtNoAlerts.visibility = View.GONE
                            binding.recyclerViewAlert.visibility = View.VISIBLE
                            alertAdapter.setList(result.data)
                            alertAdapter.notifyDataSetChanged()
                        } else {
                            binding.alertProgressBar.visibility = View.GONE
                            binding.alertLottiAnimation.visibility = View.VISIBLE
                            binding.txtNoAlerts.visibility = View.VISIBLE
                            binding.recyclerViewAlert.visibility = View.GONE
                        }
                    }
                    is ResponseState.Failure-> {
                        binding.alertProgressBar.visibility = View.VISIBLE
                        binding.alertLottiAnimation.visibility = View.GONE
                        binding.txtNoAlerts.visibility = View.GONE
                        binding.recyclerViewAlert.visibility = View.GONE
                        Snackbar.make(binding.root, result.msg.toString(), Snackbar.LENGTH_LONG)
                            .show()
                    }
                    else -> {}
                }
            }
        }
        binding.addAlertFloating.setOnClickListener {
            val alertDialogView =
                LayoutInflater.from(requireContext()).inflate(R.layout.fragment_alerts_dialog, null)
            val alertBuilder = AlertDialog.Builder(requireContext()).setView(alertDialogView)
                .setTitle(getString(R.string.setup_alert)).setIcon(R.drawable.baseline_add_alert_24)
            val alertDialog = alertBuilder.show()
            val fromDate: TextView = alertDialogView.findViewById(R.id.tv_from)
            val toDate: TextView = alertDialogView.findViewById(R.id.tv_to)
            val txtTime: TextView = alertDialogView.findViewById(R.id.tv_time)
            //   alertDialogView.findViewById<MaterialButton>(R.id.btn_cancel_alert).setOnClickListener {
            // alertDialog.dismiss()
            //  }
            if(alertType==Constants.Enum_ALERT.NOTIFICATION.toString())
            {
                alertDialogView.findViewById<RadioGroup>(R.id.radio_group).check(R.id.radio_button_notification)
            }
            else{
                alertDialogView.findViewById<RadioGroup>(R.id.radio_group).check(R.id.radio_button_alarm)
            }
            alertDialogView.findViewById<RadioGroup>(R.id.radio_group).setOnCheckedChangeListener { group, checkedId ->
                val alertTypetxt: RadioButton = alertDialogView.findViewById<View>(checkedId) as RadioButton
                when (alertTypetxt.text) {
                    getString(R.string.notifications) -> {
                        sharedPreferences.edit()
                            .putString(Constants.ALERT_TYPE, Constants.Enum_ALERT.NOTIFICATION.toString())
                            .apply()
                    }
                    getString(R.string.alarm) -> {
                        sharedPreferences.edit()
                            .putString(Constants.ALERT_TYPE, Constants.Enum_ALERT.ALARM.toString())
                            .apply()
                    }
                }

            }
            fromDate.setOnClickListener {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)


                val dpd = DatePickerDialog(
                    requireContext(), { view, year, monthOfYear, dayOfMonth ->

                        var dateString =
                            ("$dayOfMonth ${DateFormatSymbols(Locale.ENGLISH).months[monthOfYear]}, $year")
                        fromDate.text = dateString
                        val format = SimpleDateFormat("dd MMM, yyyy")
                        dateFrom = format.parse(dateString).time
                    }, year, month, day
                )
                dpd.datePicker.minDate = c.timeInMillis;
                dpd.show()
            }
            toDate.setOnClickListener {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)


                val dpd = DatePickerDialog(
                    requireContext(),
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        var dateString =
                            ("$dayOfMonth ${DateFormatSymbols(Locale.ENGLISH).months[monthOfYear]}, $year")
                        toDate.text = dateString
                        val format = SimpleDateFormat("dd MMM, yyyy")
                        dateTo = format.parse(dateString).time
                    },
                    year,
                    month,
                    day
                )

                dpd.show()
            }
            txtTime.setOnClickListener {
                val cal = Calendar.getInstance()
                val timeSetListner = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    cal.set(Calendar.MINUTE, minute)

                    val am_or_pm: String = if (hourOfDay > 12) {
                        "PM"
                    } else {
                        "AM"
                    }
                    txtTime.text = SimpleDateFormat("hh:mm a").format(cal.time)
                    time = convertTimeToLong(SimpleDateFormat("hh:mm a").format(cal.time))
                }
                val tpd = TimePickerDialog(
                    requireContext(),
                    timeSetListner,
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    false
                )

                tpd.show()

            }
            alertDialogView.findViewById<MaterialButton>(R.id.btn_save_alert).setOnClickListener {
                if (txtTime.text != "" && fromDate.text != "" && toDate.text != "") {
                    val roomAlertPojo = RoomAlertPojo(
                        dateFrom = dateFrom,
                        dateTo = dateTo,
                        time = time,
                        countryName = countryName, description = ""
                    )
                    alertViewModel.insertAlert(roomAlertPojo)
                    alertAdapter.notifyDataSetChanged()
                    setupWorker(roomAlertPojo)
                    alertDialog.dismiss()
                    Snackbar.make(
                        binding.root,
                        getString(R.string.alert_added_successfully),
                        Snackbar.LENGTH_LONG
                    ).show()

                } else {
                    val alertDialog = AlertDialog.Builder(requireContext())

                    alertDialog.apply {
                        setIcon(R.drawable.info)
                        setTitle(getString(R.string.info))
                        setMessage(getString(R.string.enter_date_time))
                        setPositiveButton(getString(R.string.ok)) { _: DialogInterface?, _: Int ->
                        }
                    }.create().show()
                }
            }
        }
    }

    override fun alertCardClick(alertObject: RoomAlertPojo) {

    }

    override fun alertDeleteClick(alertObject: RoomAlertPojo) {
        alertViewModel.deleteAlert(alertObject)
        alertAdapter.notifyDataSetChanged()
    }
    private fun setupWorker(roomAlert: RoomAlertPojo) {
        val initialDelay = calculateInitialDelay(roomAlert)

        val inputData = createWorkerInputData(roomAlert)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<AlertWork>(2, TimeUnit.MILLISECONDS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag(generateWorkTag(roomAlert))
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(requireContext())
            .enqueueUniquePeriodicWork(
                generateWorkTag(roomAlert),
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
    }

    private fun calculateInitialDelay(roomAlert: RoomAlertPojo): Long {
        val currentTime = convertTimeToLong(getTimeToAlert(Calendar.getInstance().timeInMillis, "en"))
        val targetTime = roomAlert.time
        return targetTime - currentTime
    }

    private fun createWorkerInputData(roomAlert: RoomAlertPojo): Data {
        val dataBuilder = Data.Builder()
        dataBuilder.putString("address", roomAlert.countryName)
        dataBuilder.putLong("startDate", roomAlert.dateFrom)
        dataBuilder.putLong("endDate", roomAlert.dateTo)
        val alertJson = Gson().toJson(roomAlert)
        dataBuilder.putString("alertWorker", alertJson)
        return dataBuilder.build()
    }

    private fun generateWorkTag(roomAlert: RoomAlertPojo): String {
        return roomAlert.dateFrom.toString() + roomAlert.dateTo.toString()
    }


    private fun checkOverlayPermission() {
        if (!Settings.canDrawOverlays(requireContext())) {
            val alertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
            alertDialogBuilder.setTitle(getString(R.string.weather_alarm))
                .setMessage(getString(R.string.features))
                .setPositiveButton(getString(R.string.ok)) { dialog: DialogInterface, i: Int ->
                    var myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    startActivity(myIntent)
                    dialog.dismiss()
                }.setNegativeButton(
                    getString(R.string.cancel)
                ) { dialog: DialogInterface, i: Int ->
                    dialog.dismiss()
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                }.show()
        }
    }
}





/*
class AlertsFragment : Fragment(), AlertRepoInterface  {
    private lateinit var binding: FragmentAlertsBinding
    private lateinit var sharedPreference: SharedPreferences
    private lateinit var alertAdapter: AlertAdapter
    private lateinit var alertViewModel: AlertViewModel
    private lateinit var alertViewModelFactory: AlertViewModelFactory
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var countryName: String
    private var dateFrom: Long = 0
    private var dateTo: Long = 0
    private var time: Long = 0
    private lateinit var alertType: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlertsBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getSharedPreferences(
            Constants.SHARED_PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        alertType = sharedPreferences.getString(Constants.ALERT_TYPE, Constants.Enum_ALERT.NOTIFICATION.toString())
            .toString()
        return binding.root
    }

    @SuppressLint(
        "SetTextI18n", "SimpleDateFormat", "CutPasteId", "NotifyDataSetChanged",
        "SuspiciousIndentation", "MissingInflatedId"
    )
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (context as AppCompatActivity).supportActionBar?.title = getString(R.string.alerts)
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()

        checkOverlayPermission()

        alertAdapter = AlertAdapter(ArrayList(), requireContext(), this)
        alertViewModelFactory = AlertViewModelFactory(
            AlertRepo.getInstance(
                ConcreteRemoteSource.getInstance(),
                ConcreteLocalSource(requireContext())
            )
        )
        alertViewModel = ViewModelProvider(this, alertViewModelFactory)[AlertViewModel::class.java]
        binding.recyclerViewAlert.adapter = alertAdapter

        sharedPreference =
            requireContext().getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        countryName = sharedPreference.getString(Constants.COUNTRY_NAME, "Fayoum").toString()

        lifecycleScope.launch {
            alertViewModel.alertResponse.collectLatest { result ->
                when (result) {
                    is ResponseState.Loading -> {
                        binding.alertProgressBar.visibility = View.VISIBLE
                        binding.alertLottiAnimation.visibility = View.GONE
                        binding.txtNoAlerts.visibility = View.GONE
                        binding.recyclerViewAlert.visibility = View.GONE
                    }
                    is ResponseState.Success -> {
                        if (result.data.isNotEmpty()) {
                            binding.alertProgressBar.visibility = View.GONE
                            binding.alertLottiAnimation.visibility = View.GONE
                            binding.txtNoAlerts.visibility = View.GONE
                            binding.recyclerViewAlert.visibility = View.VISIBLE
                            alertAdapter.setList(result.data)
                        } else {
                            binding.alertProgressBar.visibility = View.GONE
                            binding.alertLottiAnimation.visibility = View.VISIBLE
                            binding.txtNoAlerts.visibility = View.VISIBLE
                            binding.recyclerViewAlert.visibility = View.GONE
                        }
                    }
                    is ResponseState.Failure -> {
                        binding.alertProgressBar.visibility = View.GONE
                        binding.alertLottiAnimation.visibility = View.GONE
                        binding.txtNoAlerts.visibility = View.GONE
                        binding.recyclerViewAlert.visibility = View.GONE
                        Snackbar.make(binding.root, result.msg.toString(), Snackbar.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }

        binding.addAlertFloating.setOnClickListener {
            showAddAlertDialog()
        }
    }

    override fun alertCardClick(alertObject: RoomAlertPojo) {
        // Handle alert card click event here
    }

    override fun getAllAlerts(): Flow<List<RoomAlertPojo>> {
        TODO("Not yet implemented")

    }

    override suspend fun insertAlert(alert: RoomAlertPojo) {
       // TODO("Not yet implemented")
    }

    override fun alertDeleteClick(alertObject: RoomAlertPojo) {
        alertViewModel.deleteAlert(alertObject)
    }

    private fun showAddAlertDialog() {
        val alertDialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.fragment_alerts_dialog, null)
        val alertBuilder = AlertDialog.Builder(requireContext()).setView(alertDialogView)
            .setTitle(getString(R.string.setup_alert)).setIcon(R.drawable.baseline_add_alert_24)
        val alertDialog = alertBuilder.show()
        val fromDate: TextView = alertDialogView.findViewById(R.id.tv_from)
        val toDate: TextView = alertDialogView.findViewById(R.id.tv_to)
        val txtTime: TextView = alertDialogView.findViewById(R.id.tv_time)

        alertDialogView.findViewById<RadioGroup>(R.id.radio_group)
            .setOnCheckedChangeListener { group, checkedId ->
                val alertTypetxt: RadioButton =
                    alertDialogView.findViewById<View>(checkedId) as RadioButton
                val alertType =
                    if (alertTypetxt.text == getString(R.string.notifications))
                        Constants.Enum_ALERT.NOTIFICATION.toString()
                    else
                        Constants.Enum_ALERT.ALARM.toString()

                sharedPreferences.edit().putString(Constants.ALERT_TYPE, alertType).apply()
            }

        fromDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                requireContext(), { _, year, monthOfYear, dayOfMonth ->
                    val dateString =
                        ("$dayOfMonth ${DateFormatSymbols(Locale.ENGLISH).months[monthOfYear]}, $year")
                    fromDate.text = dateString
                    val format = SimpleDateFormat("dd MMM, yyyy")
                    dateFrom = format.parse(dateString).time
                }, year, month, day
            )
            dpd.datePicker.minDate = c.timeInMillis
            dpd.show()
        }

        toDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                requireContext(),
                { _, year, monthOfYear, dayOfMonth ->
                    val dateString =
                        ("$dayOfMonth ${DateFormatSymbols(Locale.ENGLISH).months[monthOfYear]}, $year")
                    toDate.text = dateString
                    val format = SimpleDateFormat("dd MMM, yyyy")
                    dateTo = format.parse(dateString).time
                },
                year,
                month,
                day
            )

            dpd.show()
        }

        txtTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)

                val amOrPm: String = if (hourOfDay > 12) {
                    "PM"
                } else {
                    "AM"
                }
                txtTime.text = SimpleDateFormat("hh:mm a").format(cal.time)
                time = convertTimeToLong(SimpleDateFormat("hh:mm a").format(cal.time))
            }
            val tpd = TimePickerDialog(
                requireContext(),
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                false
            )

            tpd.show()
        }

        alertDialogView.findViewById<MaterialButton>(R.id.btn_save_alert).setOnClickListener {
            if (txtTime.text.isNotEmpty() && fromDate.text.isNotEmpty() && toDate.text.isNotEmpty()) {
                val roomAlertPojo = RoomAlertPojo(
                    dateFrom = dateFrom,
                    dateTo = dateTo,
                    time = time,
                    countryName = countryName,
                    description = ""
                )
                alertViewModel.insertAlert(roomAlertPojo)
                setupWorker(roomAlertPojo)
                alertDialog.dismiss()
                Snackbar.make(
                    binding.root,
                    getString(R.string.alert_added_successfully),
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.apply {
                    setIcon(R.drawable.info)
                    setTitle(getString(R.string.info))
                    setMessage(getString(R.string.enter_date_time))
                    setPositiveButton(getString(R.string.ok)) { _, _ ->
                    }
                }.create().show()
            }
        }
    }

    private fun setupWorker(roomalert: RoomAlertPojo) {
        val calendar = Calendar.getInstance()
        val currentTime = convertTimeToLong(getTimeToAlert(calendar.timeInMillis, "en"))
        val targetTime = roomalert.time
        val initialDelay = targetTime - currentTime

        val data = Data.Builder()
        data.putString("address", roomalert.countryName)
        data.putLong("startDate", roomalert.dateFrom)
        data.putLong("endDate", roomalert.dateTo)
        val alert = Gson().toJson(roomalert)
        data.putString("alertWorker", alert)

        val workRequest = PeriodicWorkRequestBuilder<AlertWork>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag(roomalert.dateFrom.toString() + roomalert.dateTo.toString())
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .setInputData(data.build())
            .build()

        WorkManager.getInstance(requireContext())
            .enqueueUniquePeriodicWork(
                roomalert.dateFrom.toString() + roomalert.dateTo.toString(),
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
    }

    private fun checkOverlayPermission() {
        if (!Settings.canDrawOverlays(requireContext())) {
            val alertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
            alertDialogBuilder.setTitle(getString(R.string.weather_alarm))
                .setMessage(getString(R.string.features))
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    startActivity(myIntent)
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                }
                .show()
        }
    }
}
*/

/*
class AlertsFragment : Fragment(), AlertRepoInterface {
    private lateinit var binding: FragmentAlertsBinding
    private lateinit var sharedPreference: SharedPreferences
    private lateinit var alertAdapter: AlertAdapter
    private lateinit var alertViewModel: AlertViewModel
    private lateinit var alertViewModelFactory: AlertViewModelFactory
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var countryName: String
    private var dateFrom: Long = 0
    private var dateTo: Long = 0
    private var time: Long = 0
    private lateinit var alertType: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAlertsBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getSharedPreferences(
            Constants.SHARED_PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        alertType =
            sharedPreferences.getString(Constants.ALERT_TYPE, Constants.Enum_ALERT.NOTIFICATION.toString())
                .toString()

        return binding.root
    }

    @SuppressLint(
        "SetTextI18n", "SimpleDateFormat", "CutPasteId", "NotifyDataSetChanged",
        "SuspiciousIndentation", "MissingInflatedId"
    )
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (context as AppCompatActivity).supportActionBar?.title = getString(R.string.alerts)
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()

        checkOverlayPermission()

        alertAdapter = AlertAdapter(ArrayList(), requireContext(), this)
        alertViewModelFactory = AlertViewModelFactory(

           // Repository.getInstance(
                AlertRepo.getInstance(
                ConcreteRemoteSource.getInstance(),
                ConcreteLocalSource(requireContext())
            )
        )
        alertViewModel = ViewModelProvider(
            this, alertViewModelFactory
        )[AlertViewModel::class.java]
        binding.recyclerViewAlert.adapter = alertAdapter


        sharedPreference = (context)
            ?.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)!!
        countryName = sharedPreference.getString(Constants.COUNTRY_NAME, "Fayoum").toString()

        lifecycleScope.launch() {
            alertViewModel.alertResponse.collectLatest { result ->
                when (result) {
                   // is ApiState.Loading -> {
                    is ResponseState.Loading->{
                        binding.alertProgressBar.visibility = View.VISIBLE
                        binding.alertLottiAnimation.visibility = View.GONE
                        binding.txtNoAlerts.visibility = View.GONE
                        binding.recyclerViewAlert.visibility = View.GONE
                    }
                    is ResponseState.Success -> {
                        if (result.data.isNotEmpty()) {
                            binding.alertProgressBar.visibility = View.GONE
                            binding.alertLottiAnimation.visibility = View.GONE
                            binding.txtNoAlerts.visibility = View.GONE
                            binding.recyclerViewAlert.visibility = View.VISIBLE
                            alertAdapter.setList(result.data)
                            alertAdapter.notifyDataSetChanged()
                        } else {
                            binding.alertProgressBar.visibility = View.GONE
                            binding.alertLottiAnimation.visibility = View.VISIBLE
                            binding.txtNoAlerts.visibility = View.VISIBLE
                            binding.recyclerViewAlert.visibility = View.GONE
                        }
                    }
                    is ResponseState.Failure-> {
                        binding.alertProgressBar.visibility = View.VISIBLE
                        binding.alertLottiAnimation.visibility = View.GONE
                        binding.txtNoAlerts.visibility = View.GONE
                        binding.recyclerViewAlert.visibility = View.GONE
                        Snackbar.make(binding.root, result.msg.toString(), Snackbar.LENGTH_LONG)
                            .show()
                    }
                    else -> {}
                }
            }
        }
        binding.addAlertFloating.setOnClickListener {
            val alertDialogView =
                LayoutInflater.from(requireContext()).inflate(R.layout.fragment_alerts_dialog, null)
            val alertBuilder = AlertDialog.Builder(requireContext()).setView(alertDialogView)
                .setTitle(getString(R.string.setup_alert)).setIcon(R.drawable.baseline_add_alert_24)
            val alertDialog = alertBuilder.show()
            val fromDate: TextView = alertDialogView.findViewById(R.id.tv_from)
            val toDate: TextView = alertDialogView.findViewById(R.id.tv_to)
            val txtTime: TextView = alertDialogView.findViewById(R.id.tv_time)
         //   alertDialogView.findViewById<MaterialButton>(R.id.btn_cancel_alert).setOnClickListener {
               // alertDialog.dismiss()
          //  }
            if(alertType==Constants.Enum_ALERT.NOTIFICATION.toString())
            {
                alertDialogView.findViewById<RadioGroup>(R.id.radio_group).check(R.id.radio_button_notification)
            }
            else{
                alertDialogView.findViewById<RadioGroup>(R.id.radio_group).check(R.id.radio_button_alarm)
            }
            alertDialogView.findViewById<RadioGroup>(R.id.radio_group).setOnCheckedChangeListener { group, checkedId ->
                val alertTypetxt: RadioButton = alertDialogView.findViewById<View>(checkedId) as RadioButton
                when (alertTypetxt.text) {
                    getString(R.string.notifications) -> {
                        sharedPreferences.edit()
                            .putString(Constants.ALERT_TYPE, Constants.Enum_ALERT.NOTIFICATION.toString())
                            .apply()
                    }
                    getString(R.string.alarm) -> {
                        sharedPreferences.edit()
                            .putString(Constants.ALERT_TYPE, Constants.Enum_ALERT.ALARM.toString())
                            .apply()
                    }
                }

            }
            fromDate.setOnClickListener {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)


                val dpd = DatePickerDialog(
                    requireContext(), { view, year, monthOfYear, dayOfMonth ->

                        var dateString =
                            ("$dayOfMonth ${DateFormatSymbols(Locale.ENGLISH).months[monthOfYear]}, $year")
                        fromDate.text = dateString
                        val format = SimpleDateFormat("dd MMM, yyyy")
                        dateFrom = format.parse(dateString).time
                    }, year, month, day
                )
                dpd.datePicker.minDate = c.timeInMillis;
                dpd.show()
            }
            toDate.setOnClickListener {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)


                val dpd = DatePickerDialog(
                    requireContext(),
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        var dateString =
                            ("$dayOfMonth ${DateFormatSymbols(Locale.ENGLISH).months[monthOfYear]}, $year")
                        toDate.text = dateString
                        val format = SimpleDateFormat("dd MMM, yyyy")
                        dateTo = format.parse(dateString).time
                    },
                    year,
                    month,
                    day
                )

                dpd.show()
            }
            txtTime.setOnClickListener {
                val cal = Calendar.getInstance()
                val timeSetListner = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    cal.set(Calendar.MINUTE, minute)

                    val am_or_pm: String = if (hourOfDay > 12) {
                        "PM"
                    } else {
                        "AM"
                    }
                    txtTime.text = SimpleDateFormat("hh:mm a").format(cal.time)
                    time = convertTimeToLong(SimpleDateFormat("hh:mm a").format(cal.time))
                }
                val tpd = TimePickerDialog(
                    requireContext(),
                    timeSetListner,
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    false
                )

                tpd.show()

            }
            alertDialogView.findViewById<MaterialButton>(R.id.btn_save_alert).setOnClickListener {
                if (txtTime.text != "" && fromDate.text != "" && toDate.text != "") {
                    val roomAlertPojo = RoomAlertPojo(
                        dateFrom = dateFrom,
                        dateTo = dateTo,
                        time = time,
                        countryName = countryName, description = ""
                    )
                    alertViewModel.insertAlert(roomAlertPojo)
                    alertAdapter.notifyDataSetChanged()
                    setupWorker(roomAlertPojo)
                    alertDialog.dismiss()
                    Snackbar.make(
                        binding.root,
                        getString(R.string.alert_added_successfully),
                        Snackbar.LENGTH_LONG
                    ).show()

                } else {
                    val alertDialog = AlertDialog.Builder(requireContext())

                    alertDialog.apply {
                        setIcon(R.drawable.info)
                        setTitle(getString(R.string.info))
                        setMessage(getString(R.string.enter_date_time))
                        setPositiveButton(getString(R.string.ok)) { _: DialogInterface?, _: Int ->
                        }
                    }.create().show()
                }
            }
        }
    }



    override fun getAllAlerts(): Flow<List<RoomAlertPojo>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlert(alert: RoomAlertPojo) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlert(alert: RoomAlertPojo) {
        TODO("Not yet implemented")
    }


    fun setupWorker(roomalert: RoomAlertPojo) {
        val calendar = java.util.Calendar.getInstance()
        val currentTime = convertTimeToLong(getTimeToAlert(calendar.timeInMillis, "en"))
        val targetTime = roomalert.time
        val initialDelay = targetTime - currentTime
        println(getTimeToAlert(currentTime, "en") + currentTime)
        println(getTimeToAlert(roomalert.time, "en") + targetTime)
        println(initialDelay)

        val data = Data.Builder()
        data.putString("address", roomalert.countryName)
        data.putLong("startDate", roomalert.dateFrom)
        data.putLong("endDate", roomalert.dateTo)
        var alert = Gson().toJson(roomalert)
        data.putString("alertWorker", alert)
        val workRequest = PeriodicWorkRequestBuilder<AlertWork>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag(roomalert.dateFrom.toString() + roomalert.dateTo.toString())
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .setInputData(data.build())
            .build()
        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            roomalert.dateFrom.toString() + roomalert.dateTo.toString(),
            ExistingPeriodicWorkPolicy.REPLACE, workRequest
        )
    }


    private fun checkOverlayPermission() {
        if (!Settings.canDrawOverlays(requireContext())) {
            val alertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
            alertDialogBuilder.setTitle(getString(R.string.weather_alarm))
                .setMessage(getString(R.string.features))
                .setPositiveButton(getString(R.string.ok)) { dialog: DialogInterface, i: Int ->
                    var myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    startActivity(myIntent)
                    dialog.dismiss()
                }.setNegativeButton(
                    getString(R.string.cancel)
                ) { dialog: DialogInterface, i: Int ->
                    dialog.dismiss()
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                }.show()
        }
    }
}

 */








/*
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weathermate.alerts.AlertsDialogFragment
import com.example.weathermate.databinding.FragmentAlertsBinding


class AlertsFragment : Fragment() {
    private lateinit var _binding : FragmentAlertsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlertsBinding.inflate(inflater)
        _binding.myFragment = this

        return _binding.root
    }

    fun onFabClicked(view : View){
        val alertsDialogFragment = AlertsDialogFragment()
        alertsDialogFragment.show(parentFragmentManager, "MyDialogFragment")
    }
}




*/