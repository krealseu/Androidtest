package org.kreal.mt;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Enumeration;

import litesuits.common.utils.ShellUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText text = (EditText) findViewById(R.id.editText);
        Button start = (Button) findViewById(R.id.btn_start);
        Button stop = (Button) findViewById(R.id.btn_stop);
        test = (Button) findViewById(R.id.btn_test);
        ((TextView) findViewById(R.id.textView2)).setText(getLocalIpAddress(getApplicationContext()));
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        test.setOnClickListener(this);
        download("keyboard");
        download("start.sh");
        download("stop.sh");
    }

    void download(String str) {
        String filepath = getApplication().getFilesDir().getAbsolutePath() + "/" + str;
        try {
            InputStream inputStream = getAssets().open(str);
            FileOutputStream fileOutputStream = openFileOutput(str, Context.MODE_PRIVATE);
            byte[] buffer = new byte[1024];
            int len = 0;
            while (true) {
                len = inputStream.read(buffer);
                if (len == -1)
                    break;
                fileOutputStream.write(buffer, 0, len);
            }
            fileOutputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String cmd1 = "/system/bin/chmod 777 " + filepath;
            Process process = Runtime.getRuntime().exec(cmd1);
            process.waitFor();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_start) {
            String start = getApplication().getFilesDir().getAbsolutePath() + "/keyboard";
            ShellUtil.execCommand(start, true);
        } else if (v.getId() == R.id.btn_stop) {
            String stop = getApplication().getFilesDir().getAbsolutePath() + "/stop.sh";
            ShellUtil.execCommand(stop, true);
        } else if (v.getId() == R.id.btn_test) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Socket socket=null;
                    OutputStream os = null;
                    try {
                        socket = new Socket("127.0.0.1", 8088);
                        //os = socket.getOutputStream();
                        //os.write("\r\n".getBytes());
                        //socket.shutdownOutput();
                        //socket.shutdownInput();
                        //os.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            }

        }

    public static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    /**
     * 获取当前ip地址
     *
     * @param context
     * @return
     */
    public static String getLocalIpAddress(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int i = wifiInfo.getIpAddress();
            return int2ip(i);
        } catch (Exception ex) {
            return " 获取IP出错鸟!!!!请保证是WIFI,或者请重新打开网络!\n" + ex.getMessage();
        }
        // return null;
    }
}
