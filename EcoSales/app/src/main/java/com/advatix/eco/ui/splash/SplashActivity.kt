package com.advatix.whlep.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.advatix.leplogin.ui.login.LoginActivity
import com.advatix.whlep.ui.warehouse.WarehouseActivity
import com.advatix.whlep.BaseActivity
import com.advatix.whlep.R
import com.advatix.whlep.ui.landing.MainActivity
import com.advatix.whlep.utils.AppUtil

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        navigateTo()
    }

    private fun navigateTo() {
        Handler(Looper.getMainLooper()).postDelayed({
            Log.i("USER", AppUtil.getCachedUser(applicationContext).toString())
            if (AppUtil.getCachedUser(applicationContext) == null ||
                AppUtil.getCachedUser(applicationContext)!!.id.toString() == ""
            ) {
                startActivity(
                    Intent(
                        this,
                        LoginActivity::class.java
                    )
                )
            } else if (AppUtil.getCachedUser(applicationContext)!!.selectedShipper == null
                || AppUtil.getCachedUser(applicationContext)!!.selectedShipper.id == null) {
                startActivity(Intent(this, WarehouseActivity::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        }, 2000)
    }

    override fun onBackPressed() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }
}