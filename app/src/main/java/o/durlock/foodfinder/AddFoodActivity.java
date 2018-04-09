package o.durlock.foodfinder;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * AddFoodActivity: After selecting a restaurant from the google place interface,
 * we give the user a chance to input extra information and we add that to the database.
 *
 * Created by Brett on 3/22/2018.
 */

public class AddFoodActivity extends AppCompatActivity {
    private FirebaseDatabase mDatabase;
    private String mPlaceId;
    private Place mPlace;
    private TextView mFoodName;
    private RatingBar mRatingBar;
    private RatingBar mCostBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);
        mDatabase = FirebaseDatabase.getInstance();

        //Setup the geodataclient variable for the places id info
        GeoDataClient mGeoDataClient;

        //Create the mini map for the top of the screen
        final com.google.android.gms.maps.MapFragment mapFragment = (com.google.android.gms.maps.MapFragment) getFragmentManager().findFragmentById(R.id.addMapFragment);

        //extras store the id that is passed from the google places api.
        //This should never be null, if it's null then we have a problem.
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            //Get the id extra info
            mPlaceId = extras.getString("id");

            //Find the view elements
            mFoodName = findViewById(R.id.addNameText);
            mRatingBar = findViewById(R.id.addRating);
            mCostBar = findViewById(R.id.addCost);

            //Initiate the places variable
            mGeoDataClient = Places.getGeoDataClient(this);

            //Get the place based on the id.
            mGeoDataClient.getPlaceById(mPlaceId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                    if (task.isSuccessful()) {
                        PlaceBufferResponse places = task.getResult();
                        //Google says this can be safely ignored. Known issue.
                        mPlace = places.get(0);
                        final CharSequence fName = mPlace.getName();
                        mFoodName.setText(fName);
                        mRatingBar.setRating(mPlace.getRating());
                        mCostBar.setRating(mPlace.getPriceLevel());
                        final LatLng loc = mPlace.getLatLng();
                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap mMap) {
                                //For zooming automatically to the location of the marker
                                CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(12).build();
                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                mMap.addMarker(new MarkerOptions().position(loc).title((String) fName));
                            }
                        });
                    }
                }
            });
        }

        //Create the drop down menu
        Spinner spinner = findViewById(R.id.typeDropDown);
        //Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.typesArray, android.R.layout.simple_spinner_item);
        //Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt("Choose Food Type");
        //Apply the adapter to the spinner
        spinner.setAdapter(adapter);

    }

    /*
    This gets called when the 'add' button is clicked on the bottom of the page.
    We only want to store data that we need, and not anything provided to us from
    Google places, because that would be in violation of their terms of use.
     */
   public void addButtonClicked(View view){
       //Setup variables
       DatabaseReference myRef;
       EditText editDescription;

       Spinner editType;
       EditText editWho;
       EditText editNotes;

       if(mPlaceId != null){
           //Get all the elements
           //TODO: Add ratingbar and costbar support (need to fix the listfragment and uncomment the stuff here)
           editDescription = findViewById(R.id.addDescriptionEdit);
           //ratingBar = findViewById(R.id.addRating);
           //costBar = findViewById(R.id.addCost);
           editType = findViewById(R.id.typeDropDown);
           editWho = findViewById(R.id.addWhoRecommended);
           editNotes = findViewById(R.id.addNotes);

           //Pull the values out
           String getDescription = editDescription.getText().toString();
           //float getRating = ratingBar.getRating();
           //int getCost = (int) costBar.getRating();
           String getType = editType.getSelectedItem().toString();
           //I'm not sure if the typeID value will be useful, but it doesn't hurt to store it
           long getTypeID = editType.getSelectedItemId();
           String getWho = editWho.getText().toString();
           String getNotes = editNotes.getText().toString();

           //Get system time for time stamp
           long date = System.currentTimeMillis();

           //initialize the database variable
           myRef = mDatabase.getReference().child("Food");

           //Push and add all of the values
           DatabaseReference newFood = myRef.push();
           newFood.child("id").setValue(mPlaceId);
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
           //TODO: Fix this feature. Also update activity_add_food to isindicator(false) and
           //Listfragment to use the user's ratings.
           //Currently, this feature is not working so it I'm forcing it to always use the google
           //ratings.
           /*
           if (getRating != myPlace.getRating()){
               newFood.child("rating").setValue(getRating);
           }
           if (getCost != myPlace.getPriceLevel()) {
               newFood.child("cost").setValue(getCost);
           }
           */

           //Close this activity
           finish();
       }
   }
}
