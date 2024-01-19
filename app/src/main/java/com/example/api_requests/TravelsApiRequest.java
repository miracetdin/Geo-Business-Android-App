package com.example.api_requests;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class TravelsApiRequest extends AsyncTask<String, Void, String> {

    private static final String TAG = "TravelsApiRequest";
    private TravelsApiRequest.ApiCallback callback;

    public interface ApiCallback {
        void onTaskComplete(String result);
    }

    public TravelsApiRequest(TravelsApiRequest.ApiCallback callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... params) {
        String apiUrl = params[0];
        String page = params[1];
        String access_token = params[2];

        try {
            URL url = new URL(apiUrl + "/travel/?page=" + page);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", access_token);

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                return response.toString();
            } else {
                Log.e(TAG, "HTTP Error Code: " + responseCode);
            }
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (callback != null) {
            callback.onTaskComplete(result);
        }
    }

    /*
    private static final String TAG = "ApiRequest";
    private ApiCallback callback;

    public interface ApiCallback {
        void onTaskComplete(String result);
    }

    public TravelsApiRequest(ApiCallback callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... params) {
        String apiUrl = params[0];
        String requestBody = params[1];
        String requestMethod = params[2];

        // Parametreler ve başlıklar için Map nesnelerini oluştur
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("page", params[3]);

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Authorization", params[4]);

        Log.e(TAG, "Page: " + params[3]);
        Log.e(TAG, "requestParams: " + requestParams);
        Log.e(TAG, "Access Token: " + params[4]);
        Log.e(TAG, "requestHeaders: " + requestHeaders);

        try {
            URL url = new URL(apiUrl);

            // GET isteği için query parametrelerini ekle
            if (requestMethod.equals("GET")) {
                StringBuilder queryString = new StringBuilder();
                for (Map.Entry<String, String> entry : requestParams.entrySet()) {
                    if (queryString.length() > 0) {
                        queryString.append("&");
                    }
                    queryString.append(entry.getKey()).append("=").append(entry.getValue());
                }
                apiUrl += "?" + queryString.toString();
                url = new URL(apiUrl);
            }

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // İstek methodunu ayarla
            urlConnection.setRequestMethod(requestMethod);

            // Başlıkları ayarla
            for (Map.Entry<String, String> entry : requestHeaders.entrySet()) {
                urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            // POST isteği için istek body'sini ayarla
            if (requestMethod.equals("POST")) {
                urlConnection.setDoOutput(true);
                urlConnection.getOutputStream().write(requestBody.getBytes("UTF-8"));
            }

            // Sunucudan gelen cevabı al
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            } else {
                Log.e(TAG, "HTTP Error Code: " + responseCode);
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "IOException: " + e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (callback != null) {
            callback.onTaskComplete(result);
        }
    }

     */
}
