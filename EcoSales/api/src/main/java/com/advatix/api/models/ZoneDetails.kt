package com.advatix.lepapi.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ZoneDetails(
    val createdBy: Int,
    val createdOn: String,
    val id: Int,
    val status: Int,
    val updatedBy: Int,
    val updatedOn: String,
    val zoneDescr: String,
    val zoneName: String
): Parcelable