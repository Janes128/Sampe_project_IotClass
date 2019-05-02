package com.example.sample_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.os.Handler;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private Button m_btn_sample;
    private TextView m_txt_sample;
    private TextView m_txt_connect;
    private boolean m_flag;

    private Thread m_threadSocket;
    private Thread m_threadReceive;

    private Socket m_socket;
    private Handler m_handler;

    String ServerIP = "140.115.158.249";
    int socket_Port = 9527;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_handler = new Handler();

        m_threadSocket = new Thread(threadConnection_Run);
        m_threadSocket.start();

        m_btn_sample = findViewById(R.id.button_sample);
        m_btn_sample.setOnClickListener(btnSampleOnClick);

        m_txt_sample = findViewById(R.id.textView);
        m_flag = true;

        m_txt_connect = findViewById(R.id.textView_connect);
    }
    Button.OnClickListener btnSampleOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            m_txt_sample.setText(m_flag ? "click" : "");
            m_flag = !m_flag;
        }
    };

    // To Buide Socket
    private Runnable threadConnection_Run = new Runnable(){

        @Override
        public void run()
        {
            try
            {
                m_socket = new Socket(ServerIP, socket_Port);
                while (m_socket.isConnected()){
                    Log.e("text","I'm connected.");
                }
                if (m_socket.isConnected()){
                    m_threadReceive = new Thread(threadReceive_Run);
                }

                //m_txt_connect.setText(m_socket.isClosed() ? "Now, I am closed." : "");
            }
            catch (IOException e)
            {
                e.printStackTrace();
                Log.e("text","Socket connect="+e.toString());
                finish();
            }
        }
    };

    private Runnable threadReceive_Run = new Runnable() {
        @Override
        public void run() {
            m_handler.post(handlerUpdateData);
        }
    };

    private Runnable handlerUpdateData = new Runnable() {
        @Override
        public void run() {

        }
    };
}
