package com.cybertech.intelligentwalkingcan;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity {

    private BluetoothSPP bt;
    EditText edtPhoneNo;
    TextView lblinfo;
    private String callNo;
    private StringBuilder mBuilder;
    private String phoneNo = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
            }
        });

        FloatingActionButton fab_call = (FloatingActionButton) findViewById(R.id.fab_call);
        fab_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (phoneNo.trim().equals("") || edtPhoneNo.getText().toString().length() < 3) {
                    lblinfo.setText("Please enter a valid number to call!");
                } else {
                    Boolean isHash = false;
                    if (phoneNo.subSequence(phoneNo.length() - 1, phoneNo.length()).equals("#")) {
                        phoneNo = phoneNo.substring(0, phoneNo.length() - 1);
                        String callInfo = "" + phoneNo + "#";
                        edtPhoneNo.setText("");
                        if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                            callNo = callInfo;
                            bt.send("d", true);
                        } else {
                            Toast.makeText(MainActivity.this, "Sorry Device not connected!!", Toast.LENGTH_SHORT).show();
                        }
                        //Intent callIntent = new Intent(Intent.ACTION_CALL);
                        // callIntent.setData(Uri.parse(callInfo));
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        //startActivity(callIntent);
                    } else {
                        String callInfo = "" + phoneNo;
                        // Intent callIntent = new Intent(Intent.ACTION_CALL);
                        // callIntent.setData(Uri.parse(callInfo));
                        //startActivity(callIntent);
                        edtPhoneNo.setText("");
                        if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                            callNo = callInfo;
                            bt.send("d", true);
                        } else {
                            Toast.makeText(MainActivity.this, "Sorry Device not connected!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        bt = new BluetoothSPP(this);


        edtPhoneNo = (EditText) findViewById(R.id.edtPhoneNumber);
        lblinfo = (TextView) findViewById(R.id.lblinfo);


        if (!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available on this device"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        mBuilder = new StringBuilder();

        // handle for connections
        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                lblinfo.setText(name);
                Toast.makeText(getApplicationContext(), "Connected to " + name + "\n Address : " + address, Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() {
                // display_TV.setText("None");
                Toast.makeText(getApplicationContext()
                        , "Connection lost", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() {
                // display_TV.setText("Connection failed");
                Toast.makeText(getApplicationContext()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });

        // handle data received
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            @Override
            public void onDataReceived(byte[] data, String message) {
                // lblinfo.setText(data.toString() + "\n " + message);
                if (message.equals("f")) {
                    // a fall has been detected.
                    lblinfo.setText("Fall detected");

                } else if (message.equals("n")) {
                    // no fall detected.
                    lblinfo.setText("No fall detected");

                } else if (message.equals("o")) {
                    // Obstacle detected
                    lblinfo.setText("Hello Obstacle detected");

                } else if (message.equals("N")) {
                    // No Obstacle seen
                    lblinfo.setText("No Obstacle");

                } else if (message.equals("yes")) {
                    // device is now ready
                    lblinfo.setText("Dailing " + callNo + " ...");
                    bt.send(edtPhoneNo.getText().toString(), true);
                } else if (message.equals("no")) {
                    lblinfo.setText("Device not ready! ");
                } else {
                    lblinfo.setText(message);
                }


            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        bt.disconnect();

    }

    private void setup() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }


    public void buttonClickEvent(View v) {
        phoneNo = edtPhoneNo.getText().toString();
        try {

            switch (v.getId()) {
                case R.id.btnAterisk:
                    lblinfo.setText("");
                    phoneNo += "*";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnHash:
                    lblinfo.setText("");
                    phoneNo += "#";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnZero:
                    lblinfo.setText("");
                    phoneNo += "0";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnOne:
                    lblinfo.setText("");
                    phoneNo += "1";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnTwo:
                    lblinfo.setText("");
                    phoneNo += "2";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnThree:
                    lblinfo.setText("");
                    phoneNo += "3";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnFour:
                    lblinfo.setText("");
                    phoneNo += "4";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnFive:
                    lblinfo.setText("");
                    phoneNo += "5";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnSix:
                    lblinfo.setText("");
                    phoneNo += "6";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnSeven:
                    lblinfo.setText("");
                    phoneNo += "7";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnEight:
                    lblinfo.setText("");
                    phoneNo += "8";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnNine:
                    lblinfo.setText("");
                    phoneNo += "9";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btndel:
                    lblinfo.setText("");
                    if (phoneNo != null && phoneNo.length() > 0) {
                        phoneNo = phoneNo.substring(0, phoneNo.length() - 1);
                    }
                    edtPhoneNo.setText(phoneNo);
                    break;
            }

        } catch (Exception ex) {

        }
    }

}
