package com.example.yawheading;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MyView extends View {


    private Point mPoint = new Point(0,0);
    private List<Point> historyPoints = new ArrayList<Point>();


    public MyView(Context context){
        super(context);

    }
    public void onDraw(Canvas canvas) {

        Paint paint = new Paint();

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        Bitmap img = BitmapFactory.decodeResource(getResources(),R.drawable.map3);
        Bitmap resize_bitmap = Bitmap.createScaledBitmap(img,width,height,true);


        canvas.drawBitmap(resize_bitmap,0,0,null);


        paint.setColor(Color.RED);
        paint.setStrokeWidth(20f);

        for (Point p : historyPoints){
            canvas.drawPoint(p.x, p.y, paint);
        }

        // canvas.drawColor(Color.WHITE);
        //canvas.drawPoint(400 + (float) MainActivity.x, 400 + (float) MainActivity.y, paint);
        /*if (MainActivity.cnt != MainActivity.changecnt) {
            canvas.drawPoint(400 + (float) MainActivity.x, 400 + (float) MainActivity.y, paint);

            Log.d("cntvalue값", String.valueOf(MainActivity.cnt));
            Log.d("changecnt값", String.valueOf(MainActivity.changecnt));
        }*/
    }
    public void drawPoint(int x, int y){
        //mPoint.x = x;
        //mPoint.y = y;
        historyPoints.add(new Point(x,y));
        invalidate();
    }


}


/*
    public void onDraw(Canvas canvas) {
                // Bitmap bitmap = Bitmap.createBitmap(800,800,Bitmap.Config.ARGB_8888);
                // canvas = new Canvas(bitmap);
                // 비트맵 표시 canvas.drawColor(Color.BLACK);
        Bitmap img = BitmapFactory.decodeResource(getResources(),R.drawable.fruit);
        canvas.drawBitmap(img,0,0,null);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(30f);
        // canvas.drawColor(Color.WHITE);
        canvas.drawPoint(400, 400,paint);
    }*/
/*
    @Override
    protected void onCreate(Bundle savedInstancaState){
        super.onCreate(savedInstancaState);

        MyView myView = new MyView(this);
        setContentView(myView);
    }*/