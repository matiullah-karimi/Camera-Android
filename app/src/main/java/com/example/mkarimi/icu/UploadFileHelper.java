package com.example.mkarimi.icu;

import android.app.Dialog;
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
    private static Dialog progressDialog;
    private static  String serverUrl = "http://eyesonyou.today/api/uploadFile";
    private static AsyncHttpClient client;

    public UploadFileHelper(){
        client = new AsyncHttpClient();
    }

    public static void uploadMultipart(final Context context, Multipart multipart, JsonHttpResponseHandler handler) {

        client = new AsyncHttpClient();

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
        client.post(serverUrl, params, handler);
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
