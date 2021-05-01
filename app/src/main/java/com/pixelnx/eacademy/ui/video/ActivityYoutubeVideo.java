package com.pixelnx.eacademy.ui.video;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.pixelnx.eacademy.R;
import com.pixelnx.eacademy.ui.galary.galleryvideos.ActivityGalleryVideos;

import com.pixelnx.eacademy.utils.widgets.CustomTextExtraBold;


public class ActivityYoutubeVideo extends AppCompatActivity {


    String key = "";
    String vId = "";
    ImageView ivBack;

    CustomTextExtraBold tvHeader;
    YouTubePlayerView youTubePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_youtube_video);
        ivBack = findViewById(R.id.ivBack);
        if (Build.VERSION.SDK_INT <= 25) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/"+getIntent().getStringExtra("vId"))).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }

        if (getIntent().hasExtra("firebase/notice")) {
            tvHeader = findViewById(R.id.tvHeader);
            tvHeader.setText("" + getIntent().getStringExtra("firebase/notice"));
                   vId=getIntent().getStringExtra("vId");
        } else {
            tvHeader = findViewById(R.id.tvHeader);
            tvHeader.setText("" + getIntent().getStringExtra("title"));
            key = getIntent().getStringExtra("key");
            vId = getIntent().getStringExtra("vId");
        }


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {

                youTubePlayer.loadVideo("" + vId, 0f);

            }

            @Override
            public void onError(YouTubePlayer youTubePlayer, PlayerConstants.PlayerError error) {
                super.onError(youTubePlayer, error);

            }

            @Override
            public void onStateChange(YouTubePlayer youTubePlayer, PlayerConstants.PlayerState state) {
                super.onStateChange(youTubePlayer, state);

            }
        });

    }

    @Override
    public void onBackPressed() {
        if (getIntent().hasExtra("firebase/notice")) {
            startActivity(new Intent(getApplicationContext(), ActivityGalleryVideos.class));
            finish();

        }else{
        super.onBackPressed();}
    }
}
