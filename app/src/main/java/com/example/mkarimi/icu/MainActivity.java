package com.example.mkarimi.icu;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraLogger;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.SessionType;
import com.otaliastudios.cameraview.Size;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ControlView.Callback {

    private static final String IMAGE_DIRECTORY_NAME = "ICU Camera";
    public  static final int WRITE_EXTERNAL_STORAGE_CODE = 3;
    private CameraView camera;
    private ViewGroup controlPanel;

    private boolean mCapturingPicture;
    private boolean mCapturingVideo;

    // To show stuff in the callback
    private Size mCaptureNativeSize;
    private long mCaptureTime;
    private File mediaStorageDir;

    private LinearLayout stopVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        CameraLogger.setLogLevel(CameraLogger.LEVEL_VERBOSE);

        camera = findViewById(R.id.camera);
        camera.addCameraListener(new CameraListener() {
            public void onCameraOpened(CameraOptions options) { onOpened(); }
            public void onPictureTaken(byte[] jpeg) { onPicture(jpeg); }
            public void onVideoTaken(File video) { onVideo(video);}
        });


        findViewById(R.id.capturePhoto).setOnClickListener(this);
        findViewById(R.id.capturePhoto1).setOnClickListener(this);
        findViewById(R.id.captureVideo).setOnClickListener(this);
        findViewById(R.id.captureVideo1).setOnClickListener(this);
        findViewById(R.id.toggleCamera).setOnClickListener(this);
        findViewById(R.id.toggleCamera1).setOnClickListener(this);
        findViewById(R.id.btnUpload).setOnClickListener(this);
        findViewById(R.id.btnUpload1).setOnClickListener(this);
        findViewById(R.id.stopVideo1).setOnClickListener(this);
        stopVideo = findViewById(R.id.stopVideo);
        stopVideo.setEnabled(false);
        stopVideo.setOnClickListener(this);

        controlPanel = findViewById(R.id.controls);
        ViewGroup group = (ViewGroup) controlPanel.getChildAt(0);
        Control[] controls = Control.values();
        for (Control control : controls) {
            ControlView view = new ControlView(this, control, this);
            group.addView(view, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        controlPanel.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                BottomSheetBehavior b = BottomSheetBehavior.from(controlPanel);
                b.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            edit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(MainActivity.this, UploadExtraFile.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void message(String content, boolean important) {
        int length = important ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        Toast.makeText(this, content, length).show();
    }

    private void onOpened() {
        ViewGroup group = (ViewGroup) controlPanel.getChildAt(0);
        for (int i = 0; i < group.getChildCount(); i++) {
            ControlView view = (ControlView) group.getChildAt(i);
            view.onCameraOpened(camera);
        }
    }

    private void onPicture(byte[] jpeg) {
        mCapturingPicture = false;
        long callbackTime = System.currentTimeMillis();
        if (mCapturingVideo) {
            message("Captured while taking video. Size="+mCaptureNativeSize, false);
            return;
        }

        // This can happen if picture was taken with a gesture.
        if (mCaptureTime == 0) mCaptureTime = callbackTime - 300;
        if (mCaptureNativeSize == null) mCaptureNativeSize = camera.getPictureSize();

        PicturePreviewActivity.setImage(jpeg);
        Intent intent = new Intent(MainActivity.this, PicturePreviewActivity.class);
        intent.putExtra("delay", callbackTime - mCaptureTime);
        intent.putExtra("nativeWidth", mCaptureNativeSize.getWidth());
        intent.putExtra("nativeHeight", mCaptureNativeSize.getHeight());
        startActivity(intent);

        mCaptureTime = 0;
        mCaptureNativeSize = null;
    }

    private void onVideo(File video) {
        mCapturingVideo = false;
        Intent intent = new Intent(MainActivity.this, VideoPreviewActivity.class);
        intent.putExtra("video", Uri.fromFile(video));
        intent.putExtra("path", video.getAbsolutePath());
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.capturePhoto:
            case R.id.capturePhoto1:
                capturePhoto(); break;
            case R.id.captureVideo:
            case R.id.captureVideo1:
                captureVideo(); break;
            case R.id.toggleCamera:
            case R.id.toggleCamera1:
                toggleCamera(); break;
            case R.id.stopVideo:
            case R.id.stopVideo1:
                stopCapturingVideo(); break;
            case R.id.btnUpload:
            case R.id.btnUpload1:
                uploadFile(); break;
        }
    }

    @Override
    public void onBackPressed() {
        BottomSheetBehavior b = BottomSheetBehavior.from(controlPanel);
        if (b.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            b.setState(BottomSheetBehavior.STATE_HIDDEN);
            return;
        }
        super.onBackPressed();
    }

    private void edit() {
        BottomSheetBehavior b = BottomSheetBehavior.from(controlPanel);
        b.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void capturePhoto() {
        if (checkAndRequestPermissions()){
            if (mCapturingPicture) return;
            mCapturingPicture = true;
            mCaptureTime = System.currentTimeMillis();
            mCaptureNativeSize = camera.getPictureSize();
            message("Capturing picture...", false);
            camera.capturePicture();
        }
    }

    private void captureVideo() {
        if(checkAndRequestPermissions()){
            if (camera.getSessionType() != SessionType.VIDEO) {
                message("Can't record video while session type is 'picture'.", false);
                return;
            }

            if (mCapturingPicture || mCapturingVideo) return;

            mCapturingVideo = true;

            final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date());

            File folder = new File(Environment.getExternalStorageDirectory().toString()+"/EOY/Videos");
            folder.mkdirs();

            //Save the path as a string value
            String extStorageDirectory = folder.toString();

            mediaStorageDir = new File(extStorageDirectory,"VID_" + timeStamp + ".mp4");
            camera.startCapturingVideo(mediaStorageDir);
            stopVideo.setEnabled(true);
        }
        else {
            message("You have not grant privilige", true);
        }
    }

    private void toggleCamera() {
        if (mCapturingPicture) return;
        switch (camera.toggleFacing()) {
            case BACK:
                message("Switched to back camera!", false);
                break;

            case FRONT:
                message("Switched to front camera!", false);
                break;
        }
    }

    @Override
    public boolean onValueChanged(Control control, Object value, String name) {
        if (!camera.isHardwareAccelerated() && (control == Control.WIDTH || control == Control.HEIGHT)) {
            if ((Integer) value > 0) {
                message("This device does not support hardware acceleration. " +
                        "In this case you can not change width or height. " +
                        "The view will act as WRAP_CONTENT by default.", true);
                return false;
            }
        }
        control.applyValue(camera, value);
        BottomSheetBehavior b = BottomSheetBehavior.from(controlPanel);
        b.setState(BottomSheetBehavior.STATE_HIDDEN);
        message("Changed " + control.getName() + " to " + name, false);
        return true;
    }

    //region Boilerplate

    @Override
    protected void onResume() {
        super.onResume();
        camera.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        camera.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        camera.destroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean valid = true;
        for (int grantResult : grantResults) {
            valid = valid && grantResult == PackageManager.PERMISSION_GRANTED;
        }
        if (valid && !camera.isStarted()) {
            camera.start();
        }
    }

    public void stopCapturingVideo(){
        camera.stopCapturingVideo();
    }

    private  boolean checkAndRequestPermissions() {
        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (read != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]),1);
            return false;
        }
        return true;
    }

    private void uploadFile(){
        Intent intent = new Intent(MainActivity.this, UploadExtraFile.class);
        startActivity(intent);
    }
    //endregion
}