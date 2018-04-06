package o.durlock.foodfinder;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.app.Fragment;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;

/**
 * Created by Brett on 3/22/2018.
 */

public class AddFoodActivity extends AppCompatActivity {
   private FirebaseDatabase database;
   private DatabaseReference myRef;
   private GeoDataClient mGeoDataClient;
   private GoogleMap mMap;
   private String place_id;
   private Place myPlace;
   TextView foodName;
   EditText editDescription;
   RatingBar ratingBar;
   RatingBar costBar;
   Spinner editType;
   EditText editWho;
   EditText editNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);
        database = FirebaseDatabase.getInstance();

        final com.google.android.gms.maps.MapFragment mapFragment = (com.google.android.gms.maps.MapFragment) getFragmentManager().findFragmentById(R.id.addMapFragment);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            place_id = extras.getString("id");
            foodName = findViewById(R.id.addNameText);
            ratingBar = findViewById(R.id.addRating);
            costBar = findViewById(R.id.addCost);

            mGeoDataClient = Places.getGeoDataClient(this, null);

            mGeoDataClient.getPlaceById(place_id).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                    if (task.isSuccessful()) {
                        PlaceBufferResponse places = task.getResult();
                        myPlace = places.get(0);
                        final CharSequence fName = myPlace.getName();
                        foodName.setText(fName);
                        ratingBar.setRating(myPlace.getRating());
                        costBar.setRating(myPlace.getPriceLevel());
                        final LatLng loc = myPlace.getLatLng();
                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap mMap) {
                                // For zooming automatically to the location of the marker
                                CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(12).build();
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                mMap.addMarker(new MarkerOptions().position(loc).title((String) fName));
                            }
                        });
                    }
                }
            });
        }

        //Create the drop down menu
        Spinner spinner = (Spinner) findViewById(R.id.typeDropDown);
        //Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.typesArray, android.R.layout.simple_spinner_item);
        //Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt("Choose Food Type");
        //Apply the adapter to the spinner
        spinner.setAdapter(adapter);

    }

   public void addButtonClicked(View view){
       if(place_id != null){
           editDescription = (EditText) findViewById(R.id.addDescriptionEdit);
           ratingBar = (RatingBar) findViewById(R.id.addRating);
           costBar = (RatingBar) findViewById(R.id.addCost);
           editType = (Spinner) findViewById(R.id.typeDropDown);
           editWho = (EditText) findViewById(R.id.addWhoRecommended);
           editNotes = (EditText) findViewById(R.id.addNotes);

           String getDescription = editDescription.getText().toString();
           float getRating = ratingBar.getRating();
           int getCost = (int) costBar.getRating();
           String getType = editType.getSelectedItem().toString();
           long getTypeID = editType.getSelectedItemId();
           String getWho = editWho.getText().toString();
           String getNotes = editNotes.getText().toString();

           long date = System.currentTimeMillis();

           myRef = database.getInstance().getReference().child("Food");

           DatabaseReference newFood = myRef.push();
           newFood.child("id").setValue(place_id);
           if(!TextUtils.isEmpty(getDescription)){
               newFood.child("description").setValue(getDescription);
           }
           newFood.child("type").setValue(getType);
           newFood.child("typeid").setValue(getTypeID);
           if(!TextUtils.isEmpty(getWho)){
               newFood.child("who").setValue(getWho);
           }
           if(!TextUtils.isEmpty(getNotes)){
               newFood.child("notes").setValue(getNotes);
           }

           //Only store the rating and the cost if it's different from the google provided values
           //This allows the user to set their own rating if they disagree with the one from google
           if (getRating != myPlace.getRating()){
               newFood.child("rating").setValue(getRating);
           }
           if (getCost != myPlace.getPriceLevel()) {
               newFood.child("cost").setValue(getCost);
           }

           finish();
       }
   }
}
