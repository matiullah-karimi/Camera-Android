package com.example.mkarimi.icu;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

import cz.msebera.android.httpclient.Header;

public class UploadExtraFile extends AppCompatActivity {

    private EditText name, province, district, organization;
    private static FilePickerDialog dialog;
    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_extra_file);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        name = findViewById(R.id.name);
        province = findViewById(R.id.province);
        district = findViewById(R.id.district);
        organization = findViewById(R.id.organization);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Multipart multipart = new Multipart();
                multipart.setName(name.getText().toString());
                multipart.setProvince(province.getText().toString());
                multipart.setDistrict(district.getText().toString());
                multipart.setOrganization(organization.getText().toString());

                DialogProperties properties = new DialogProperties();
                properties.selection_mode = DialogConfigs.SINGLE_MODE;
                properties.selection_type = DialogConfigs.FILE_SELECT;
                properties.root = new File(DialogConfigs.STORAGE_DIR);
                properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
                properties.offset = new File(DialogConfigs.DEFAULT_DIR);
                properties.extensions = null;

                dialog = new FilePickerDialog(UploadExtraFile.this, properties);
                dialog.setTitle("Select a File");

                dialog.setDialogSelectionListener(new DialogSelectionListener() {
                    @Override
                    public void onSelectedFilePaths(String[] files) {
                        //uploadMultipart(context, files[0]);
                        multipart.setFile(new File(files[0]));

                        progressDialog = new Dialog(UploadExtraFile.this);
                        progressDialog.setTitle("Upload File");
                        progressDialog.setContentView(R.layout.progress_dialog);
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        UploadFileHelper.uploadMultipart(UploadExtraFile.this, multipart,
                                new JsonHttpResponseHandler(){
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
                                        goToMain();
                                        showMessage("File successfully uploaded...");
                                    }

                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                                        super.onSuccess(statusCode, headers, responseString);
                                        progressDialog.dismiss();
                                        goToMain();
                                        showMessage("File successfully uploaded...");
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

                dialog.show();
            }
        });
    }

    private void goToMain(){
        Intent intent = new Intent(UploadExtraFile.this, MainActivity.class);
        startActivity(intent);
    }

    private void showMessage(String message){
        Toast.makeText(UploadExtraFile.this, message, Toast.LENGTH_LONG).show();
    }

}
