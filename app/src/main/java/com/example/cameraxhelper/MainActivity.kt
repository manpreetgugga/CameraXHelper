package com.example.cameraxhelper

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : CameraActivity() {
    override fun hasPermissions() {
        CameraXHelper.startCamera(this@MainActivity, cameraTextureView)
        takePicture.setOnClickListener { view ->
            if (view.id == R.id.takePicture) {
                handleOnTakePictureEvent()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)
    }

    private fun handleOnTakePictureEvent() {
        CameraXHelper.takePhoto(this@MainActivity )
    }
}