package com.example.mkarimi.icu;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

import cz.msebera.android.httpclient.Header;


public class VideoPreviewActivity extends AppCompatActivity {

    private VideoView videoView;
    private String path;
    private EditText name, province, district, organization;
    private Dialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview);

        name = findViewById(R.id.name);
        province = findViewById(R.id.province);
        district = findViewById(R.id.district);
        organization = findViewById(R.id.organization);

        videoView = findViewById(R.id.video);
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playVideo();
            }
        });

        final Uri videoUri = getIntent().getParcelableExtra("video");
        path = getIntent().getStringExtra("path");

        MediaController controller = new MediaController(this);
        controller.setAnchorView(videoView);
        controller.setMediaPlayer(videoView);
        videoView.setMediaController(controller);
        videoView.setVideoURI(videoUri);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
            ViewGroup.LayoutParams lp = videoView.getLayoutParams();
            float videoWidth = mp.getVideoWidth();
            float videoHeight = mp.getVideoHeight();
            float viewWidth = videoView.getWidth();
            //lp.height = (int) (viewWidth * (videoHeight / videoWidth));
            videoView.setLayoutParams(lp);
                //playVideo();
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

            if (new File(path).length() > 100 * 1024 * 1024) {
                showMessage("Can't upload files larger than 100 mb.");
                return;
            }

            progressDialog = new Dialog(VideoPreviewActivity.this);
            progressDialog.setTitle("Upload File");
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.setCancelable(false);
            progressDialog.show();

            UploadFileHelper.uploadMultipart(VideoPreviewActivity.this, multipart,
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
                    progressDialog.dismiss();
                    showMessage("Upload failed...");
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

    void playVideo() {
        if (videoView.isPlaying()) return;
        videoView.start();
    }

    private void goToMain(){
        Intent intent = new Intent(VideoPreviewActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void showMessage(String message){
        Toast.makeText(VideoPreviewActivity.this, message, Toast.LENGTH_LONG).show();
    }
}
