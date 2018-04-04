package o.durlock.foodfinder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;

/**
 * Created by Brett on 3/22/2018.
 */

public class AddFoodActivity extends AppCompatActivity {
   private FirebaseDatabase database;
   private DatabaseReference myRef;
   private GeoDataClient mGeoDataClient;
   EditText editName;
   EditText editDistance;
   EditText editRating;
   EditText editDescription;
   TextView foodName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);
        database = FirebaseDatabase.getInstance();

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            String place_id = extras.getString("id");
            foodName = findViewById(R.id.addNameText);

            mGeoDataClient = Places.getGeoDataClient(this, null);

            mGeoDataClient.getPlaceById(place_id).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                    if (task.isSuccessful()) {
                        PlaceBufferResponse places = task.getResult();
                        Place myPlace = places.get(0);
                        foodName.setText(myPlace.getName());
                    }
                }
            });
        }
    }

   public void addButtonClicked(View view){
       editDescription = (EditText) findViewById(R.id.addDescriptionEdit);

       String name = editName.getText().toString();
       String distance = editDistance.getText().toString();
       String rating = editRating.getText().toString();
       String description = editDescription.getText().toString();

       long date = System.currentTimeMillis();

       SimpleDateFormat sdf = new SimpleDateFormat("MMM dd YYYY, yyy h:mm a");
       String dateString = sdf.format(date);

       myRef = database.getInstance().getReference().child("Food");

       DatabaseReference newFood = myRef.push();
       newFood.child("name").setValue(name);
       newFood.child("distance").setValue(distance);
       newFood.child("rating").setValue(rating);
       newFood.child("description").setValue(description);
       newFood.child("time").setValue(dateString);

       finish();
   }
}
