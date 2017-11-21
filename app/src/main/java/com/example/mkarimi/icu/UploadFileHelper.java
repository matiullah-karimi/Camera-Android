package com.example.mkarimi.icu;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import java.io.File;

/**
 * Created by mkarimi on 11/21/17.
 */

public class UploadFileHelper {
    private static FilePickerDialog dialog;
    private static ProgressDialog progressDialog;
    private static  String serverUrl = "http://192.168.43.84:8000/api/uploadFile";

    public static void uploadMultipart(final Context context, String filePath) {
        try {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Its uploading....");
            progressDialog.setTitle("Upload File");
            progressDialog.setCancelable(false);
            progressDialog.show();

            String uploadId =
                    new MultipartUploadRequest(context, serverUrl)
                            .addFileToUpload(filePath, "file")
                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(2)
                            .setDelegate(new UploadStatusDelegate() {
                                @Override
                                public void onProgress(Context context, UploadInfo uploadInfo) {
                                    // your code here
                                }

                                @Override
                                public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse,
                                                    Exception exception) {
                                    // your code here
                                    progressDialog.dismiss();
                                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show();
                                }
                                @Override
                                public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                                    // your code here
                                    // if you have mapped your server response to a POJO, you can easily get it:
                                    // YourClass obj = new Gson().fromJson(serverResponse.getBodyAsString(), YourClass.class);
                                    progressDialog.dismiss();
                                    Toast.makeText(context, "Successfully Uploaded!!!", Toast.LENGTH_LONG).show();

                                }

                                @Override
                                public void onCancelled(Context context, UploadInfo uploadInfo) {
                                    // your code here
                                    progressDialog.dismiss();
                                    Toast.makeText(context, "Upload canceled!!!", Toast.LENGTH_LONG).show();
                                }
                            })
                            .startUpload();
        } catch (Exception exc) {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
            Toast.makeText(context, exc.getMessage(), Toast.LENGTH_LONG).show();
        }
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
                uploadMultipart(context, files[0]);
            }
        });

        dialog.show();
    }
}
