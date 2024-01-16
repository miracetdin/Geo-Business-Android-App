package com.example.geo_business;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telecom.Call;
import android.text.TextUtils;
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


import com.example.adapters.TravelAdapter;
import com.example.api_requests.CityApiRequest;
import com.example.api_requests.CreateTravelApiRequest;
import com.example.api_requests.TravelsApiRequest;
import com.example.models.Travel;
import com.example.models.User;
import com.example.shared_data.TokenData;
import com.example.shared_data.UserData;
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
import com.google.gson.Gson;
import com.googlecode.tesseract.android.TessBaseAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;


public class Map extends AppCompatActivity implements OnMapReadyCallback {

    private static final int MAP_PERMISSION_CODE = 101;
    public static final int CAMERA_PERM_CODE = 102;
    public static final int CAMERA_REQUEST_CODE = 103;
    private static final String TAG = "MapActivity";
    FusedLocationProviderClient fusedLocationProviderClient;
    GoogleMap map;
    double userLat, userLong;
    private LatLng destinationLocation, userLocation;
    Button startButton, endButton;
    TextView info;
    private boolean isTravelStarted;
    static String currentPhotoPath;
    static String currentPhotoName;
    static String currentCity;
    static String startLocation;
    static String endLocation;
    static String photoLink;

    static String travelDistance;
    static Float invoicePrice;

    static Float priceEstimate;


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

            Log.d("Konum api response:", jsonResponse.toString());

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

            // Başlangıç noktasını içeren "start_address" alanını al
            String startAddress = null;
            try {
                startAddress = firstLeg.getString("start_address");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            String endAddress = null;
            try {
                endAddress = firstLeg.getString("end_address");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            // Başlangıç noktasını kullanarak bir şeyler yapabilirsiniz
            // Örneğin, bir Toast mesajı göstermek:
            Toast.makeText(Map.this, "Başlangıç Şehri: " + startAddress, Toast.LENGTH_SHORT).show();
            Log.d("Başlangıç Şehri:", startAddress);
            String city = extractCity(startAddress);
            System.out.println("Şehir: " + city);
            currentCity = city;
            startLocation = startAddress;
            endLocation = endAddress;

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

            // String distanceText = "10.6 km";

            // Sayısal değeri çıkarmak için regex
            String regex = "[0-9]+\\.*[0-9]*";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(distanceText);

            // Eşleşen değeri bulma
            if (matcher.find()) {
                String matchedValue = matcher.group(); // Eşleşen tam sayısal değeri içerir
                System.out.println("Distance: " + matchedValue);
                travelDistance = matchedValue;
            }

            // Harita üzerinde çizgi çiz
            drawPolylineOnMap(result);
        }
    }

