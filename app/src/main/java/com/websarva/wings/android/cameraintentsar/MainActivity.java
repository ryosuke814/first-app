package com.websarva.wings.android.cameraintentsar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private EditText[] timeInputs = new EditText[7];
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // EditTextフィールドを取得
        timeInputs[0] = findViewById(R.id.time1);
        timeInputs[1] = findViewById(R.id.time2);
        timeInputs[2] = findViewById(R.id.time3);
        timeInputs[3] = findViewById(R.id.time4);
        timeInputs[4] = findViewById(R.id.time5);
        timeInputs[5] = findViewById(R.id.time6);
        timeInputs[6] = findViewById(R.id.time7);

        Button cameraButton = findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                if (imageBitmap != null) {


                    File directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    File imageFile = new File(directory, "my_image.jpg");
                    String imagePath = imageFile.getAbsolutePath();
                    try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //timeInputsから入力されたデータを取得
                    String[] timeData = new String[7];
                    for (int i = 0; i < 7; i++) {
                        timeData[i] = timeInputs[i].getText().toString();
                    }

                    Intent subIntent = new Intent(this, SubActivity.class);
                    subIntent.putExtra("imageFilePath", imagePath); // 画像ファイルのパス
                    subIntent.putExtra("timeData", timeData); // 入力データ
                    startActivity(subIntent);
                }
            }
        }
    }

    private Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}