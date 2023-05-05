package com.heterocera.fragments

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.heterocera.R
import com.heterocera.databinding.FragmentCameraBinding
import com.heterocera.ml.Replica
import com.heterocera.utils.YuvToRgbConverter
import org.tensorflow.lite.support.image.TensorImage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraFragment : Fragment() {
    private val storage = Firebase.storage
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var _fragmentCameraBinding: FragmentCameraBinding? = null
    private lateinit var activityContext: Context
    private val fragmentCameraBinding get() = _fragmentCameraBinding!!
    lateinit var highestLabel : String

    override fun onAttach(context: Context) {
        // the absolute beginning of a fragment's lifecycle
        // the activity context is helpful here to bind bits of data and state
        // to the activity ro the fragment
        super.onAttach(context)
        this.activityContext = context
        activityContext.theme.applyStyle(R.style.Theme_Heterocera,true)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentCameraBinding = FragmentCameraBinding.inflate(inflater, container, false)
        return fragmentCameraBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(activityContext)
        val navControl = this.findNavController()

        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(fragmentCameraBinding.viewFinder.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder()
                .build()

            // Select back camera as a default
            // my phone only has the back, and most models I know
            // primarily use the back camera

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val imageAnalyzer = ImageAnalysis.Builder()
                .build()

            // bind our particular imageAnalyzer to this camera

            imageAnalyzer.setAnalyzer(cameraExecutor, MothImageAnalyzer(activityContext))

            // bind various UI buttons to UI Navigation
            fragmentCameraBinding.captureButton.setOnClickListener{
                takePhoto(this)
            }

            fragmentCameraBinding.mapButton.setOnClickListener {
                navControl.navigate(R.id.action_cameraFragment_to_mapsFragment)
            }

            fragmentCameraBinding.observations.setOnClickListener{
                navControl.navigate(R.id.action_cameraFragment_to_observationsFragment)
            }

            view.findViewById<ImageButton>(R.id.search_button).setOnClickListener {
                navControl.navigate(R.id.action_cameraFragment_to_encyclopediaFragment)
            }
            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview,imageAnalyzer,imageCapture)
                Log.e(TAG,"Use case binding succeeded")

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(activityContext))
    }

    inner class MothImageAnalyzer(context: Context) : ImageAnalysis.Analyzer {
        // get the model
        private val mothModel = Replica.newInstance(context)

        override fun analyze(image: ImageProxy) {
            val tfImage = TensorImage.fromBitmap(toBitmap(image))
            val outputs = mothModel.process(tfImage).probabilityAsCategoryList
                .apply{sortByDescending { it.score }}.take(1)

            for (output in outputs) {
                Log.i(TAG,output.label)
                highestLabel = output.label
                Log.i(TAG, output.score.toString())
            }
            image.close()
        }
        // helper method
        private val yuvToRgbConverter = YuvToRgbConverter(context)
        private lateinit var bitmapBuffer: Bitmap
        private lateinit var rotationMatrix: Matrix

        @SuppressLint("UnsafeExperimentalUsageError")
        private fun toBitmap(imageProxy: ImageProxy): Bitmap? {
            @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
            val image = imageProxy.image ?: return null

            // Initialise Buffer
            if (!::bitmapBuffer.isInitialized) {
                // The image rotation and RGB image buffer are initialized only once
                Log.d(TAG, "Initalise toBitmap()")
                rotationMatrix = Matrix()
                rotationMatrix.postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
                bitmapBuffer = Bitmap.createBitmap(
                    imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888
                )
            }

            // Pass image to an image analyser
            yuvToRgbConverter.yuvToRgb(image, bitmapBuffer)

            // Create the Bitmap in the correct orientation
            return Bitmap.createBitmap(
                bitmapBuffer,
                0,
                0,
                bitmapBuffer.width,
                bitmapBuffer.height,
                rotationMatrix,
                false
            )
        }

    }

    private fun takePhoto(fragment: Fragment) {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return
        // lol there's no guarantee the directory exists beforehand
        var pictureDirectory: File? =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        pictureDirectory?.mkdirs()

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.UK)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Heterocera")
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(requireActivity().contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(activityContext),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults){
                    // only go to the image capture AFTER
                    // you've taken the image
                    // it's rare but entirely possible
                    // to navigate to image capture with a non-existent file
                    // which will immediately crash
                    val msg = "Photo capture succeeded: ${output.savedUri?.path}"
                    Toast.makeText(activityContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                    val action = CameraFragmentDirections.actionCameraFragmentToObservationCaptureFragment(highestLabel,
                        output.savedUri!!
                    )
                    fragment.findNavController().navigate(action)
                }
            }
        )


    }

    companion object {
        const val TAG = "CameraFragment"
        const val FILENAME_FORMAT = "yyyyy.MMMMM.dd GGG hh:mm aaa"
    }

}
