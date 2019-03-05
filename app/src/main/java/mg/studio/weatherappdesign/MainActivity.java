package mg.studio.weatherappdesign;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void btnClick(View view) {
        new DownloadUpdate().execute();
    }

    // 判断网络状态
    private boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }


    private class DownloadUpdate extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            String stringUrl = "https://mpianatra.com/Courses/forecast.json";
            HttpURLConnection urlConnection = null;
            BufferedReader reader;

            try {
                URL url = new URL(stringUrl);

                // Create the request to get the information from the server, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Mainly needed for debugging
                    Log.d("TAG", line);
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                //The temperature
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }



        @Override
        protected void onPostExecute(String temperature) {

            try{
                int i=0;
                if ( i==0) {
                    JSONObject root= new JSONObject(temperature);
                    JSONArray list = root.getJSONArray("list");
                    JSONObject e1 = list.getJSONObject(1);
                    JSONObject main1 = e1.getJSONObject("main");
                    String temp = main1.getString("temp");
                    JSONArray curweather = e1.getJSONArray("weather");
                    JSONObject weatherob=curweather.getJSONObject(0);
                    String weather=weatherob.getString("main");

                    //String weatherdis=curweather.getString(1);

                    int inttemp = new Float(Float.parseFloat(temp)).intValue();
                    inttemp = inttemp - 272;
                    //Update the temperature displayed
                    ((TextView) findViewById(R.id.temperature_of_the_day)).setText(String.valueOf(inttemp));
                    if(weather.contains("Clear"))
                    {((ImageView)findViewById(R.id.img_weather_condition)).setImageResource(R.drawable.sunny_small);}
                    else if(weather.contains("Clouds"))
                    {((ImageView)findViewById(R.id.img_weather_condition)).setImageResource(R.drawable.partly_sunny_small);}
                    else if(weather.contains("Rainy"))
                    {((ImageView)findViewById(R.id.img_weather_condition)).setImageResource(R.drawable.rainy_small);}
                    else if(weather.contains("Windy"))
                    {((ImageView)findViewById(R.id.img_weather_condition)).setImageResource(R.drawable.windy_small);}
                    Toast toast = Toast.makeText(getApplicationContext(), "the update is avaliable", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else if(i>0){
                    Toast toast = Toast.makeText(getApplicationContext(), "please connect to the internet", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
}
