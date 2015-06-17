package com.peter.notes;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.peter.db.NotesDB;


public class NotesActivity extends ListActivity {
    private SimpleCursorAdapter adapter = null;
    private NotesDB db;
    private SQLiteDatabase dbRead;

    public static final int REQUEST_CODE_ADD_NOTE = 1;
    public static final int REQUEST_CODE_EDIT_NOTE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        db = new NotesDB(this);
        dbRead = db.getReadableDatabase();

        adapter = new SimpleCursorAdapter(this, R.layout.notes_list_cell, null, new String[]{NotesDB.COLUMN_NAME_NOTES_NAME, NotesDB.COLUMN_NAME_NOTES_DATE},
                new int[]{R.id.tv_Name, R.id.tv_Date});
        setListAdapter(adapter);
        refreshNotesListView();
        findViewById(R.id.btn_AddNotes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(NotesActivity.this, EditNotesActivity.class), REQUEST_CODE_ADD_NOTE);
            }
        });
    }

    public void refreshNotesListView() {
        adapter.changeCursor(dbRead.query(NotesDB.TABLE_NAME_NOTES, null, null, null, null,
                null, null));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Cursor c = adapter.getCursor();
        c.moveToPosition(position);
        Intent i = new Intent(NotesActivity.this, EditNotesActivity.class);
        i.putExtra(EditNotesActivity.EXTRA_NOTE_ID, c.getInt(c.getColumnIndex(NotesDB.COLUMN_NAME_ID)));
        i.putExtra(EditNotesActivity.EXTRA_NOTE_NAME, c.getInt(c.getColumnIndex(NotesDB.COLUMN_NAME_NOTES_NAME)));
        i.putExtra(EditNotesActivity.EXTRA_NOTE_CONTENT, c.getInt(c.getColumnIndex(NotesDB.COLUMN_NAME_NOTES_CONTENT)));
        startActivityForResult(i, REQUEST_CODE_EDIT_NOTE);

        super.onListItemClick(l, v, position, id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_ADD_NOTE:
            case REQUEST_CODE_EDIT_NOTE:
                if (requestCode == Activity.RESULT_OK) {
                    refreshNotesListView();
                }
                break;
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
