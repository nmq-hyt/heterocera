package com.example.heterocera

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.widget.Toast
import androidx.camera.lifecycle.ProcessCameraProvider
import android.util.Log
import androidx.activity.viewModels
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.example.heterocera.databinding.ActivityCameraBinding
import com.example.heterocera.fragments.CameraFragment
import com.example.heterocera.ml.Replica
import com.example.heterocera.utils.YuvToRgbConverter
import com.example.heterocera.viewmodel.PredictedMoth
import com.example.heterocera.viewmodel.PredictorTextViewModel
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapsSdkInitializedCallback
import com.google.android.gms.maps.SupportMapFragment
import org.tensorflow.lite.support.image.*
import java.text.SimpleDateFormat
import java.util.Locale

class CameraActivity: AppCompatActivity() {

    private lateinit var activityCameraBinding: ActivityCameraBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityCameraBinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(R.layout.fragment_camera)

        if (savedInstanceState == null) {
            val fragment = CameraFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentCamera, fragment)
                .commit()

        }

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        /** Combination of all flags required to put activity into immersive mode */
        const val TAG = "Heterocera"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }



}