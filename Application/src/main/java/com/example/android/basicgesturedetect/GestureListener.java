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

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.example.android.common.logger.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


public class GestureListener extends GestureDetector.SimpleOnGestureListener {

    public static final String TAG = "GestureListener";
    float distanceX1,distanceY1;
    String distX1, distY1, cm1;


    public class coordinate {
        String x,y;
        public coordinate ()
        {
            x="";
            y="";
        }
    }

    List<coordinate> coordinates = new ArrayList<coordinate>();
    // The UDP connection requires to work in an AsyncTask
    public class CallAPI extends AsyncTask<String, String, String> {

        String cm;

        public CallAPI() {
            //set context variables if required
            // cm contains the command and parameters to be executed on the server (raspberry PI). /
            // Commands and parameters are separated by semicolons
            // ex: command,X,Y
            //      1,100,200
           cm=cm1;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... params) {
            String resultToDisplay = "";

            //UDP port
            int UDP_SERVER_PORT = 5005;


            DatagramSocket ds = null;

            try {

                ds = new DatagramSocket();

                // IP address
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

    // BEGIN_INCLUDE(init_gestureListener)
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // Up motion completing a single tap occurred.
        Log.i(TAG, "Single Tap Up" + getTouchType(e));
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // Touch has been long enough to indicate a long press.
        // Does not indicate motion is complete yet (no up event necessarily)
        Log.i(TAG, "Long Press" + getTouchType(e));
    }


    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
    float distanceY) {
        // User attempted to scroll
        InputStream in = null;
        Log.i(TAG, "Scroll" + getTouchType(e1) + " x: " + String.valueOf(distanceX)+ " y: " + String.valueOf(distanceY) );

        distX1 = String.valueOf((int) (-1 * distanceX));
        distY1 = String.valueOf((int) (-1 * distanceY));
        cm1="1"+","+distX1+","+distY1;
        new CallAPI().execute("");
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
    float velocityY) {
        // Fling event occurred.  Notification of this one happens after an "up" event.
        Log.i(TAG, "Fling" + getTouchType(e1));
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // User performed a down event, and hasn't moved yet.
        Log.i(TAG, "Show Press" + getTouchType(e));
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // "Down" event - User touched the screen.
        Log.i(TAG, "Down" + getTouchType(e));

        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        // User tapped the screen twice.
        Log.i(TAG, "Double tap" + getTouchType(e));
        cm1="3";
        new CallAPI().execute("");
        return false;


    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        // Since double-tap is actually several events which are considered one aggregate
        // gesture, there's a separate callback for an individual event within the doubletap
        // occurring.  This occurs for down, up, and move.
        Log.i(TAG, "Event within double tap" + getTouchType(e));
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        // A confirmed single-tap event has occurred.  Only called when the detector has
        // determined that the first tap stands alone, and is not part of a double tap.
        Log.i(TAG, "Single tap confirmed" + getTouchType(e));
        cm1="2";
        new CallAPI().execute("");

        return false;
    }
    // END_INCLUDE(init_gestureListener)


    /**
     * Returns a human-readable string describing the type of touch that triggered a MotionEvent.
     */

    private static String getTouchType(MotionEvent e){

        String touchTypeDescription = " ";
        int touchType = e.getToolType(0);

        switch (touchType) {
            case MotionEvent.TOOL_TYPE_FINGER:
                touchTypeDescription += "(finger)";
                break;
            case MotionEvent.TOOL_TYPE_STYLUS:
                touchTypeDescription += "(stylus, ";
                //Get some additional information about the stylus touch
                float stylusPressure = e.getPressure();
                touchTypeDescription += "pressure: " + stylusPressure;

                if(Build.VERSION.SDK_INT >= 21) {
                    touchTypeDescription += ", buttons pressed: " + getButtonsPressed(e);
                }

                touchTypeDescription += ")";
                break;
            case MotionEvent.TOOL_TYPE_ERASER:
                touchTypeDescription += "(eraser)";
                break;
            case MotionEvent.TOOL_TYPE_MOUSE:
                touchTypeDescription += "(mouse)";
                break;
            default:
                touchTypeDescription += "(unknown tool)";
                break;
        }

        return touchTypeDescription;
    }

    /**
     * Returns a human-readable string listing all the stylus buttons that were pressed when the
     * input MotionEvent occurred.
     */
    @TargetApi(21)
    private static String getButtonsPressed(MotionEvent e){
        String buttons = "";

        if(e.isButtonPressed(MotionEvent.BUTTON_PRIMARY)){
            buttons += " primary";
        }

        if(e.isButtonPressed(MotionEvent.BUTTON_SECONDARY)){
            buttons += " secondary";
        }

        if(e.isButtonPressed(MotionEvent.BUTTON_TERTIARY)){
            buttons += " tertiary";
        }

        if(e.isButtonPressed(MotionEvent.BUTTON_BACK)){
            buttons += " back";
        }

        if(e.isButtonPressed(MotionEvent.BUTTON_FORWARD)){
            buttons += " forward";
        }

        if (buttons.equals("")){
            buttons = "none";
        }

        return buttons;
    }

}
