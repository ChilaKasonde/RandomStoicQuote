package com.devguilds.randomstoicquote;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends ActionBarActivity {
    Button btnNext, btnPrevious, btnClip;
    TextView txtQuote;
    JSONArray parentArray;
    int value = 0;
    ClipboardManager myClipboard;
    ClipData myClip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        loadQuote();
        myClipboard = (ClipboardManager)MainActivity.this.getSystemService(MainActivity.this.CLIPBOARD_SERVICE);
        setContentView(R.layout.activity_main);
        btnNext = (Button)findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                value++;
                loadQuote();
            }
        });
        btnPrevious = (Button)findViewById(R.id.btnPrevious);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                value--;
                loadQuote();
            }
        });
        btnClip = (Button)findViewById(R.id.btnClip);

        btnClip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipboard();
            }
        });
        txtQuote = (TextView)findViewById(R.id.txtQuote);

    }

    private void copyToClipboard() {
        if (!txtQuote.getText().toString().equals("Loading...")) {
            String text = txtQuote.getText().toString();
            myClip = ClipData.newPlainText("text", text);
            myClipboard.setPrimaryClip(myClip);
            Toast.makeText(MainActivity.this, "Copied: " + text + " Copied", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(MainActivity.this,"Wait for text to load", Toast.LENGTH_SHORT).show();
        }

    }

    private void loadQuote() {
        new QuoteTaskNested().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    class QuoteTaskNested extends AsyncTask<Void,Void,String> {

        String json_url ="https://randomstoicquotesapi.herokuapp.com/api/v1/quotes";
        private String json_string;


        @Override
        protected String doInBackground(Void... voids) {

            try {
                URL url = new  URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();

                while((json_string=bufferedReader.readLine())!= null){
                    stringBuilder.append(json_string+"\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return stringBuilder.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        public QuoteTaskNested() {
            super();
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                JSONObject parentObject = new JSONObject(result);
                JSONArray parentArray = parentObject.getJSONArray("data");
                JSONObject jsonObject = parentArray.getJSONObject(value);
                String quote = jsonObject.getJSONObject("attributes").getString("text");
                txtQuote.setText(quote);

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }




}
