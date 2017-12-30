package com.example.mkarimi.icu;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.otaliastudios.cameraview.AspectRatio;
import com.otaliastudios.cameraview.CameraUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class PicturePreviewActivity extends AppCompatActivity {
    private static WeakReference<byte[]> image;
    private String path;
    private EditText name, province, district, organization;
    private Dialog progressDialog;

    public static void setImage(@Nullable byte[] im) {
        image = im != null ? new WeakReference<>(im) : null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_preview);

        final ImageView imageView = findViewById(R.id.image);
        name = findViewById(R.id.name);
        province = findViewById(R.id.province);
        district = findViewById(R.id.district);
        organization = findViewById(R.id.organization);

        final long delay = getIntent().getLongExtra("delay", 0);
        final int nativeWidth = getIntent().getIntExtra("nativeWidth", 0);
        final int nativeHeight = getIntent().getIntExtra("nativeHeight", 0);
        byte[] b = image == null ? null : image.get();
        if (b == null) {
            finish();
            return;
        }

        CameraUtils.decodeBitmap(b, 1000, 1000, new CameraUtils.BitmapCallback() {
            @Override
            public void onBitmapReady(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
                saveImage(bitmap);
            }
        });

        findViewById(R.id.btnUpload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Multipart multipart = new Multipart();
                multipart.setName(name.getText().toString());
                multipart.setProvince(province.getText().toString());
                multipart.setDistrict(district.getText().toString());
                multipart.setOrganization(organization.getText().toString());
                multipart.setFile(new File(path));

                progressDialog = new Dialog(PicturePreviewActivity.this);
                progressDialog.setTitle("Upload File");
                progressDialog.setContentView(R.layout.progress_dialog);
                progressDialog.setCancelable(false);
                progressDialog.show();

                UploadFileHelper.uploadMultipart(PicturePreviewActivity.this, multipart,
                        new JsonHttpResponseHandler(){
                            @Override
                            public void onProgress(long bytesWritten, long totalSize) {
                                super.onProgress(bytesWritten, totalSize);
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                                super.onSuccess(statusCode, headers, response);
                                Log.d("tag: ", response.toString());
                                progressDialog.dismiss();
                                showMessage("File successfully uploaded...");
                                goToMain();
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                super.onSuccess(statusCode, headers, response);
                                progressDialog.dismiss();
                                showMessage("File successfully uploaded...");
                                goToMain();
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                                super.onSuccess(statusCode, headers, responseString);
                                progressDialog.dismiss();
                                showMessage("File successfully uploaded...");
                                goToMain();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                super.onFailure(statusCode, headers, responseString, throwable);
                                showMessage("Upload failed...");
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                                super.onFailure(statusCode, headers, throwable, errorResponse);
                                progressDialog.dismiss();
                                showMessage("Upload failed...");
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                super.onFailure(statusCode, headers, throwable, errorResponse);
                                progressDialog.dismiss();
                                showMessage("Upload failed...");
                            }
                        });
            }
        });
    }

    private static float getApproximateFileMegabytes(Bitmap bitmap) {
        return (bitmap.getRowBytes() * bitmap.getHeight()) / 1024 / 1024;
    }

    private void saveImage(Bitmap finalBitmap) {

        File folder = new File(Environment.getExternalStorageDirectory().toString()+"/EOY/Images");
        folder.mkdirs();

        //Save the path as a string value
        String extStorageDirectory = folder.toString();

        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());

        String fname = "IMG" + timeStamp + ".jpg";
        File file = new File (extStorageDirectory, fname);
        path = file.getAbsolutePath();
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goToMain(){
        Intent intent = new Intent(PicturePreviewActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void showMessage(String message){
        Toast.makeText(PicturePreviewActivity.this, message, Toast.LENGTH_LONG).show();
    }
}
