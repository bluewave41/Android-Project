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
import java.util.ArrayList;

public class Main extends AppCompatActivity {

    CanvasView view1;
    CanvasView view2;
    boolean image = false;
    int frameCount = 5;
    ArrayList<Drawable> pictures = new ArrayList<Drawable>();
    int index = 4; //sketchy ass shit sorry dennis

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

    public void hello(View view) {
        view1.setBackground(pictures.get(index--)); //this shit is backwards ok? dont ask plz
    }

    public ArrayList<Drawable> createBitmap(CanvasView src, ArrayList<Difference> difference) {
        Bitmap blank;
        Drawable view1Immutable = src.getBackground(); //get both backgrounds
        Bitmap view1Background = ((BitmapDrawable) view1Immutable).getBitmap();
        ArrayList<Drawable> frames = new ArrayList<>();

        view1Background = Bitmap.createScaledBitmap(view1Background, 400, 400, false);
        double newX, newY;
        int width = view1.getWidth();
        int height = view1.getHeight();
        for (int i = 1; i <= frameCount; i++) { //for each frame
            ArrayList<Line> temp = new ArrayList();
            blank = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
            for (int q = 0; q < view1.lines.size(); q++) {
                Line srcLine = src.lines.get(q);
                Difference d = difference.get(q);
                Line l = new Line(srcLine.startX + Math.round(d.X1 * i), srcLine.startY + Math.round(d.Y1 * i), srcLine.stopX + Math.round(d.X2 * i), srcLine.stopY + Math.round(d.Y2 * i));
                temp.add(l);
            }

            for (int x = 0; x < width; x++) { //for each x pixel
                for (int y = 0; y < height; y++) { //for each y pixel
                    double totalWeight = 0;
                    double xDisplacement = 0;
                    double yDisplacement = 0;
                    for (int l = 0; l < view1.lines.size(); l++) { //for each line
                        Line dest = view2.lines.get(l);
                        Line srcLine = temp.get(l);
                        double ptx = dest.startX - x;
                        double pty = dest.startY - y;
                        double pqx = dest.stopX - dest.startX;
                        double pqy = dest.stopY - dest.startY;
                        double nx = dest.yLength * -1;
                        double ny = dest.xLength;

                        double d = ((nx * ptx) + (ny * pty)) / ((Math.sqrt(nx * nx + ny * ny))); //clean up all this shit since half of it probably isn't needed
                        double fp = ((pqx * (ptx * -1)) + (pqy * (pty * -1)));
                        fp = fp / (Math.sqrt(pqx * pqx + pqy * pqy));
                        fp = fp / (Math.sqrt(pqx * pqx + pqy * pqy));

                        nx = srcLine.yLength * -1;
                        ny = srcLine.xLength;

                        newX = ((srcLine.startX) + (fp * srcLine.xLength)) - ((d * nx / (Math.sqrt(nx * nx + ny * ny))));
                        newY = ((srcLine.startY) + (fp * srcLine.yLength)) - ((d * ny / (Math.sqrt(nx * nx + ny * ny))));

                        double weight = (1 / (0.01 + Math.abs(d)));
                        totalWeight += weight;
                        xDisplacement += (newX - x) * weight;
                        yDisplacement += (newY - y) * weight;
                    }

                    newX = x + (xDisplacement / totalWeight);
                    newY = y + (yDisplacement / totalWeight);

                    if (xDisplacement == x - newX)
                        newX = x - xDisplacement;

                    if (yDisplacement == y - newY)
                        newY = y - yDisplacement;

                    if (newX < 0)
                        newX = 0;
                    if (newY < 0)
                        newY = 0;
                    if (newY >= 400)
                        newY = 399;
                    if (newX >= 400)
                        newX = 399;

                    //if (newX != -1 && newY != -1)
                    blank.setPixel(x, y, view1Background.getPixel((int) Math.abs(newX), (int) Math.abs(newY)));
                }
            }
            view1Immutable = new BitmapDrawable(getResources(), blank);
            frames.add(view1Immutable);
        }
        return frames;
    }

    public void crossDisolve(ArrayList<Drawable> frames1, ArrayList<Drawable> frames2) {
        int width = view1.getWidth();
        int height = view1.getHeight();
        int factor1 = 1;
        int factor2 = frameCount-factor1;
        for(int i=0; i <frames1.size();i++) {
            Drawable image1 = frames1.get(i);
            Drawable image2 = frames2.get(i);
            Bitmap view1Background = ((BitmapDrawable) image1).getBitmap();
            Bitmap view2Background = ((BitmapDrawable) image2).getBitmap();
            Bitmap blank = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
            for(int x=0;x<width;x++) {
                for(int y=0;y<height;y++) {
                    int pixel1 = view1Background.getPixel(x, y);
                    int pixel2 = view2Background.getPixel(x, y);
                    blank.setPixel(x, y, ((pixel1*(factor1/frameCount))+(pixel2*(factor2/frameCount))));
                }
            }
            image1 = new BitmapDrawable(getResources(), blank);
            pictures.add(image1);
            factor1++;
            factor2 = frameCount-factor1;
        }
    }

    public void morph(View view) {
        ArrayList<Difference> difference = new ArrayList();
        ArrayList<Difference> difference2 = new ArrayList();
        ArrayList<Drawable> frames1, frames2;

        for(int i=0;i<view1.lines.size();i++) {
            Line dest = view2.lines.get(i);
            Line src = view1.lines.get(i);
            Difference d = new Difference((dest.startX-src.startX)/(frameCount+1), (dest.stopX-src.stopX)/(frameCount+1), (dest.startY-src.startY)/(frameCount+1), (dest.stopY-src.stopY)/(frameCount+1));
            difference.add(d);
            d = new Difference((src.startX-dest.startX)/(frameCount+1), (src.stopX-dest.stopX)/(frameCount+1), (src.startY-dest.startY)/(frameCount+1), (src.stopY-dest.stopY)/(frameCount+1));
            difference2.add(d);
        }

        frames1 = createBitmap(view1, difference);
        frames2 = createBitmap(view2, difference2);
        //pictures = frames1;
        crossDisolve(frames1, frames2);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}

