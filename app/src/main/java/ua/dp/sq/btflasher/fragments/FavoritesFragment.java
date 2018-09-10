package ua.dp.sq.btflasher.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ua.dp.sq.btflasher.R;

import ua.dp.sq.btflasher.activities.DevicesListActivity;

import ua.dp.sq.btflasher.activities.userCabinet.LoginActivity;
import ua.dp.sq.btflasher.adapters.FavViewDataAdapter;
import ua.dp.sq.btflasher.data.LocationItem;


public class FavoritesFragment extends Fragment {

     Button sendCoordinates;

    private RecyclerView recyclerView;
    private TextView emptyView_tv;
    private Button emptyView_btn;
    private Button sendValues;
    private Intent intent;
    private RecyclerView.Adapter adapter;
    private ItemTouchHelper.SimpleCallback simpleItemTouchCallback;
    private ItemTouchHelper itemTouchHelper;

    private DatabaseReference databaseReference;
    private ArrayList<LocationItem> locationItems;
    private ProgressBar progressBar;
    public FavoritesFragment() {

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("locations");

        locationItems = new ArrayList<>();

        progressBar=(ProgressBar)getView().findViewById(R.id.progressBar);
        sendValues = (Button) getView().findViewById(R.id.sendCoordinates);
        recyclerView = (RecyclerView) getView().findViewById(R.id.favList);
        emptyView_tv = (TextView) getView().findViewById(R.id.empty_view_tv);
        emptyView_btn = (Button)getView(). findViewById(R.id.empty_view_btn);

        getFavorites();

        sendCoordinates=(Button) getView().findViewById(R.id.sendCoordinates);
        sendCoordinates.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                List<LocationItem> locationItems = ((FavViewDataAdapter) adapter).getLocationItems();

                if(locationItems.size() > 0) {
                    ArrayList<String> values = new ArrayList<>();
                    ArrayList checkDublicates = new ArrayList();

                    for (int i = 0; i < locationItems.size(); i++) {
                        LocationItem locationItem = locationItems.get(i);

                        if (locationItem.getChecked()) {
                            int properNavNumber = Integer.parseInt(String.valueOf(locationItem.getNavigatorNumber())) - 1;
                            checkDublicates.add(properNavNumber);
                            String res = properNavNumber + "";

                            String lat = String.format("%02.6f", locationItem.getLat());
                            String lng = String.format("%02.6f", locationItem.getLng());

                            lat = lat.replace(",", "");
                            lng = lng.replace(",", "");

                            values.add("progeeprom" + lat + lng + "W" + res);
                            Log.e("TAG", res);
                        }
                    }

                    for (int i = 0; i < checkDublicates.size(); i++) {
                        for (int j = i + 1; j < checkDublicates.size(); j++) {
                            if(checkDublicates.get(i) == checkDublicates.get(j)) {
                               // Toast.makeText(FavoritesFragment.this, R.string.check_dublicates,
                                      //  Toast.LENGTH_SHORT).show();
                                Toast.makeText(FavoritesFragment.super.getContext(),R.string.check_dublicates,
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }

                    Intent intent = new Intent(FavoritesFragment.super.getContext(), DevicesListActivity.class);
                    intent.putStringArrayListExtra("list", values);
                    startActivity(intent);
                }

            }
        });







        return view;
    }




    private void getFavorites() {
        if (!isSignedIn()) {
            Log.d("firebase", "not auth");
            AlertDialog.Builder builder = new AlertDialog.Builder(FavoritesFragment.super.getContext());
            builder.setTitle(R.string.warning)
                    .setMessage(R.string.login_first)
                    .setIcon(R.mipmap.ic_launcher)
                    .setCancelable(true)
                    .setNegativeButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    Intent intent = new Intent(FavoritesFragment.super.getContext(), LoginActivity.class);
                                    startActivity(intent);
                                }
                            });
            AlertDialog alert = builder.create();

            alert.show();
        } else {
            checkEmptyList();

            databaseReference.child(getUid()).addValueEventListener(new ValueEventListener() {
                public void onDataChange(DataSnapshot snapshot) {
                    showProgress();
                    locationItems = new ArrayList<LocationItem>();

                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        LocationItem locationItem = postSnapshot.getValue(LocationItem.class);
                        locationItem.setKey(postSnapshot.getKey());
                        locationItems.add(locationItem);
                        adapter = new FavViewDataAdapter(locationItems);
                    }

                    simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                        @Override
                        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                            return false;
                        }

                        @Override
                        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                            // Remove swiped item from list and notify the RecyclerView

                            final int position = viewHolder.getAdapterPosition();

                            databaseReference.child(getUid()).child(locationItems.get(position).getKey()).removeValue();

                            adapter.notifyItemRemoved(position);
                        }
                    };

                    itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
                    itemTouchHelper.attachToRecyclerView(recyclerView);
                    recyclerView.setAdapter(adapter);
                    hideProgress();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }
    }

    private void checkEmptyList() {
        databaseReference.child(getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    recyclerView.setVisibility(View.INVISIBLE);
                    emptyView_tv.setVisibility(View.VISIBLE);
                    emptyView_btn.setVisibility(View.VISIBLE);
                    sendValues.setVisibility(View.INVISIBLE);

                    emptyView_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getActivity().finish();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




    /*  public void onSendValues(View view) {
        List<LocationItem> locationItems = ((FavViewDataAdapter) adapter).getLocationItems();

        if(locationItems.size() > 0) {
            ArrayList<String> values = new ArrayList<>();
            ArrayList checkDublicates = new ArrayList();

            for (int i = 0; i < locationItems.size(); i++) {
                LocationItem locationItem = locationItems.get(i);

                if (locationItem.getChecked()) {
                    int properNavNumber = Integer.parseInt(String.valueOf(locationItem.getNavigatorNumber())) - 1;
                    checkDublicates.add(properNavNumber);
                    String res = properNavNumber + "";

                    String lat = String.format("%02.6f", locationItem.getLat());
                    String lng = String.format("%02.6f", locationItem.getLng());

                    lat = lat.replace(",", "");
                    lng = lng.replace(",", "");

                    values.add("progeeprom" + lat + lng + "W" + res);
                    Log.e("TAG", res);
                }
            }

            for (int i = 0; i < checkDublicates.size(); i++) {
                for (int j = i + 1; j < checkDublicates.size(); j++) {
                    if(checkDublicates.get(i) == checkDublicates.get(j)) {
                        Toast.makeText(FavoritesFragment.this, R.string.check_dublicates,
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }

            Intent intent = new Intent(this, DevicesListActivity.class);
            intent.putStringArrayListExtra("list", values);
            startActivity(intent);
        }
    }*/

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }
    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public boolean isSignedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}