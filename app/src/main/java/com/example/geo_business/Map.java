package com.example.geo_business;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.Polyline;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Map extends AppCompatActivity implements OnMapReadyCallback {

    private static final int MAP_PERMISSION_CODE = 101;
    public static final int CAMERA_PERM_CODE = 102;
    public static final int CAMERA_REQUEST_CODE = 103;
    FusedLocationProviderClient fusedLocationProviderClient;
    GoogleMap map;
    double userLat, userLong;
    private LatLng destinationLocation, userLocation;
    Button startButton, endButton;
    TextView info;
    private boolean isTravelStarted;
    String currentPhotoPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        startButton = (Button) findViewById(R.id.startButton);
        endButton = (Button) findViewById(R.id.endButton);
        info = (TextView) findViewById(R.id.info);

        endButton.setVisibility(View.INVISIBLE);
        info.setVisibility(View.INVISIBLE);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTravelStarted = true;
                startButton.setVisibility(View.INVISIBLE);
                endButton.setVisibility(View.VISIBLE);
                info.setVisibility(View.VISIBLE);
            }
        });

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTravelStarted = false;
                startButton.setVisibility(View.VISIBLE);
                endButton.setVisibility(View.INVISIBLE);
                info.setVisibility(View.INVISIBLE);
                verifyCameraPermisson();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // İzin yoksa izin talebi yap
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, MAP_PERMISSION_CODE);
            return;
        }
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                if(!isTravelStarted) {
                    map.clear();
                    destinationLocation = latLng;
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.icon(setIcon(Map.this, R.drawable.lock_black_24dp));
                    map.addMarker(markerOptions);

                    getRoute(userLocation, destinationLocation);
                }
            }
        });

        fetchMyLocation();
    }

    private void getRoute(LatLng origin, LatLng destination) {
        String apiKey = "AIzaSyDiA-6dALFcffd3sVMwzPCue0IFk4tB0uw"; // Replace with your Google Maps API Key
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" +
                origin.latitude + "," + origin.longitude +
                "&destination=" + destination.latitude + "," + destination.longitude +
                "&mode=driving&key=" + apiKey;

        // Use a networking library or AsyncTask to make the API request
        // and parse the response to get route details (polyline points)

        // For simplicity, you can use AsyncTask for demonstration purposes
        new FetchDirectionsTask().execute(url);
    }

    private class FetchDirectionsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // Use a networking library (e.g., Retrofit, Volley) to make the API request
            // and obtain the JSON response from the Directions API

            // For simplicity, we'll use a basic HttpURLConnection here
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                return result.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // Parse the JSON response to obtain the polyline points
            // Draw the polyline on the map
            /*
            Log.d("TAG", "API Response: " + result);
            Toast.makeText(Map.this, "Mesafe: "+result., Toast.LENGTH_SHORT).show();

            drawPolylineOnMap(result);

             */

            // JSON yanıtını çözümle
            JSONObject jsonResponse = null;
            try {
                jsonResponse = new JSONObject(result);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            // "routes" dizisinden ilk rota al
            JSONArray routesArray = null;
            try {
                routesArray = jsonResponse.getJSONArray("routes");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            JSONObject firstRoute = null;
            try {
                firstRoute = routesArray.getJSONObject(0);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            // İlk rota içinden "legs" dizisinden ilk bacak al
            JSONArray legsArray = null;
            try {
                legsArray = firstRoute.getJSONArray("legs");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            JSONObject firstLeg = null;
            try {
                firstLeg = legsArray.getJSONObject(0);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            // İlk bacak içinden "distance" alanını al
            JSONObject distanceObject = null;
            try {
                distanceObject = firstLeg.getJSONObject("distance");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            // "text" alanından mesafe metnini al
            String distanceText = null;
            try {
                distanceText = distanceObject.getString("text");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            // Toast mesajını oluştur ve göster
            String toastMessage = "Mesafe: " + distanceText;
            Toast.makeText(Map.this, toastMessage, Toast.LENGTH_SHORT).show();
            info.setText("Distance: " + distanceText);

            // Harita üzerinde çizgi çiz
            drawPolylineOnMap(result);
        }
    }

    private void drawPolylineOnMap(String result) {
        try {
            JSONObject json = new JSONObject(result);

            JSONArray routes = json.getJSONArray("routes");
            /*
            JSONObject route = routes.getJSONObject(0);
            JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
            String encodedPolyline = overviewPolyline.getString("points");

            List<LatLng> polylineList = decodePoly(encodedPolyline);

            PolylineOptions polylineOptions = new PolylineOptions()
                    .addAll(polylineList)
                    .width(10)
                    .color(Color.BLUE);

            Polyline polyline = map.addPolyline(polylineOptions);

             */

            JSONObject route = routes.length() > 0 ? routes.getJSONObject(0) : null;
            if (route != null) {
                JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                String encodedPolyline = overviewPolyline.getString("points");

                List<LatLng> polylineList = decodePoly(encodedPolyline);

                PolylineOptions polylineOptions = new PolylineOptions()
                        .addAll(polylineList)
                        .width(10)
                        .color(Color.BLUE);

                Polyline polyline = map.addPolyline(polylineOptions);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            double latDouble = lat / 1e5;
            double lngDouble = lng / 1e5;
            poly.add(new LatLng(latDouble, lngDouble));
        }

        return poly;
    }

    private void fetchMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();

        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                userLat = location.getLatitude();
                userLong = location.getLongitude();

                userLocation = new LatLng(userLat, userLong);

                LatLng latLng = new LatLng(userLat, userLong);

                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(14).build();

                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                map.addMarker(new MarkerOptions().position(latLng)
                        .icon(setIcon(Map.this, R.drawable.baseline_register_circle_24)));

            }
        });
    }

    public void verifyCameraPermisson() {
        String[] permissions = {
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
        };

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[2]) == PackageManager.PERMISSION_GRANTED) {
            Log.d("Kamera" , "İzin alındı");
            dispatchTakePictureIntent();
        }
        else {
            Log.d("Kamera" , "İzin alınamadıdı");
            ActivityCompat.requestPermissions(this, permissions, CAMERA_PERM_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_PERM_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent();
            }else {
                Toast.makeText(this, "Kamerayı kullanmak için izin gerekmektedir!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Log.d("Kamera" , "1");
        Intent takePıctureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Log.d("Kamera" , "2");
        if(takePıctureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;

            try {
                Log.d("Kamera" , "3");
                photoFile = createImageFile();
            }
            catch (IOException exception) {
                Toast.makeText(this, "Fotoğraf dosyası oluşturulamadı!", Toast.LENGTH_SHORT).show();
            }

            if(photoFile != null) {
                Log.d("Kamera" , "4");
                Uri photoUri = FileProvider.getUriForFile(this, "com.example.geo_business", photoFile);
                takePıctureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                Log.d("Kamera" , "5");
                startActivityForResult(takePıctureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Geo_Business_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        currentPhotoPath = image.getAbsolutePath();

        return image;
    }

    private BitmapDescriptor setIcon(Activity activity, int drawableId) {
        Drawable drawable = ActivityCompat.getDrawable(activity, drawableId);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }



}