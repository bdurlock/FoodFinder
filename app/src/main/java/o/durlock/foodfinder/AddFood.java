package o.durlock.foodfinder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;

/**
 * Created by Brett on 3/22/2018.
 */

public class AddFood extends AppCompatActivity {
   private FirebaseDatabase database;
   private DatabaseReference myRef;
   EditText editName;
   EditText editDistance;
   EditText editRating;
   EditText editDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);
        database = FirebaseDatabase.getInstance();
    }

   public void addButtonClicked(View view){
       editName = (EditText) findViewById(R.id.addNameEdit);
       editDistance = (EditText) findViewById(R.id.addDistanceEdit);
       editRating = (EditText) findViewById(R.id.addRatingEdit);
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

   }
}
