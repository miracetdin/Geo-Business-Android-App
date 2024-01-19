package com.example.geo_business;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapters.TravelAdapter;
import com.example.api_requests.LoginApiRequest;
import com.example.api_requests.TravelsApiRequest;
import com.example.models.Travel;
import com.example.models.User;
import com.example.shared_data.TokenData;
import com.example.shared_data.UserData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Travels extends AppCompatActivity {

    public static ArrayList<Travel> travelList = new ArrayList<>();
    private static final String TAG = "TravelsActivity";
    private static final String page = "1";

    private String id, username, travelDate, startLocation, endLocation, invoiceInfo, invoiceNote,
            suspicious, status, approveByAccountant, approveDate;
    private String invoicePhoto;
    private Float invoicePrice, priceEstimate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travels);



        TravelAdapter adapter = new TravelAdapter(travelList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.travel_recycler_view);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        viewTravels(adapter);
    }

    public void viewTravels(TravelAdapter adapter) {
        // create JSON Object with username and password
        JSONObject jsonParams = new JSONObject();

        // Map<String, String> params = new HashMap<>();
        // params.put("page", page);

        // Map<String, String> headers = new HashMap<>();
        // headers.put("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiNjU4NWI1ZTE2NmYzN2NjMjMxOGNhN2JhIiwicm9sZSI6ImVtcGxveWVlIiwiaWF0IjoxNzA0MTMzMTE2LCJleHAiOjE3MDQ5OTcxMTYsImlzcyI6Imdlby1idXNpbmVzcy10cmF2ZWwtbW9kdWxlLmFwcCJ9.WeIiGywN9aoGYfkuKyq0dJKK6ztK4nld84kLg20TE6g");

        // String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiNjU4NWI1ZTE2NmYzN2NjMjMxOGNhN2JhIiwicm9sZSI6ImVtcGxveWVlIiwiaWF0IjoxNzA0MTMzMTE2LCJleHAiOjE3MDQ5OTcxMTYsImlzcyI6Imdlby1idXNpbmVzcy10cmF2ZWwtbW9kdWxlLmFwcCJ9.WeIiGywN9aoGYfkuKyq0dJKK6ztK4nld84kLg20TE6g";

        String[] tokens = TokenData.getInstance().getSharedData();
        String accessToken = tokens[0];
        Log.e(TAG, "Access Token: " + accessToken);

        // API request
        String apiUrl = "http://192.168.1.54:4000/travel/?page=1";
        TravelsApiRequest travelsApiRequest = new TravelsApiRequest(new TravelsApiRequest.ApiCallback() {
            @Override
            public void onTaskComplete(String result) {

                Log.d(TAG, "API Response: " + result);

                try {
                    // Transform JSON response to JSON Array
                    JSONArray jsonResponse = new JSONArray(result);
                    Log.d(TAG, "jsonResponse: " + jsonResponse);
                    Log.d(TAG, "jsonResponseLength: " + jsonResponse.length());

                    // Clear the existing data before adding new data
                    travelList.clear();

                    // Assuming that jsonResponse is a JSONArray
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        try {
                            JSONObject jsonObject = jsonResponse.getJSONObject(i);
                            Log.d(TAG, "jsonResponse(i): " + jsonResponse.getJSONObject(i));




                            // Accessing the fields in the JSON object and assigning them to the class properties
                            id = jsonObject.getString("_id");
                            username = jsonObject.getString("employeeUsername");
                            travelDate = jsonObject.getString("travelDate");
                            startLocation = jsonObject.getString("startLocation");
                            endLocation = jsonObject.getString("endLocation");
                            invoiceInfo = jsonObject.getString("invoiceInfo");
                            invoiceNote = jsonObject.getString("invoiceNote");
                            suspicious = jsonObject.getString("suspicious");
                            status = jsonObject.getString("status");
                            approveByAccountant = jsonObject.getString("approveByAccountant");
                            approveDate = jsonObject.getString("approveDate");

                            // Assuming invoicePhoto is an integer in the JSON, adjust if it's different
                            invoicePhoto = jsonObject.getString("invoicePhoto");

                            // Assuming invoicePrice and priceEstimate are floats in the JSON, adjust if they are different
                            invoicePrice = (float) jsonObject.getDouble("invoicePrice");
                            priceEstimate = (float) jsonObject.getDouble("priceEstimate");

                            Log.d(TAG, "startLocation: " + startLocation);

                            // Now, you have assigned values to the class properties for each JSON object in the array
                            // You can use these values as needed
                            travelList.add(new Travel(id, username, travelDate, startLocation, endLocation, invoicePhoto,
                                    invoiceInfo, invoiceNote, invoicePrice, priceEstimate, suspicious, status,
                                    approveByAccountant, approveDate));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    Log.d("travels size", String.valueOf(travelList.size()));

                    // Notify the adapter that the data has changed
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
                    Toast.makeText(Travels.this, "HATA!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //travelsApiRequest.execute(apiUrl, jsonParams.toString(), "GET", page, accessToken);
        travelsApiRequest.execute(apiUrl, page, accessToken);
    }
}
