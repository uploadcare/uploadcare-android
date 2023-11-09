package com.uploadcare.android.example.fragments

import android.app.Service
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.squareup.picasso.Picasso
import com.uploadcare.android.example.R
import com.uploadcare.android.example.databinding.FragmentCdnBinding
import com.uploadcare.android.example.viewmodels.CdnViewModel
import com.uploadcare.android.library.api.UploadcareFile
import com.uploadcare.android.library.urls.Urls

/**
 * Fragment showcase different CdnPathBuilder options for image files.
 */
class CdnFragment : Fragment(), MenuProvider {

    private lateinit var binding: FragmentCdnBinding
    private lateinit var viewModel: CdnViewModel

    private val args: CdnFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentCdnBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get()

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        (activity as AppCompatActivity).let {
            it.setSupportActionBar(binding.toolbar)
            val appBarConfiguration = AppBarConfiguration(setOf(R.id.uploadFragment))
            binding.toolbar.setupWithNavController(findNavController(), appBarConfiguration)
        }

        viewModel.uploadcareFile.observe(this.viewLifecycleOwner, { uploadcareFile ->
            uploadcareFile?.let { loadImages(it) }
        })
        viewModel.setFile(args.uploadcareFile)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().addMenuProvider(
            this,
            viewLifecycleOwner
        )
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.cdn_file_actions, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
        when (menuItem.itemId) {
            R.id.action__convert_document -> {
                viewModel.convertDocument()
                true
            }
            R.id.action__convert_video -> {
                viewModel.convertVideo()
                true
            }
            R.id.action_execute_aws_rekognition -> {
                viewModel.executeAWSRekognitionAddOn()
                true
            }
            R.id.action_execute_aws_rekognition_moderation -> {
                viewModel.executeAWSRekognitionModerationAddOn()
                true
            }
            R.id.action_execute_clam_av -> {
                viewModel.executeClamAVAddOn()
                true
            }
            R.id.action_execute_remove_bg -> {
                viewModel.executeRemoveBgAddOn()
                true
            }
            else -> false
        }

    /**
     * Populates views. Generates different CDN urls for various effects and loads images to views.
     */
    private fun loadImages(uploadcareFile: UploadcareFile) {
        val width = requireContext().getDisplayMetrics().widthPixels

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

    private fun Context.getDisplayMetrics(): DisplayMetrics {
        val displayMetrics = DisplayMetrics()
        val windowManager = getSystemService(Service.WINDOW_SERVICE) as WindowManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            with(windowManager.currentWindowMetrics.bounds) {
                displayMetrics.widthPixels = width()
                displayMetrics.heightPixels = height()
            }
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        }
        displayMetrics.densityDpi = resources.configuration.densityDpi
        return displayMetrics
    }
}
