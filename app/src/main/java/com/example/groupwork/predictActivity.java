package com.example.groupwork;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.groupwork.ml.NewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class predictActivity extends AppCompatActivity {
    Button camera, gallery;
    TextView result,classified,des1;
    int imageSize = 128;
    String [] labels;
    ImageView imageView,footer1,footer2;
    BottomNavigationView bottomNavigationView;
    FloatingActionButton fab;
    LinearLayout lin;
    ScrollView scroll;
    RelativeLayout re;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predict);
        camera = findViewById(R.id.button);
        gallery = findViewById(R.id.button2);
        result = findViewById(R.id.result);
        imageView=findViewById(R.id.imageView);
        classified=findViewById(R.id.classified);
        des1=findViewById(R.id.description1);
        footer1=findViewById(R.id.foter);
        footer2=findViewById(R.id.foterr);
        bottomNavigationView=findViewById(R.id.bottomNavigationView);
        scroll=findViewById(R.id.scroll);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        labels=new String[10];

        int count=0;
         try{
             BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(getAssets().open("temple_labels.txt")));
             String line=bufferedReader.readLine();

             while(line != null){
                 labels[count]=line;
                 count++;
                 line=bufferedReader.readLine();
             }
         }
         catch(Exception e){
             e.printStackTrace();
        }


         //gets permission from phone and takes photo
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 3);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });

         //Uploads the image from gallery
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(cameraIntent, 1);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(predictActivity.this,
                        predictActivity.class);
                startActivity(i);
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.aboutUs:
                        startActivity(new Intent(getApplicationContext(), AboutusActivity3.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }

    //loads the model
    public void classifyImage(Bitmap image){
        try {
            NewModel model = NewModel.newInstance(getApplicationContext());

            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 128, 128, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            inputFeature0.loadBuffer(byteBuffer);

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            //iterate over each pixel and extract R, G, and B values. Add those values individually to the byte buffer.
            for(int i = 0; i < imageSize; i ++){
                for(int j = 0; j < imageSize; j++){
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255));
                }
            }


            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            NewModel.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();

            System.out.println("...................."+confidences);
            // find the index of the class with the biggest confidence.
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                //System.out.println(confidences[i]);
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            result.setText(labels[maxPos]);
            des1.setText(R.string.changangkha);
            switch(maxPos) {
                case 0:
                    des1.setText(R.string.changangkha);
                    break;

                case 1:
                    des1.setText(R.string.chimi);
                    break;

                case 2:
                    des1.setText(R.string.dechenphu);
                    break;

                case 3:
                    des1.setText(R.string.dema);
                    break;

                case 4:
                    des1.setText(R.string.pangrizampa);
                    break;

                case 5:
                    des1.setText(R.string.paro_taksang);
                    break;

                case 6:
                    des1.setText(R.string.milarepa);
                    break;

                case 7:
                    des1.setText(R.string.dechenphodrang);
                    break;

                case 8:
                    des1.setText(R.string.jampa);
                    break;

                case 9:
                    des1.setText(R.string.kurjey);
                    break;
            }

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        camera.setVisibility(View.INVISIBLE);
        gallery.setVisibility(View.INVISIBLE);
        footer1.setVisibility(View.INVISIBLE);
        footer2.setVisibility(View.INVISIBLE);

        //lin.setVisibility(View.VISIBLE);

        if(resultCode == RESULT_OK){
            if(requestCode == 3){
                Bitmap image = (Bitmap) data.getExtras().get("data");
                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                imageView.setImageBitmap(image);
                imageView.setVisibility(View.VISIBLE);
                classified.setVisibility(View.VISIBLE);
                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImage(image);
            }
            else
            {
                Uri dat = data.getData();
                Bitmap image = null;
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(image);
                imageView.setVisibility(View.VISIBLE);
                classified.setVisibility(View.VISIBLE);
                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImage(image);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}