package com.advatix.lepapi.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ShipperX(
    val accessLicenseNumber: String,
    val address1: String,
    val address2: String,
    val citytCode: String,
    val companyName: String,
    val countryCode: String,
    val email: String,
    val firstname: String,
    val id: Int,
    val lastname: String,
    val marketCode: String,
    val marketDesc: String,
    val password: String,
    val phoneNo: String,
    val roleId: Int,
    val stateCode: String,
    val status: Int,
    val username: String,
    val zipCode: Int
): Parcelable