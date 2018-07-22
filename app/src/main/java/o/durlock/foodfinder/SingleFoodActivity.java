package o.durlock.foodfinder;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

/**
 * Created by Brett on 3/22/2018.
 */

public class SingleFoodActivity extends AppCompatActivity {

    //GPS Constant Permission
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;

    private LatLng sourcePoint;

    private String food_key = null;
    private TextView singleName;
    private TextView singleDistance;
    private TextView singleDescription;
    private TextView singleNotes;
    private TextView singleType;
    private TextView singleWho;

    private RatingBar singleRating;
    private RatingBar singleCost;

    private String food_id;

    private DatabaseReference mDatabase;
    private GeoDataClient mGeoDataClient;
    LatLng destinationPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_food);

        food_key = getIntent().getExtras().getString("FoodId");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Food");

        singleName = findViewById(R.id.singleName);
        singleDistance = findViewById(R.id.singleDistance);

        singleDescription = findViewById(R.id.singleDescription);
        singleNotes = findViewById(R.id.singleNotes);
        singleType = findViewById(R.id.singleType);
        singleWho = findViewById(R.id.singleWho);

        singleRating = findViewById(R.id.singleRating);
        singleCost = findViewById(R.id.singleCost);

        mGeoDataClient = Places.getGeoDataClient(this);

        //Create the mini map for the top of the screen
        final com.google.android.gms.maps.MapFragment mapFragment = (com.google.android.gms.maps.MapFragment) getFragmentManager().findFragmentById(R.id.singleMapFragment);

        //Get the user's current location
        LocationManager mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        if (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //One or both permissions denied
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSION_ACCESS_COARSE_LOCATION);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSION_ACCESS_FINE_LOCATION);
            }
        } else {
            Location location = mLocationManager.getLastKnownLocation(mLocationManager.getBestProvider(criteria, false));

            double lat = location.getLatitude();
            double lon = location.getLongitude();

            sourcePoint = new LatLng(lat,lon);
        }

        mDatabase.child(food_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                food_id = (String) dataSnapshot.child("id").getValue();

                //Values from the database
                String food_description = (String) dataSnapshot.child("description").getValue();
                String food_notes = (String) dataSnapshot.child("notes").getValue();
                String food_type = (String) dataSnapshot.child("type").getValue();
                String food_who = (String) dataSnapshot.child("who").getValue();

                singleType.setText(food_type);

                //These are optional, so we'll make sure they exist
                if(food_description == null || food_description == ""){
                    singleDescription.setVisibility(View.GONE);
                } else{
                    singleDescription.setText(food_description);
                }

                if(food_notes == null || food_notes == ""){
                    singleNotes.setVisibility(View.GONE);
                } else {
                    singleNotes.setText("Notes: " + food_notes);
                }

                if(food_who == null || food_who == ""){
                    singleWho.setVisibility(View.GONE);
                } else {
                    singleWho.setText("Recommended by: " + food_who);
                }

                //Values from the Google Places ID
                if(food_id != null){
                    mGeoDataClient.getPlaceById(food_id).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                            if (task.isSuccessful()){
                                PlaceBufferResponse places = task.getResult();
                                Place mPlace = places.get(0);

                                final String food_name = (String) mPlace.getName();
                                Float food_rating = mPlace.getRating();
                                int food_cost = mPlace.getPriceLevel();

                                singleName.setText(food_name);
                                singleRating.setRating(food_rating);
                                singleCost.setRating(food_cost);

                                destinationPoint = mPlace.getLatLng();

                                //Check if sourcePoint is set, if not then we don't display distance
                                if (sourcePoint != null){
                                    //TODO: Get the distance using google's distancematrix to get a useful distance, not just straight distance
                                    float[] results = new float[1];
                                    Location.distanceBetween(sourcePoint.latitude, sourcePoint.longitude, destinationPoint.latitude, destinationPoint.longitude, results);

                                    double dist_miles = results[0] * 0.000621371;
                                    String outDistance = String.format(Locale.getDefault(),"%.1f",dist_miles) + " mi";
                                    singleDistance.setText(outDistance);
                                } else {
                                    singleDistance.setVisibility(View.GONE);
                                }

                                mapFragment.getMapAsync(new OnMapReadyCallback() {
                                    @Override
                                    public void onMapReady(GoogleMap mMap) {
                                        mMap.setMyLocationEnabled(true);

                                        //For zooming automatically to the location of the marker
                                        CameraPosition cameraPosition = new CameraPosition.Builder().target(destinationPoint).zoom(12).build();
                                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                        mMap.addMarker(new MarkerOptions().position(destinationPoint).title(food_name));
                                    }
                                });
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Navigate button
        Button navigate_btn = findViewById(R.id.singleNav);
        navigate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //We start the intent with the navigation going to the destination lat and long
                Intent navIntent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q=" + destinationPoint.latitude + "," + destinationPoint.longitude ));
                startActivity(navIntent);
            }
        });

        //Delete button
        Button delete_btn = findViewById(R.id.singleDelete);
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(SingleFoodActivity.this)
                        .setTitle("Confirm Delete")
                        .setMessage("Are you sure you want to delete this restaurant?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //android.app.Fragment frg = null;
                                //frg = getFragmentManager().findFragmentById(R.id.map_fragment);
                                //final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                //ft.detach(frg);
                                //ft.attach(frg);
                                //ft.commit();
                                mDatabase.child(food_key).removeValue();
                                finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_delete)
                        .show();

            }
        });

    }
}
