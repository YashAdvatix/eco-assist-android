package com.advatix.whlep.utils

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.NotificationCompat
import com.advatix.leplogin.models.UserDetails
import com.advatix.whlep.R
import com.advatix.whlep.ui.landing.MainActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
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

        fun getCurrentTime(): String {
            val sdf = SimpleDateFormat("MM-dd-yyyy HH:mm:ss")
            return sdf.format(Date())
        }

        fun getCurrentDate(): String {
            val sdf = SimpleDateFormat("MM-dd-yyyy")
            return sdf.format(Date())
        }

        fun convertLongToTime(time: Long): String {
            val date = Date(time)
            val format = SimpleDateFormat("MM-dd-yyyy HH:mm:ss 000 +0000")
            format.timeZone = TimeZone.getTimeZone(Locale.getDefault().displayName)
            return format.format(date)
        }

        fun getJsonFromAssets(
            context: Context,
            fileName: String
        ): String? {
            val jsonString: String
            try {
                jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            } catch (ioException: IOException) {
                ioException.printStackTrace()
                return null
            }
            return jsonString
        }

        fun convertTimeFormat(time: String): String {
            val dateFormatter = SimpleDateFormat("MM-dd-yyyy HH:mm:ss 000 +0000")
            dateFormatter.timeZone = TimeZone.getTimeZone(Locale.getDefault().displayName)
            val date = dateFormatter.parse(time)
            val timeFormatter = SimpleDateFormat("dd MMM, yy | HH:mm")
            return timeFormatter.format(date)
        }

        fun showNotification(
            context: Context,
            title: String?,
            body: String?
        ) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationId = 1
            val channelId = "channel-01"
            val channelName = "Xpdel-RCM"
            val importance = NotificationManager.IMPORTANCE_HIGH
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val mChannel = NotificationChannel(
                    channelId, channelName, importance
                )
                notificationManager.createNotificationChannel(mChannel)
            }
            val mBuilder =
                NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(body)
            val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addNextIntent(Intent(context, MainActivity::class.java))
            val resultPendingIntent: PendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            mBuilder.setContentIntent(resultPendingIntent)
            notificationManager.notify(notificationId, mBuilder.build())
        }

        fun compressAndSave(file: File): File? {
            return try {
                val directory = commonDocumentDirPath("XPDEL-RCM")
                // BitmapFactory options to downsize the image
                val o = BitmapFactory.Options()
                o.inJustDecodeBounds = true
                o.inSampleSize = 6
                // factor of downsizing the image
                var inputStream = FileInputStream(file)
                //Bitmap selectedBitmap = null;
                BitmapFactory.decodeStream(inputStream, null, o)
                inputStream.close()

                // The new size we want to scale to
                val requiredSize = 75

                // Find the correct scale value. It should be the power of 2.
                var scale = 1
                while (o.outWidth / scale / 2 >= requiredSize &&
                    o.outHeight / scale / 2 >= requiredSize
                ) {
                    scale *= 2
                }
                val o2 = BitmapFactory.Options()
                o2.inSampleSize = scale
                inputStream = FileInputStream(file)
                val selectedBitmap: Bitmap? = BitmapFactory.decodeStream(inputStream, null, o2)
                inputStream.close()

                val files =
                    File(directory?.path.toString() + "/" + "XPDEL_" + System.currentTimeMillis() + ".jpg")
                files.createNewFile()
                val fo = FileOutputStream(files)
                selectedBitmap?.compress(Bitmap.CompressFormat.JPEG, 75, fo)
                fo.close()
                files
            } catch (e: Exception) {
                Log.e("Image Crash", e.printStackTrace().toString())
                null
            }
        }

        private fun commonDocumentDirPath(folderName: String): File? {
            var dir: File? = null
            dir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                        .toString() + "/" + folderName
                )
            } else {
                File(Environment.getExternalStorageDirectory().toString() + "/" + folderName)
            }

            // Make sure the path directory exists.
            if (!dir.exists()) {
                // Make it, if it doesn't exit
                val success = dir.mkdirs()
                if (!success) {
                    dir = null
                }
            }
            return dir
        }

        fun confirmationDialog(context: Context, title: String, msg: String) {
            val alert: AlertDialog.Builder = AlertDialog.Builder(context)
            alert.setTitle(title)
            alert.setMessage(msg)
            alert.setPositiveButton(
                android.R.string.ok
            ) { dialog, _ ->
                dialog.dismiss()
            }
            alert.show()
        }

        fun loadCursor(ctx: Context): Cursor? {
            val columns = arrayOf(
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID
            )
            val orderBy = MediaStore.Images.Media.DATE_ADDED
            return ctx.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy
            )
        }

        fun getImagePaths(
            cursor: Cursor?,
            startPosition: Int
        ): Array<String?>? {
            val paths = arrayOfNulls<String>(cursor!!.count - startPosition)
            val dataColumnIndex = cursor
                .getColumnIndex(MediaStore.Images.Media.DATA)
            for (i in startPosition until cursor.count) {
                cursor.moveToPosition(i)
                paths[i - startPosition] = cursor.getString(dataColumnIndex)
            }
            return paths
        }

        fun getDrawableBackground(color: Int): GradientDrawable {
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.OVAL
            shape.setSize(48, 48)
            shape.setColor(color)
            return shape
        }
    }
}