package com.example.taxiapplication;

import androidx.annotation.DrawableRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HomeScreen extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener, GoogleMap.OnPolygonClickListener {
    private MapView mapView;
    private GoogleMap map;
    private DrawerLayout mDrawerLayout;
    private ConstraintLayout bottomSheet;
    private RelativeLayout relativearea,relativearea2,relativearea3;
    private NavigationView navView;
    private CardView cardView;
    private Toolbar toolbar;
    private  String  NOTIFICATION_TITLE,NOTIFICATION_MESSAGE, TOPIC,source1, destination1;


    private LocationManager locationManager;
    private LatLng destintionLatlng, start;
    private Location loc,updatedloc;

    private ProgressDialog progressDialog;
    private EditText source, destination;
    private Button close,bookCab,getCurrentLocation,getLocationMap,getLocationMap2,confirm_location,findRoute;
    private TextView distance, time, price;



    private FirebaseDatabase database;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;




    //Messaging FCM
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAJt54zPs:APA91bF5fGyBi3H58EutohPNCuOQFXxlAsU_NZZ0lAQTHDVouZp08w2NAhz2nBCH2krAR3S1_TbPNmoOv2mraPsAbgACYwzWf3Y0K-rXtWwJJeJ5gtTEMK_aaeRYPxWXuvNm2E0LT5P1";
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
//        multiple Threadpolicy
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        mapView.setClickable(false);
//      init burger side menu code
        mDrawerLayout = findViewById(R.id.drawer);
        navView = findViewById(R.id.navView);
        toolbar = findViewById(R.id.toolbar_main);
        setupDrawer();


        loc=getLastKnownLocation();

        distance = findViewById(R.id.distance);
        time = findViewById(R.id.time);
        price = findViewById(R.id.price);
        source = findViewById(R.id.editsource);


        getCurrentLocation=(Button) findViewById(R.id.currentLocationbtn);
        getCurrentLocation.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            setCurrentLocationSource();

        }});
        getLocationMap=(Button)findViewById(R.id.loacateonmapbtn);
        getLocationMap.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {selectLocatioOnMap(); }});
        getLocationMap2=(Button)findViewById(R.id.loacateonmapbtn2);
        getLocationMap2.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {selectLocatioOnMap(); }});

        relativearea=(RelativeLayout) findViewById(R.id.mapSearchArea);
        relativearea2=(RelativeLayout)findViewById(R.id.mapSearchArea2);
        relativearea3=(RelativeLayout)findViewById(R.id.mapSearchArea3);


        bottomSheet = findViewById(R.id.carshow);
        cardView = findViewById(R.id.cardviewplaces);

        //book a Cab
        bookCab=(Button) findViewById(R.id.bookCabbtn);
        findRoute=(Button) findViewById(R.id.findRoutebtn);
        bookCab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookaCab();
                sendBookingData();

            }
        });

        bottomSheet.setVisibility(View.INVISIBLE);
//        Init ProgressBar
        progressDialog = new ProgressDialog(HomeScreen.this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Searching places....");
        destination = (EditText) findViewById(R.id.editdestination);

        source.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    relativearea.setVisibility(View.INVISIBLE);
                } else {
                    relativearea.setVisibility(View.VISIBLE);
                }
            }
        });
        destination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus==true) {
                    relativearea2.setVisibility(View.VISIBLE);
                } else {
                    relativearea2.setVisibility(View.INVISIBLE);
                }
            }
        });

        close = findViewById(R.id.close);
        close.setVisibility(View.INVISIBLE);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.clear();
                bottomSheet.setVisibility(View.INVISIBLE);
                close.setVisibility(View.INVISIBLE);
            }
        });
        confirm_location=findViewById(R.id.confirm_Location);
        confirm_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address =placeNameSearchByLatlng(destintionLatlng.latitude,destintionLatlng.longitude);
                destination.setText(address);
                cardView.setVisibility(View.VISIBLE);
                relativearea3.setVisibility(View.INVISIBLE);
                findRoute.setVisibility(View.VISIBLE);
            }
        });

        findRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativearea.setVisibility(View.VISIBLE);
                source1 = source.getText().toString();

                destination1 = destination.getText().toString();
                progressDialog.show();
                map.clear();
                mapView.setClickable(true);
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            placessearch();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        destination.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    findRoute.setVisibility(View.VISIBLE);
                    return true;
                }
                return false;
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.uber_style));
        map.getUiSettings().setMyLocationButtonEnabled(false);
