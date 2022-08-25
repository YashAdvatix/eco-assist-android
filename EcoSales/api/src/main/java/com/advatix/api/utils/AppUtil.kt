package com.advatix.lepapi.utils

import android.app.*
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import com.advatix.lepapi.models.UserDetails
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.time.Instant
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class AppUtil {
    companion object {
        fun clearPrefs(context: Context) {
            val preferences: SharedPreferences =
                context.getSharedPreferences(
                    AppConstants.Preferences.PREFS_NAME,
                    Context.MODE_PRIVATE
                )
            val editor = preferences.edit()
            editor.clear()
            editor.apply()
        }

        fun getMd5Password(s: String): String {
            val md5 = "MD5"
            try {
                // Create MD5 Hash
                val digest = MessageDigest
                    .getInstance(md5)
                digest.update(s.toByteArray())
                val messageDigest = digest.digest()

                // Create Hex String
                val hexString = StringBuilder()
                for (aMessageDigest in messageDigest) {
                    var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
                    while (h.length < 2) h = "0$h"
                    hexString.append(h)
                }
                return hexString.toString()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
            return ""
        }

        val timeStamp: String
            get() {
                var instant: Instant? = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val date = Date()
                    instant = date.toInstant()
                    println(instant)
                }
                return instant.toString()
            }

        fun isValidEmail(email: String): Boolean {
            val pattern: Pattern
            val emailPattern =
                "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
            pattern = Pattern.compile(emailPattern)
            val matcher: Matcher = pattern.matcher(email)
            return matcher.matches()
        }

        fun isValidPassword(password: String): Boolean {
            val pattern: Pattern
            val passPattern =
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$])(?=\\S+$).{4,}$"
            pattern = Pattern.compile(passPattern)
            val matcher: Matcher = pattern.matcher(password)
            return matcher.matches()
        }

        fun cacheUser(user: UserDetails?, context: Context?) {
            val sp = getAppSharedPreference(context)
            if (null != user && null != sp) {
                val gson = customGson
                val userJson = gson.toJson(user)
                val ed = sp.edit()
                ed.putString(AppConstants.Preferences.SP_KEY_USER_DETAIL, userJson)
                ed.apply()
            }
        }

        fun getCachedUser(context: Context?): UserDetails? {
            var user: UserDetails? = null
            val sp = getAppSharedPreference(context)
            if (null != sp) {
                val userDetail = sp.getString(AppConstants.Preferences.SP_KEY_USER_DETAIL, "")
                if (userDetail!!.isNotEmpty()) {
                    val gson = customGson
                    user = gson.fromJson(userDetail, UserDetails::class.java)
                }
            }
            return user
        }

        private fun getAppSharedPreference(aContext: Context?): SharedPreferences? {
            var sp: SharedPreferences? = null
            if (null != aContext) {
                sp = aContext.getSharedPreferences(
                    AppConstants.Preferences.PREFS_NAME,
                    Context.MODE_PRIVATE
                )
            }
            return sp
        }

        private val customGson: Gson
            get() {
                val gb = GsonBuilder()
                return gb.create()
            }
    }
}