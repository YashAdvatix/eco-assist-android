package com.advatix.whlep.utils

object AppConstants {
    const val ALREADY_SCANNED: Int = -2
    const val INVALID_SCAN: Int = -1

    object Bundles {
        const val KEY_OBJECT = "dataObject"
        const val KEY_ITEM = "item"
    }

    object Preferences{
        const val PREFS_NAME = "xpdel_rcm"
        const val SP_KEY_USER_DETAIL = "userinfo"
        const val SP_KEY_FCM_TOKEN = "token"
        const val PROFILE_PIC = "profilePic"
    }

    object EndPoints{
        const val BASE_URL_STAGING: String = "http://34.196.236.28/xpdel/api/v1/"
        const val BASE_URL_TRAINING: String = "https://test.xpdel.com/xpdel/api/v1/"
        const val BASE_URL_DEMO: String = "https://demo.advatix.net/xpdel/api/v1/"
        const val BASE_URL_PROD: String = "https://felep.xpdel.com/xpdel/api/v1/"
        const val BASE_URL: String = "baseUrl"
        const val FCM_TOKEN: String = "fcmToken"
    }

    object KeyValues{
        const val SHIPPER_ID: String="ShipperID"
        const val LANE_ID: String="Lane_ID_IN"
    }
}