//        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
//        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
//        // position on right bottom
//        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
//        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
//        rlp.setMargins(0, 600, 30, 20);
        loc= getLastKnownLocation();
//        System.out.println(loc);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
            }
        }
        map.setMyLocationEnabled(true);
//        System.out.println(" >> latitude >>   " + loc.getLatitude() + "  >>longitude >> " + loc.getLongitude());
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
        map.animateCamera(cameraUpdate);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 10));
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void placessearch() throws IOException {
        String uDestination = destination1.replaceAll(" ", "%20").toLowerCase();
        String uSource = source1.replaceAll(" ", "%20").toLowerCase();
        LatLng latLng = null,latlng1 =null;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://trueway-places.p.rapidapi.com/FindPlaceByText?text="+uDestination+"&language=en")
                .get()
                .addHeader("x-rapidapi-key", "8a77f9e494msh82716613b999ab7p1de30bjsn6dc44a841b60")
                .addHeader("x-rapidapi-host", "trueway-places.p.rapidapi.com")
                .build();
        Response response = client.newCall(request).execute();
        String _response = response.body().string();
        System.err.println(_response);
        try {
            JSONObject result = new JSONObject(_response);
            JSONArray jsonArray = result.getJSONArray("results");
            System.err.println(jsonArray);

            for (int z = 0; z < jsonArray.length(); z++) {
                JSONObject latlong = jsonArray.getJSONObject(z);
                System.err.println(latlong);
                JSONObject jsonObject = latlong.getJSONObject("location");
                System.err.println(jsonObject.getString("lat")+"     >>     "+jsonObject.getString("lng"));

                latLng= new LatLng(Double.parseDouble(jsonObject.getString("lat")),Double.parseDouble(jsonObject.getString("lng")));

                int height = 200;
                int width = 200;
//
                BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.locationvectoricon);
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(latlong.getString("name")));
//                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Source latlong
        Request request1 = new Request.Builder()
                .url("https://trueway-places.p.rapidapi.com/FindPlaceByText?text="+ uSource+"&language=en")
                .get()
                .addHeader("x-rapidapi-key", "c38e3fca3emsh193f0ff4b0e46a7p15237bjsn17a03d810a01")
                .addHeader("x-rapidapi-host", "trueway-places.p.rapidapi.com").build();

        Response response1 = client.newCall(request1).execute();

        String _response1 = response1.body().string();
        System.err.println(_response1);
        try {
            JSONObject result = new JSONObject(_response1);
            JSONArray jsonArray = result.getJSONArray("results");
            System.err.println(jsonArray);

            for (int z = 0; z < jsonArray.length(); z++) {
                JSONObject latlong = jsonArray.getJSONObject(z);
                System.err.println(latlong);
                JSONObject jsonObject = latlong.getJSONObject("location");
                System.err.println(jsonObject.getString("lat")+"     >>     "+jsonObject.getString("lng"));

                latlng1= new LatLng(Double.parseDouble(jsonObject.getString("lat")),Double.parseDouble(jsonObject.getString("lng")));


                int height = 200;
                int width = 200;

                BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.locationvectoricon);
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                map.animateCamera(CameraUpdateFactory.newLatLng(latlng1));
                map.addMarker(new MarkerOptions().position(latlng1).
                        icon(BitmapDescriptorFactory.fromBitmap(
                                createCustomMarker(HomeScreen.this,R.drawable.propic)))).setTitle(latlong.getString("name"));
                mapResponse(latLng.latitude,latLng.longitude,latlng1.latitude,latlng1.longitude);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static Bitmap createCustomMarker(Context context, @DrawableRes int resource) {
        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
        CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.user_dp);
        markerImage.setImageResource(resource);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);
        return bitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private Location getLastKnownLocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
//        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        } else {
            @SuppressLint("MissingPermission") Location locationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            @SuppressLint("MissingPermission") Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//            System.out.println("@@@###$$$"+locationGps+" This is Location By Gps"+locationGps.getAccuracy());
//            System.out.println("@@@###$$$"+locationNet+" This is Location By Network"+locationNet.getAccuracy());

            if(locationGps!=null&&locationNet!=null){
                if ( locationNet.getAccuracy() <locationGps.getAccuracy()) bestLocation = locationGps;
                else bestLocation = locationNet;
            }else if(locationGps==null){
                System.out.println("Network Location");
                bestLocation=locationNet;
            }else if(locationNet==null){
                System.out.println("GPS Location");
                bestLocation=locationGps;
            }
            else {
                Toast.makeText(HomeScreen.this,"unable to Find Location",Toast.LENGTH_SHORT).show();}
        }
        return bestLocation;
    }
    // Show the burger button on the ActionBar
    private void setupDrawer() {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(HomeScreen.this,mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            mDrawerLayout.addDrawerListener(toggle);
            toggle.syncState();
    }
    private void bookaCab(){
//        TOPIC = "/topics/weather"; //topic must match with what the receiver subscribed to
//        NOTIFICATION_TITLE = "### Demo Practice @@@";
//        NOTIFICATION_MESSAGE = "This is for the demo purpashnot sale";
//
//        JSONObject notification = new JSONObject();
//        JSONObject notifcationBody = new JSONObject();
//        try {
//            notifcationBody.put("title", NOTIFICATION_TITLE);
//            notifcationBody.put("message", NOTIFICATION_MESSAGE);
//
//            notification.put("to", TOPIC);
//            notification.put("data", notifcationBody);
//        } catch (JSONException e) {
//            System.out.println("@@ ###"+e.getMessage() );
//        }
//        sendNotification(notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void mapResponse(double locLatitude, double locLongitude, double latitude, double longitude) {
        progressDialog.setMessage("Finding routes... Please wait..");
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    String ou_response;
                    Request request = new Request.Builder()
                            .url("https://trueway-directions2.p.rapidapi.com/FindDrivingRoute?stops=" + locLatitude + "%2C%20" +
                                    +locLongitude + "%3B%20" + latitude + "%2C" + longitude + "")
                            .get()
                            .addHeader("x-rapidapi-key", "8a77f9e494msh82716613b999ab7p1de30bjsn6dc44a841b60")
                            .addHeader("x-rapidapi-host", "trueway-directions2.p.rapidapi.com")
                            .build();

                    Response response = client.newCall(request).execute();
                    if (response == null)
                    {
                        progressDialog.dismiss();
                        Toast.makeText(HomeScreen.this,"Location not Found!!!!",Toast.LENGTH_LONG).show();

                    } else {

                        ou_response = response.body().string().trim();
                        JSONObject result = new JSONObject(ou_response);
                        System.err.println(result);

                        bottomSheet.setVisibility(View.VISIBLE);

                        close.setVisibility(View.VISIBLE);
                        JSONObject postsArray = result.getJSONObject("route");
                        Double dis = Double.valueOf(postsArray.getString("distance"));
                        String totaodis = String.valueOf((dis / 1000));

                        distance.setText(totaodis + " KM");
                        System.out.println("Total Distance :>  " + postsArray.getString("distance"));
                        System.out.println("Total Time :>  " + postsArray.getString("duration"));
                        int tym = Integer.parseInt((postsArray.getString("duration")));
                        int day = (int) TimeUnit.SECONDS.toDays(tym);
                        long hours = TimeUnit.SECONDS.toHours(tym) - (day * 24);
                        long minute = TimeUnit.SECONDS.toMinutes(tym) - (TimeUnit.SECONDS.toHours(tym) * 60);
                        long second = TimeUnit.SECONDS.toSeconds(tym) - (TimeUnit.SECONDS.toMinutes(tym) * 60);
                        System.err.println(hours + " : " + minute + " : " + second);
                        time.setText(day + "Day " + hours + "Hr " + minute + "min " + second + "sec");

                        String pri = String.valueOf(12 * Double.parseDouble(totaodis));
                        price.setText(new DecimalFormat("##").format(Double.parseDouble(pri)));

                        System.out.println("Total Time :>  " + postsArray.getString("bounds"));

                        JSONObject geometry = postsArray.getJSONObject("geometry");

                        JSONArray coordinates = geometry.getJSONArray("coordinates");
                        System.out.println("coordinates  :>  " + coordinates);

                        PolylineOptions options = new PolylineOptions().width(20).color(Color.BLUE).geodesic(true);
                        for (int z = 0; z < coordinates.length(); z++) {
                            JSONArray latlong = coordinates.getJSONArray(z);
                            start = new LatLng(latlong.getDouble(0), latlong.getDouble(1));
                            options.add(start);

                        }
                        map.addPolyline(options);
                        progressDialog.dismiss();
                        progressDialog.setMessage("Searching places....");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }


//    private void sendNotification(JSONObject notification) {
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
//                new com.android.volley.Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response)
//                    {
//                        System.out.println("onResponse:11" + response.toString());
//                        createbookingonline();
////                        Log.i(TAG, "onResponse: " + response.toString());
////                        edtTitle.setText("");
////                        edtMessage.setText("");
//                    }
//                },
//                new com.android.volley.Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(HomeScreen.this, "Request error", Toast.LENGTH_LONG).show();
////                        Log.i(TAG, "onErrorResponse: Didn't work");
//                    }
//                }){
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("Authorization", serverKey);
//                params.put("Content-Type", contentType);
//                return params;
//            }
//        };
//        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
//    }
    private void createbookingonline(){
//        database = FirebaseDatabase.getInstance();
//        myRef = database.getReference("Bookings");
//        String sourceLoaction=source1, destination=destination1,userid="",distance="14528";
//        BookingHelper bookingHelper=new BookingHelper(sourceLoaction,destination,userid,distance);
//        myRef.setValue(bookingHelper);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setCurrentLocationSource(){
        String sourceaddress=placeNameSearchByLatlng(loc.getLatitude(),loc.getLongitude());
        source.setText(sourceaddress);
        source.clearFocus();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void selectLocatioOnMap(){
        cardView.setVisibility(View.INVISIBLE);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                // Setting the position for the marker
                markerOptions.position(latLng);
                markerOptions.getTitle();
                destintionLatlng=latLng;
                // Clears the previously touched position
                map.clear();
                // Animating to the touched position
                map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                // Placing a marker on the touched position
                map.addMarker(markerOptions);
                relativearea3.setVisibility(View.VISIBLE);
//                        mapResponse(loc.getLatitude(), loc.getLongitude(), latLng.latitude, latLng.longitude);
            }
        });



    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public String placeNameSearchByLatlng( double lag,double logt){
        String address ="null";

        try {
            String ouresponse;
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://trueway-geocoding.p.rapidapi.com/ReverseGeocode?location="+lag+"%2C"+logt+"&language=en")
                    .get()
                    .addHeader("x-rapidapi-key", "12b846ab87msh877142dc8ea1df2p1e2d3cjsnd6a16d0308c1")
                    .addHeader("x-rapidapi-host", "trueway-geocoding.p.rapidapi.com")
                    .build();

            Response response = client.newCall(request).execute();
            System.out.println("*****"+response);
            if(response==null) {
                System.out.println(response.body() + "-------------------------------");
                Toast.makeText(HomeScreen.this,"Location not Found!!!! 22222",Toast.LENGTH_LONG).show();
            }else{
                System.out.println(response.body() + "************");
                ouresponse=response.body().string().trim();
                JSONObject result=new JSONObject(ouresponse);
                JSONArray a=result.getJSONArray("results");
                JSONObject placeresult=a.getJSONObject(1);
                address=placeresult.getString("address");



            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        System.out.println("@@@@@@@@@@@@##############-------"+address);
        return address;
    }
    public void sendBookingData(){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Booking");

        String sourceLoaction=source.getText().toString()
                ,destinationd=destination.getText().toString(),userid="",distancetotal=distance.getText().toString();

//        BookingHelper bhelper=new BookingHelper(sourceLoaction,destinationd,userid,distancetotal);
        Log.e("User id  >> ",mAuth.getCurrentUser().getUid());
//        myRef.child(mAuth.getUid()).setValue(
//                uhelper);
        if(sourceLoaction.length()>0&&destination.length()>0&&userid.length()>0&&distance.length()>0) {


//            myRef.child(mAuth.getUid()).setValue(bhelper);
            Toast.makeText(HomeScreen.this, "Registration Succesfull", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(HomeScreen.this, WelcomeCoustomer.class));
        }
        else{
            Toast.makeText(HomeScreen.this,"Field Should not be empty",Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }
    //79.470395 29.279377
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    public void onPolygonClick(Polygon polygon) {

    }
    @Override
    public void onPolylineClick(Polyline polyline) {
    }

    public void onNavigationButtonClick(View view) {

        switch (view.getId()) {
            case R.id.profile:
                startActivity(new Intent(this,ProfileDetails.class));
                break;
            case R.id.rides:
                startActivity(new Intent(this,RidesDetails.class));
                break;
            case R.id.rideshistory:
                startActivity(new Intent(this,RidesDetails.class));
                break;
            case R.id.payment:
//                startActivity(new Intent(this,.class));
                break;
            case R.id.promocodes:
                startActivity(new Intent(this,Promocode.class));
                break;
            case R.id.suppot:
                startActivity(new Intent(this,TermsAndCodition.class));
                break;
            case R.id.termsandPolicy:
                startActivity(new Intent(this,TermsAndCodition.class));
                break;
            case R.id.btnLogout:

                break;

    }

    }



}