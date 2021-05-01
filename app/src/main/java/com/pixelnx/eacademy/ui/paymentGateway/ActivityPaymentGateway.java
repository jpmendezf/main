package com.pixelnx.eacademy.ui.paymentGateway;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Bundle;

import android.os.Environment;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.pixelnx.eacademy.R;
import com.pixelnx.eacademy.model.ModelSettingRecord;
import com.pixelnx.eacademy.model.modellogin.ModelLogin;
import com.pixelnx.eacademy.ui.batch.ModelBachDetails;
import com.pixelnx.eacademy.ui.home.ActivityHome;
import com.pixelnx.eacademy.utils.AppConsts;
import com.pixelnx.eacademy.utils.ProjectUtils;
import com.pixelnx.eacademy.utils.sharedpref.SharedPref;
import com.pixelnx.eacademy.utils.widgets.CustomSmallText;
import com.pixelnx.eacademy.utils.widgets.CustomTextBold;
import com.pixelnx.eacademy.utils.widgets.CustomTextExtraBold;
import com.pixelnx.eacademy.utils.widgets.CustomeTextRegular;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;


import static com.pixelnx.eacademy.utils.AppConsts.IS_REGISTER;

public class ActivityPaymentGateway extends AppCompatActivity implements View.OnClickListener, PaymentResultListener {
    ImageView ivBack;
    CustomTextExtraBold tvHeader;
    public static String PAYPAL_CLIENT_ID;
    ImageView copyId, copyPass;
    static final int PAYPAL_REQUEST_CODE = 7777;
    static PayPalConfiguration config;
    CustomeTextRegular tvAmount, symbol, amountText;
    CustomTextBold tvEnrollment, tvPassword;
    LinearLayout llLoginDetailsForNewStudents, llPayment;
    static String amountForPayment, BatchId = "", password, name, email, mobile, token, versionCode, paymentType, amount;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    ModelSettingRecord modelSettingRecord;
    Context context;
    ModelBachDetails.batchData batchData;
    CustomSmallText tvMove;
    static String clientIdPaypal = "", rZPKey = "";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_gateway);
        context = ActivityPaymentGateway.this;
        sharedPref = SharedPref.getInstance(context);

        modelSettingRecord = sharedPref.getSettingInfo(AppConsts.APP_INFO);


        if (modelSettingRecord != null) {
            if (modelSettingRecord.getData().getPaypalClientId() != null) {
                clientIdPaypal = modelSettingRecord.getData().getPaypalClientId();
                rZPKey = modelSettingRecord.getData().getRazorpayKeyId();
                initial();
            } else {
                siteSettings();
            }
        } else {
            siteSettings();
        }
        modelLogin = sharedPref.getUser(AppConsts.STUDENT_DATA);



    }

    void siteSettings() {
        ProjectUtils.showProgressDialog(context, false, getResources().getString(R.string.Loading___));
        AndroidNetworking.get(AppConsts.BASE_URL + AppConsts.API_HOMEGENERAL_SETTING).build()
                .getAsObject(ModelSettingRecord.class, new ParsedRequestListener<ModelSettingRecord>() {
                    @Override
                    public void onResponse(ModelSettingRecord response) {
                        ProjectUtils.pauseProgressDialog();
                        if (response.getStatus().equalsIgnoreCase("true")) {

                            sharedPref.setSettingInfo(AppConsts.APP_INFO, response);
                            clientIdPaypal = response.getData().getPaypalClientId();
                            rZPKey=response.getData().getRazorpayKeyId();
                            initial();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        ProjectUtils.pauseProgressDialog();

                    }
                });
    }

    void initial() {
        Checkout.preload(getApplicationContext());
        paypalSetUp();

        copyId = findViewById(R.id.copyId);
        copyPass = findViewById(R.id.copyPass);
        ivBack = findViewById(R.id.ivBack);
        symbol = findViewById(R.id.symbol);
        ivBack.setVisibility(View.GONE);
        tvMove = findViewById(R.id.tvMove);
        tvPassword = findViewById(R.id.tvPassword);
        tvEnrollment = findViewById(R.id.tvEnrollment);
        tvMove.setOnClickListener(this);
        llLoginDetailsForNewStudents = findViewById(R.id.llLoginDetailsForNewStudents);
        llPayment = findViewById(R.id.llPayment);
        tvAmount = findViewById(R.id.tvAmount);
        amountText = findViewById(R.id.amountText);


        tvHeader = findViewById(R.id.tvHeader);
        tvHeader.setText("" + getResources().getString(R.string.app_name));

        if (getIntent().hasExtra("data")) {
            batchData = (ModelBachDetails.batchData) getIntent().getSerializableExtra("data");
            if (batchData.getBatchType().equals("2")) {
                symbol.setText(batchData.getCurrencyDecimalCode());
                amountForPayment = batchData.getBatchPriceConvert();
            } else {
                symbol.setVisibility(View.GONE);
                amountForPayment = "Free";

            }


        }

        if (getIntent().hasExtra("name")) {
            name = getIntent().getStringExtra("name");
            email = getIntent().getStringExtra("email");
            mobile = getIntent().getStringExtra("mobile");
            versionCode = getIntent().getStringExtra("versionCode");
            token = getIntent().getStringExtra("token");
            amount = getIntent().getStringExtra("amount");
            BatchId = getIntent().getStringExtra("BatchId");
            paymentType = getIntent().getStringExtra("paymentType");


        }


        tvAmount.setText("" + amount);


        if (amountForPayment.equalsIgnoreCase("free")) {

            successSignUpApi("" + BatchId, "", "");

        } else {


            whichGatewayUsed();


        }


        copyId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation scaleAnim = AnimationUtils.loadAnimation(context, R.anim.blink_anim);
                copyId.startAnimation(scaleAnim);
                tvEnrollment.startAnimation(scaleAnim);
                ClipboardManager cManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData cData = ClipData.newPlainText("text", tvEnrollment.getText().toString());
                cManager.setPrimaryClip(cData);
                Toast.makeText(context, getResources().getString(R.string.Copied_to_clipboard), Toast.LENGTH_SHORT).show();
            }
        });
        copyPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation scaleAnim = AnimationUtils.loadAnimation(context, R.anim.blink_anim);
                copyPass.startAnimation(scaleAnim);
                tvPassword.startAnimation(scaleAnim);
                ClipboardManager cManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData cData = ClipData.newPlainText("text", tvPassword.getText().toString());
                cManager.setPrimaryClip(cData);
                Toast.makeText(context, getResources().getString(R.string.Copied_to_clipboard), Toast.LENGTH_SHORT).show();

            }
        });


    }

    void whichGatewayUsed() {
        if (paymentType != null) {
            if (paymentType.equalsIgnoreCase("1")) {
                rzp();
            } else {
                Intent intentSer = new Intent(this, PayPalService.class);
                intentSer.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                startService(intentSer);
                paypalPayment();
            }
        }

    }

    void paypalSetUp() {
        PAYPAL_CLIENT_ID = "" + clientIdPaypal;
        config = new PayPalConfiguration()
                .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
                .clientId(PAYPAL_CLIENT_ID);


        // or live (ENVIRONMENT_PRODUCTION)

    }

    void rzp() {


        if (!amountForPayment.isEmpty()) {

            startPayment("" + amountForPayment);
        }

    }

    public void startPayment(String payments) {

        Checkout checkout = new Checkout();
        checkout.setKeyID(rZPKey);
        final Activity activity = this;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "" + getResources().getString(R.string.app_name));
            jsonObject.put("description", "Pay Fee");
            jsonObject.put("currency", "INR");
            jsonObject.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            jsonObject.put("payment_capture", true);

            String payment = "" + payments;
            double totalPay = Double.parseDouble(payment);
            totalPay = totalPay * 100;

            jsonObject.put("amount", "" + totalPay);
            checkout.open(activity, jsonObject);

        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Error in payment", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }


    }


    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    private void paypalPayment() {


        if (!amountForPayment.isEmpty()) {
            PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(amountForPayment)), "USD",
                    "pay fee", PayPalPayment.PAYMENT_INTENT_SALE);
            Intent intent = new Intent(this, PaymentActivity.class);
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
            startActivityForResult(intent, PAYPAL_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {

                        String paymentDetails = confirmation.toJSONObject().toString(4);

                        JSONObject jsonObject = new JSONObject(paymentDetails);


                        JSONObject jsonObject1 = new JSONObject("" + jsonObject.getString("response"));
                        tvAmount.setText("" + amount + "  Paid ");
                        if (!amountForPayment.equalsIgnoreCase("free")) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.Payment_Done), Toast.LENGTH_SHORT).show();
                        }
                        successSignUpApi("" + BatchId, "" + jsonObject1.getString("id"), amountForPayment);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                finish();
                Toast.makeText(this, getResources().getString(R.string.Cancel), Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {

            Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.tvMove:
                startActivity(new Intent(context, ActivityHome.class));
                Toast.makeText(context, getResources().getString(R.string.Welcome) + ", " + modelLogin.getStudentData().getFullName(), Toast.LENGTH_SHORT)
                        .show();
                break;

        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        if (!amountForPayment.equalsIgnoreCase("free")) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.Payment_Done), Toast.LENGTH_SHORT).show();
        }


        successSignUpApi("" + BatchId, s, amountForPayment);
    }

    @Override
    public void onPaymentError(int i, String s) {

        Toast.makeText(getApplicationContext(), getResources().getString(R.string.Payment_Cancel), Toast.LENGTH_SHORT).show();
        finish();


    }

    void successSignUpApi(String batchId, String transectionId, String amountt) {

        ProjectUtils.showProgressDialog(context, false, getResources().getString(R.string.Loading___));


        AndroidNetworking.post(AppConsts.BASE_URL + AppConsts.API_STUDENT_REGISTRATION)
                .addBodyParameter(AppConsts.NAME, "" + name)
                .addBodyParameter(AppConsts.EMAIL, "" + email)
                .addBodyParameter(AppConsts.MOBILE, "" + mobile)
                .addBodyParameter(AppConsts.TOKEN, "" + token)
                .addBodyParameter(AppConsts.TRANSACTION_ID, "" + transectionId)
                .addBodyParameter(AppConsts.AMOUNT, "" + amountForPayment)
                .addBodyParameter(AppConsts.BATCH_ID, "" + BatchId)
                .addBodyParameter(AppConsts.VERSION_CODE, "" + versionCode)
                .build()
                .getAsObject(ModelLogin.class, new ParsedRequestListener<ModelLogin>() {
                    @Override
                    public void onResponse(ModelLogin response) {
                        ProjectUtils.pauseProgressDialog();
                        try {


                            if (response.getStatus().equalsIgnoreCase("true")) {
                                tvMove.setVisibility(View.VISIBLE);
                                sharedPref.setUser(AppConsts.STUDENT_DATA, response);
                                sharedPref.setBooleanValue(IS_REGISTER, true);
                                modelLogin = sharedPref.getUser(AppConsts.STUDENT_DATA);
                                tvAmount.setVisibility(View.GONE);
                                amountText.setVisibility(View.GONE);

                                tvEnrollment.setText("" + getResources().getString(R.string.EnrollmentId) + "   " + modelLogin.getStudentData().getEnrollmentId());
                                tvPassword.setText(getResources().getString(R.string.Password) + "    " + response.getStudentData().getPassword());
                                llLoginDetailsForNewStudents.setVisibility(View.VISIBLE);
                                llPayment.setVisibility(View.GONE);
                                llLoginDetailsForNewStudents.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                                    @Override
                                    public void onGlobalLayout() {
                                        llLoginDetailsForNewStudents.setDrawingCacheEnabled(true);
                                        Bitmap myBitmap = llLoginDetailsForNewStudents.getDrawingCache();
                                        saveImage(myBitmap);
                                        Animation scaleAnim = AnimationUtils.loadAnimation(context, R.anim.blink_anim);
                                        llLoginDetailsForNewStudents.startAnimation(scaleAnim);
                                        Toast.makeText(context, getResources().getString(R.string.Screenshot_Captured), Toast.LENGTH_SHORT).show();

                                    }
                                });

                            } else {
                                ProjectUtils.pauseProgressDialog();
                                Toast.makeText(context, "" + response.getMsg(), Toast.LENGTH_SHORT).show();
                            }


                        } catch (Exception e) {
                            ProjectUtils.pauseProgressDialog();
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(context, getResources().getString(R.string.Try_again), Toast.LENGTH_SHORT).show();
                        ProjectUtils.pauseProgressDialog();
                    }
                });


    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.app_name));

        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(context,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();


            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}