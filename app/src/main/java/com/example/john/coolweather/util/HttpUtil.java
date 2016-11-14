package com.example.john.coolweather.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by John on 2016/11/12.
 */

public class HttpUtil {
    public static void sendHttpRequest(final String address, final HttpCallbackListener listener) {

        try {
            new Thread() {
                @Override
                public void run() {
                    HttpURLConnection conn = null;
                    ByteArrayOutputStream baos = null;
                    try {
                        URL url = new URL(address);
                        conn = (HttpURLConnection) url.openConnection();
                        conn.connect();
                        int responseCode = conn.getResponseCode();
                        if (responseCode == 200) {
                            InputStream inputStream = conn.getInputStream();
                            baos = new ByteArrayOutputStream();
                            byte[] buff = new byte[1024];
                            int len = -1;
                            while ((len = inputStream.read(buff)) != -1) {
                                baos.write(buff, 0, len);
                            }
                            if (listener != null) {

                                listener.onSuccess(new String(baos.toString().getBytes(), "UTF-8"));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (listener != null) {
                            listener.onError(e);
                        }
                    } finally {
                        if (conn != null) {
                            conn.disconnect();
                        }
                        if (baos != null) {
                            try {
                                baos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
