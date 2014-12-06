package com.example.bluetooth1;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;
 
public class MainActivity extends Activity {
  private static final String TAG = "bluetooth1";
   
  Button left, right, back, forward;
   
  private static final int REQUEST_ENABLE_BT = 1;
  private BluetoothAdapter btAdapter = null;
  private BluetoothSocket btSocket = null;
  private OutputStream outStream = null;
   
  // SPP UUID сервиса 
  private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
 
  // MAC-адрес Bluetooth модуля
  private static String address = "20:14:04:03:16:28";
   
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
 
    setContentView(R.layout.activity_main);
 
    forward = (Button) findViewById(R.id.button2);
    back = (Button) findViewById(R.id.button3);
    left = (Button) findViewById(R.id.button4);
    right = (Button) findViewById(R.id.button5);
     
    btAdapter = BluetoothAdapter.getDefaultAdapter();
    checkBTState();
 
    forward.setOnTouchListener(new OnTouchListener() {
		
		public boolean onTouch(View v, MotionEvent event) {
			
			if (event.getAction() == MotionEvent.ACTION_DOWN ) {
				sendData("f");
            } else if (event.getAction() == MotionEvent.ACTION_UP ) {
            	 sendData("s");
            }
			return false;
		}
	});
 
    back.setOnTouchListener(new OnTouchListener() {
		
		public boolean onTouch(View v, MotionEvent event) {
			
			if (event.getAction() == MotionEvent.ACTION_DOWN ) {
				sendData("b");
            } else if (event.getAction() == MotionEvent.ACTION_UP ) {
            	 sendData("s");
            }
			return false;
		}
	});
    
    left.setOnTouchListener(new OnTouchListener() {
		
		public boolean onTouch(View v, MotionEvent event) {
			
			if (event.getAction() == MotionEvent.ACTION_DOWN ) {
				sendData("l");
            } else if (event.getAction() == MotionEvent.ACTION_UP ) {
            	 sendData("s");
            }
			return false;
		}
	});
    
    right.setOnTouchListener(new OnTouchListener() {
		
		public boolean onTouch(View v, MotionEvent event) {
			
			if (event.getAction() == MotionEvent.ACTION_DOWN ) {
				sendData("r");
            } else if (event.getAction() == MotionEvent.ACTION_UP ) {
            	 sendData("s");
            }
			return false;
		}
	});
  }
   
  @Override
  public void onResume() {
    super.onResume();
 
    Log.d(TAG, "...onResume - попытка соединения...");
   
    // Set up a pointer to the remote node using it's address.
    BluetoothDevice device = btAdapter.getRemoteDevice(address);
   
    // Two things are needed to make a connection:
    //   A MAC address, which we got above.
    //   A Service ID or UUID.  In this case we are using the
    //     UUID for SPP.
    try {
      btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
    } catch (IOException e) {
      errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
    }
   
    // Discovery is resource intensive.  Make sure it isn't going on
    // when you attempt to connect and pass your message.
    btAdapter.cancelDiscovery();
   
    // Establish the connection.  This will block until it connects.
    Log.d(TAG, "...Соединяемся...");
    try {
      btSocket.connect();
      Log.d(TAG, "...Соединение установлено и готово к передачи данных...");
    } catch (IOException e) {
      try {
        btSocket.close();
      } catch (IOException e2) {
        errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
      }
    }
     
    // Create a data stream so we can talk to server.
    Log.d(TAG, "...Создание Socket...");
 
    try {
      outStream = btSocket.getOutputStream();
    } catch (IOException e) {
      errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
    }
  }
 
  @Override
  public void onPause() {
    super.onPause();
 
    Log.d(TAG, "...In onPause()...");
 
    if (outStream != null) {
      try {
        outStream.flush();
      } catch (IOException e) {
        errorExit("Fatal Error", "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
      }
    }
 
    try     {
      btSocket.close();
    } catch (IOException e2) {
      errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
    }
  }
   
  private void checkBTState() {
    // Check for Bluetooth support and then check to make sure it is turned on
    // Emulator doesn't support Bluetooth and will return null
    if(btAdapter==null) { 
      errorExit("Fatal Error", "Bluetooth не поддерживается");
    } else {
      if (btAdapter.isEnabled()) {
        Log.d(TAG, "...Bluetooth включен...");
      } else {
        //Prompt user to turn on Bluetooth
        Intent enableBtIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
      }
    }
  }
 
  private void errorExit(String title, String message){
    Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
    finish();
  }
 
  private void sendData(String message) {
    byte[] msgBuffer = message.getBytes();
 
    Log.d(TAG, "...Посылаем данные: " + message + "...");
 
    try {
      outStream.write(msgBuffer);
    } catch (IOException e) {
      String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
      if (address.equals("00:00:00:00:00:00")) 
        msg = msg + ".\n\nВ переменной address у вас прописан 00:00:00:00:00:00, вам необходимо прописать реальный MAC-адрес Bluetooth модуля";
      	msg = msg +  ".\n\nПроверьте поддержку SPP UUID: " + MY_UUID.toString() + " на Bluetooth модуле, к которому вы подключаетесь.\n\n";
       
      	errorExit("Fatal Error", msg);       
    }
  }
}

