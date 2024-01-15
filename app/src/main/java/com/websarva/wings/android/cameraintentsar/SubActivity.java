package com.websarva.wings.android.cameraintentsar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;




public class SubActivity extends AppCompatActivity {

    private static final String API_ENDPOINT = "https://southcentralus.api.cognitive.microsoft.com/customvision/v3.0/Prediction/6a018518-b3d4-4e99-a45d-8c15034907f4/classify/iterations/Iteration8/image";
    private static final String API_KEY = "2a8acc18b30b4ae2b3f067ef68eed1da";
    private double[] hour = new double[7];



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        TextView[] table = new TextView[7];

        ImageView imageView = findViewById(R.id.image_view);
        table[0] = findViewById(R.id.textView21);
        table[1] = findViewById(R.id.textView22);
        table[2] = findViewById(R.id.textView23);
        table[3] = findViewById(R.id.textView24);
        table[4] = findViewById(R.id.textView25);
        table[5] = findViewById(R.id.textView26);
        table[6] = findViewById(R.id.textView27);


        // MainActivity から画像ファイルのパスを受け取る
        String imagePath = getIntent().getStringExtra("imageFilePath");
        if (imagePath != null) {
            // ファイルパスからBitmapに変換して表示
            Bitmap imageBitmap = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(imageBitmap);

            // カスタムビジョンAPIにファイルパスを送信
            analyzeImage(imagePath);
        }

        // MainActivity からEditTextからのデータを受け取る
        String[] timeData = getIntent().getStringArrayExtra("timeData");
        if (timeData != null) {
            // timeDataを使用して処理を行う
            for(int i=0;i<7;i++){
                table[i].setText(timeData[i]);
            }

            for(int i=0;i<7;i++){
                hour[i] = Double.parseDouble(timeData[i]);
                if(hour[i]>11.59){
                    table[i].setBackgroundColor(0xFFff3838);
                }
                else if(hour[i]>7.76){
                    table[i].setBackgroundColor(0xFF60ff60);
                }
            }




        }
    }

    private void analyzeImage(String imagePath) {
        AnalyzeImageTask task = new AnalyzeImageTask();
        task.execute(imagePath);
    }

    private class AnalyzeImageTask extends AsyncTask<String, Void, Map<String, Double>> {
        @Override
        protected Map<String, Double> doInBackground(String... params) {
            String imagePath = params[0];
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/octet-stream");
            RequestBody body = RequestBody.create(mediaType, new File(imagePath));

            Request request = new Request.Builder()
                    .url(API_ENDPOINT)
                    .addHeader("Prediction-Key", API_KEY)
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();

                Map<String, Double> tagProbabilities = new HashMap<>();
                JsonElement jsonElement = JsonParser.parseString(responseBody);
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonArray predictions = jsonObject.getAsJsonArray("predictions");

                for (JsonElement prediction : predictions) {
                    JsonObject tag = prediction.getAsJsonObject();
                    String tagName = tag.get("tagName").getAsString();
                    double probability = tag.get("probability").getAsDouble();
                    tagProbabilities.put(tagName, probability);
                }

                return tagProbabilities;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Map<String, Double> tagProbabilities) {
            if (tagProbabilities != null) {
                boolean hasAcne = tagProbabilities.getOrDefault("ニキビあり", 0.0) > 0.5;
                boolean hasDarkCircles = tagProbabilities.getOrDefault("目の隈", 0.0) > 0.5;
                boolean hasRedEyes = tagProbabilities.getOrDefault("目の充血", 0.0) > 0.5;



                TextView textView = findViewById(R.id.text_view);
                String nikibi;
                String kuma;
                String juuketu;
                String a = "";
                String b = "";
                if(hasAcne||hasDarkCircles||hasRedEyes) {
                    if (hasAcne) {
                        nikibi = "ニキビ";
                    } else {
                        nikibi = "";
                    }

                    if (hasDarkCircles) {
                        kuma = "隈";
                    } else {
                        kuma = "";
                    }

                    if (hasRedEyes) {
                        juuketu = "目の充血";
                    } else {
                        juuketu = "";
                    }

                    if (hasAcne && hasDarkCircles) {
                        a = "と";
                    }

                    if ((hasAcne && hasRedEyes) || (hasDarkCircles && hasRedEyes)) {
                        b = "と";
                    }

                    textView.setText("あなたの顔には"+nikibi+a+kuma+b+juuketu+"が見られます");
                }

                else{
                    textView.setText("あなたの顔は健康です");
                }

                TextView textView2 = findViewById(R.id.text_view2);
                if((tagProbabilities.getOrDefault("ニキビあり", 0.0) > 0.7)&&
                        (tagProbabilities.getOrDefault("目の隈", 0.0) > 0.7)&&
                        (tagProbabilities.getOrDefault("目の充血", 0.0) > 0.7)){
                    textView2.setText("全体的に肌の状態が悪いようです。\n適切なスキンケアを行い、しっかりと睡眠を取ることが必要です。");
                }

                else if(tagProbabilities.getOrDefault("ニキビあり", 0.0) > 0.7){
                    textView2.setText("ニキビが特に目立つようです。\n適切なスキンケアを行いましょう。");
                }

                else if(tagProbabilities.getOrDefault("目の隈", 0.0) > 0.7){
                    textView2.setText("隈が特に目立つようです。\n十分な睡眠をとりましょう。");
                }

                else if(tagProbabilities.getOrDefault("目の充血", 0.0) > 0.7){
                    textView2.setText("目の充血が特に目立つようです。\n適度に目を休め、目の周りを冷やすと良いかもしれません。");
                }

                else if(!hasAcne&&!hasDarkCircles&&!hasRedEyes){
                    textView2.setText("肌は健康的で元気です。\nこのまま健康的な生活を送りましょう。");
                }

                else{
                    textView2.setText("目立って悪いところはありませんが良いとは言えません。十分に睡眠をとり、健康的な生活を送りましょう。");
                }


            } else {
                // エラー処理
                TextView textView = findViewById(R.id.text_view);
                textView.setText("エラー");
            }
        }


    }
}



