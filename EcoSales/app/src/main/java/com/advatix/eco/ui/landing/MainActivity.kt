package com.advatix.fep.ui.landing.views

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.gridlayout.widget.GridLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.advatix.fep.BaseActivity
import com.advatix.fep.BuildConfig
import com.advatix.fep.R
import com.advatix.fep.application.HomeAloneApplication
import com.advatix.fep.constants.AppConstants
import com.advatix.fep.databinding.ActivityMainBinding
import com.advatix.fep.listeners.ClickListener
import com.advatix.fep.ui.inventory.views.InventoryListActivity
import com.advatix.fep.ui.kitting.views.SelectKittingProcessActivity
import com.advatix.fep.ui.landing.adapter.DashboardAdapter
import com.advatix.fep.ui.landing.model.login.Module
import com.advatix.fep.ui.landing.viewmodels.MainActivityViewModel
import com.advatix.fep.ui.landing.viewmodels.MainActivityViewModelFactory
import com.advatix.fep.ui.pack.SelectPackProcessActivity
import com.advatix.fep.ui.palletize.views.LPNListActivity
import com.advatix.fep.ui.pickup.views.SelectPickupProcessActivity
import com.advatix.fep.ui.qrcode.views.SelectLevelTypeActivity
import com.advatix.fep.ui.recieving.views.SelectReceiveProcessActivity
import com.advatix.fep.ui.returnpickup.SelectReturnProcessActivity
import com.advatix.fep.ui.settings.SettingsActivity
import com.advatix.fep.ui.stow.views.ScanContainerActivity
import com.advatix.fep.ui.ticket.TicketListActivity
import com.advatix.fep.ui.truckload.views.TruckLoadLPNListActivity
import com.advatix.fep.utils.AppUtil
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import org.json.JSONException
import org.json.JSONObject

class MainActivity : BaseActivity(), View.OnClickListener, ClickListener {

