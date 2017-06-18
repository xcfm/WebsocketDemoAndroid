package com.example.fmz.websocketdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.crossbar.autobahn.WebSocketConnection;
import io.crossbar.autobahn.WebSocketConnectionHandler;
import io.crossbar.autobahn.WebSocketException;


public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private EditText editText;
    private Button login;
    private final WebSocketConnection mConnection = new WebSocketConnection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_main);
        initView();
        login.setOnClickListener(
                view -> startActivityForResult(new Intent(MainActivity.this, ScanActivity.class), 101)
        );
    }

    private void initView() {
        editText = (EditText) findViewById(R.id.editText);
        login = (Button) findViewById(R.id.login);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && data != null) {
            String reg = data.getStringExtra("DATA");
            if (!TextUtils.isEmpty(reg) && reg.contains("reg")) {
                String sessionID = reg.substring(3, reg.length());
                send(sessionID);
            }
        }
    }

    private void send(String sessionID) {
        if (mConnection.isConnected()) {
            mConnection.sendTextMessage("login-" + sessionID);
        } else {
            start(sessionID);
        }
    }

    private void start(final String sessionID) {

        final String wsuri = "ws://191.101.224.56:9090";
//        final String wsuri = "ws://192.168.199.203:9090";

        try {
            mConnection.connect(wsuri, new WebSocketConnectionHandler() {

                @Override
                public void onOpen() {
                    Log.d(TAG, "Status: Connected to " + wsuri);
                    mConnection.sendTextMessage("login-" + sessionID);
                }

                @Override
                public void onTextMessage(String payload) {
                    Log.d(TAG, "Got echo: " + payload);
                    Toast.makeText(MainActivity.this, payload, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onClose(int code, String reason) {
                    Log.d(TAG, "Connection lost.");
                }
            });
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }
}