    private static String extractCity(String address) {
        // Örnek regex deseni
        Pattern pattern = Pattern.compile("(\\w+/\\w+),");
        Matcher matcher = pattern.matcher(address);

        if (matcher.find()) {
            // İlk eşleşen kısmı al (Çukurova/Adana)
            String cityPart = matcher.group(1);

            // / işaretine göre bölerek şehir ismini al
            String[] parts = cityPart.split("/");
            if (parts.length > 1) {
                return parts[1].trim();
            }
        }

        return null; // Eşleşme bulunamazsa null döndürülebilir
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
                // Image captured successfully, now start OCR
            }else {
                Toast.makeText(this, "Kamerayı kullanmak için izin gerekmektedir!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Log.d("Kamera", "1");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Log.d("Kamera", "2");
        File photoFile = null;
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            photoFile = null;

            try {
                Log.d("Kamera", "3");
                photoFile = createImageFile();
            } catch (IOException exception) {
                Toast.makeText(this, "Fotoğraf dosyası oluşturulamadı!", Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                Log.d("Kamera", "4");
                Uri photoUri = FileProvider.getUriForFile(this, "com.example.geo_business", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                Log.d("Kamera", "5");
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }

    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Geo_Business_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        currentPhotoPath = image.getAbsolutePath();
        currentPhotoName = imageFileName;

        return image;
    }

    // startActivityForResult sonucunu dinleyen metod
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            // Image captured successfully, start OCR
            startOCR();
        }
    }

    private void startOCR() {
        // Copy the traineddata file from assets to internal storage
        copyTessData();
        // Initialize Tesseract API
        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        tessBaseAPI.init(getFilesDir().getPath(), "tur"); // You may need to replace "eng" with the appropriate language code
        // tessBaseAPI.init(getExternalFilesDir(null).getAbsolutePath(), "tur");
        // Set the image for OCR processing

        // Save the Bitmap to a file in internal storage
        // Bitmap bitmap = getBitmapFromFile(currentPhotoPath);

        BitmapFactory.Options options = new BitmapFactory.Options();
        // options.inSampleSize = 100; // 2 ile örneğin resmi yarı boyuta indirir
        // options.inPurgeable = true;
        // Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, options);

        int maxWidth = 1920; // Maksimum genişlik değeri
        int maxHeight = 1080; // Maksimum yükseklik değeri

        // Resmin boyutunu al
        int originalWidth = options.outWidth;
        int originalHeight = options.outHeight;

        // Ölçek faktörünü belirle
        int inSampleSize = 1;
        if (originalWidth > maxWidth || originalHeight > maxHeight) {
            final int halfWidth = originalWidth / 2;
            final int halfHeight = originalHeight / 2;

            while ((halfWidth / inSampleSize) > maxWidth && (halfHeight / inSampleSize) > maxHeight) {
                inSampleSize *= 2;
            }
        }

        // options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;

        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeFile(currentPhotoPath, options);
        /*
        try {
            FileInputStream fileInputStream = new FileInputStream(currentPhotoPath);
            if(fileInputStream == null) {
                Log.d("TAG", "fileinpuıtstreamn null");
            }
            Log.d("ocr", "fileInputStream" + fileInputStream);
            // bitmap = BitmapFactory.decodeStream(fileInputStream, null, options);
            bitmap = BitmapFactory.decodeFile(currentPhotoPath, options);
            if (bitmap == null) {
                Log.e("bitmap deneme", "Cannot decode bitmap");
            }
            // FileInputStream'ı kapat (unutmayın!)
            fileInputStream.close();
            // Log.d("ocr", String.valueOf(bitmap.getWidth()));
            // Rest of the code...
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ocr", "Error decoding file: " + e.getMessage());
        }

         */

        Log.d("ocr", "başarılı");
        // Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
        //Log.d("ocr", currentPhotoPath);
        // File file = new File(currentPhotoPath);
        tessBaseAPI.setImage(bitmap);
        //Log.d("ocr", file.getAbsolutePath());
        //tessBaseAPI.setImage(file);
        //Log.d("ocr", "bitmap 1");

        // Get the recognized text
        String recognizedText = tessBaseAPI.getUTF8Text();

        // Display or process the recognized text as needed
        if (!TextUtils.isEmpty(recognizedText)) {
            // Do something with the recognized text, for example, display it in a TextView
            Toast.makeText(this, "OCR Result: " + recognizedText, Toast.LENGTH_LONG).show();
            Log.d("OCR Result: ", recognizedText);

            // String exampleText = "Tutar 10, Top 20.50, Toplam: 30.75, Tutar: 10.55, Top: *10.55";

            // Fatura fiyatını tespit etmek için regex
            String regex = "(?i)(?:Tutar|Top|Toplam)\\s*(?::|)\\s*[*]*\\s*([\\d,.]+)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(recognizedText);

            // Eşleşen değerleri bulma
            while (matcher.find()) {
                String matchedValue = matcher.group(1); // Grup 1, sayısal değeri içerir
                // Burada matchedValue değerini kullanabilirsiniz
                System.out.println("Fatura Fiyatı: " + matchedValue);
                invoicePrice = Float.valueOf(matchedValue);
            }

            getCity();
            saveImageToServer(new File(currentPhotoPath));
            // setTravelData();
            // You can also use recognizedText for further processing or store it in a variable.
        } else {
            Toast.makeText(this, "OCR failed. No text recognized.", Toast.LENGTH_SHORT).show();
            Log.d("OCR Result: ", "OCR failed. No text recognized.");
        }

        // End the OCR processing
        tessBaseAPI.end();
    }

    private void copyTessData() {
        try {
            String path = getFilesDir().getPath() + "/tessdata/";
            String filePath = path + "tur.traineddata";

            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    return;
                }
            }
            else {
                Log.d("ocr", "tur için dosya var");
            }

            InputStream inputStream = getAssets().open("tessdata/tur.traineddata");
            FileOutputStream outputStream = new FileOutputStream(filePath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }

            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getCity() {
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
        // String city = "Adana";

        // API request
        String apiUrl = "http://192.168.1.54:4000/fee/"+currentCity;
        CityApiRequest cityApiRequest = new CityApiRequest(new CityApiRequest.ApiCallback() {
            @Override
            public void onTaskComplete(String result) {

                Log.d(TAG, "API Response: " + result);

                try {
                    // Transform JSON response to JSON Array
                    JSONObject jsonResponse = new JSONObject(result);
                    Log.d(TAG, "jsonResponse: " + jsonResponse);
                    Log.d(TAG, "jsonResponseLength: " + jsonResponse.length());

                    if (jsonResponse.has("error")) {
                        // Login error
                        Toast.makeText(Map.this, "Error!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Login successfully
                        String city = jsonResponse.getString("city");
                        String openingFee = jsonResponse.getString("openingFee");
                        String feePerKm = jsonResponse.getString("feePerKm");

                        priceEstimate = Float.valueOf(openingFee) + Float.valueOf(travelDistance) * Float.valueOf(feePerKm);

                            Toast.makeText(Map.this, "Welcome "+city+" "+openingFee+" "+feePerKm, Toast.LENGTH_SHORT).show();
                            Log.d("City API", "Welcome "+city+" "+openingFee+" "+feePerKm);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
                    Toast.makeText(Map.this, "HATA!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cityApiRequest.execute(apiUrl, "GET", currentCity, accessToken);
    }

    private void saveImageToServer(File file) {
        // File'ı sunucuya gönderme işlemini gerçekleştirebilirsiniz.
        // Burada HttpClient, Retrofit, veya diğer HTTP kütüphanelerini kullanabilirsiniz.
        // Örnek bir HTTP POST request kullanımı:

        Log.d("Kayıt", "1");

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("photo", currentPhotoName+".jpg",
                        RequestBody.create(MediaType.parse("image/*"), file))
                .build();
        Log.d("Kayıt", "2");
        Request request = new Request.Builder()
                .url("http://192.168.1.54:4000/upload")
                .post(requestBody)
                .build();
        Log.d("Kayıt", "3");
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Sunucu yanıtını burada işleyebilirsiniz.
                    String responseData = response.body().string();
                    Log.d("Server Response", responseData);

                    // Assuming `responseData` contains the JSON response
                    try {
                        JSONObject json = new JSONObject(responseData);
                        photoLink = json.getString("fileLink");
                        Log.d("Server Response", "File Link: " + photoLink);

                        setTravelData();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("Server Response", "Error parsing JSON: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

        });
    }

    /*
    public void setTravelData() {
        User user = UserData.getInstance().getSharedData();
        String employeeUsername = user.getUsername();

        // Tarih formatını belirle
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        // Tarihi metin olarak temsil eden bir String
        String dateString = "12/23/2023";
        // String'i LocalDate nesnesine çevir
        LocalDate travelDate = LocalDate.parse(dateString, formatter);

        // startLocation;
        // endLocation;
        // photoLink;

    }


     */
    public void setTravelData() {

        User user = UserData.getInstance().getSharedData();
        String employeeUsername = user.getUsername();

        // Bugünün tarihini al
        LocalDate today = LocalDate.now();
        // Tarih formatını belirle
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        // Tarihi metin olarak temsil eden bir String
        //String dateString = "12/23/2023";
        // String'i LocalDate nesnesine çevir
        //LocalDate travelDate = LocalDate.parse(dateString, formatter);
        // Tarihi belirlenen formata çevir
        String travelDate = today.format(formatter);


        String suspicious = "yes";

        if((invoicePrice != null) && (priceEstimate != null)) {
            if (invoicePrice > priceEstimate * 1.25) {
                suspicious = "yes";
            }
            else {
                suspicious = "no";
            }
        }

        String invoicePhoto = photoLink;
        String invoiceInfo = travelDistance;


        String status = "waiting";
        String approveByAccountant = null;
        String approveDate = null;


        Travel travel = new Travel(
                employeeUsername, travelDate.toString(), startLocation, endLocation, invoicePhoto, invoiceInfo+" km",
                "note", invoicePrice, priceEstimate, suspicious, status, approveByAccountant, approveDate
        );

        Log.d("test", travel.toString());

        Gson gson = new Gson();
        String body =  gson.toJson(travel);

        Log.d("body içerik: ", body);

        // create JSON Object with username and password
        JSONObject jsonParams = new JSONObject();

        String[] tokens = TokenData.getInstance().getSharedData();
        String accessToken = tokens[0];
        Log.e(TAG, "Access Token: " + accessToken);

        // API request
        String apiUrl = "http://192.168.1.54:4000/travel/";
        CreateTravelApiRequest createTravelApiRequest = new CreateTravelApiRequest(new CreateTravelApiRequest.ApiCallback() {
            @Override
            public void onTaskComplete(String result) {

                Log.d(TAG, "API Response: " + result);

            }
        });
        createTravelApiRequest.execute(apiUrl, body, accessToken);
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