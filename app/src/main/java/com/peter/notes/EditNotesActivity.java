package com.peter.notes;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.peter.db.NotesDB;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 回忆 on 2015/2/6 0006.
 */
public class EditNotesActivity extends ListActivity implements View.OnClickListener {
    private int noteId = -1;
    public static final String EXTRA_NOTE_ID = "noteId";
    public static final String EXTRA_NOTE_NAME = "noteName";
    public static final String EXTRA_NOTE_CONTENT = "noteContent";

    public static final int REQUEST_CODE_GET_PHOTO = 1;


    public static final int REQUEST_CODE_GET_VIDEO = 2;
    private EditText et_name, et_content;


    private Button btn_save, btn_cancle, btn_addPhoto, btn_addVideo;
    private MediaAdapter adapter;
    private NotesDB db;

    private SQLiteDatabase dbRead, dbWrite;
    private String currentPath = null;
    Intent i;

    File file;

    static class MediaType {
        static final int PHOTO = 1;
        static final int VIDEO = 2;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_Save:
                //保存日志   ----->保存日志相关的媒体
                saveMedia(saveNote());
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.btn_Cancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.btn_AddPhoto:
                i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                file = new File(getMediaDir(), System.currentTimeMillis() + ".jpg");
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                startActivityForResult(i, REQUEST_CODE_GET_PHOTO);
                i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                break;
            case R.id.btn_AddVideo:
                i = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                file = new File(getMediaDir(), System.currentTimeMillis() + ".mp4");
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                startActivityForResult(i, REQUEST_CODE_GET_VIDEO);
                i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                break;
            default:
                break;
        }

    }

    /**
     * 创建一个目录存放所有的媒体文件
     *
     * @return
     */
    public File getMediaDir() {
        File dir = new File(
                Environment.getExternalStorageDirectory(),
                "NotesMedia");
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir;
    }

    public int saveNote() {
        ContentValues values = new ContentValues();
        values.put(NotesDB.COLUMN_NAME_NOTES_NAME, et_name.getText().toString());
        values.put(NotesDB.COLUMN_NAME_NOTES_CONTENT, et_content.getText().toString());
        values.put(NotesDB.COLUMN_NAME_NOTES_DATE,
                new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        if (noteId > -1) {
            dbWrite.update(NotesDB.TABLE_NAME_NOTES, values, NotesDB.COLUMN_NAME_ID + "=?",
                    new String[]{noteId + ""});
            return noteId;
        } else {
            return (int) dbWrite.insert(NotesDB.TABLE_NAME_NOTES, null, values);
        }
    }

    public void saveMedia(int noteId) {
        MediaListCellData data;
        ContentValues values;
        for (int i = 0; i < adapter.getCount(); i++) {
            data = adapter.getItem(i);
            if (data.id <= -1) {
                values = new ContentValues();
                values.put(NotesDB.COLUMN_NAME_MEDIA_PATH, data.path);
                values.put(NotesDB.COLUMN_NAME_MEDIA_OWNER_NOTES_ID, noteId);
                dbWrite.insert(NotesDB.TABLE_NAME_MEDIA, null, values);
            }
        }
    }

    static class MediaListCellData {
        int id = -1;
        int type = 0;
        String path = "";
        int iconID = R.drawable.ic_launcher;

        public MediaListCellData(String path) {
            this.path = path;
            if (path.endsWith(".jpg")) {
                iconID = R.drawable.icon_photo;
                type = MediaType.PHOTO;

            } else if (path.endsWith(".mp4")) {
                iconID = R.drawable.icon_video;
                type = MediaType.VIDEO;
            }

        }

        public MediaListCellData(String path, int id) {
            this(path);
            this.id = id;
        }

    }

    static class MediaAdapter extends BaseAdapter {
        private Context context;
        private List<MediaListCellData> list = new ArrayList<MediaListCellData>();

        public void add(MediaListCellData data) {
            list.add(data);
        }

        public MediaAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public MediaListCellData getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            viewHolder holder;

            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.media_list_cell, null);
                holder = new viewHolder();
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_Icon);
                holder.tv_path = (TextView) convertView.findViewById(R.id.tv_Path);
                convertView.setTag(holder);

            } else {
                holder = (viewHolder) convertView.getTag();
                MediaListCellData data = getItem(position);
                if (data != null) {
                    holder.iv_icon.setImageResource(data.iconID);
                    holder.tv_path.setText(data.path);
                }
            }


            return convertView;
        }

        static final class viewHolder {
            ImageView iv_icon;
            TextView tv_path;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_eidt_note);
        initViews();
        db = new NotesDB(this);
        dbRead = db.getReadableDatabase();
        dbWrite = db.getWritableDatabase();
        adapter = new MediaAdapter(this);
        setListAdapter(adapter);

        noteId = getIntent().getIntExtra(EXTRA_NOTE_ID, -1);
        if (noteId > -1) {
            //修改日志操作
            et_name.setText(getIntent().getStringExtra(EXTRA_NOTE_NAME));
            et_content.setText(getIntent().getStringExtra(EXTRA_NOTE_CONTENT));
            Cursor cursor = dbRead.query(NotesDB.TABLE_NAME_MEDIA, null, NotesDB.COLUMN_NAME_MEDIA_OWNER_NOTES_ID + "=?",
                    new String[]{noteId + ""}, null, null, null);
            while (cursor.moveToNext()) {
                adapter.add(new MediaListCellData(cursor.getString(cursor.getColumnIndex(NotesDB.COLUMN_NAME_MEDIA_PATH)),
                        cursor.getInt(cursor.getColumnIndex(NotesDB.COLUMN_NAME_ID))));
            }
            adapter.notifyDataSetChanged();

        }
        btn_save.setOnClickListener(this);
        btn_cancle.setOnClickListener(this);
        btn_addVideo.setOnClickListener(this);
        btn_addPhoto.setOnClickListener(this);


    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        MediaListCellData data = adapter.getItem(position);
        Intent i ;
        switch (data.type){
            case MediaType.PHOTO:
                i=new Intent(this,PhotoViewerActivity.class);
                i.putExtra(PhotoViewerActivity.EXTRA_PATH,data.path);
                startActivity(i);
                break;
            case MediaType.VIDEO:
                i=new Intent(this,VideoViewerActivity.class);
                i.putExtra(VideoViewerActivity.EXTRA_PATH,data.path);
                startActivity(i);
                break;
        }

        super.onListItemClick(l, v, position, id);
    }

    @Override
    protected void onDestroy() {
        dbRead.close();
        dbWrite.close();
        super.onDestroy();
    }

    private void initViews() {
        et_content = (EditText) findViewById(R.id.et_Content);
        et_name = (EditText) findViewById(R.id.et_Name);
        btn_save = (Button) findViewById(R.id.btn_Save);
        btn_cancle = (Button) findViewById(R.id.btn_Cancel);
        btn_addPhoto = (Button) findViewById(R.id.btn_AddPhoto);
        btn_addVideo = (Button) findViewById(R.id.btn_AddVideo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_GET_PHOTO:
            case REQUEST_CODE_GET_VIDEO:
                if (requestCode == RESULT_OK) {
                    adapter.add(new MediaListCellData(currentPath));
                    adapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }


        super.onActivityResult(requestCode, resultCode, data);
    }
}
