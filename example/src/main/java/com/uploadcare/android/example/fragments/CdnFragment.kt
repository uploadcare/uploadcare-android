package com.uploadcare.android.example.fragments

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.squareup.picasso.Picasso
import com.uploadcare.android.example.R
import com.uploadcare.android.example.databinding.FragmentCdnBinding
import com.uploadcare.android.library.api.UploadcareFile
import com.uploadcare.android.library.urls.Urls

/**
 * Fragment showcase different CdnPathBuilder options for image files.
 */
class CdnFragment : Fragment() {

    private lateinit var binding: FragmentCdnBinding

    private val args: CdnFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentCdnBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).let {
            it.setSupportActionBar(binding.toolbar)
            val appBarConfiguration = AppBarConfiguration(setOf(R.id.uploadFragment))
            binding.toolbar.setupWithNavController(findNavController(), appBarConfiguration)
        }

        loadImages(args.uploadcareFile)

        return binding.root
    }

    /**
     * Populates views. Generates different CDN urls for various effects and loads images to views.
     */
    private fun loadImages(uploadcareFile: UploadcareFile) {
        val displaymetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displaymetrics)
        val width = displaymetrics.widthPixels

        for (i in 0..6) {
            val builder = uploadcareFile.cdnPath()
            builder.resizeWidth(width)
            when (i) {
                0 -> {
                    val url = Urls.cdn(builder)
                    Picasso.get().load(url.toString()).into(binding.cdn1)
                }
                1 -> {
                    builder.grayscale()
                    val url2 = Urls.cdn(builder)
                    Picasso.get().load(url2.toString()).into(binding.cdn2)
                }
                2 -> {
                    builder.flip()
                    val url3 = Urls.cdn(builder)
                    Picasso.get().load(url3.toString()).into(binding.cdn3)
                }
                3 -> {
                    builder.invert()
                    val url4 = Urls.cdn(builder)
                    Picasso.get().load(url4.toString()).into(binding.cdn4)
                }
                4 -> {
                    builder.mirror()
                    val url5 = Urls.cdn(builder)
                    Picasso.get().load(url5.toString()).into(binding.cdn5)
                }
                5 -> {
                    builder.blur(100)
                    val url6 = Urls.cdn(builder)
                    Picasso.get().load(url6.toString()).into(binding.cdn6)
                }
                6 -> {
                    builder.sharp(10)
                    val url7 = Urls.cdn(builder)
                    Picasso.get().load(url7.toString()).into(binding.cdn7)
                }
            }
        }
    }
}