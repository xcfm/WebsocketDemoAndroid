package com.example.fmz.websocketdemo;

import android.Manifest;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.List;

import camera.CameraPreview;

public class ScanActivity extends AppCompatActivity {

    private CameraPreview mPreview;
    String TAG = "scan";
    private ImageView scanLine;
    private TextView ivLight;
    private TextView qrcodeIcBack;
    private boolean isFlashLightOn = false;
    private Camera camera = Camera.open();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code);
        initView();
        initListener();
        AndPermission.with(this)
                .requestCode(200)
                .permission(Manifest.permission.CAMERA)
                .rationale((requestCode, rationale) ->
                        AndPermission.rationaleDialog(ScanActivity.this, rationale).show())
                .callback(listener)
                .start();
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                0.9f);
        animation.setDuration(4500);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        scanLine.startAnimation(animation);

    }


    private void initView() {
        mPreview = (CameraPreview) findViewById(R.id.capture_preview);
        ivLight = (TextView) findViewById(R.id.iv_light);
        qrcodeIcBack = (TextView) findViewById(R.id.qrcode_ic_back);
        scanLine = (ImageView) findViewById(R.id.capture_scan_line);
    }

    private void initListener() {
        mPreview.setScanCallback(content -> {
            // Successfully.
            setResult(RESULT_OK, new Intent().putExtra("DATA", content));
            finish();
        });
        qrcodeIcBack.setOnClickListener(v -> finish());


        ivLight.setOnClickListener(v -> {
            mPreview.switchLight();
        });

    }


    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            // Successfully.
            if (requestCode == 200) {
                if (mPreview.start()) {
                    Log.d(TAG, "onCreate: ");
                } else {
                    // The camera starts failing and you can tell the user.
                    Toast.makeText(ScanActivity.this, "未获取相机权限", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // Failure.
            if (requestCode == 200) {
                Toast.makeText(ScanActivity.this, "未获取相机权限", Toast.LENGTH_SHORT).show();
            }
        }
    };


    /**
     * Stop camera.
     */
    private void stopScan() {
        mPreview.stop();
    }


    @Override
    protected void onPause() {
        // Must be called here, otherwise the camera should not be released properly.
        stopScan();
        super.onPause();
    }


}
