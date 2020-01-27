package com.collegesoftware.cyclopes;



import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.BreakIterator;

import cz.msebera.android.httpclient.Header;

public class TextProcess extends AppCompatActivity {
    private static final String TAG="TextProcess";
    private EditText mTextFromCam;
    private EditText mTranslated;
    private String[] mCamTextLangArrayReal={"en","fr","de","it","es","tr"};

    private String[] mTranslatedTextLangArray={"English","French","German","Italian","Spanish","Turkish"};
    private ArrayAdapter<String> dataAdapterForTranslatedLang;
    private Spinner mTranslatedLang,mTextFromCamSpinner;

    private ImageButton mCopyBtn,mCopyBtnTrns,mBackBtn,mDeleteAllBtn,mDeleteAllBtn1;
    private ImageView mSwitchBtn;
    private Button mTranslateBtn;
    private static final String BASE_URL_SEARCH="https://www.google.com/search?";
    private String mDetectedLang="en";
    private String mToTranslateLang="tr";
    private ClipboardManager mClipManager;
    private SharedPreferences mSharedPreferences;
    private int mTextFromCamIndex=0,mTranslatedTextIndex=5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_process);


        mSharedPreferences=getSharedPreferences("USER",MODE_PRIVATE);
        final String text=mSharedPreferences.getString("word","");

        mClipManager=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        Log.d("DetectedLang","DETECTED LANG:"+mDetectedLang);
        mCopyBtnTrns=(ImageButton)findViewById(R.id.copy_btn_in_trns);
        mDeleteAllBtn=(ImageButton)findViewById(R.id.delete_all_btn);
        mDeleteAllBtn1=(ImageButton)findViewById(R.id.delete_all_btn_trns);
        mDeleteAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextFromCam.setText("");
            }
        });
        mDeleteAllBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTranslated.setText("");
            }
        });
        mBackBtn=(ImageButton)findViewById(R.id.backbtn);
        mSwitchBtn=(ImageView)findViewById(R.id.switch_icon);
        mSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextFromCamSpinner.setSelection(mTranslatedTextIndex);
                mTranslatedLang.setSelection(mTextFromCamIndex);
            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        mCopyBtn=(ImageButton)findViewById(R.id.copy_btn);
        mCopyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mytext=mTextFromCam.getText().toString();
                ClipData myclip=ClipData.newPlainText("text", mytext);
                mClipManager.setPrimaryClip(myclip);
                Toast.makeText(TextProcess.this,"Copied",Toast.LENGTH_SHORT).show();

            }
        });
        mCopyBtnTrns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mytext=mTranslated.getText().toString();
                ClipData myclip=ClipData.newPlainText("text", mytext);
                mClipManager.setPrimaryClip(myclip);
                Toast.makeText(TextProcess.this,"Copied",Toast.LENGTH_SHORT).show();
            }
        });
        mTranslatedLang=(Spinner)findViewById(R.id.dest_lang);
        mTextFromCamSpinner=(Spinner)findViewById(R.id.detected_lang);
        dataAdapterForTranslatedLang = new ArrayAdapter<String>(this, R.layout.spinner_item_row,R.id.spinner_item_row,mTranslatedTextLangArray);
        mTextFromCamSpinner.setAdapter(dataAdapterForTranslatedLang);
        mTextFromCamSpinner.setSelection(0);
        mTextFromCamSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mDetectedLang=mCamTextLangArrayReal[position];
                mTextFromCamIndex=position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mTranslatedLang.setAdapter(dataAdapterForTranslatedLang);
        mTranslatedLang.setSelection(5);


        mTranslatedLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mToTranslateLang=mCamTextLangArrayReal[position];
                mTranslatedTextIndex=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mTextFromCam=(EditText)findViewById(R.id.text_from_cam);
        mTextFromCam.setMovementMethod(new ScrollingMovementMethod());
        mTranslated=(EditText)findViewById(R.id.translated);
        mTextFromCam.setMovementMethod(new ScrollingMovementMethod());
        mTranslateBtn=(Button)findViewById(R.id.trns_btn);
        final Handler textViewHandler = new Handler();
        mTextFromCam.setText(text);

        mTextFromCam.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                getSelectedWordInEditor();

                return false;
            }
        });








        mTranslateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG,"DETECTED LANG in translate btn"+mDetectedLang);



                String textfromcam=mTextFromCam.getText().toString();
                Http.post(textfromcam, mDetectedLang, mToTranslateLang, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            Log.d(TAG,"Textlang: "+mDetectedLang);
                            Log.d(TAG,"Textlang: "+mToTranslateLang);

                            JSONObject serverResp = new JSONObject(response.toString());
                            JSONObject jsonObject = serverResp.getJSONObject("data");
                            JSONArray transObject = jsonObject.getJSONArray("translations");
                            JSONObject transObject2 =  transObject.getJSONObject(0);

                            String string=transObject2.getString("translatedText").replace("&#39;","'").replace("&quot;","'");
                            mTranslated.setText(string);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });







            }
        });








    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void getSelectedWordInEditor(){
        Log.d(TAG,"step 0 in selection");
        int cursorPosition = mTextFromCam.getSelectionStart();

        if (cursorPosition==mTextFromCam.getText().length()){
            cursorPosition=cursorPosition-1;
        }
// initialize the BreakIterator
        BreakIterator iterator = BreakIterator.getWordInstance();
        iterator.setText(mTextFromCam.getText().toString());



// find the word boundaries before and after the cursor position
        int wordStart;
        if (iterator.isBoundary(cursorPosition)) {
            wordStart = cursorPosition;
            Log.d(TAG,"step 2 in selection");

        } else {
            wordStart = iterator.preceding(cursorPosition);
            Log.d(TAG,"step 3 in selection");

        }
        int wordEnd = iterator.following(cursorPosition);
        Log.d(TAG,"step 4 in selection");


// get the word
        final String word = mTextFromCam.getText().subSequence(wordStart, wordEnd).toString();

        Log.d(TAG,"selected word is :"+word);
        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(TextProcess.this);

        View mview=getLayoutInflater().inflate(R.layout.choosed_word_dialog,null);

        TextView search=(TextView) mview.findViewById(R.id.searchBtn_dialog1);
        TextView translate=(TextView)mview.findViewById(R.id.translate_btn_dialog);
        final TextView choosedWord=(TextView)mview.findViewById(R.id.choosed_word);
        final TextView translatedWord=(TextView)mview.findViewById(R.id.translation_in_dialog);

        choosedWord.setText(word);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searhInChrome(choosedWord.getText().toString());
            }
        });
        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Http.post(word, mDetectedLang, mToTranslateLang, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            Log.d(TAG,"Textlang: "+mDetectedLang);
                            Log.d(TAG,"Textlang: "+mToTranslateLang);

                            JSONObject serverResp = new JSONObject(response.toString());
                            JSONObject jsonObject = serverResp.getJSONObject("data");
                            JSONArray transObject = jsonObject.getJSONArray("translations");
                            JSONObject transObject2 =  transObject.getJSONObject(0);
                            translatedWord.setText(transObject2.getString("translatedText"));
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        builderSingle.setView(mview);
        builderSingle.show();

    }
    private void searhInChrome(String text){
        String encodedText = URLEncoder.encode(text);
        String url=BASE_URL_SEARCH+"q="+encodedText;
        PackageManager pm = getPackageManager();
        Intent launchIntent = pm.getLaunchIntentForPackage("com.android.chrome");
        launchIntent.setData(Uri.parse(url));
        if (launchIntent != null) {
            startActivity(launchIntent);
        } else {
            Toast.makeText(TextProcess.this, "Chrome not found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSharedPreferences.edit().putString("word"," ").apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSharedPreferences.edit().putString("word"," ").apply();

    }
}
