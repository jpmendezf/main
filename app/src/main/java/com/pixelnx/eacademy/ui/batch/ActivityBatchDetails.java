package com.pixelnx.eacademy.ui.batch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.pixelnx.eacademy.R;
import com.pixelnx.eacademy.ui.signup.ActivitySignUp;
import com.pixelnx.eacademy.utils.ProjectUtils;
import com.pixelnx.eacademy.utils.widgets.CustomSmallText;
import com.pixelnx.eacademy.utils.widgets.CustomTextBold;
import com.pixelnx.eacademy.utils.widgets.CustomTextExtraBold;
import com.pixelnx.eacademy.utils.widgets.CustomeTextRegular;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityBatchDetails extends AppCompatActivity implements View.OnClickListener {
    ImageView ivBack, ivBatch;
    Context context;
    CustomTextExtraBold tvHeader,tvDiscriptionHeading;
    static ModelBachDetails.batchData batchData;
    CustomTextBold tvOfferPrice;
    CustomSmallText startOn, endOn, readMore, timing;
    CustomeTextRegular description;
    CustomSmallText btBuyNow, batchPrice;
    LinearLayout dynamicLayout;
    static String amount = "", BatchId = "", paymentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_details);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context = ActivityBatchDetails.this;
        init();

    }

    void init() {
        ivBack = findViewById(R.id.ivBack);
        tvDiscriptionHeading = findViewById(R.id.tvDiscriptionHeading);
        btBuyNow = findViewById(R.id.btBuyNow);
        btBuyNow.setOnClickListener(this);
        readMore = findViewById(R.id.readMore);
        ivBatch = findViewById(R.id.ivBatch);
        timing = findViewById(R.id.timing);
        dynamicLayout = findViewById(R.id.dynamicLayout);
        batchPrice = findViewById(R.id.batchPrice);
        description = findViewById(R.id.description);
        endOn = findViewById(R.id.endOn);
        startOn = findViewById(R.id.startOn);
        tvOfferPrice = findViewById(R.id.tvOfferPrice);
        ivBack.setOnClickListener(this);
        tvHeader = (CustomTextExtraBold) findViewById(R.id.tvHeader);


        if (getIntent().hasExtra("dataBatch")) {
            batchData = (ModelBachDetails.batchData) getIntent().getSerializableExtra("dataBatch");

        }

        BatchId = batchData.getId();
        paymentType = batchData.getPaymentType();
        if(batchData.getDescription().isEmpty()){
            tvDiscriptionHeading.setVisibility(View.GONE);
        }

        if (batchData.getBatchFecherd().size() > 0) {

            for (int i = 0; i < batchData.getBatchFecherd().size(); i++) {

                try {


                    JSONArray jsonArray = new JSONArray(batchData.getBatchFecherd().get(i).getFecherd());
                    if (!batchData.getBatchFecherd().get(i).getBatchSpecification().isEmpty()) {

                        TextView textView = new CustomTextExtraBold(context);
                        textView.setTextColor(getResources().getColor(R.color.text_color));
                        textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                        textView.setText("" + batchData.getBatchFecherd().get(i).getBatchSpecification());
                        textView.setTextSize(20f);
                        dynamicLayout.addView(textView);

                    }
                    int count = 1;
                    for (int x = 0; x < jsonArray.length(); x++) {
                        if (!jsonArray.get(x).toString().isEmpty()) {
                            LinearLayout parentview = new LinearLayout(context);
                            parentview.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 6));
                            parentview.setOrientation(LinearLayout.HORIZONTAL);

                            TextView textViewFeatures = new CustomTextExtraBold(context);
                            textViewFeatures.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                            textViewFeatures.setText(" " + jsonArray.get(x));
                            ImageView imageView = new ImageView(context);
                            imageView.setPadding(2, 6, 2, 2);
                            imageView.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_box_24));

                            parentview.addView(imageView);
                            parentview.addView(textViewFeatures);
                            dynamicLayout.addView(parentview);


                            count++;
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        }

        if (batchData.getBatchType().equals("2")) {
            if (!batchData.getBatchOfferPrice().isEmpty()) {
                batchPrice.setText(batchData.getCurrencyDecimalCode() + " " + batchData.getBatchPrice());
                tvOfferPrice.setText(batchData.getCurrencyDecimalCode()+ " " + batchData.getBatchOfferPrice());
                batchPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                btBuyNow.setText(getResources().getString(R.string.BuyNow) + "   " + batchData.getCurrencyDecimalCode()+ " " + batchData.getBatchOfferPrice());
                amount = batchData.getBatchOfferPrice();
            } else {
                batchPrice.setText(batchData.getCurrencyDecimalCode() + " " + batchData.getBatchPrice());
                tvOfferPrice.setVisibility(View.GONE);
                amount = batchData.getBatchPrice();
                btBuyNow.setText(getResources().getString(R.string.BuyNow) + "   " + batchData.getCurrencyDecimalCode() + " " + batchData.getBatchPrice());
            }
        } else {
            batchPrice.setText(getResources().getString(R.string.Free));
            amount = "free";
            btBuyNow.setText(getResources().getString(R.string.EnrollNow));
            tvOfferPrice.setVisibility(View.GONE);

        }

        tvHeader.setText("" + batchData.getBatchName());


        try {

            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateStartObj = sdf.parse(batchData.getStartTime());
            final Date dateEndObj = sdf.parse(batchData.getEndTime());
            endOn.setText("" + batchData.getEndDate());
            startOn.setText(batchData.getStartDate());
            timing.setText("" + new SimpleDateFormat("K:mm a").format(dateStartObj) + "  To " + new SimpleDateFormat("K:mm a").format(dateEndObj));
            if (batchData.getDescription().length() <= 99) {
                readMore.setVisibility(View.GONE);
                description.setText("" + batchData.getDescription());
            } else {
                description.setText("" + batchData.getDescription().substring(0, 99));
            }

            readMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (readMore.getText().toString().equalsIgnoreCase("" + getResources().getString(R.string.Viewmore))) {
                        readMore.setText("" + getResources().getString(R.string.hide__));
                        description.setText("" + batchData.getDescription());
                    } else {
                        readMore.setText("" + getResources().getString(R.string.Viewmore));
                        description.setText("" + batchData.getDescription().substring(0, 99));
                    }
                }
            });

        } catch (Exception e) {
        }


        if (batchData.getBatchImage() != null && !batchData.getBatchImage().isEmpty()) {
            Picasso.get().load("" + batchData.getBatchImage()).placeholder(R.drawable.noimage).into(ivBatch);
        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.btBuyNow:
                if (ProjectUtils.checkConnection(context)) {
                    startActivity(new Intent(context, ActivitySignUp.class).putExtra("data",batchData).putExtra
                            ("amount", "" + amount).putExtra("BatchId", "" + BatchId).putExtra("paymentType", "" + paymentType));
                } else {
                    Toast.makeText(context, getResources().getString(R.string.NoInternetConnection), Toast.LENGTH_SHORT).show();
                }
                break;

        }

    }
}