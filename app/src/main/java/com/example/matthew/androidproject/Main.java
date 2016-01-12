package com.example.matthew.androidproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Main extends AppCompatActivity {

    CanvasView view1;
    CanvasView view2;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.change) {
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("*/*");
            Intent c = Intent.createChooser(chooseFile, "Choose file");
            startActivityForResult(c, 1);
        }
        else if(id == R.id.clear) {
            view1.clear();
            view2.clear();
        }
        else if(id == R.id.undo) {
            if(view1.lines.size() > 0) {
                view1.lines.remove(view1.lines.size() - 1);
                view1.points.remove(view1.points.size() - 1);
            }
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
                view1.setBackground(draw);
            }
            catch(FileNotFoundException e) {

            }
        }
    }
}

