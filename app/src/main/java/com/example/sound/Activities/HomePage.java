package com.example.sound.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sound.R;
import com.google.android.material.snackbar.Snackbar;

public class HomePage extends AppCompatActivity {

    private static final  int REQUEST_CODE = 1000;
    private static final int REQUEST_PERMISSION = 1001;
    Button b_mic,b_vid;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        b_vid = findViewById(R.id.video);
        b_mic = findViewById(R.id.mic);

        textView = findViewById(R.id.textView);

        Typeface MontserratSub = Typeface.createFromAsset(getAssets(),"fonts/MontserratSubrayada-Bold.ttf");

        textView.setTypeface(MontserratSub);

        b_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomePage.this, AudioRecorder.class);
                startActivity(intent);
//                if (ContextCompat.checkSelfPermission(HomePage.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    + ContextCompat.checkSelfPermission(HomePage.this, Manifest.permission.RECORD_AUDIO)
//                    != PackageManager.PERMISSION_GRANTED)
//            {
//                if (ActivityCompat.shouldShowRequestPermissionRationale(HomePage.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        ||
//                        ActivityCompat.shouldShowRequestPermissionRationale(HomePage.this,Manifest.permission.RECORD_AUDIO))
//                {
//                    Intent intent = new Intent(HomePage.this, AudioRecorder.class);
//                    startActivity(intent);
//                }
//                else
//                {
//                    ActivityCompat.requestPermissions(HomePage.this,
//                            new String[]{
//                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                                    Manifest.permission.RECORD_AUDIO
//                            },REQUEST_PERMISSION);
//                }
//            }

            }
        });

        b_vid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, ScreenRecorder.class);
                startActivity(intent);
            }
        });
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode)
//        {
//            case REQUEST_PERMISSION:
//            {
//                if ((grantResults.length > 0) && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED))
//                {
//                    Intent intent = new Intent(HomePage.this, AudioRecorder.class);
//                    startActivity(intent);
//                }
//                return;
//            }
//        }
//    }
}
