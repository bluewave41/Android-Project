package com.example.matthew.androidproject;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class Main extends AppCompatActivity {

    CanvasView view1;
    CanvasView view2;
    boolean image = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Project");
        view1 = (CanvasView)findViewById(R.id.view);
        view2 = (CanvasView)findViewById(R.id.view2);
        view1.setTwin(view2);
        view2.setTwin(view1);
    }

    public void edit(View view) {
        view1.edit = !view1.edit;
        System.out.print(view1.getHeight());
        //view2.lines.add(new Line(5, 16, 1, 20));
        //view2.lines.add(new Line(5, 30, 15, 35));
        //view1.lines.add(new Line(1, 40, 5, 1));
        //view1.lines.add(new Line(8, 1, 40, 40));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
        public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.change) {
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("*/*");
            Intent c = Intent.createChooser(chooseFile, "Choose file");
            startActivityForResult(c, 1);
        }
        else if (id == R.id.change2) {
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("*/*");
            Intent c = Intent.createChooser(chooseFile, "Choose file");
            startActivityForResult(c, 1);
            image = true;
        }
        else if(id == R.id.clear) {
            view1.clear();
        }
        else if(id == R.id.undo) {
            view1.undo();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                final Uri map = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(map);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                BitmapDrawable draw = new BitmapDrawable(null, selectedImage);
                if(!image)
                    view1.setBackground(draw);
                else
                    view2.setBackground(draw);
                image = false;
            }
            catch(FileNotFoundException e) {

            }
        }
    }

    public void morph(View view) {
        Drawable view1Immutable = view1.getBackground();
        Bitmap blank = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        Bitmap view1Background = ((BitmapDrawable)view1Immutable).getBitmap();
        view1Background = Bitmap.createScaledBitmap(view1Background, 400, 400, false);
        double newX, newY;
        int width = view1.getWidth();
        int height = view1.getHeight();
        for(int x=10;x<width;x++) { //for each x pixel
            for(int y=10;y<height;y++) { //for each y pixel
                double totalWeight = 0;
                double xDisplacement = 0;
                double yDisplacement = 0;
                for(int l=0;l<view1.lines.size();l++) { //for each line
                    Line dest = view2.lines.get(l);
                    Line src = view1.lines.get(l);
                    double ptx = dest.startX - x;
                    double pty = dest.startY - y;
                    double pqx = dest.stopX - dest.startX;
                    double pqy = dest.stopY - dest.startY;
                    double nx = dest.yLength * -1;
                    double ny = dest.xLength;

                    double d = ((nx * ptx) + (ny * pty)) / ((Math.sqrt(nx * nx + ny * ny)));
                    double fp = ((pqx * (ptx * -1)) + (pqy * (pty * -1)));
                    fp = fp / (Math.sqrt(pqx * pqx + pqy * pqy));
                    fp = fp / (Math.sqrt(pqx * pqx + pqy * pqy));

                    ptx = x - src.startX;
                    pty = y - src.startY;
                    nx = src.yLength * -1;
                    ny = src.xLength;

                    newX = ((src.startX) + (fp * src.xLength)) - ((d * nx / (Math.sqrt(nx * nx + ny * ny))));
                    newY = ((src.startY) + (fp * src.yLength)) - ((d * ny / (Math.sqrt(nx * nx + ny * ny))));

                    double weight = (1/(0.01+Math.abs(d)));
                    totalWeight += weight;
                    xDisplacement += (newX-x)*weight;
                    yDisplacement += (newY-y)*weight;
                }

                newX = x+(xDisplacement/totalWeight);
                newY = y+(yDisplacement/totalWeight);

                if(xDisplacement == x-newX)
                    newX = x-xDisplacement;

                if(yDisplacement == y-newY)
                    newY = y-yDisplacement;

                if(newX<0)
                    newX = 0;
                if(newY<0)
                    newY = 0;
                if(newY>=400)
                    newY = 399;
                if(newX>=400)
                    newX = 399;

                //if (newX != -1 && newY != -1)
                    blank.setPixel(x, y, view1Background.getPixel((int)Math.abs(newX), (int)Math.abs(newY)));
            }
        }
        view1Immutable = new BitmapDrawable(getResources(), blank);
        view2.setBackground(view1Immutable);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}

