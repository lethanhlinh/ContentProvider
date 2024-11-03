package com.example.contenprovider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.provider.MediaStore;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Map;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button btnShowAllContact;
    private Button btnAccessCallLog;
    private Button btnAccessMediaStore;
    private Button btnAccessBookmarks;
    private Button btnShowMassage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnShowMassage = findViewById(R.id.btnshowallmassage);
        btnShowMassage.setOnClickListener(this);
        btnShowAllContact = findViewById(R.id.btnshowallcontact);
        btnShowAllContact.setOnClickListener(this);
        btnAccessCallLog = findViewById(R.id.btnaccesscalllog);
        btnAccessCallLog.setOnClickListener(this);
        btnAccessMediaStore = findViewById(R.id.btnmediastore);
        btnAccessMediaStore.setOnClickListener(this);
        btnAccessBookmarks = findViewById(R.id.btnaccessbookmarks);
        btnAccessBookmarks.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == btnShowAllContact) {
            Intent intent = new Intent(this, ShowAllContactActivity.class);
            startActivity(intent);
        } else if (v == btnShowMassage) {
            Intent intent = new Intent(this, ShowMassageActivity.class);
            startActivity(intent);
        }else if (v == btnAccessCallLog) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, 1);
            } else {
                accessTheCallLog();
            }
        } else if (v == btnAccessMediaStore) {
            accessMediaStore();
        } else if (v == btnAccessBookmarks) {
            saveBookmark("Example Title", "https://example.com");
            getBookmarks();
        }
    }

    public void accessTheCallLog() {
        String[] projection = new String[]{
                CallLog.Calls.DATE,
                CallLog.Calls.NUMBER,
                CallLog.Calls.DURATION
        };
        Cursor c = getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                projection,
                CallLog.Calls.DURATION + "<?", new String[]{"30"},
                CallLog.Calls.DATE + " ASC");

        if (c != null) {
            StringBuilder s = new StringBuilder();
            while (c.moveToNext()) {
                for (int i = 0; i < c.getColumnCount(); i++) {
                    s.append(c.getString(i)).append(" - ");
                }
                s.append("\n");
            }
            Toast.makeText(this, s.toString(), Toast.LENGTH_LONG).show();
            c.close();
        }
    }

    public void accessMediaStore() {
        String[] projection = {
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.DATE_ADDED,
                MediaStore.MediaColumns.MIME_TYPE
        };
        Cursor c = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, null);

        if (c != null) {
            StringBuilder s = new StringBuilder();
            while (c.moveToNext()) {
                for (int i = 0; i < c.getColumnCount(); i++) {
                    s.append(c.getString(i)).append(" - ");
                }
                s.append("\n");
            }
            Toast.makeText(this, s.toString(), Toast.LENGTH_LONG).show();
            c.close();
        }
    }

    public void saveBookmark(String title, String url) {
        SharedPreferences sharedPref = getSharedPreferences("Bookmarks", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(title, url);
        editor.apply();
        Toast.makeText(this, "Bookmark saved!", Toast.LENGTH_SHORT).show();
    }

    public void getBookmarks() {
        SharedPreferences sharedPref = getSharedPreferences("Bookmarks", Context.MODE_PRIVATE);
        Map<String, ?> allBookmarks = sharedPref.getAll();

        StringBuilder bookmarks = new StringBuilder();
        for (Map.Entry<String, ?> entry : allBookmarks.entrySet()) {
            bookmarks.append(entry.getKey()).append(" - ").append(entry.getValue().toString()).append("\n");
        }

        Toast.makeText(this, bookmarks.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                accessTheCallLog();
            } else {
                Toast.makeText(this, "Permission denied to read call log", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
