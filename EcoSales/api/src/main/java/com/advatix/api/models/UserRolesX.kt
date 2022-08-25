package com.advatix.lepapi.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserRolesX(
    val id: Int,
    val mobile: Boolean,
    val role: String,
    val roleDescr: String,
    val status: Int,
    val web: Boolean
): Parcelable