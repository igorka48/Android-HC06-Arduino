package ua.dp.sq.btflasher.fragments;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ua.dp.sq.btflasher.R;
import ua.dp.sq.btflasher.activities.DevicesListActivity;
import ua.dp.sq.btflasher.activities.FavoritesActivity;
import ua.dp.sq.btflasher.activities.SendToHCModule;
import ua.dp.sq.btflasher.activities.userCabinet.LoginActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeviceListFragment extends Fragment {

    private static final String ARG_LIST = "list";

    public DeviceListFragment() {
        // Required empty public constructor
    }

    public static DeviceListFragment newInstance(ArrayList<String> list) {
        DeviceListFragment fragment = new DeviceListFragment();
        if (list != null) {
            Bundle args = new Bundle();
            args.putStringArrayList(ARG_LIST, list);
            fragment.setArguments(args);
        }
        return fragment;
    }

    private ListView mListDevices;
    private List<Map<String, String>> mDeviceList = new ArrayList<>();
    private BluetoothAdapter mBluetoothAdapter;
    ArrayList<String> ar1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ar1 = getArguments().getStringArrayList(ARG_LIST);
        } else {
            ar1 = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Устройства");


        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_device_list, container, false);
        v.findViewById(R.id.pair_btn).setOnClickListener( b -> onPairDeviceBtn(b));
        mListDevices = (ListView) v.findViewById(R.id.listView);
        return v;
    }


    @Override
    public void onStart() {
        super.onStart();

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            Set<BluetoothDevice> bondedSet = mBluetoothAdapter.getBondedDevices();
            Log.v("LOG", "BluetoothDemo : bondedSet: " + bondedSet);

            int count = 0;
            mDeviceList.clear();
            if (bondedSet.size() > 0) {
                for (BluetoothDevice device : bondedSet) {
                    Map<String, String> datum = new HashMap<>(2);
                    datum.put("name", device.getName());
                    datum.put("address", device.getAddress());
                    mDeviceList.add(datum);
                }

                SimpleAdapter adapter = new SimpleAdapter(getContext(), mDeviceList,
                        android.R.layout.simple_list_item_2,
                        new String[]{"name", "address"},
                        new int[]{android.R.id.text1, android.R.id.text2});
                mListDevices.setAdapter(adapter);
                mListDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Map<String, String> data = mDeviceList.get(i);
                        Intent intent1 = new Intent(getContext(), SendToHCModule.class);
                        intent1.putExtra("name", data.get("name"));
                        intent1.putExtra("address", data.get("address"));

                        if (ar1.size() > 0) {
                            intent1.putStringArrayListExtra("list", ar1);
                            startActivity(intent1);
                        } else {
                            Toast.makeText(getContext(), R.string.add_location_warning,
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), FavoritesActivity.class);
                            startActivity(intent);
                        }
                    }
                });

            } else {
                //bondedAdapter.add("No Devices");
            }
            // listViewPairedDevices.setAdapter(bondedAdapter);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("LOG", e.toString(), e.fillInStackTrace());
        }
    }

    public void onPairDeviceBtn(View view) {
        startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
    }


//    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                BluetoothDevice device = intent
//                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//
//
//                Map<String, String> datum = new HashMap<>(2);
//                datum.put("name", device.getName());
//                datum.put("address", device.getAddress());
//                mDeviceList.add(datum);
//
//
//
//                Log.i("BT", device.getName() + "\n" + device.getAddress());
//
//
//                SimpleAdapter adapter = new SimpleAdapter(DevicesListActivity.this, mDeviceList,
//                        android.R.layout.simple_list_item_2,
//                        new String[] {"name", "address" },
//                        new int[] {android.R.id.text1, android.R.id.text2 });
//
//                listView.setAdapter(adapter);
//                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                        Map<String, String> data = mDeviceList.get(i);
//                        Intent intent1 = new Intent(DevicesListActivity.this, SendToHCModule.class);
//                        intent1.putExtra("name", data.get("name"));
//                        intent1.putExtra("address", data.get("address"));
//                    }
//                });
//            }
//        }
//    };

}
