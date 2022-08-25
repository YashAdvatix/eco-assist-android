package com.advatix.lepapi.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ZoneDetail(
    val id: Int,
    val status: Int,
    val subZoneId: Int,
    val subZoneName: String,
    val userId: Int,
    val zoneDetails: ZoneDetails,
    val zoneId: Int
): Parcelable