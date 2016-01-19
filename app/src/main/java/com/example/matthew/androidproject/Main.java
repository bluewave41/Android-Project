package com.example.matthew.androidproject;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

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
        int width = view1.getWidth();
        int height = view1.getHeight();
        Drawable m2 = view2.getBackground();
        Bitmap bitmap2 = ((BitmapDrawable)m2).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        m2 = view1.getBackground();
        Bitmap bitmap = ((BitmapDrawable)m2).getBitmap();
        for(int x=0;x<width;x++) {
            for(int y=0;y<height;y++) {
                Line dest = view2.lines.get(0);
                Line src = view1.lines.get(0);
                double ptx = dest.startX-x;
                double pty = dest.startY-y;
                double pqx = dest.stopX-dest.startX;
                double pqy = dest.stopY-dest.startY;
                double nx = dest.yLength*-1;
                double ny = dest.xLength;

                double d = ((nx*ptx)+(ny*pty))/((Math.sqrt(nx*nx+ny*ny)));
                double fp = ((pqx*-ptx)+(pqy*-pty));
                fp = fp/(Math.sqrt(pqx*pqx+pqy*pqy));
                fp = fp/(Math.sqrt(pqx*pqx+pqy*pqy));

                ptx = x-src.startX;
                pty = y-src.startY;
                nx = src.yLength*-1;
                ny = src.xLength;

                double newX = ((src.startX)+(fp*src.xLength))-((d*nx/(Math.sqrt(nx*nx+ny*ny))));
                double newY = ((src.startY)+(fp*src.yLength))-((d*ny/(Math.sqrt(nx*nx+ny*ny))));
                if(newY>=400)
                    newY = 399;
                if(newX>=400)
                    newX = 399;

                bitmap2.setPixel(x, y, bitmap.getPixel((int)Math.abs(newX), (int)Math.abs(newY)));
            }
        }
        m2 = new BitmapDrawable(getResources(), bitmap2);
        view2.setBackground(m2);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}

