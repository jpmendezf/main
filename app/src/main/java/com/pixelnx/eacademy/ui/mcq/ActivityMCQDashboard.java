package com.pixelnx.eacademy.ui.mcq;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;


import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pixelnx.eacademy.R;
import com.pixelnx.eacademy.model.modellogin.ModelLogin;
import com.pixelnx.eacademy.ui.base.BaseActivity;
import com.pixelnx.eacademy.ui.home.ActivityHome;

import com.pixelnx.eacademy.utils.AppConsts;
import com.pixelnx.eacademy.utils.sharedpref.SharedPref;
import com.pixelnx.eacademy.utils.widgets.CustomTextExtraBold;


import java.util.ArrayList;

public class ActivityMCQDashboard extends BaseActivity implements View.OnClickListener {

    Context mContext;
    ArrayList<String> listData;
    RecyclerView rvMcq;
    CustomTextExtraBold tvHeader;
    ImageView ivUser;
    ImageView  ivBack;
    ModelLogin modelLogin;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mcqdashboard);
        mContext = ActivityMCQDashboard.this;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUser(AppConsts.STUDENT_DATA);


        initial();

    }

    private void initial() {
        rvMcq = (RecyclerView) findViewById(R.id.rvMcq);

        tvHeader = (CustomTextExtraBold) findViewById(R.id.tvHeader);
        ivUser = (ImageView) findViewById(R.id.ivUser);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivUser.setVisibility(View.GONE);
        tvHeader.setText(""+getResources().getString(R.string.mcq));
        ivBack.setOnClickListener(this);
        rvMcq.setLayoutManager(new GridLayoutManager(mContext, 1));

        addDataToList();

    }

    private void addDataToList() {
        listData = new ArrayList<>();
        listData.add(getResources().getString(R.string.Practice_Paper));
        listData.add(getResources().getString(R.string.Practice_Result));
        listData.add(getResources().getString(R.string.Mock_Test_Paper));
        listData.add(getResources().getString(R.string.Mock_Test_Result));
        listData.add(getResources().getString(R.string.Academic_Record));


        AdapterMCQ homeAdapter = new AdapterMCQ(mContext, listData);
        rvMcq.setAdapter(homeAdapter);
    }

    @Override
    public void onClick(View v) {





        if (v.getId() == R.id.ivBack) {
            startActivity(new Intent(mContext, ActivityHome.class));
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

}
