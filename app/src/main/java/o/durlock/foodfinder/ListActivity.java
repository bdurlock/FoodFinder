package o.durlock.foodfinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ListActivity extends AppCompatActivity {

    private RecyclerView mFoodList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mFoodList = (RecyclerView) findViewById(R.id.food_list);
        mFoodList.setHasFixedSize(true);
        mFoodList.setLayoutManager(new LinearLayoutManager(this));
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Food");
    }

    /*
    Purpose: These are the tools that allow us to set variables in the layout
     */
    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public FoodViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView tv_restaurant = (TextView) mView.findViewById(R.id.restaurantText);
            tv_restaurant.setText(name);
        }

        public void setDistance(String dist) {
            TextView tv_distance = (TextView) mView.findViewById(R.id.distanceText);
            tv_distance.setText(dist);
        }

        public void setRating(String rating) {
            TextView tv_rating = (TextView) mView.findViewById(R.id.ratingText);
            tv_rating.setText(rating);
        }

        public void setDescription(String desc) {
            TextView tv_description = (TextView) mView.findViewById(R.id.descriptionText);
            tv_description.setText(desc);
        }
    }

    /*
purpose: Runs at start. Begins the Firebase session and sets up click listeners
 */
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Food, FoodViewHolder> FBRA = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_row,
                FoodViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                final String food_key = getRef(position).getKey().toString();
                viewHolder.setName(model.getName());
                viewHolder.setDistance(model.getDistance());
                viewHolder.setRating(model.getRating());
                viewHolder.setDescription(model.getDescription());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent singleFoodActivity = new Intent(ListActivity.this, SingleFoodActivity.class);
                        singleFoodActivity.putExtra("FoodId",food_key);
                        startActivity(singleFoodActivity);
                    }
                });
            }
        };
        mFoodList.setAdapter(FBRA);

        // Create listener for temporary add button
        Button add_btn = (Button)findViewById(R.id.add_button);

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListActivity.this, AddFoodActivity.class));
            }
        });

        // Create the button linking between main and discovery mode
        Button mode_btn = (Button)findViewById(R.id.mode_button);

        mode_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startActivity(new Intent(ListActivity.this, DiscoveryActivity.class));
            }
        });

    }
}
