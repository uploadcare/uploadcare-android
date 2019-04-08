package com.uploadcare.android.widget.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SocialSourcesResponse(val sources: List<SocialSource>) : Parcelable