package o.durlock.foodfinder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 *
 * This listfragment displays the list of restaurants on the main page
 */
public class ListFragment extends Fragment {

    //GPS Constant Permission
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;

    private OnFragmentInteractionListener mListener;
    private GeoDataClient mGeoDataClient;

    private LatLng sourcePoint;

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Setup local variables
        RecyclerView mRecyclerView;
        DatabaseReference mDatabaseReference;

        //Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Food");

        //Initializes Recycler View and Layout Manager.
        mRecyclerView = rootView.findViewById(R.id.food_list);
        final LinearLayoutManager lm = new LinearLayoutManager(getContext());

        LocationManager mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        //We check for permission now
        // https://stackoverflow.com/questions/33327984/call-requires-permissions-that-may-be-rejected-by-user
        if (ContextCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //One or both permissions denied
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSION_ACCESS_COARSE_LOCATION);
            }
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSION_ACCESS_FINE_LOCATION);
            }
        } else {
            Location location = mLocationManager.getLastKnownLocation(mLocationManager.getBestProvider(criteria, false));

            double lat = location.getLatitude();
            double lon = location.getLongitude();

            sourcePoint = new LatLng(lat,lon);
        }

        FirebaseRecyclerAdapter<Food, FoodViewHolder> FBRA = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_row,
                FoodViewHolder.class,
                mDatabaseReference
        ) {
            @Override
            protected void populateViewHolder(final FoodViewHolder viewHolder, Food model, int position) {
                final String id = model.getID();
                mGeoDataClient = Places.getGeoDataClient(getActivity());
                mGeoDataClient.getPlaceById(id).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                        if (task.isSuccessful()){
                            PlaceBufferResponse places = task.getResult();
                            //This is a known bug. Fine to ignore.
                            Place mPlace = places.get(0);
                            String mName = (String) mPlace.getName();
                            viewHolder.setName(mName);
                            viewHolder.setRating(mPlace.getRating());
                            viewHolder.setCost(mPlace.getPriceLevel());

                            //Check if sourcePoint is set, if not then we don't display distance
                            if (sourcePoint != null){
                                //Now calculate the distance
                                LatLng destinationPoint = mPlace.getLatLng();

                                //TODO: Get the distance using google's distancematrix to get a useful distance, not just straight distance
                                float[] results = new float[1];
                                Location.distanceBetween(sourcePoint.latitude, sourcePoint.longitude, destinationPoint.latitude, destinationPoint.longitude, results);

                                double dist_miles = results[0] * 0.000621371;
                                viewHolder.setDistance(String.format(Locale.getDefault(),"%.1f",dist_miles) + " mi");
                            } else {
                                viewHolder.setDistance("");
                            }

                        }
                    }
                });
                final String food_key = getRef(position).getKey();

                viewHolder.setTypeText(model.getType());

                //Make the whole box an onclick listener
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent singleFoodActivity = new Intent(getActivity(), SingleFoodActivity.class);
                        singleFoodActivity.putExtra("FoodId",food_key);
                        startActivity(singleFoodActivity);
                    }
                });
            }
        };

        mRecyclerView.setAdapter(FBRA);
        mRecyclerView.setLayoutManager(lm);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedIstanceState){
        //Create the listener to find new food
        FloatingActionButton find_btn = getActivity().findViewById(R.id.find_button);
        find_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FindFood();
            }
        });
    }

    public void FindFood(){
        //Google Place Picker API
        try{
            AutocompleteFilter  typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
                    .build();
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .setFilter(typeFilter)
                    .build(getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesNotAvailableException e){
            e.printStackTrace();
        } catch (GooglePlayServicesRepairableException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                Log.i(TAG, "Place: " + place.getName());

                //Get the data from the place
                final String id = place.getId();

                //Spawn the add activity with the extra information
                Intent mAddIntent = new Intent(getActivity(), AddFoodActivity.class);
                mAddIntent.putExtra("id",id);
                startActivity(mAddIntent);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public FoodViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView tv_restaurant = mView.findViewById(R.id.restaurantText);
            tv_restaurant.setText(name);
        }

        public void setRating(float rating) {
            RatingBar tv_rating = mView.findViewById(R.id.ratingBar);
            tv_rating.setRating(rating);
        }

        public float getRating() {
            RatingBar tv_rating_get = mView.findViewById(R.id.ratingBar);
            return tv_rating_get.getRating();
        }

        public void setCost(int Cost) {
            RatingBar tv_cost = mView.findViewById(R.id.costBar);
            tv_cost.setRating(Cost);
        }

        public void setTypeText(String type){
            TextView tv_type = mView.findViewById(R.id.typeText);
            tv_type.setText(type);
        }

        public void setDistance(String distance){
            TextView dist = mView.findViewById(R.id.distanceText);
            dist.setText(distance);
        }
    }
}
