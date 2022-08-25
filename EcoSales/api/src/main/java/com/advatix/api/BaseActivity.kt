package com.advatix.createorder

import android.os.Bundle
import android.os.StrictMode
import androidx.appcompat.app.AppCompatActivity
import com.advatix.api.ApiInterface
import com.advatix.api.RetrofitInstance
import com.advatix.api.models.UserDetails
import com.advatix.api.utils.AppSharedPreferences
import com.advatix.api.utils.AppUtil
import com.advatix.login.custom.CustomToast

open class BaseActivity : AppCompatActivity() {
    lateinit var customToast: CustomToast
    var sharedPreference: AppSharedPreferences? = null
    lateinit var apiInterface: ApiInterface
    lateinit var userDetails: UserDetails


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customToast = CustomToast()
        sharedPreference = AppSharedPreferences()
        var url = sharedPreference!!.getPreferencesString(this, com.advatix.login.ui.utils.AppConstants.BASE_URL)
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
