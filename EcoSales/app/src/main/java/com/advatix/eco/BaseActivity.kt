package com.advatix.whlep

import android.os.Bundle
import android.os.StrictMode
import androidx.appcompat.app.AppCompatActivity
import com.advatix.lepapi.ApiInterface
import com.advatix.lepapi.RetrofitInstance
import com.advatix.leplogin.models.UserDetails
import com.advatix.leplogin.custom.CustomToast
import com.advatix.whlep.utils.AppSharedPreferences
import com.advatix.whlep.utils.AppUtil

open class BaseActivity : AppCompatActivity() {
    lateinit var customToast: CustomToast
    var sharedPreference: AppSharedPreferences? = null
    lateinit var apiInterface: ApiInterface
    lateinit var userDetails: UserDetails


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customToast = CustomToast()
        sharedPreference = AppSharedPreferences()
        var url = sharedPreference!!.getPreferencesString(this, com.advatix.leplogin.ui.utils.AppConstants.BASE_URL)
        apiInterface = RetrofitInstance.getClientWithHeader(url!!)!!.create(ApiInterface::class.java)
        try {
            userDetails = AppUtil.getCachedUser(applicationContext)!!
        } catch (e: Exception) {
        }

        val policy = StrictMode.ThreadPolicy.Builder()
            .permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }
}
