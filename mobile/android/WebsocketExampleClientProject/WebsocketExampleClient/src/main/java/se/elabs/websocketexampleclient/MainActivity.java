package se.elabs.websocketexampleclient;

import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import java.net.URI;
import java.net.URISyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import android.os.Handler;
public class MainActivity extends Activity {
    public static Handler mHandler;
    private ImageView image ;
    private TextView label ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = (ImageView) findViewById(R.id.imageview);
        label = (TextView) findViewById(R.id.label);

        mHandler = new Handler(){
            public void postDelayed(Message msg) {
                Log.i("Data",msg.arg1+"");// ("Data comes:"+msg.arg1);
                int data = msg.arg1;
                if (data%2 == 0) {
                    image.setImageResource(R.drawable.green);
                    label.setText("Free");
                    Log.d("test1","Free");
                } else {
                    image.setImageResource(R.drawable.red);
                    label.setText("Busy");
                    Log.d("test2","Busy");
                }
            }
        };
       startTimerThread();

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    private void startTimerThread() {

        Runnable runnable = new Runnable() {
            private long startTime = System.currentTimeMillis();
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5000);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable(){
                        public void run() {
                            int value =GET("https://betul-turkicin-predixpark-service.run.aws-usw02-pr.ice.predix.io/services/parkingservices/data/slotid/PredixPark:SlotFull/status");
                            Log.d("value",Integer.toString(value));
                            ImageView image = (ImageView) findViewById(R.id.imageview);
                            TextView label = (TextView)findViewById(R.id.label);
                            if(value == 0) {
                                image.setImageResource(R.drawable.green);
                                label.setText("EMPTY");
                            } else {
                                image.setImageResource(R.drawable.red);
                                label.setText("FULL");
                            }
                        }
                    });
                }
            }
        };
        new Thread(runnable).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }


    public static int GET(String url){
            InputStream inputStream = null;
            int result = -1;
            try {
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();

                // make GET request to the given URL
                HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
                // receive response as inputStream
                inputStream = httpResponse.getEntity().getContent();

                // convert inputstream to string
                if(inputStream != null)
                    result = convertInputStreamToInteger(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 11. return result
            return result;
    }




    private static int convertInputStreamToInteger(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return  Integer.parseInt(result);
    }

}

