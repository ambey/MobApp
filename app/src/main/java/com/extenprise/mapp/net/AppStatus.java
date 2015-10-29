package com.extenprise.mapp.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


/**
 * Created by avinash on 26/10/15.
 */
public class AppStatus {

    private static AppStatus instance = new AppStatus();
    static Context context;
    ConnectivityManager connectivityManager;
    NetworkInfo wifiInfo, mobileInfo;
    boolean connected = false;

    /**
     * Set the number of retries when reestablishing Internet connection.
     */
    /*private static int retryConnectionNumber = 0;

    *//**
     * The maximum number of retries allowed for Internet connection.
     *//*
    private final static int CONNECTION_RETRY_MAX = 5;

    *//**
     * The timeout of the HTTP request when checking for Internet connection.
     *//*
    private final static int REQUEST_TIMEOUT = 2000;*/

    public static AppStatus getInstance(Context ctx) {
        context = ctx.getApplicationContext();
        return instance;
    }

    public boolean isOnline() {

        //boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;

        try {
            /*connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();*/

            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }

            /*if(connected) {

            }*/
            //return connected;


        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
        }
        return connected;
    }

/* private static void isNetworkAvailable(final Handler handler,
                                           final int timeout) {
        new Thread() {
            private boolean responded = false;

            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL("http://your_server_addres.com");
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                }
                String host = "";
                if (null != url) {
                    host = url.getHost();
                }

                Log.i("NetworkCheck", "[PING] host: " + host);
                Process process = null;
                try {
                    process = new ProcessBuilder()
                            .command("/system/bin/ping", "-c 1",
                                    "-w " + (timeout / 1000), "-n", host)
                            .redirectErrorStream(true).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                InputStream in = process.getInputStream();
                StringBuilder s = new StringBuilder();
                int i;

                try {
                    while ((i = in.read()) != -1) {
                        s.append((char) i);

                        if ((char) i == '\n') {
                            Log.i("NetworkCheck",
                                    "[PING] log: " + s.toString());
                            if (s.toString().contains("64 bytes from")) {
                                // If there were a response from the server at
                                // all, we have Internet access
                                responded = true;
                            }
                            s.delete(0, s.length());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    // Destroy the PING process
                    process.destroy();

                    try {
                        // Close the input stream - avoid memory leak
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Send the response to the handler, 1 for success, 0
                    // otherwise
                    handler.sendEmptyMessage(!responded ? 0 : 1);
                }
            }
        }.start();
    }*/


     /** Handler used that receives the connection status for the Internet.
     * If no active Internet connection will retry #CONNECTION_RETRY_MAX times*/

    /*private static Handler listenForNetworkAvailability = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what != 1) { // code if not connected
                Log.i("NetworkCheck", "not connected");

                if (retryConnectionNumber <= CONNECTION_RETRY_MAX) {
                    Log.i("NetworkCheck", "checking for connectivity");
                    //Here you could disable & re-enable your WIFI/3G connection before retry

                    // Start the ping process again with delay
                    Handler handler = new Handler() {
                        @Override
                        public void close() {

                        }

                        @Override
                        public void flush() {

                        }

                        @Override
                        public void publish(LogRecord record) {

                        }
                    };



                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isNetworkAvailable(listenForNetworkAvailability, REQUEST_TIMEOUT);
                        }
                    }, 5000);
                    retryConnectionNumber++;
                } else {
                    Log.i("NetworkCheck", "failed to establish an connection");
                    // code if not connected
                }
            } else {
                Log.i("NetworkCheck", "connected");
                retryConnectionNumber = 0;
                // code if connected
            }
        }
    }*/

}
