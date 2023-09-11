package com.uploadcare.android.widget.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SocialSourcesResponse(val sources: List<SocialSource>) : Parcelable
