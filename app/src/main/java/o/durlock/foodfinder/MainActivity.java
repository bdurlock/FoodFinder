package o.durlock.foodfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mFoodList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFoodList = (RecyclerView) findViewById(R.id.food_list);
        mFoodList.setHasFixedSize(true);
        mFoodList.setLayoutManager(new LinearLayoutManager(this));
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Food");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /* FoodViewHolder
    purpose: These are the tools that allow us to set variables in the layout
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
                        Intent singleFoodActivity = new Intent(MainActivity.this, SingleFood.class);
                        singleFoodActivity.putExtra("FoodId",food_key);
                        startActivity(singleFoodActivity);
                    }
                });
            }
        };
        mFoodList.setAdapter(FBRA);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        else if(id == R.id.addFood){
            Intent addIntent = new Intent(MainActivity.this, AddFood.class);
            startActivity(addIntent);
        }

        else if (id == R.id.map) {
            Intent addIntent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(addIntent);
        }

        else if (id == R.id.mode_button) {
            Intent addIntent = new Intent(MainActivity.this, Discovery.class);
            startActivity(addIntent);
        }
        return super.onOptionsItemSelected(item);
    }

}

