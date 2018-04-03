package o.durlock.foodfinder;

import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Brett on 3/22/2018.
 */

public class SingleFoodActivity extends AppCompatActivity {

    private String food_key = null;
    private TextView singleName;
    private TextView singleDistance;
    private TextView singleRating;
    private TextView singleDescription;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_food);

        final String food_key = getIntent().getExtras().getString("FoodId");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Food");

        singleName = (TextView) findViewById(R.id.singleName);
        singleDistance = (TextView) findViewById(R.id.singleDistance);
        singleRating = (TextView) findViewById(R.id.singleRating);
        singleDescription = (TextView) findViewById(R.id.singleDescription);

        mDatabase.child(food_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String food_name = (String) dataSnapshot.child("name").getValue();
                String food_distance = (String) dataSnapshot.child("distance").getValue();
                String food_rating = (String) dataSnapshot.child("rating").getValue();
                String food_description = (String) dataSnapshot.child("description").getValue();

                singleName.setText(food_name);
                singleDistance.setText(food_distance);
                singleRating.setText(food_rating);
                singleDescription.setText(food_description);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Button delete_btn = (Button) findViewById(R.id.singleDelete);
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(SingleFoodActivity.this)
                        .setTitle("Confirm Delete")
                        .setMessage("Are you sure you want to delete this restaurant?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
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
