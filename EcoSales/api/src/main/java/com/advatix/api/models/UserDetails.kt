package com.advatix.lepapi.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserDetails(
    val available: String,
    val created: String,
    val firstName: String,
    val id: Int,
    val lastName: String,
    val loginAttempts: Int,
    val numberOfLogin: Int,
    val online: Int,
    val parentId: Int,
    val phoneNo: Long,
    val profilePicPath: String,
    val role: Int,
    val shipper: List<ShipperX>,
    var selectedShipper: ShipperX,
    val status: Int,
    val tokenString: String,
    val updated: String,
    val userEmail: String,
    val userName: String,
    val userRoles: UserRolesX,
    val zoneDetails: List<ZoneDetail>
) : Parcelable