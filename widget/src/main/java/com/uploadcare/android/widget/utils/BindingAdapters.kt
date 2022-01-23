package com.uploadcare.android.widget.utils

import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

@Suppress("unused")
class BindingAdapters private constructor() {

    companion object {

        /**
         * View
         */
        @BindingAdapter("isVisible")
        @JvmStatic
        fun setIsVisible(view: View, isVisible: Boolean) {
            view.isVisible = isVisible
        }

        /**
         * ImageView
         */

        @BindingAdapter("imageDrawable")
        @JvmStatic
        fun setImageBitmap(view: ImageView, drawableRes: Int?) {
            drawableRes?.let { resource ->
                if (resource < 0) {
                    view.setImageDrawable(null)
                    return
                }

                view.setImageResource(resource)
                view.requestLayout()
            } ?: view.setImageDrawable(null)
        }

        /**
         * TextInputLayout
         */

        @BindingAdapter("errorText")
        @JvmStatic
        fun setErrorMessage(view: TextInputLayout, errorMessage: String?) {
            view.error = errorMessage
        }
    }
}