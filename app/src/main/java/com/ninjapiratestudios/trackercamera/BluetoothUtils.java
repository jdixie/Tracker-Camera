package com.ninjapiratestudios.trackercamera;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class BluetoothUtils extends Activity {
	
	private final String TAG = "BluetoothLog";//To isolate
    ArrayList<BluetoothDevice> mArrayAdapter = new ArrayList<>();
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothDevice raspberryPi2;
    EditText degree;


	public boolean startBluetooth(){
        //Start of bluetooth
        //Determine if Android supports Bluetooth
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            //Toast.makeText(getApplicationContext(), "Your device does not support Bluetooth", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Your device does not support Bluetooth");
            return false;
        } else {
            //Toast.makeText(getApplicationContext(), "Your device support Bluetooth", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Your device support Bluetooth");
            //        Turn on Bluetooth if disabled
            if (!mBluetoothAdapter.isEnabled()) {
                int REQUEST_ENABLE_BT = 1;
                //Prompt the user to enable bluetooth
                //Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                mBluetoothAdapter.enable();
                //or just do this, which does not prompt user to enable the bluetooth it just enables it in the background -> mBluetoothAdapter.enable();
                Log.d(TAG, "Successfully enabled Bluetooth");
                return true;
            } else {
                Log.d(TAG, "Bluetooth is already enabled");
                //Toast.makeText(getApplicationContext(), "Bluetooth is already enabled", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
	}

    public void discover_helper(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run(){
                discoverM(null);
            }
        }, 1000);
    }


    /**
     * Method that gets called when the discover button is clicked. It will look for any device in the
     * area, but only add to the arrayadapter the address specified. We will be using the address of the
     * raspberry pi.
     * @param view
     */
    public void discoverM(View view) {
        //Make it discoverable (Print the nearby device)
        // Create a BroadcastReceiver for ACTION_FOUND
        final BroadcastReceiver mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView
//                    if (device.getAddress().equals("FC:F8:AE:36:F4:42")) { //Dean
                    if (device.getAddress().equals("00:15:83:09:16:B0")) {//Jalen
//                    if (device.getAddress().equals("5C:F3:70:71:A0:B5")) {//Raspberie pi
//                            mArrayAdapter.add(device);
                        raspberryPi2 = device;
                        Log.d(TAG, "Device: " + raspberryPi2.getName() + "\n" + raspberryPi2.getAddress());
                        //Toast.makeText(getApplicationContext(), "Successfully found: " + raspberryPi2.getName() + "\n" + raspberryPi2.getAddress(), Toast.LENGTH_SHORT).show();
                        connecting(null);
                    }

                }
            }
        };

        mBluetoothAdapter.startDiscovery();
//        for(BluetoothDevice i: mArrayAdapter){
//            Log.d(TAG,"Device: "+ i.getName() + "\n" + i.getAddress());
//            Toast.makeText(getApplicationContext(),"Device: "+ i.getName() + "\n" + i.getAddress(),Toast.LENGTH_SHORT).show();
//        }
//
//
//        mArrayAdapter.clear();

        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
    }


    public void connecting(View view) {
        new ConnectThread(raspberryPi2).start();
    }
    boolean sendBool = false;

    public void sendMethod(View view) {
        sendBool = true;
    }

    /**
     * This threaded class connects the devices together via a uuid. The device will be the
     * one discovered earlier by the address, which will be the raspberry pi.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        /** Method that gets called from the .start that gets the connection started*/
        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            manageConnectedSocket(mmSocket);
        }


        private void manageConnectedSocket(BluetoothSocket mmSocket) {
//            Toast.makeText(getApplicationContext(),"Going to send something..",Toast.LENGTH_SHORT).show();
            Log.d("BluetoothLog", "Going to Start connected thread");
            new ConnectedThread(mmSocket).start();
        }

        /**
         * This threaded class holds the connection between the raspberry pi and the android device.
         */
        private class ConnectedThread extends Thread {
            private final BluetoothSocket mmSocket;
            private final InputStream mmInStream;
            private final OutputStream mmOutStream;

            public ConnectedThread(BluetoothSocket socket) {
                Log.d(TAG, "Successfully connected with raspberryPi2");
//                Toast.makeText(getApplicationContext(),"Sucesufully connected with raspbery..",Toast.LENGTH_SHORT).show();

                mmSocket = socket;
                InputStream tmpIn = null;
                OutputStream tmpOut = null;

                // Get the input and output streams, using temp objects because
                // member streams are final
                try {
                    tmpIn = socket.getInputStream();
                    tmpOut = socket.getOutputStream();
                } catch (IOException e) { }

                mmInStream = tmpIn;
                mmOutStream = tmpOut;
            }

            public void run() {
                byte[] buffer = new byte[1024];  // buffer store for the stream
                int bytes; // bytes returned from read()
                // Keep listening to the InputStream until an exception occurs
//                while (true) {
//                try {
                while (true){

                    //Send Information to server
//                    write("hello".getBytes());
                    if(sendBool == true) {
                        Log.d(TAG,"Sending->"+degree.getText());
                        write(degree.getText().toString().getBytes());
//                            degree.setText("");
                        degree.clearComposingText();
                        sendBool=false;
                    }
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Read from the InputStream
//                        bytes = mmInStream.read(buffer);
//                        // Send the obtained bytes to the UI activity
//                        messageHandler.obtainMessage(1, 0, bytes, buffer).sendToTarget();
//                        write("1".getBytes());
                }

//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                }
            }

            /** Call this from the main activity to send data to the remote device */
            public void write(byte[] bytes) {
                try {
                    mmOutStream.write(bytes);
                } catch (IOException e) { }
            }

            /** Call this from the main activity to shutdown the connection */
            public void cancel() {
                try {
                    mmSocket.close();
                } catch (IOException e) { }
            }
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

/*
    public void getReadPermissions() {
        //check for permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            //ask for permission
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSION_CODE);
        }
    }

    public void getCameraPermissions(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    public void getAudioPermissions(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_PERMISSION_CODE);
        }
    }
*/

}
