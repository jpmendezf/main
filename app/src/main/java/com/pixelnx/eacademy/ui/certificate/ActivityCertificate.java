package com.pixelnx.eacademy.ui.certificate;

import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.github.barteksc.pdfviewer.PDFView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.pixelnx.eacademy.R;
import com.pixelnx.eacademy.model.modelCertificate.ModelCertificate;
import com.pixelnx.eacademy.model.modellogin.ModelLogin;
import com.pixelnx.eacademy.ui.base.BaseActivity;
import com.pixelnx.eacademy.ui.home.ActivityHome;
import com.pixelnx.eacademy.utils.AppConsts;
import com.pixelnx.eacademy.utils.ProjectUtils;
import com.pixelnx.eacademy.utils.sharedpref.SharedPref;
import com.pixelnx.eacademy.utils.widgets.CustomTextExtraBold;
import java.io.File;
import java.util.List;

public class ActivityCertificate extends BaseActivity {
    Context mContext;
    PDFView pdfView;
    ImageView ivBack;
    ImageView noResultTv;
    CustomTextExtraBold tvHeader;
    RelativeLayout btDownload;
    String baseUrl;
    String fileName;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    boolean found = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);
        mContext = ActivityCertificate.this;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUser(AppConsts.STUDENT_DATA);
        ivBack = findViewById(R.id.ivBack);
        tvHeader = (CustomTextExtraBold) findViewById(R.id.tvHeader);
        tvHeader.setText(getResources().getString(R.string.Certificate));
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        } else {
            initial();
        }
    }

    private void requestPermission() {

        Dexter.withActivity(ActivityCertificate.this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {


                            initial();

                        }else{
                            Toast.makeText(mContext,getResources().getString(R.string.Please_allow_permissions),Toast.LENGTH_SHORT).show();
                        }


                        if (report.isAnyPermissionPermanentlyDenied()) {


                            openSettingsDialog();
                        }
                    }


                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }

                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(mContext, getResources().getString(R.string.ErrorOccurred), Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();

    }

    private void openSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(getResources().getString(R.string.Please_allow_permissions));
        builder.setMessage(getResources().getString(R.string.This_app_needs_permission));
        builder.setPositiveButton(getResources().getString(R.string.GOTO_SETTINGS), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 101);
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    void initial() {

        noResultTv = findViewById(R.id.noResultTv);
        btDownload = findViewById(R.id.btDownload);
        btDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!fileName.isEmpty()) {
                    if (ProjectUtils.checkConnection(mContext)) {
                        File fileFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+getResources().getString(R.string.app_name)+"/");
                        if (fileFolder.isDirectory()) {
                            File[] files = fileFolder.listFiles();
                            for (int i = 0; i < files.length; i++) {
                                if (files[i].getName().equalsIgnoreCase("" + fileName)) {

                                    found = true;
                                }
                            }
                            if (!found) {
                                ProjectUtils.showProgressDialog(mContext, false, ""+getResources().getString(R.string.Downloading___));
                                apiDownload();
                            } else {
                                Toast.makeText(mContext, "File download in "+getResources().getString(R.string.app_name)+" folder.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            ProjectUtils.showProgressDialog(mContext, false, ""+getResources().getString(R.string.Downloading___));
                            apiDownload();
                        }
                    } else {
                        Toast.makeText(mContext, ""+getResources().getString(R.string.NoInternetConnection), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(mContext, getResources().getString(R.string.Try_again), Toast.LENGTH_SHORT).show();
                }
            }
        });




        pdfView = findViewById(R.id.webView);


        apiCertificate();


    }

    @Override
    public void onBackPressed() {
        if(getIntent().hasExtra(AppConsts.IS_PUSH)){
            startActivity(new Intent(getApplicationContext(), ActivityHome.class));
            finish();

        }else{
        super.onBackPressed();}
    }

    void apiDownload() {
        File root = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/"+getResources().getString(R.string.app_name)+"/");
        if (!root.exists()) {
            root.mkdirs();
        }


        AndroidNetworking.download(baseUrl + fileName, "" + root, fileName)
                .setTag("downloadTest")
                .setPriority(Priority.HIGH)
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        // do anything with progress

                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        Toast.makeText(mContext, getResources().getString(R.string.DownloadCompleted)+" pdf save inside "+getResources().getString(R.string.app_name), Toast.LENGTH_SHORT).show();

                        ProjectUtils.pauseProgressDialog();
                    }

                    @Override
                    public void onError(ANError error) {
                        ProjectUtils.pauseProgressDialog();
                        Toast.makeText(mContext, getResources().getString(R.string.Downloadingfailed) , Toast.LENGTH_SHORT).show();

                    }
                });


    }

    void apiCertificate() {

        ProjectUtils.showProgressDialog(mContext, false, getResources().getString(R.string.Loading___));
        AndroidNetworking.post(AppConsts.BASE_URL + AppConsts.API_CERTIFICATE)
                .addBodyParameter(AppConsts.BATCH_ID, modelLogin.getStudentData().getBatchId())
                .addBodyParameter(AppConsts.STUDENT_ID, modelLogin.getStudentData().getStudentId())
                .build()
                .getAsObject(ModelCertificate.class, new ParsedRequestListener<ModelCertificate>() {
                    @Override
                    public void onResponse(ModelCertificate response) {

                        if (response.getStatus().equalsIgnoreCase("true")) {

                            baseUrl = "" + response.getFilesUrl();
                            fileName = "" + response.getFileName();
                            noResultTv.setVisibility(View.GONE);
                            btDownload.setVisibility(View.VISIBLE);
                            File root = new File(Environment.getExternalStorageDirectory()
                                    .getAbsolutePath() + "/"+getResources().getString(R.string.app_name)+"/");
                            if (!root.exists()) {
                                root.mkdirs();
                            }


                            AndroidNetworking.download(baseUrl + fileName, "" + root, fileName)
                                    .setTag("downloadTest")
                                    .setPriority(Priority.HIGH)
                                    .build()
                                    .setDownloadProgressListener(new DownloadProgressListener() {
                                        @Override
                                        public void onProgress(long bytesDownloaded, long totalBytes) {
                                            // do anything with progress

                                        }
                                    })
                                    .startDownload(new DownloadListener() {
                                        @Override
                                        public void onDownloadComplete() {


                                            ProjectUtils.pauseProgressDialog();
                                            if (!fileName.isEmpty()) {
                                                File outputFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+getResources().getString(R.string.app_name)+"/", "" + fileName + "");
                                                pdfView.fromFile(outputFile).load();
                                            } else {
                                                Toast.makeText(mContext, getResources().getString(R.string.Try_again), Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onError(ANError error) {
                                            ProjectUtils.pauseProgressDialog();


                                        }
                                    });


                        } else {
                            noResultTv.setVisibility(View.VISIBLE);
                            ProjectUtils.pauseProgressDialog();
                            btDownload.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        ProjectUtils.pauseProgressDialog();
                    }
                });
    }
}