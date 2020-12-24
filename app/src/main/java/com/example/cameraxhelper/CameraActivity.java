package com.example.cameraxhelper;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public abstract class CameraActivity extends AppCompatActivity {
    private boolean handleOnResume = false;

    private boolean isCameraPermissionAsked = false;
    private static final int MEDIA_PERMISSION = 888;

    abstract void hasPermissions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }
    }

    public void requestPermission(String permission, int requestCode) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (handleOnResume)
                checkPermission();
        } else {
            hasPermissions();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkPermission() {
        handleOnResume = false;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            if (!isCameraPermissionAsked) {
                isCameraPermissionAsked = true;
                requestPermission(Manifest.permission.CAMERA, MEDIA_PERMISSION);
            } else if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Needs camera permission");
                builder.setMessage("App needs camera permission.");
                builder.setCancelable(false);
                builder.setPositiveButton("Grant", (dialog, which) -> {
                    dialog.cancel();
                    handleOnResume = true;
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, MEDIA_PERMISSION);
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.cancel();
                    finish();
                });
                builder.show();
            } else {
                requestPermission(Manifest.permission.CAMERA, MEDIA_PERMISSION);
            }
        } else {
            handleOnResume = false;
            hasPermissions();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MEDIA_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                handleOnResume = false;
                hasPermissions();
            } else {
                checkPermission();
            }
        }
    }
}
