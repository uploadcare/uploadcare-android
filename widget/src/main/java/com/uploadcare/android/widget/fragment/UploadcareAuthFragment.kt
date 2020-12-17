package com.uploadcare.android.widget.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.uploadcare.android.widget.BuildConfig
import com.uploadcare.android.widget.databinding.UcwFragmentAuthBinding

class UploadcareAuthFragment : Fragment() {

    private lateinit var binding: UcwFragmentAuthBinding

    private lateinit var mOnAuthListener: OnAuthListener

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mOnAuthListener = try {
            if (parentFragment != null) {
                parentFragment as OnAuthListener
            } else {
                context as OnAuthListener
            }
        } catch (e: ClassCastException) {
            throw ClassCastException("Parent must implement OnAuthListener")
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = UcwFragmentAuthBinding.inflate(inflater, container, false)

        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(binding.ucwWebview, true)
        binding.ucwWebview.settings.apply {
            javaScriptEnabled = true
            userAgentString = "Chrome/56.0.0.0 Mobile"
        }
        binding.ucwWebview.webViewClient = UploadcareWebViewClient({ result ->
            result?.let { mOnAuthListener.onAuthSuccess(it) } ?: mOnAuthListener.onAuthError()
        }, { showProgress ->
            binding.progress.isVisible = showProgress
        })

        arguments?.getString("loginLink")?.let { binding.ucwWebview.loadUrl(it) }

        return binding.root
    }

    companion object {

        /**
         * Create a new instance of UploadcareAuthFragment, initialized to
         * show the login form.
         */
        fun newInstance(loginLink: String): UploadcareAuthFragment {
            return UploadcareAuthFragment().apply {
                val args = Bundle().apply {
                    putString("loginLink", loginLink)
                }
                arguments = args
            }
        }
    }
}

interface OnAuthListener {

    fun onAuthSuccess(cookie: String)

    fun onAuthError()
}

private class UploadcareWebViewClient(private val resultObserver: ((String?) -> Unit),
                                      private val progressObserver: ((Boolean) -> Unit))
    : WebViewClient() {

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        progressObserver(true)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        progressObserver(false)
        if (url?.startsWith(BuildConfig.SOCIAL_API_ENDPOINT, true) == true) {
            val cookies = CookieManager.getInstance().getCookie(url)
            resultObserver(cookies)
        }
    }

    override fun onReceivedError(view: WebView?,
                                 request: WebResourceRequest?,
                                 error: WebResourceError?) {
        super.onReceivedError(view, request, error)
        progressObserver(false)
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?) = false
}