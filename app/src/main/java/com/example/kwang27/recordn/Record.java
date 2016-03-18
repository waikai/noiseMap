package com.example.kwang27.recordn;

import android.media.MediaRecorder;
import android.nfc.Tag;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
public class Record extends AppCompatActivity {
    private Button startButton;
    private MediaRecorder recorder;
    private File myFile;
    private TextView mytext;
    private int max;
    private int count;
    private long startTime;
    private long endTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        startButton = (Button) findViewById(R.id.recordButton);
//        recorder = new MediaRecorder();
        mytext = (TextView) findViewById(R.id.textView);


        View.OnLongClickListener rec = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {


                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                    Toast.makeText(getApplicationContext(), "media file can find", Toast.LENGTH_LONG).show();
                    return true;
                }
                try{
                    Random rand = new Random();
                    int n = rand.nextInt(4000);
                    String s = "" + n;
                    myFile = File.createTempFile(rand.nextInt(4000)+"a",".amr", Environment.getExternalStorageDirectory());
                    Toast.makeText(getApplicationContext(), "Recording....", Toast.LENGTH_SHORT).show();
                    recorder = new MediaRecorder();
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                    recorder.setOutputFile(myFile.getAbsolutePath());
                    recorder.setMaxDuration(5000);
                    recorder.prepare();
                    max = 0;
                    count = 0;
                    recorder.start();

                    startTime = System.currentTimeMillis();
                    double db =  updateMicStatus();
                    int c = (int) db;
                    mytext.setText(""+max);


                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        };
        startButton.setOnLongClickListener(rec);
//
        View.OnTouchListener endRec = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Toast.makeText(getApplicationContext(), "Record End", Toast.LENGTH_SHORT).show();
                    mytext.setText("" +max/count);
                    if (myFile != null) {
                        endTime = System.currentTimeMillis();

                        Toast.makeText(getApplicationContext(), "Record succ", Toast.LENGTH_LONG).show();
                        recorder.stop();
                        recorder.reset();
                        recorder.release();
//                        recorder = new MediaRecorder();
                        recorder = null;
                    }
                    return true;
                }
                return false;
            }
        };

        startButton.setOnTouchListener(endRec);
        

    }

    private int BASE = 1;
    private int SPACE = 100;

    private  double updateMicStatus() {
        if (recorder != null) {
            double ratio = (double)recorder.getMaxAmplitude()/BASE;
            double db = 0;
            if (ratio > 1) db = 20*Math.log10(ratio);
            max += (int) db;
            count++;
            Log.d("tag", "fvb" + max);
            mHander.postDelayed(mUpdateMicStatusTimer, SPACE);
            return db;
        }
        return 0;

    }
    private final android.os.Handler mHander = new android.os.Handler();
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        @Override
        public void run() {
            updateMicStatus();
        }
    };



}
