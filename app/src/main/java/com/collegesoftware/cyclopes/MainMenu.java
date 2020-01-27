package com.collegesoftware.cyclopes;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainMenu extends AppCompatActivity {
    private static final int PICKFILE_REQUEST_CODE = 1234;
    private ImageView mCamAct,mTranslateAct,mImgToTextAct,mInfoBtn;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        mSharedPreferences = getSharedPreferences("USER",MODE_PRIVATE);
        mInfoBtn=(ImageView)findViewById(R.id.info_btn);
        mInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainMenu.this,InfoActivity.class);
                startActivity(intent);
            }
        });

        mCamAct=(ImageView)findViewById(R.id.camera_act);
        mTranslateAct=(ImageView)findViewById(R.id.text_process_act);
        mImgToTextAct=(ImageView)findViewById(R.id.img_to_text_act);

        mCamAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainMenu.this,OcrCaptureActivity.class);
                startActivity(intent);
            }
        });
        mTranslateAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainMenu.this,TextProcess.class);
                startActivity(intent);
            }
        });
        mImgToTextAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICKFILE_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICKFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Uri selectedImageUri = data.getData();
            mSharedPreferences.edit().putString("imgUrl",selectedImageUri.toString()).apply();
            Intent intent=new Intent(MainMenu.this,ImgToText.class);
            startActivity(intent);


        }
    }
}
