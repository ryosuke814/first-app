package com.websarva.wings.android.cameraintentsar;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;



public class SubActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        ImageView imageView = findViewById(R.id.image_view);
        TextView textView = findViewById(R.id.text_view);

        //結果の表示
        textView.setText("test");



        // MainActivity から写真データを受け取る
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Bitmap imageBitmap = (Bitmap) extras.get("imageBitmap");
            if (imageBitmap != null) {
                imageView.setImageBitmap(imageBitmap);
            }
        }



    }
}