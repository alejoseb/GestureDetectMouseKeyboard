/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


package com.example.android.basicgesturedetect;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.widget.EditText;

import com.example.android.common.activities.SampleActivityBase;
import com.example.android.common.logger.Log;
import com.example.android.common.logger.LogFragment;
import com.example.android.common.logger.LogWrapper;
import com.example.android.common.logger.MessageOnlyLogFilter;

import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URLEncoder;
import java.util.Iterator;

/**
 * A simple launcher activity containing a summary sample description
 * and a few action bar buttons.
 */
public class MainActivity extends SampleActivityBase {

    public static final String TAG = "MainActivity";

    public static final String FRAGTAG = "BasicGestureDetectFragment";

    String cm1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportFragmentManager().findFragmentByTag(FRAGTAG) == null ) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            BasicGestureDetectFragment fragment = new BasicGestureDetectFragment();
            transaction.add(fragment, FRAGTAG);
            transaction.commit();
        }
        final EditText editKeyboard;
        editKeyboard= (EditText) findViewById(R.id.editKeyboard);

        editKeyboard.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (editKeyboard.getText().length()>0)
                {
                    cm1="4,"+editKeyboard.getText();
                    new CallAPI().execute("");
                    editKeyboard.setText("");
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    //Put your code here.
                    //Runs when delete/backspace pressed on soft key (tested on htc m8)
                    //You can use EditText.getText().length() to make if statements here

                }

            }
        });


    }



    public class CallAPI extends AsyncTask<String, String, String> {
        int x,y;
        String ax, ay, cm;

        public CallAPI() {
                cm=cm1;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... params) {
            String resultToDisplay = "";


            int UDP_SERVER_PORT = 5005;


            DatagramSocket ds = null;

            try {

                ds = new DatagramSocket();

                InetAddress serverAddr = InetAddress.getByName("192.168.3.103");

                DatagramPacket dp;

                dp = new DatagramPacket(cm.getBytes(), cm.length(), serverAddr,UDP_SERVER_PORT);

                ds.send(dp);

            } catch (SocketException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            } catch (Exception e) {

                e.printStackTrace();

            } finally {

                if (ds != null) {

                    ds.close();

                }

            }


            /*
            //String cm= params[0];
            String urlString ="http://192.168.3.103:8080/generator"; // URL to call
            InputStream in = null;
            try {

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("cm", cm);
                postDataParams.put("ax",ax);
                postDataParams.put("ay",ay);

                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");

                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

               // in = new BufferedInputStream(urlConnection.getInputStream());
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));
                writer.flush();
                writer.close();
                os.close();
                int responseCode=urlConnection.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    BufferedReader in2=new BufferedReader(new
                            InputStreamReader(
                            urlConnection.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in2.readLine()) != null) {

                        sb.append(line);
                        break;
                    }
                    in.close();
                    return sb.toString();
                }
            } catch (Exception e) {

                System.out.println(e.getMessage());

                return e.getMessage();
            }
            */

            return resultToDisplay;

        }


        @Override
        protected void onPostExecute(String result) {
            //Update the UI
        }

        public String getPostDataString(JSONObject params) throws Exception {

            StringBuilder result = new StringBuilder();
            boolean first = true;

            Iterator<String> itr = params.keys();

            while(itr.hasNext()){

                String key= itr.next();
                Object value = params.get(key);

                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value.toString(), "UTF-8"));

            }
            return result.toString();
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /** Create a chain of targets that will receive log data */
    @Override
    public void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);

        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.
        LogFragment logFragment = (LogFragment) getSupportFragmentManager()
                .findFragmentById(R.id.log_fragment);
        msgFilter.setNext(logFragment.getLogView());
        logFragment.getLogView().setTextAppearance(this, R.style.Log);
        logFragment.getLogView().setBackgroundColor(Color.WHITE);


        Log.i(TAG, "Ready");
    }
}