    var json: String? = null
    var modules: ArrayList<Module>? = null
    private var mAppUpdateManager: AppUpdateManager? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainActivityViewModel: MainActivityViewModel

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        modules = user!!.modules
        binding.mainLayout.recyclerDashboard.setHasFixedSize(true)
        binding.mainLayout.recyclerDashboard.layoutManager =
            GridLayoutManager(this, 2, GridLayout.VERTICAL, false)
        val adapter = DashboardAdapter(modules, this)
        binding.mainLayout.recyclerDashboard.adapter = adapter
        initViews()
        initViewModel()
    }

    override fun onStart() {
        super.onStart()
        mAppUpdateManager = AppUpdateManagerFactory.create(this)
        mAppUpdateManager!!.registerListener(installStateUpdatedListener)
        mAppUpdateManager!!.appUpdateInfo.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                try {
                    mAppUpdateManager!!.startUpdateFlowForResult(
                        appUpdateInfo, AppUpdateType.IMMEDIATE, this@MainActivity, RC_APP_UPDATE
                    )
                } catch (e: SendIntentException) {
                    e.printStackTrace()
                }
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
            } else {
                if (BuildConfig.DEBUG)
                    Log.i("MainActivity", "checkForAppUpdateAvailability: something else")
            }
        }
    }

    var installStateUpdatedListener: InstallStateUpdatedListener =
        object : InstallStateUpdatedListener {
            override fun onStateUpdate(state: InstallState) {
                if (state.installStatus() == InstallStatus.DOWNLOADED) {
                    //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                } else if (state.installStatus() == InstallStatus.INSTALLED) {
                    if (mAppUpdateManager != null) {
                        mAppUpdateManager!!.unregisterListener(this)
                    }
                } else {
                    if (BuildConfig.DEBUG)
                    Log.i(
                        "MainActivity",
                        "InstallStateUpdatedListener: state: " + state.installStatus()
                    )
                }
            }
        }

    fun initViews() {
        binding.toolbarNew.drawerToggle.visibility = View.VISIBLE
        binding.tvLogOut.setOnClickListener(this)
        binding.tvManageTicket.setOnClickListener(this)
        binding.tvSettings.setOnClickListener(this)
        binding.toolbarNew.drawerToggle.setOnClickListener { v: View? ->
            binding.drawer.openDrawer(
                GravityCompat.START
            )
        }
        binding.textViewUserName.text = "Hi, " + user!!.username
    }

    private fun logout() {
        val preferences = getSharedPreferences("LPNList", MODE_PRIVATE)
        val ed = preferences!!.edit()
        var json: String? = ""
        json = preferences.getString("LPNList", "")
        if (json!!.isNotEmpty()) {
            try {
                val jsonObject = JSONObject(json)
                val jsonArray = jsonObject.getJSONArray("responseObject")
                if (jsonArray.length() == 0) {
                    val intent = Intent(this@MainActivity, SplashActivity::class.java)
                    ed.clear()
                    ed.apply()
                    AppUtil.clearAppSharedPreference(this@MainActivity)
                    startActivity(intent)
                } else {
                    showDialog2(
                        this@MainActivity,
                        "",
                        "Need to finish Palletize before logout."
                    )
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } else {
            val intent = Intent(this@MainActivity, SplashActivity::class.java)
            ed.clear()
            ed.apply()
            AppUtil.clearAppSharedPreference(this@MainActivity)
            startActivity(intent)
        }
    }

    private fun showDialog2(activity: Activity?, title: String?, message: CharSequence?) {
        val builder = AlertDialog.Builder(
            activity!!
        )
        if (title != null) builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, id -> dialog.cancel() }
        builder.show()
    }

    override fun onResume() {
        super.onResume()
        mainActivityViewModel.getPartnerList(user!!.token)
        mainActivityViewModel.getReceivedBarcode(user!!.token)
        mainActivityViewModel.getLabelList(user!!.token)
        mainActivityViewModel.getSkuNumber(user!!.token)
        mainActivityViewModel.getCustomerNames(user!!.token)

//        Commented API call as per discussed with YASH - currently not using this API
//        mainActivityViewModel.getLicensePlate(user!!.token)
    }

    private fun initViewModel() {
        mainActivityViewModel =
            ViewModelProvider(
                this,
                MainActivityViewModelFactory(
                    apiInterface!!
                )
            )[MainActivityViewModel::class.java]

        mainActivityViewModel.partnerResult.observe(this@MainActivity, Observer {
            val response = it ?: return@Observer
            hideNewDialog()
            if (response.success != null) {
                HomeAloneApplication.sPartnerList.clear()
                HomeAloneApplication.sPartnerList.addAll(response.success)
            } else if (response.error != null) {
                if (BuildConfig.DEBUG)
                    Log.i("Error: ", response.error)
            }
        })

        mainActivityViewModel.receiveBarcodeResult.observe(this@MainActivity, Observer {
            val response = it ?: return@Observer
            hideNewDialog()
            if (response.success != null) {
                HomeAloneApplication.receiveBarcodeList.clear()
                HomeAloneApplication.receiveBarcodeList.addAll(response.success)
            } else if (response.error != null) {
                if (BuildConfig.DEBUG)
                    Log.i("Error: ", response.error)
            }
        })

        mainActivityViewModel.labelPackageResult.observe(this@MainActivity, Observer {
            val response = it ?: return@Observer
            hideNewDialog()
            if (response.success != null) {
                HomeAloneApplication.getLabelPackageList.clear()
                HomeAloneApplication.getLabelPackageList.addAll(response.success)
            } else if (response.error != null) {
                if (BuildConfig.DEBUG)
                    Log.i("Error: ", response.error)
            }
        })

        mainActivityViewModel.skuResult.observe(this@MainActivity, Observer {
            val response = it ?: return@Observer
            hideNewDialog()
            if (response.success != null) {
                HomeAloneApplication.getSkuProducts.clear()
                HomeAloneApplication.getSkuProducts.addAll(response.success)
            } else if (response.error != null) {
                if (BuildConfig.DEBUG)
                    Log.i("Error: ", response.error)
            }
        })

        mainActivityViewModel.customerListResult.observe(this@MainActivity, Observer {
            val response = it ?: return@Observer
            hideNewDialog()
            if (response.success != null) {
                HomeAloneApplication.getCustomerList.clear()
                HomeAloneApplication.getCustomerList.addAll(response.success)
            } else if (response.error != null) {
                if (BuildConfig.DEBUG)
                    Log.i("Error: ", response.error)
            }
        })

        mainActivityViewModel.licencePlateResult.observe(this@MainActivity, Observer {
            val response = it ?: return@Observer
            hideNewDialog()
            if (response.success != null) {
                val preferences = getSharedPreferences("LPNList", MODE_PRIVATE)
                val ed = preferences!!.edit()
                val json = preferences.getString("LPNList", "")
                if (!json!!.isEmpty()) {
                    val jsonObject = JSONObject(json)
                    jsonObject.put("responseObject", response.success.toString())
                    ed.putString("LPNList", jsonObject.toString())
                    ed.apply()
                } else {
                    ed.putString("LPNList", response.success.toString())
                    ed.apply()
                }
            } else if (response.error != null) {
                if (BuildConfig.DEBUG)
                    Log.i("Error: ", response.error)
            }
        })
    }

    override fun onBackPressed() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }

    override fun onClick(v: View) {
        val intent: Intent
        when (v.id) {
            R.id.tvManageTicket -> {
                intent = Intent(this@MainActivity, TicketListActivity::class.java)
                startActivity(intent)
            }
            R.id.tvSettings -> {
                intent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.tvLogOut -> {
                logout()
            }
        }
        binding.drawer.closeDrawer(GravityCompat.START)
    }

    override fun onClick(position: Int) {
        val intent: Intent
        when (modules!![position].name) {
            AppConstants.AppModule.MODULE_LABEL -> {
                intent = Intent(this@MainActivity, SelectLevelTypeActivity::class.java)
                startActivity(intent)
                finish()
            }
            AppConstants.AppModule.MODULE_RECEIVE -> {
                startActivity(SelectReceiveProcessActivity.newInstance(this))
            }
            AppConstants.AppModule.MODULE_STOW -> {
                startActivity(ScanContainerActivity.newInstance(this))
            }
            AppConstants.AppModule.MODULE_PICK -> {
                startActivity(SelectPickupProcessActivity.newInstance(this))
            }
            AppConstants.AppModule.MODULE_PACK -> {
                intent = Intent(this@MainActivity, SelectPackProcessActivity::class.java)
                startActivity(intent)
                finish()
            }
            AppConstants.AppModule.MODULE_PALLETIZE -> {
                startActivity(LPNListActivity.newInstance(this))
            }
            AppConstants.AppModule.MODULE_TRUCKLOAD -> {
                startActivity(TruckLoadLPNListActivity.newInstance(this))
            }
            AppConstants.AppModule.MODULE_INVENTORY_TRANSFER -> {
                startActivity(InventoryListActivity.newInstance(this))
            }
            AppConstants.AppModule.MODULE_KITTING -> {
                startActivity(SelectKittingProcessActivity.newInstance(this))
            }
            AppConstants.AppModule.MODULE_RETURN -> {
                startActivity(Intent(this@MainActivity, SelectReturnProcessActivity::class.java))
                finish()
            }
            else -> {}
        }
    }

    override fun onStop() {
        super.onStop()
        if (mAppUpdateManager != null) {
            mAppUpdateManager!!.unregisterListener(installStateUpdatedListener)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_APP_UPDATE) {
            if (resultCode != RESULT_OK) {
                if (BuildConfig.DEBUG)
                    Log.i("MainActivity", "onActivityResult: app download failed")
            }
        }
    }

    companion object {
        const val MyPREFERENCES = "MyPrefs"
        private const val RC_APP_UPDATE = 11
    }
}