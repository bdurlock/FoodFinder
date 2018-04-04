package o.durlock.foodfinder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private Integer status;

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;


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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Food");

        //Initializes Recycler View and Layout Manager.
        recyclerView = (RecyclerView) rootView.findViewById(R.id.food_list);
        final LinearLayoutManager lm = new LinearLayoutManager(getContext());

        FirebaseRecyclerAdapter<Food, FoodViewHolder> FBRA = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_row,
                FoodViewHolder.class,
                databaseReference
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
                        Intent singleFoodActivity = new Intent(getActivity(), SingleFoodActivity.class);
                        singleFoodActivity.putExtra("FoodId",food_key);
                        startActivity(singleFoodActivity);
                    }
                });
            }
        };

        recyclerView.setAdapter(FBRA);
        recyclerView.setLayoutManager(lm);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedIstanceState){

       Button add_btn = (Button) getActivity().findViewById(R.id.add_button);

       add_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view){
               startActivity(new Intent(getActivity(), AddFoodActivity.class));
           }
       });

       Button list_btn = (Button) getActivity().findViewById(R.id.find_button);
       list_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view){
               //Google Place Picker API
               try{
                   int PLACE_PICKER_REQUEST = 1;
                   PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                   startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
               } catch (GooglePlayServicesNotAvailableException e){
                   e.printStackTrace();
               } catch (GooglePlayServicesRepairableException e){
                   e.printStackTrace();
               }
            }
        });
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
