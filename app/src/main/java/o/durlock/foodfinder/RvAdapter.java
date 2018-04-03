package o.durlock.foodfinder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Brett on 3/29/2018.
 */
public class RvAdapter extends RecyclerView.Adapter<RvAdapter.FoodViewHolder> {

    private ArrayList<Food> entries;

    public RvAdapter(ArrayList<Food> entries) {
        this.entries = entries;
    }

    @Override
    public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_row, null);

        return new FoodViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FoodViewHolder holder, int position){
        holder.setDescription(entries.get(position).getDescription());
        holder.setDistance(entries.get(position).getDistance());
        holder.setName(entries.get(position).getName());
        holder.setRating(entries.get(position).getRating());
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

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
}
