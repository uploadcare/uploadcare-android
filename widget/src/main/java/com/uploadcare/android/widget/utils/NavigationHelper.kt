package com.uploadcare.android.widget.utils

import androidx.fragment.app.FragmentManager
import com.uploadcare.android.widget.controller.FileType
import com.uploadcare.android.widget.data.SocialSource
import com.uploadcare.android.widget.dialogs.ProgressDialog
import com.uploadcare.android.widget.dialogs.SocialSourcesDialog
import com.uploadcare.android.widget.viewmodels.ProgressData

class NavigationHelper private constructor() {

    companion object {

        fun showProgressDialog(fragmentManager: FragmentManager,
                               progressData: ProgressData) {
            var dialog = fragmentManager.findFragmentByTag(ProgressDialog::class.simpleName)
                    as ProgressDialog?

            if (dialog != null || fragmentManager.isStateSaved) {
                return
            }

            dialog = ProgressDialog.newInstance(progressData)
            dialog.show(fragmentManager, ProgressDialog::class.simpleName)
        }

        fun dismissProgressDialog(fragmentManager: FragmentManager) {
            val dialog = fragmentManager.findFragmentByTag(ProgressDialog::class.simpleName)
                    as ProgressDialog?

            dialog?.dismissAllowingStateLoss()
        }

        fun updateProgressDialogProgress(fragmentManager: FragmentManager, progress: Int) {
            val dialog = fragmentManager.findFragmentByTag(ProgressDialog::class.simpleName)
                    as ProgressDialog?

            dialog?.updateProgress(progress)
        }

        fun showSocialSourcesDialog(fragmentManager: FragmentManager,
                                    sources: List<SocialSource>,
                                    title: String,
                                    fileType: FileType) {
            var dialog = fragmentManager
                    .findFragmentByTag(SocialSourcesDialog::class.simpleName) as SocialSourcesDialog?

            if (dialog != null || fragmentManager.isStateSaved) {
                return
            }

            dialog = SocialSourcesDialog.newInstance(sources, title, fileType)
            dialog.show(fragmentManager, SocialSourcesDialog::class.simpleName)
        }

        fun dismissSocialSourcesDialog(fragmentManager: FragmentManager) {
            val dialog = fragmentManager.findFragmentByTag(SocialSourcesDialog::class.simpleName)
                    as SocialSourcesDialog?

            dialog?.dismissAllowingStateLoss()
        }
    }
}