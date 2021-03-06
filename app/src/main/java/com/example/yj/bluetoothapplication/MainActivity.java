package com.example.yj.bluetoothapplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String TAG = "bluetooth2";

    Button sendButton;
    TextView logTextView;
    EditText editText;

    RelativeLayout rlayout;
    Handler h;

    final int RECIEVE_MESSAGE = 1;        // Status  for Handler
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder sb = new StringBuilder();
    private static int flag = 0;

    private ConnectedThread mConnectedThread;

    private String deviceName;
    private String deviceAddress;

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("0000110E-0000-1000-8000-00805F9B34FB");

    // MAC-address of Bluetooth module (you must edit this line)
    //private static String address = "20:16:03:08:64:39";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        deviceName = getIntent().getStringExtra("name");
        deviceAddress = getIntent().getStringExtra("address");


        sendButton = (Button) findViewById(R.id.sendButton);
        editText = (EditText) findViewById(R.id.messageEditText);
//        btnLed2 = (Button) findViewById(R.id.btnLed2);
//        btnLed3 = (Button) findViewById(R.id.btnLed3);
//        btnpado = (Button) findViewById(R.id.btnPado);

        logTextView = (TextView) findViewById(R.id.logTextView);
        rlayout = (RelativeLayout) findViewById(R.id.layout);
        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);
                        sb.append(strIncom);
                        int endOfLineIndex = sb.indexOf("\r\n");
                        if (endOfLineIndex > 0) {
                            String sbprint = sb.substring(0, endOfLineIndex);
                            sb.delete(0, sb.length());
                            logTextView.setText("Data from BT: " + sbprint);
                            if (flag % 4 == 3) {
                                rlayout.setBackgroundColor(Color.rgb(255, 255, 255));
                            } else if (flag % 4 == 1) {
                                rlayout.setBackgroundColor(Color.rgb(255, 0, 0));
                            } else if (flag % 4 == 2) {
                                rlayout.setBackgroundColor(Color.rgb(0, 255, 0));
                            } else if (flag % 4 == 0) {
                                rlayout.setBackgroundColor(Color.rgb(0, 0, 255));
                            }
                            flag++;
//                            btnLed1.setEnabled(true);
//                            btnLed2.setEnabled(true);
//                            btnLed3.setEnabled(true);
//                            btnpado.setEnabled(true);

                        }
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
                checkBTState();
            }
        }).start();

        sendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(mConnectedThread == null) return;
                mConnectedThread.write(editText.getText().toString());
                editText.setText("");
                //Toast.makeText(getBaseContext(), "Turn on First LED", Toast.LENGTH_SHORT).show();
            }
        });

//        btnLed1.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                mConnectedThread.write("progeeprom4845088335052316W4");
//                //Toast.makeText(getBaseContext(), "Turn on First LED", Toast.LENGTH_SHORT).show();
//            }
//        });
//        btnLed2.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                mConnectedThread.write("progeeprom4845088335052316W5");
//                //Toast.makeText(getBaseContext(), "Turn on Second LED", Toast.LENGTH_SHORT).show();
//            }
//        });
//        btnLed3.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                mConnectedThread.write("progeeprom4845088335052316W1");
//                //Toast.makeText(getBaseContext(), "Turn on Third LED", Toast.LENGTH_SHORT).show();
//            }
//        });
//        btnpado.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                mConnectedThread.write("progeeprom4845088335052316W10");
//                //Toast.makeText(getBaseContext(), "Turn on all LEDs", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
               // final Method  m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                BluetoothSocket socket =(BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(device,1);
                    return socket;
               // return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection",e);
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    @Override
    public void onResume() {
        super.onResume();


        Runnable r = new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "...onResume - try connect...");

                // Set up a pointer to the remote node using it's address.
                BluetoothDevice device = btAdapter.getRemoteDevice(deviceAddress);

                // Two things are needed to make a connection:
                //   A MAC address, which we got above.
                //   A Service ID or UUID.  In this case we are using the
                //     UUID for SPP.

                try {
                    btSocket = createBluetoothSocket(device);
                } catch (Exception e) {
                    errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
                }

                // Discovery is resource intensive.  Make sure it isn't going on
                // when you attempt to connect and pass your message.
                btAdapter.cancelDiscovery();

                // Establish the connection.  This will block until it connects.
                Log.d(TAG, "...Connecting... " + deviceAddress);
                try {
                    Thread.sleep(500);
                    btSocket.connect();
                    Log.d(TAG, "....Connection ok...");
                } catch (Exception e) {
                    Log.e(TAG, "Error: " + e.getLocalizedMessage());
                    try {
                        btSocket.close();
                    } catch (Exception e2) {
                        errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
                    }
                }

                // Create a data stream so we can talk to server.
                Log.d(TAG, "...Create Socket...");

                mConnectedThread = new ConnectedThread(btSocket);
                mConnectedThread.start();
            }
        };
        new Thread(r).start();
    }



    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "...In onPause()...");

        try {
            btSocket.close();
        } catch (Exception e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if (btAdapter == null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private void errorExit(String title, String message) {
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);        // Get number of bytes and message in "buffer"
                    String strIncom = new String(buffer, 0, bytes);
                    Log.d(TAG, "...read bytes " + strIncom);

                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();     // Send to message queue Handler
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String message) {
            Log.d(TAG, "...Data to send: " + message + "...");
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
            }
        }
    }
}