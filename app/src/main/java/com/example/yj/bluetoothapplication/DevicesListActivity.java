package com.example.yj.bluetoothapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DevicesListActivity extends AppCompatActivity {


    private ListView listView;
    private List<Map<String, String>> mDeviceList = new ArrayList<>();
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_list);

        listView = (ListView) findViewById(R.id.listView);

    }


    @Override
    protected void onDestroy() {
        //unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    protected  void onStart(){
        super.onStart();
        try{
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            Set<BluetoothDevice> bondedSet = mBluetoothAdapter.getBondedDevices();
            Log.v("LOG", "BluetoothDemo : bondedSet: "+bondedSet);

            int count = 0;
            mDeviceList.clear();
            if(bondedSet.size() > 0){
                for(BluetoothDevice device : bondedSet){
                    Map<String, String> datum = new HashMap<>(2);
                    datum.put("name", device.getName());
                    datum.put("address", device.getAddress());
                    mDeviceList.add(datum);
                }



                SimpleAdapter adapter = new SimpleAdapter(DevicesListActivity.this, mDeviceList,
                        android.R.layout.simple_list_item_2,
                        new String[]{"name", "address"},
                        new int[]{android.R.id.text1, android.R.id.text2});
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Map<String, String> data = mDeviceList.get(i);
                        Intent intent1 = new Intent(DevicesListActivity.this, MainActivity.class);
                        intent1.putExtra("name", data.get("name"));
                        intent1.putExtra("address", data.get("address"));
                        startActivity(intent1);
                    }
                });

            }else{
                //bondedAdapter.add("No Devices");
            }

            // listViewPairedDevices.setAdapter(bondedAdapter);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("LOG", e.toString(),e.fillInStackTrace());
        }
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
//                        Intent intent1 = new Intent(DevicesListActivity.this, MainActivity.class);
//                        intent1.putExtra("name", data.get("name"));
//                        intent1.putExtra("address", data.get("address"));
//                    }
//                });
//            }
//        }
//    };
}
