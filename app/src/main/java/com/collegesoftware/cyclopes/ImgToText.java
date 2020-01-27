package com.collegesoftware.cyclopes;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.ArrayList;

public class ImgToText extends AppCompatActivity implements IPoints {
    private Point mPoint1,mPoint2,mPoint3,mPoint4;
    private static final int PICKFILE_REQUEST_CODE = 345;
    private static final int CAPTURE_REQUEST_CODE = 543;

    private static final String TAG ="ImgToText" ;
    private SharedPreferences mSharedPreferences;
    private ImageView mChoosedImg;
    private ImageButton mProcessBtn,mChooseImg,mBackBtn,mTakePhoto;
    private String mImgUrl;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_to_text);

        initImageLoader();

        mSharedPreferences = getSharedPreferences("USER",MODE_PRIVATE);
        mProcessBtn=(ImageButton)findViewById(R.id.process_btn);


        mBackBtn=(ImageButton)findViewById(R.id.backbtn);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mChooseImg=(ImageButton)findViewById(R.id.chooseImg);
        mChooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICKFILE_REQUEST_CODE);
            }
        });
        mChoosedImg=(ImageView)findViewById(R.id.choosed_img);
        setImage(mSharedPreferences.getString("imgUrl",""));
        mProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Log.d(TAG,"PROCESS1");
                    Bitmap bitmap= MediaStore.Images.Media.getBitmap(ImgToText.this.getContentResolver(), Uri.parse(mImgUrl));
                    int bitmapHeight=bitmap.getHeight();
                    int bitmapWidth=bitmap.getWidth();
                    Log.d(TAG,"point1 x: "+mPoint1.x);
                    Log.d(TAG,"point1 y: "+mPoint1.y);
                    Log.d(TAG,"point2 x: "+mPoint2.x);
                    Log.d(TAG,"point2 y: "+mPoint2.y);
                    Log.d(TAG,"point3 x: "+mPoint3.x);
                    Log.d(TAG,"point3 y: "+mPoint3.y);
                    Log.d(TAG,"point4 x: "+mPoint4.x);
                    Log.d(TAG,"point4 y: "+mPoint4.y);
                    Log.d(TAG,"crop width: "+(mPoint4.x-mPoint1.x));
                    Log.d(TAG,"crop height: "+(mPoint2.y-mPoint1.y));


                    int x=mPoint1.x;
                    int y=mPoint1.y;
                    float width=mPoint4.x-mPoint1.x;
                    float height=mPoint2.y-mPoint1.y;


                    float imgViewHeight=mChoosedImg.getHeight();
                    float imgViewWidth=mChoosedImg.getWidth();

                    float realx = x,realy=y,multiH,multiW;

                    float realhe=height;
                    if (bitmapHeight>imgViewHeight){
                        multiH=bitmapHeight/imgViewHeight;
                        realhe=Math.round(height*multiH);
                        realy=multiH*y;



                    }else if (imgViewHeight>bitmapHeight){
                        multiH=bitmapHeight/imgViewHeight;
                        realhe=Math.round(height*multiH);
                        realy=multiH*y;

                    }
                    float realw=width;

                    if (bitmapWidth>imgViewWidth){
                        multiW=bitmapWidth/imgViewWidth;
                        realw=Math.round(width*multiW);
                        realx=multiW*x;



                    }else if (bitmapWidth<imgViewWidth){
                        multiW=bitmapWidth/imgViewWidth;
                        realw=Math.round(width*multiW);
                        realx=multiW*x;

                    }
                    int finalx=Math.round(realx);
                    int finay=Math.round(realy);





                    if (realhe+finay>bitmapHeight){
                        realhe=bitmapHeight-finay;
                    }
                    if (realw+finalx>bitmapWidth){
                        realw=bitmapWidth-finalx;
                    }





                    int realheF=Math.round(realhe);
                    int realwF=Math.round(realw);



                    Bitmap bitmap1=cropBitmap(bitmap,finalx,finay,realwF,realheF);







                    TextRecognizer textRecognizer=new TextRecognizer.Builder(getApplicationContext()).build();
                    if (!textRecognizer.isOperational()){
                        Log.d(TAG,"PROCESS2");

                        Toast.makeText(ImgToText.this,"not working",Toast.LENGTH_SHORT).show();
                    }else {
                        Log.d(TAG,"PROCESS3");

                        Frame frame=new Frame.Builder().setBitmap(bitmap1).build();
                        SparseArray<TextBlock> items=textRecognizer.detect(frame);
                        StringBuilder stringBuilder=new StringBuilder();
                        for (int i=0;i<items.size();i++){
                            TextBlock text=items.valueAt(i);
                            stringBuilder.append(text.getValue());
                            stringBuilder.append("\n");
                        }
                        Log.d(TAG,"PROCESS4");

                        mSharedPreferences.edit().putString("word",stringBuilder.toString().toLowerCase().replace("\n"," ")).apply();
                        Log.d(TAG,"PROCESS5");

                        Intent intent=new Intent(ImgToText.this,TextProcess.class);
                        startActivity(intent);
                        Log.d(TAG,"PROCESS6");

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    private void setImage(String url){
        ImageLoader imageLoader = ImageLoader.getInstance();
        Log.d(TAG,"img url "+url);
        mImgUrl=url;
        imageLoader.displayImage(url, mChoosedImg);

    }
    private void setImage(Bitmap url){
        mChoosedImg.setImageBitmap(url);

    }

    private void initImageLoader(){
        UniversalImageLoader imageLoader = new UniversalImageLoader(ImgToText.this);
        ImageLoader.getInstance().init(imageLoader.getConfig());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICKFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Log.d(TAG,"img url");
            Uri selectedImageUri = data.getData();
            mImgUrl=selectedImageUri.toString();
            try {
                mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                setImage(mBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }



//            setImage(selectedImageUri.toString());


        }else if (requestCode == CAPTURE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Bitmap bitmap=(Bitmap)data.getExtras().get("data");
            mChoosedImg.setImageBitmap(bitmap);
        }
    }

    private Bitmap cropBitmap(Bitmap bitmap, int x, int y, int width, int height){
//        Bitmap defaultBitmap = Bitmap.createBitmap(width, height, bitmap.getConfig());
//        Canvas canvas = new Canvas(defaultBitmap);
//        canvas.drawBitmap(bitmap, new Rect(x,y,width,height), new Rect(0,0,width,height), new Paint());
        return Bitmap.createBitmap(bitmap,x,y,width,height);
    }

    @Override
    public void getPoints(Point point1, Point point2, Point point3, Point point4) {
        if (point1.x<0){
            point1.x=0;
        }else if (point1.y<0){
            point1.y=0;
        }else if(point4.y<0){
            point4.y=0;
        }
        int point1cOUNT=point1.x+point1.y;
        int point2cOUNT=point2.x+point2.y;
        int point3cOUNT=point3.x+point3.y;
        int point4cOUNT=point4.x+point4.y;
        ArrayList<Integer> list=new ArrayList<>();
        ArrayList<Point> listBalls=new ArrayList<>();
        listBalls.add(point1);
        listBalls.add(point2);
        listBalls.add(point3);
        listBalls.add(point4);



        list.add(point1cOUNT);
        list.add(point2cOUNT);
        list.add(point3cOUNT);
        list.add(point4cOUNT);
        int maxIndex=0;
        int max=0;
        for(int i=0;i<4;i++){
            if (list.get(i)>max){
                maxIndex=i;
            }
        }
        int minIndex=0;

        for(int i=0;i<4;i++){
            if (list.get(i)<point1cOUNT){
                minIndex=i;
            }
        }


        Log.d(TAG,"min point is x "+listBalls.get(minIndex).x);
        Log.d(TAG,"max point is x "+listBalls.get(maxIndex).x);
        Log.d(TAG,"min point is y "+listBalls.get(minIndex).y);
        Log.d(TAG,"max point is y "+listBalls.get(maxIndex).y);
        mPoint1=listBalls.get(minIndex);
        mPoint3=listBalls.get(maxIndex);
        listBalls.remove(maxIndex);
        listBalls.remove(minIndex);

        if(listBalls.get(0).x>listBalls.get(1).x){
            mPoint4=listBalls.get(0);
            mPoint2=listBalls.get(1);
        }else{
            mPoint4=listBalls.get(1);
            mPoint2=listBalls.get(0);
        }





//
//
//        mPoint1=point1;
//        mPoint2=point2;
//        mPoint3=point3;
//        mPoint4=point4;



    }


}
