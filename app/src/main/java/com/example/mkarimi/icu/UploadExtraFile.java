package com.example.mkarimi.icu;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;

public class UploadExtraFile extends AppCompatActivity {

    private EditText name, province, district, organization;
    private static FilePickerDialog dialog;

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

                        UploadFileHelper.uploadMultipart(UploadExtraFile.this, multipart);
//                        Intent intent = new Intent(UploadExtraFile.this, MainActivity.class);
//                        startActivity(intent);
                    }
                });

                dialog.show();
            }
        });
    }

}
