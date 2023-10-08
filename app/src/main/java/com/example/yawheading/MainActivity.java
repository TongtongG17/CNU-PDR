package com.example.yawheading;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
//import android.support.v7.app.AppCompatActivity;
// import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    /*Wizets*/
    private TextView tv_roll, tv_pitch, tv_yaw, tv_x, tv_y;

    /*Used for Accelometer & Gyroscoper*/
    private SensorManager mSensorManager = null;
    private UserSensorListner userSensorListner;
    private Sensor mGyroscopeSensor = null;
    private Sensor mAccelerometer = null;
    private Sensor cntAccelerometer = null;

    /*Sensor variables*/
    private float[] mGyroValues = new float[3];
    private float[] mAccValues = new float[3];
    private float[] cntValues = new float[3];
    private double mAccPitch, mAccRoll;

    /*for unsing complementary fliter*/
    private float a = 0.47f;
    // time constant/time constant+dt=a
    private double RAD2DGR = 180 / Math.PI;
    private static final float NS2S = 1.0f/1000000000.0f;
    private double pitch = 0, roll = 0, yaw = 0, pitch1=0, roll1=0,pitch2=0,roll2=0;
    private double g1=0,g2=0;
    private double timestamp;
    private double dt;
    private double temp;
    private double temp1;
    private double temp2;
    static boolean running;
    private boolean gyroRunning;
    private boolean accRunning;
    private boolean cntRunning;
    static double x=720,y=1120,heading=0;
    //time constant = 0.0175
