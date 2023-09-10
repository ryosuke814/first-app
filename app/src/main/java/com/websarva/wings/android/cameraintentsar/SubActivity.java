package com.websarva.wings.android.cameraintentsar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SubActivity extends AppCompatActivity {

    private static final String CUSTOM_VISION_ENDPOINT = "YOUR_CUSTOM_VISION_ENDPOINT";
    private static final String API_KEY = "YOUR_API_KEY";

    private ImageView imageView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        imageView = findViewById(R.id.image_view);
        //progressBar = findViewById(R.id.progress_bar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            byte[] imageByteArray = extras.getByteArray("imageByteArray");
            if (imageByteArray != null) {
                // バイト配列からBitmap画像を作成し、ImageViewに表示
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
                imageView.setImageBitmap(imageBitmap);
                // Custom Vision APIリクエストを非同期で送信
                new CustomVisionTask().execute(imageByteArray);
            }
        }
    }

    private class CustomVisionTask extends AsyncTask<byte[], Void, String> {
       //@Override
        //protected void onPreExecute() {
            //super.onPreExecute();
            // プログレスバーを表示
            //progressBar.setVisibility(View.VISIBLE);
        //}

        @Override
        protected String doInBackground(byte[]... params) {
            byte[] imageByteArray = params[0];
            OkHttpClient client = new OkHttpClient();

            // HTTPリクエストを構築
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", "image.jpg", RequestBody.create(MediaType.parse("image/jpeg"), imageByteArray))
                    .build();

            Request request = new Request.Builder()
                    .url(CUSTOM_VISION_ENDPOINT)
                    .addHeader("Prediction-Key", API_KEY)
                    .post(requestBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // プログレスバーを非表示
            //progressBar.setVisibility(View.GONE);

            if (result != null) {
                // Custom Vision APIからの応答を処理し、結果を表示するための適切な処理を追加



            } else {
                // エラーが発生した場合の処理
            }
        }
    }
}

