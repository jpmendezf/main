package com.pixelnx.eacademy.ui.settingdashboard;

import androidx.appcompat.app.AlertDialog;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.pixelnx.eacademy.R;
import com.pixelnx.eacademy.model.modellogin.ModelLogin;
import com.pixelnx.eacademy.ui.base.BaseActivity;
import com.pixelnx.eacademy.ui.editProfile.ActivityProfile;
import com.pixelnx.eacademy.ui.aboutapp.ActivityAboutApp;
import com.pixelnx.eacademy.ui.aboutapp.ActivityOpenSourceLibrary;
import com.pixelnx.eacademy.ui.applyleave.ActivityApplyLeave;
import com.pixelnx.eacademy.ui.certificate.ActivityCertificate;
import com.pixelnx.eacademy.ui.home.ActivityPrivacyPolicy;
import com.pixelnx.eacademy.ui.login.ActivityLogin;
import com.pixelnx.eacademy.utils.AppConsts;
import com.pixelnx.eacademy.utils.sharedpref.SharedPref;
import com.pixelnx.eacademy.utils.widgets.CustomTextExtraBold;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivitySettingDashboard extends BaseActivity implements View.OnClickListener {
    CustomTextExtraBold tvHeader;
    ImageView ivBack;
    LinearLayout llEditProfile;
    LinearLayout llApplyLeave;
    LinearLayout llCertificate;
    LinearLayout btnPrivacyPolicy;
    LinearLayout llLogout;
    LinearLayout btnAboutApp;
    LinearLayout btnOpenSourceLibrary;
    Context mContext;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_dashboard);
        tvHeader = (CustomTextExtraBold) findViewById(R.id.tvHeader);
        tvHeader.setText(getResources().getString(R.string.Settings));
        mContext = ActivitySettingDashboard.this;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUser(AppConsts.STUDENT_DATA);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivBack.setOnClickListener(this);
        llApplyLeave = findViewById(R.id.llApplyLeave);
        btnAboutApp = findViewById(R.id.btnAboutApp);
        llCertificate = findViewById(R.id.llCertificate);
        llEditProfile = findViewById(R.id.llEditProfile);
        btnPrivacyPolicy = findViewById(R.id.btnPrivacypolicy);
        btnOpenSourceLibrary = findViewById(R.id.btnOpenSourceLibrary);
        llLogout = findViewById(R.id.llLogout);
        llLogout.setOnClickListener(this);
        btnAboutApp.setOnClickListener(this);
        llEditProfile.setOnClickListener(this);
        llApplyLeave.setOnClickListener(this);
        llCertificate.setOnClickListener(this);
        btnPrivacyPolicy.setOnClickListener(this);
        btnOpenSourceLibrary.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llEditProfile:
                Intent intent = new Intent(mContext, ActivityProfile.class);
                startActivity(intent);
                break;
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.llCertificate:
                Intent intentCertificate = new Intent(mContext, ActivityCertificate.class);
                startActivity(intentCertificate);
                break;
            case R.id.llApplyLeave:
                Intent intentApplyLeave = new Intent(mContext, ActivityApplyLeave.class);
                startActivity(intentApplyLeave);
                break;
            case R.id.btnPrivacypolicy:
                Intent intentP = new Intent(mContext, ActivityPrivacyPolicy.class);
                intentP.putExtra("WEB_URL", AppConsts.URL_PRIVACY_POLICY);
                intentP.putExtra("HEADER", "Privacy Policy");
                startActivity(intentP);
                break;
            case R.id.llLogout:
                logoutAppDialog();
                break;
            case R.id.btnAboutApp:
                Intent intentAboutApp = new Intent(mContext, ActivityAboutApp.class);
                startActivity(intentAboutApp);
                break;
            case R.id.btnOpenSourceLibrary:
                Intent intentOpenSourceLibrary = new Intent(mContext, ActivityOpenSourceLibrary.class);
                startActivity(intentOpenSourceLibrary);
                break;
        }
    }

    private void logoutAppDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getResources().getString(R.string.Are_you_sure_you_want_to_logout))
                .setCancelable(false)

                .setTitle(getResources().getString(R.string.app_name))
                .setPositiveButton(getResources().getString(R.string.Yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logoutApi();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.No), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                if(modelLogin.getStudentData().getLanguageName().equalsIgnoreCase("arabic")){
                alertDialog.findViewById(android.R.id.message).setTextDirection(View.TEXT_DIRECTION_RTL);}
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));

            }
        });
        alertDialog.show();
    }

    private void logoutApi() {
        AndroidNetworking.post(AppConsts.BASE_URL + AppConsts.API_LOGOUT)
                .addBodyParameter(AppConsts.STUDENT_ID, modelLogin.getStudentData().getStudentId())
                .setTag(AppConsts.API_LOGOUT)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject mainJson = new JSONObject(response);
                            if (AppConsts.TRUE.equals(mainJson.getString(AppConsts.STATUS))) {
                                sharedPref.clearAllPreferences();
                                Intent loginScreen = new Intent(mContext, ActivityLogin.class);
                                loginScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(loginScreen);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(mContext, getResources().getString(R.string.Something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(mContext, getResources().getString(R.string.Try_again), Toast.LENGTH_SHORT).show();
                    }

                });
    }
}