//////////////////////////////////////////
    private double pi = Math.PI;
    public static int cnt = 0;
    public static int changecnt = 0;

    private long lastTime;
    private float prev_svm;
    private float svm1;
    private float x1, y1, z1;
    private float max;
    private float min;

    private Boolean currentSvm1; //현재 svm
    private Boolean prevSvm1; //이전 svm
    public static Boolean change = false;

    private char prev_area; //이전 svm
    private char current_area; //이전 svm

    private static final float ALPHA = 0.7f;
    private static final double UP_THRESHOLD = 11.35;
    private static final double DOWN_THRESHOLD = 9.22;

    public static long currentTime;
    public static long gabOfTime;

    private MyView customCanvas;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customCanvas = new MyView(this);
        setContentView(R.layout.activity_main);

        tv_roll = (TextView)findViewById(R.id.tv_roll);
        tv_pitch = (TextView)findViewById(R.id.tv_pitch);
        tv_yaw = (TextView)findViewById(R.id.tv_yaw);
        tv_x = (TextView)findViewById(R.id.tv_x);
        tv_y = (TextView)findViewById(R.id.tv_y);

        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        userSensorListner = new UserSensorListner();
        mGyroscopeSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mAccelerometer= mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        cntAccelerometer= mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        Button filterButton = (Button) findViewById(R.id.filter);
        filterButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /* 실행 중이지 않을 때 -> 실행 */
                if(!running){
                    setContentView(customCanvas);
                    /*
                    Intent intent = new Intent(getApplicationContext(),MyView.class);
                    startActivity(intent);*/
                    running = true;
                    mSensorManager.registerListener(userSensorListner, mGyroscopeSensor, 20000);
                    mSensorManager.registerListener(userSensorListner, mAccelerometer, 20000);
                    mSensorManager.registerListener(userSensorListner, cntAccelerometer, SensorManager.SENSOR_DELAY_GAME);

                    customCanvas.drawPoint((int)x,(int)y);
                }
                //SensorManager.SENSOR_DELAY_UI = 60000ms
                //20000 = 50Hz

                /* 실행 중일 때 -> 중지 */
                else if(running)
                {
                    running = false;
                    mSensorManager.unregisterListener(userSensorListner);
                }
            }
        });
    }

    /**
     * 1차 상보필터 적용 메서드 */

    private void complementaty(double new_ts){

        /* 자이로랑 가속 해제 */
        gyroRunning = false;
        accRunning = false;

        /*센서 값 첫 출력시 dt(=timestamp - event.timestamp)에 오차가 생기므로 처음엔 break */
        if(timestamp == 0){
            timestamp = new_ts;
            return;
        }
        dt = (new_ts - timestamp) * NS2S; // ns->s 변환
        timestamp = new_ts;

        /* degree measure for accelerometer */
        mAccPitch = -Math.atan2(mAccValues[0], mAccValues[2]) * 180.0 / Math.PI; // Y 축 기준
        mAccRoll= Math.atan2(mAccValues[1], mAccValues[2]) * 180.0 / Math.PI; // X 축 기준

        /**
         * 1st complementary filter.
         *  mGyroValuess : 각속도 성분.
         *  mAccPitch : 가속도계를 통해 얻어낸 회전각.
         */
        temp = (1/a) * (mAccPitch - pitch) + mGyroValues[1];
        pitch = pitch + (temp*dt);

        temp = (1/a) * (mAccRoll - roll) + mGyroValues[0];
        roll = roll + (temp*dt);

        temp = mGyroValues[2];
        yaw = yaw+(temp*dt);
        //
        //
        //lpf 가속도
        temp = a*(mAccPitch-pitch1);
        pitch1 = pitch1 +temp;

        temp = a*(mAccRoll-roll1);
        roll1 = roll1 +temp;

        //
        //
        //hpf 자이로
        temp = a*(pitch2+mGyroValues[1]);
        pitch2 = pitch2+(temp*dt);

        temp = a*(roll2+mGyroValues[0]);
        roll2 = roll2+(temp*dt);
        //
        //
        temp1 = mGyroValues[0];
        temp2 = mGyroValues[1];
        g1 = g1 +(temp1*dt);
        g2 = g2 +(temp2*dt);
        heading = yaw*RAD2DGR;

        tv_roll.setText("roll : "+roll);
        tv_pitch.setText("pitch : "+pitch);
        tv_yaw.setText("heading: "+heading);

        // Log.d(" ", "  " + String.format("%.4f", heading));

        ///////////////////////////////////////////////

        currentTime = System.currentTimeMillis();
        gabOfTime = (currentTime - lastTime);

        if (gabOfTime > 50) {

            x1 = cntValues[0];
            y1 = cntValues[1];
            z1 = cntValues[2];

            if(prev_svm > 10.17){
                prevSvm1 = true;
            } else {
                prevSvm1 = false;
            }

            if(prev_svm > UP_THRESHOLD){
                prev_area = 'A';
            }
            else if(prev_svm < UP_THRESHOLD && prev_svm > 10.17){
                prev_area = 'B';
            }
            else if(prev_svm < 10.17 && prev_svm > DOWN_THRESHOLD){
                prev_area = 'C';
            }
            else{
                prev_area = 'D';
            }


            svm1 = (float) (Math.sqrt((x1 * x1) + (y1 * y1) + (z1 * z1)));

            prev_svm = svm1;

            if(svm1 > 10.17){
                currentSvm1 = true;
            } else {
                currentSvm1 = false;
            }

            if(svm1 > UP_THRESHOLD){
                current_area = 'A';
            }
            else if(svm1 < UP_THRESHOLD && svm1 > 10.17){
                current_area = 'B';
            }
            else if(svm1 < 10.17 && svm1 > DOWN_THRESHOLD){
                current_area = 'C';
            }
            else{
                current_area = 'D';
            }

            if(cnt == changecnt){
                change = false;
            }
            else if (cnt != changecnt){
                change = true;
                changecnt++;
            }

            if( max > UP_THRESHOLD && min < DOWN_THRESHOLD){
                if( prevSvm1 == false && currentSvm1 == true ){
                    cnt++;
                    //tView.setText("Count:" + cnt);
                }
            }

            if (svm1 > UP_THRESHOLD) {
                // 기존의 최대값에 새로운 최대값을 대입한다
                max = svm1;

                //time1 = currentTime;
            }
            else if(svm1 < UP_THRESHOLD)
            {
                //tv_max.setText(String.valueOf(max));
                if (svm1 < DOWN_THRESHOLD) {
                    min = svm1;
                    //time2 = currentTime;

                }

                if((prev_area == 'A' && current_area == 'B') || (prev_area == 'A' && current_area == 'C'
                        || prev_area == 'B' && current_area == 'B') || (prev_area == 'B' && current_area == 'C')){
                    min = (float) 9.3;
                }
            }

            lastTime = currentTime;

            //Log.d("cntvalue값", String.valueOf(cnt));
            //Log.d("changecnt값", String.valueOf(changecnt));
            // Log.d("change트루", String.valueOf(change));
            // Log.d("svm값", String.valueOf(svm1));

        }
        if(change == true) {
            x = 6.86 * Math.sin(heading*pi/180) + x;
            y = 6.86 * Math.cos(heading*pi/180) + y;
        }
        /*
        if(x < 0){
            x = 0;
        }
        else if (x > 1440){
            x = 1440;
        }
        if(y < 0){
            y = 0;
        }
        else if (y > 2240){
            y = 2240;
        }*/

        tv_x.setText("x : "+x);
        tv_y.setText("y : "+y);

        customCanvas.drawPoint((int)x,(int)y);

        Log.d("x위치", String.valueOf(x));
        Log.d("y위치", String.valueOf(y));
        //Log.d("세타값", String.valueOf(heading));
    }


    public class UserSensorListner implements SensorEventListener{

        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {

                /** GYROSCOPE */
                case Sensor.TYPE_GYROSCOPE:

                    /*센서 값을 mGyroValues에 저장*/
                    mGyroValues = event.values;

                    if (!gyroRunning)
                        gyroRunning = true;

                    break;

                /** ACCELEROMETER */
                case Sensor.TYPE_ACCELEROMETER:


                    /*센서 값을 mAccValues에 저장*/
                    mAccValues = event.values;
                    cntValues = event.values;

                    cntValues = lowPass(cntValues , event.values.clone());

                    if (!accRunning)
                        accRunning = true;
                    cntRunning = true;
                    break;
            }

            /**두 센서 새로운 값을 받으면 상보필터 적용*/
            if (gyroRunning && accRunning) {
                complementaty(event.timestamp);
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) { }
    }

    protected float[] lowPass( float[] input, float[] output ) {
        if ( output == null ) return input;

        for ( int i = 0; i < input.length; i++ ) {
            output[i] = ALPHA * input[i] + (1 - ALPHA) * output[i]; //output[i] + ALPHA * (input[i] - output[i]);
        }
        //input <= Xk-1 output <= Xk
        return output;
    }

}