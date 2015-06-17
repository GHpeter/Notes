package com.peter.notes;

import android.app.Activity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * 用于查看视频的activity
 * Created by 回忆 on 2015/2/6 0006.
 */
public class VideoViewerActivity extends Activity {
    public static String EXTRA_PATH = "path";
    private VideoView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new VideoView(this);
        view.setMediaController(new MediaController(this));
        setContentView(view);
        String path = getIntent().getStringExtra(EXTRA_PATH);
        if (path != null) {
            view.setVideoPath(path);
        } else {
            finish();
        }


    }
}
