package com.example.evitected.speechcamera;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.AlarmClock;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> result;
    private TextView tvSpeechText;
    private ImageView ivCamera, ivMicrophone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindWidget();
        speech();
        eventTap();
    }

    private void eventTap() {
        ivMicrophone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                speech();
                return false;
            }
        });
    }

    private void bindWidget() {
        tvSpeechText = (TextView) findViewById(R.id.tvSpeechText);
        ivCamera = (ImageView) findViewById(R.id.ivCamera);
        ivMicrophone = (ImageView) findViewById(R.id.ivMicrophone);
    }

    private void speech() {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something...");
        try{
            startActivityForResult(i, 1);
        }catch (ActivityNotFoundException e){
            Toast.makeText(MainActivity.this, "Speech is not supported", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case 1:
                if(resultCode == RESULT_OK && null != data){
                    result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    tvSpeechText.setText(result.get(0));
                    if(result.get(0).contains("open camera")){
                        try{
                            openCamera();
                        }catch (Exception e){
                            Toast.makeText(MainActivity.this, "Camera is not supported.", Toast.LENGTH_SHORT).show();
                        }
                    }else if(result.get(0).contains("open alarm")){
                        try{
                            openAlarm();
                        }catch (Exception e){
                            Toast.makeText(MainActivity.this, "Alarm is not supported.", Toast.LENGTH_SHORT).show();
                        }
                    }else if(result.get(0).contains("open phone")){
                        try{
                            openPhone();
                        }catch (Exception e){
                            Toast.makeText(MainActivity.this, "Phone is not supported.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            case 2:
                Bitmap bp = (Bitmap) data.getExtras().get("data");
                ivCamera.setImageBitmap(bp);
        }
    }

    private void openCamera() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, 2);
    }
    public void openAlarm(){
        Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
        startActivity(i);
    }



    //Create Alarm Fix by Voice
    public void createAlarm(String message, int hour, int minutes){
        Intent i = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_MESSAGE, message)
                .putExtra(AlarmClock.EXTRA_HOUR, hour)
                .putExtra(AlarmClock.EXTRA_MINUTES, minutes);
        if(i.resolveActivity(getPackageManager()) != null){
            startActivity(i);
        }
    }
    public void openPhone(){
        Intent i = new Intent(Intent.ACTION_DIAL);
        startActivity(i);
    }

    //Calling Phonenumber fix by voice
    public void dialPhoneNumber(String phoneNumber){
        Intent i = new Intent(Intent.ACTION_DIAL);
        i.setData(Uri.parse("tel: "+ phoneNumber));
        if(i.resolveActivity(getPackageManager()) != null){
            startActivity(i);
        }
    }
}
