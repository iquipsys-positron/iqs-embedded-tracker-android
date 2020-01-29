package com.iquipsys.tracker.phone.rest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class RestClient {

    public static boolean hasNetworkAccess(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
            .getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        } catch (Exception e) {
            return false;
        }
    }

    protected String executeGet(String route) throws IOException {
        return executeRequest("GET", route, null);
    }

    protected String executePost(String route, String body) throws IOException {
        return executeRequest("POST", route, body);
    }

    protected String executeRequest(String method, String route, String body) throws IOException {
        InputStream stream = null;

        try {
            URL url = new URL(route);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod(method);
            conn.setDoInput(true);

            if (body != null) {
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(body);
                writer.flush();
                writer.close();
            }

            conn.connect();

            int responseCode = conn.getResponseCode();

            if (responseCode == 204) {
                return null;
            }

            if (responseCode != 200) {
                String errorJson = readStream(conn.getErrorStream());
                Gson gson = new Gson();
                ErrorDescription error = gson.fromJson(errorJson, ErrorDescription.class);
                String message = error != null ? error.getMessage() : error.getCode();
                message = message != null ? message : "Got response code " + responseCode;
                throw new IOException(message);
            }

            stream = conn.getInputStream();
            return readStream(stream);
        } finally {
            if (stream != null)
                stream.close();
        }
    }

    protected String readStream(InputStream stream) throws IOException {
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        BufferedOutputStream out = null;

        try {
            int length = 0;
            out = new BufferedOutputStream(byteArray);
            while ((length = stream.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            out.flush();
            return byteArray.toString();
        } finally {
            if (out != null)
                out.close();
        }
    }

}
