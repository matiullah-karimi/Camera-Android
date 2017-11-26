package com.example.mkarimi.icu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by mkarimi on 11/21/17.
 */

public class UploadFileHelper {
    private static FilePickerDialog dialog;
    private static ProgressDialog progressDialog;
    private static  String serverUrl = "http://192.168.43.84:8000/api/uploadFile";
    private static AsyncHttpClient client;
    private static boolean isFileUploaded;

    public UploadFileHelper(){
        client = new AsyncHttpClient();
    }

    public static void uploadMultipart(final Context context, Multipart multipart) {

        client = new AsyncHttpClient();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Its uploading....");
        progressDialog.setTitle("Upload File");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.show();

        RequestParams params = new RequestParams();

        params.put("name", multipart.getName());
        params.put("province", multipart.getProvince());
        params.put("district", multipart.getDistrict());
        params.put("organization", multipart.getOrganization());
        try {
            params.put("file", multipart.getFile());
        }
        catch (FileNotFoundException ex){

        }
        client.post(serverUrl, params, new JsonHttpResponseHandler(){
            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                progressDialog.setProgress((int) bytesWritten);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                message("Successfully uploaded!!!", context);
                Log.d("tag: ", response.toString());
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                message("Successfully uploaded!!!", context);
                Log.d("tag: ", response.toString() + isFileUploaded);
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                message("Successfully uploaded!!!", context);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                message("Uploading failed!!!", context);
                Log.d("asdfasdf2", responseString + "");
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                message("Uploading failed!!!", context);
                Log.d("asdfasdf1", isFileUploaded + "");
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                message("Uploading failed!!!", context);
                Log.d("asdfasdf", errorResponse + "");
                progressDialog.dismiss();
            }
        });
    }

    public static void openFileChooser(final Context context){
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.STORAGE_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;

        dialog = new FilePickerDialog(context, properties);
        dialog.setTitle("Select a File");

        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                //uploadMultipart(context, files[0]);
            }
        });

        dialog.show();
    }

    private static void message(String content, Context context) {
        Toast.makeText(context, content, Toast.LENGTH_LONG).show();
    }
}
