package com.example.sample_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.os.Handler;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    private Button m_btn_sample;
    private TextView m_txt_sample;
    private TextView m_txt_connect;
    private boolean m_flag;
    private EditText m_edt_ip;
    private EditText m_edt_port;
    private Button m_btn_send;
    private EditText m_edt_send;

    private Thread m_threadSocket;
    private Thread m_threadReceive;

    private Socket m_socket;
    private Handler m_handler;

    String tmp = ""; // storage temp. text

    String ServerIP = "140.115.158.249";
    int socket_Port = 9527;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_handler = new Handler();

        m_btn_sample = findViewById(R.id.btn_connect);
        m_btn_sample.setOnClickListener(btnSampleOnClick);

        m_btn_send = findViewById(R.id.btn_send);
        m_btn_send.setOnClickListener(btnSendOnClick);

        m_txt_sample = findViewById(R.id.textView);
        m_flag = true; // true is disconnect; otherwise.

        m_edt_ip = findViewById(R.id.ipt_IP);
        m_edt_port = findViewById(R.id.ipt_Port);

        m_txt_connect = findViewById(R.id.textView_connect);
    }
    Button.OnClickListener btnSampleOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            m_btn_sample.setText(m_flag ? "Disconnect" : "Connect");

            if (m_flag){
                ServerIP = m_edt_ip.getText().toString();
                socket_Port =  Integer.parseInt(m_edt_port.getText().toString());

                // Go Connecting
                m_threadSocket = new Thread(threadConnection_Run);
                m_threadSocket.start();
            }else {
                try {
                    m_socket.close();

                } catch(IOException e){
                    e.printStackTrace();
                }
            }
            m_flag = !m_flag;
        }
    };

    Button.OnClickListener btnSendOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (m_socket.isConnected()) {
                BufferedWriter bw;

                try {
                    bw = new BufferedWriter(new OutputStreamWriter(m_socket.getOutputStream()));

                    bw.write(m_edt_send.getText() + "\n");

                    bw.flush();
                } catch (IOException e) {

                }
                m_edt_send.setText("");
            }
        }
    };

    // To Build Socket
    private Runnable threadConnection_Run = new Runnable(){

        @Override
        public void run()
        {
            try
            {
                // Connect to the server
                m_socket = new Socket(ServerIP, socket_Port);

                /*while (m_socket.isConnected()){
                    Log.e("text","I'm connected.");
                }*/
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

    // Update the messages to the Server


    private Runnable threadReceive_Run = new Runnable() {
        @Override
        public void run() {
            try {
                // Get the Internet input stream
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        m_socket.getInputStream()));

                // When getting connected
                while (m_socket.isConnected()) {
                    tmp = br.readLine(); // Get the Internet messages

                    // if not empty message
                    if (tmp != null) {
                        m_handler.post(handlerUpdateData);
                    }
                }

                if (m_socket.isClosed()) {
                    tmp = "Now, the Server is DISCONNECTED!";
                }
            }
            catch (IOException e){
                e.printStackTrace();
                Log.e("text","Socket connect="+e.toString());
                finish();
            }
        }
    };

    // Display the messages
    private Runnable handlerUpdateData = new Runnable() {
        @Override
        public void run() {
            // add new messages
            m_txt_connect.append(tmp + "\n");
        }
    };
}
