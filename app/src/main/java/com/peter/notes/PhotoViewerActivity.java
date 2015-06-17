package com.peter.notes;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;
import java.net.URI;
import java.net.URL;

/**
 * 用于查看照片的activity
 * Created by 回忆 on 2015/2/6 0006.
 */
public class PhotoViewerActivity extends Activity {
    public static String EXTRA_PATH = "path";
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageView = new ImageView(this);
        setContentView(imageView);

        String path = getIntent().getStringExtra(EXTRA_PATH);
        if (path != null) {
            imageView.setImageURI(Uri.fromFile(new File(path)));
        } else {
            finish();
        }
    }
}
