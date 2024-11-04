package com.example.contenprovider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.CursorLoader;
import android.provider.CallLog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Date;

public class ShowCallLogActivity extends Activity {
    private Button btnBack;
    private static final int REQUEST_READ_CALL_LOG = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_call_log);

        btnBack = findViewById(R.id.btnback);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Kiểm tra và yêu cầu quyền truy cập Call Log
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, REQUEST_READ_CALL_LOG);
        } else {
            showAllCallLogs();
        }
    }

    public void showAllCallLogs() {
        Uri uri = CallLog.Calls.CONTENT_URI;
        String[] projection = new String[] {
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION
        };
        ArrayList<String> list = new ArrayList<>();

        CursorLoader loader = new CursorLoader(this, uri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
                long dateMillis = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
                String duration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION));

                String callType;
                switch (type) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        callType = "Cuộc gọi đi";
                        break;
                    case CallLog.Calls.INCOMING_TYPE:
                        callType = "Cuộc gọi đến";
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        callType = "Cuộc gọi nhỡ";
                        break;
                    default:
                        callType = "Không xác định";
                        break;
                }

                Date date = new Date(dateMillis);
                list.add("Số: " + number + "\nKiểu: " + callType + "\nNgày: " + date + "\nThời gian: " + duration + " Giây");
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Toast.makeText(this, "No call logs found", Toast.LENGTH_SHORT).show();
        }

        ListView listView = findViewById(R.id.listView1);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_show_all_call_logs, menu);
//        return true;
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CALL_LOG) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showAllCallLogs();
            } else {
                Toast.makeText(this, "Permission denied to read call logs", Toast.LENGTH_SHORT).show();
            }
        }
    }
}