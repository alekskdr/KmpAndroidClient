package com.alekskdr.kmplayer.pult.kmpclient;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TcpClient mTcpClient;
    private String SERVER_IP;
    private EditText editText;
    private ListView mList;
    private SharedPreferences sPref;
    private String serverIp;
    connectTask mt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        
        this.editText = (EditText) findViewById(R.id.editText);
        Button saveButton = (Button) findViewById(R.id.send_button);
        Button reloadButton = (Button) findViewById(R.id.reload);
        Button fleft = (Button) findViewById(R.id.fast_left);
        Button fright = (Button) findViewById(R.id.fast_right);
        Button left = (Button) findViewById(R.id.left);
        Button right = (Button) findViewById(R.id.right);
        Button sendUp = (Button) findViewById(R.id.send_up);
        Button sendDown = (Button) findViewById(R.id.send_down);
        Button sendEnter = (Button) findViewById(R.id.send_enter);
        Button sendSpace = (Button) findViewById(R.id.send_space);
        ImageButton sendVkUp = (ImageButton) findViewById(R.id.send_vk_up);
        ImageButton sendVkDown = (ImageButton) findViewById(R.id.send_vk_down);
        loadIp();
        this.serverIp = this.editText.getText().toString();
        SERVER_IP = serverIp;

        mt = new connectTask();
        mt.execute();

        saveButton.setOnClickListener(this);
        reloadButton.setOnClickListener(this);
        fleft.setOnClickListener(this);
        fright.setOnClickListener(this);
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        sendUp.setOnClickListener(this);
        sendDown.setOnClickListener(this);
        sendEnter.setOnClickListener(this);
        sendSpace.setOnClickListener(this);
        sendVkUp.setOnClickListener(this);
        sendVkDown.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (mTcpClient != null) {
            switch (view.getId()) {
                case R.id.send_space:
                    mTcpClient.sendMessage("space");
                    break;
                case R.id.send_enter:
                    mTcpClient.sendMessage("enter");
                    break;
                case R.id.send_down:
                    mTcpClient.sendMessage("down");
                    break;
                case R.id.send_up:
                    mTcpClient.sendMessage("up");
                    break;
                case R.id.send_vk_down:
                    mTcpClient.sendMessage("vk_down");
                    break;
                case R.id.send_vk_up:
                    mTcpClient.sendMessage("vk_up");
                    break;
                case R.id.fast_left:
                    mTcpClient.sendMessage("shift_left");
                    break;
                case R.id.fast_right:
                    mTcpClient.sendMessage("shift_right");
                    break;
                case R.id.left:
                    mTcpClient.sendMessage("left");
                    break;
                case R.id.right:
                    mTcpClient.sendMessage("right");
                    break;
                case R.id.send_button:
                    saveIp();
                    break;
                case R.id.reload:
                    cancelTask();
                    mt = new connectTask();
                    mt.execute();
                    Toast.makeText(this, "Reload connect", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private void loadIp() {
        this.sPref = getPreferences(0);
        this.editText.setText(this.sPref.getString("server_ip", "192.168.0.53"));
        Toast.makeText(this, "Text loaded", Toast.LENGTH_SHORT).show();
    }

    private void saveIp() {
        this.sPref = getPreferences(0);
        SharedPreferences.Editor ed = this.sPref.edit();
        ed.putString("server_ip", this.editText.getText().toString());
        ed.commit();
        Toast.makeText(this, "Text saved", Toast.LENGTH_SHORT).show();
    }

    private void cancelTask() {
        if (mt == null) return;
        mt.cancel(false);
    }

    public class connectTask extends AsyncTask<String,String,TcpClient> {

        @Override
        protected TcpClient doInBackground(String... message) {

            if (MainActivity.this.serverIp.length() <= 0) {
                Log.e("TCP Client", "Server empty IP: " + MainActivity.this.serverIp);
                //we create a TcpClient object and
                mTcpClient = new TcpClient(new TcpClient.OnMessageReceived() {
                    @Override
                    //here the messageReceived method is implemented
                    public void messageReceived(String message) {
                        //this method calls the onProgressUpdate
                        publishProgress(message);
                    }
                });
            } else {
                Log.e("TCP Client", "Server IP: " + MainActivity.this.serverIp);
                //we create a TcpClient object and
                mTcpClient = new TcpClient(MainActivity.this.serverIp, new TcpClient.OnMessageReceived() {
                    @Override
                    //here the messageReceived method is implemented
                    public void messageReceived(String message) {
                        //this method calls the onProgressUpdate
                        publishProgress(message);
                    }
                });
            }

            mTcpClient.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (mTcpClient != null) {
                mTcpClient.stopClient();
            }
        }
    }
}
