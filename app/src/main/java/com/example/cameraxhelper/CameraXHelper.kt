package com.example.cameraxhelper

import android.content.Context
import android.content.ContextWrapper
import android.view.Surface
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import java.io.File

object CameraXHelper {

    var imageCapture: ImageCapture? = null

    fun startCamera(activity: AppCompatActivity, viewFinder: PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(activity)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .setTargetRotation(Surface.ROTATION_0)
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.createSurfaceProvider())
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                imageCapture = ImageCapture.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                    .setTargetRotation(Surface.ROTATION_90).build()
                // Bind use cases to camera


                cameraProvider.bindToLifecycle(
                    activity, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
            }
        }, ContextCompat.getMainExecutor(activity))
    }

    fun takePhoto(activity: AppCompatActivity) {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture

        // Create time-stamped output file to hold the image
        val photoFile = File(
            getOutputDirectory(activity),
            "JPEG_" + System.currentTimeMillis() + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture?.takePicture(
            outputOptions, ContextCompat.getMainExecutor(activity),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(activity, "error" + exc.message, Toast.LENGTH_LONG).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    if (activity is OnTakePhotoEvent) {
                        (activity as OnTakePhotoEvent).onImageClicked(photoFile.absolutePath)
                    }
                }
            })
    }

    private fun getOutputDirectory(activity: AppCompatActivity): File {
        val wrapper = ContextWrapper(activity)
        return wrapper.getDir("InspectionImages", Context.MODE_PRIVATE)
    }

    interface OnTakePhotoEvent {
        fun onImageClicked(file: String)
    }

}