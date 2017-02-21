package com.example.internethttp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ClientInfoStatus;
import java.sql.Connection;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText mPath;
    private Button mSend;
    private TextView mResponseText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();


    }

    private void initView() {
        mPath = (EditText) findViewById(R.id.edt_path);
        mSend = (Button) findViewById(R.id.btn_seng);
        mResponseText = (TextView) findViewById(R.id.tv_response);
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //=======使用HttpURLConnection访问网络
                // sendRequestWithHttpURLConnection();
                //=======使用OkHttp访问网络，需要添加依赖compile 'com.squareup.okhttp3:okhttp:3.6.0'
                sendRequestWithOkHttp();
            }
        });
    }

    //使用HttpURLConnection访问网络
    /*
    *======POST请求方法.提交数据给服务器
    *connection.setRequestMethod("POST");
    *DataOutputStream out = new DataOutputStream(connection.getOutputStream());
    * out.writeBytes("username=admin&password=123456");
    * */
    private void sendRequestWithHttpURLConnection() {

        //开启线程访问网络
        new Thread(new Runnable() {

            private HttpURLConnection mConnection;
            private BufferedReader mReader;

            @Override
            public void run() {
                //获取网址
                String path = mPath.getText().toString().trim();
                try {
                    //new出一个URL对象并传入path，然后调用一下openConnection()方法,获取HttpURLConnection的实例
                    URL url = new URL(path);
                    mConnection = (HttpURLConnection) url.openConnection();
                    //设置请求方法
                    mConnection.setRequestMethod("GET");
                    //设置读取超时
                    mConnection.setReadTimeout(8000);
                    //设置连接超时
                    mConnection.setConnectTimeout(8000);
                    //获取服务器返回的输入流
                    InputStream in = mConnection.getInputStream();
                    //读取输入流
                    mReader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = mReader.readLine()) != null) {

                        response.append(line);
                    }
                    //在主线程更新UI
                    showResponseData(response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //关闭
                    if (mReader != null) {
                        try {
                            mReader.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (mConnection != null) {
                        mConnection.disconnect();
                    }
                }

            }
        }).start();
    }

    //使用OkHttp访问网络
    /*
    * 如果是发起一条POST请求会比GET请求稍微复杂一点
    * RequestBody requestBody = new ForBody.Builder()
    *                           .add("username","admin")
    *                           .add("password","123456")
    *                           .build();
    *
    * Request request = new Request.Builder()
    *                      .url(path)
    *                      .post(requestBody)
    *                      .build();
    *
    * */
    private void sendRequestWithOkHttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取网址
                String path = mPath.getText().toString().trim();
                try {
                    //创建OkHttpClient实例
                    OkHttpClient client = new OkHttpClient();
                    //创建Request对象并连缀其他方法
                    Request request = new Request.Builder()
                            .url(path)
                            .build();
                    //调用OkHttpClient的newCall()方法来创建一个Call对象，并调用它的execute()方法来发送请求并获取服务器返回的数据
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    //在主线程更新UI
                    showResponseData(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void showResponseData(final String responseData) {
        //在主线程更新UI
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mResponseText.setText(responseData);
            }
        });
    }
}