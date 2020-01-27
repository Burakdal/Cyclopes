package com.collegesoftware.cyclopes;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;


import java.net.URLEncoder;

public class Http {
    private static final String BASE_URL = "https://translation.googleapis.com/language/translate/v2?";
    private static final String BASE_URL_LANG_DETECTION = "https://translation.googleapis.com/language/translate/v2/detect?";


    private static final String KEY = "AIzaSyAB0vADfRzwc2V6TUMcVkaj5PQV02wM39M";



    private static AsyncHttpClient client = new AsyncHttpClient();
    private static   AsyncHttpClient clientfordetection=new AsyncHttpClient();


    public static void post(String transText,String sourceLang, String destLang,AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(transText, sourceLang, destLang), responseHandler);
    }

    public static   void postForLangDetection(String transText,AsyncHttpResponseHandler responseHandler) {
        clientfordetection.get(getAbsoluteUrlForLangDetection(transText), responseHandler);

    }

    private static String makeKeyChunk(String key) {
        return "key=" + KEY;
    }



    private static String makeTransChunk(String transText) {
        String encodedText = URLEncoder.encode(transText);
        return "&q=" + encodedText;
    }

    private static String langSource(String langSource) {
        return "&source=" + langSource;
    }

    private static String langDest(String langDest) {
        return "&target=" + langDest;

    }

    private static String getAbsoluteUrl(String transText, String sourceLang, String destLang) {
        String apiUrl = BASE_URL + makeKeyChunk(KEY) + makeTransChunk(transText) + langSource(sourceLang) + langDest(destLang);
        Log.d("URL",apiUrl);
        return apiUrl;
    }

    private static String getAbsoluteUrlForLangDetection(String transText) {
        String apiUrl = BASE_URL_LANG_DETECTION + makeKeyChunk(KEY) + makeTransChunk(transText);
        Log.d("URL2",apiUrl);
        return apiUrl;
    }
}
