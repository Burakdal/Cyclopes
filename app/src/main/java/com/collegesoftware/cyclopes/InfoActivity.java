package com.collegesoftware.cyclopes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class InfoActivity extends AppCompatActivity {


    private ImageButton mBackBtn;
    private Button mContactBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        mBackBtn=(ImageButton)findViewById(R.id.backbtn);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mContactBtn=(Button)findViewById(R.id.contactBtn);
        mContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"collegesoftware9@gmail.com"});

                intent.setPackage("com.google.android.gm");
                if (intent.resolveActivity(getPackageManager())!=null){
                    startActivity(intent);

                }else{

                    Toast.makeText(InfoActivity.this,"Gmail App is not installed.Mail is :collegesoftware9@gmail.com",Toast.LENGTH_SHORT).show();

                }
            }
        });


    }
}
