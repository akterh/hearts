package com.hearts.customer;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;

import android.app.AlertDialog;

import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;

import android.net.NetworkInfo;


import android.net.Uri;
import android.os.Build;
import android.os.Bundle;


import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;


import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {
    private TextView back;
    private WebView webView;
    private SwipeRefreshLayout swipe;
    private ProgressBar progress;
    private LinearLayout noInternet;
    private final int REQUEST_LOCATION_PERMISSION = 1;
    private Context context;
    private static final String TAG = "SingleDomain";
    String url ="https://hearts.com.bd";


    public MainActivity() throws MalformedURLException {
    }

    @RequiresApi(api = Build.VERSION_CODES.P)



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webView);
        swipe = findViewById(R.id.swipe);
        progress= findViewById(R.id.progressBar);
        noInternet = findViewById(R.id.noInternet);
        back = findViewById(R.id.btn_back);




        back.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                         webView.loadUrl(url);
                                         back.setVisibility(View.INVISIBLE);

                                       }
                                   }


        );





        if (Build.VERSION.SDK_INT < 18) {
            //speed webview
            webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        }






        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().getAllowFileAccessFromFileURLs();
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new myWebViewClient()
                                 {

                                     @Override
                                     public boolean shouldOverrideUrlLoading(final WebView view, String url) {




                                         if (!url.contains()){

                                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                    builder.setIcon(R.drawable.ic_dialog_alert);
                                                    builder.setTitle("Will you go to this url?");
                                                    builder.setPositiveButton("Yes",
                                                            new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int id) {
                                                                    back.setVisibility(View.VISIBLE);


                                                                    if(url.contains("https://")) {

                                                                        view.loadUrl(url);

                                                                        //flag=false;

                                                                    }


                                                                    // it will load in app
                                                //                                                             Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)); //it will load in browser
                                                //                                                             startActivity(intent);
                                                                    //flag=true;

                                                                }
                                                            });
                                                    builder.setNeutralButton("No", new DialogInterface.OnClickListener()
                                                    {
                                                        public void onClick(DialogInterface dialog, int which)
                                                        {
                                                            webView.stopLoading();
                                                        }
                                                    });
                                                    AlertDialog alert = builder.create();

                                                    alert.show();





                                                }else {
                                                    webView.loadUrl(url);
                                                }



//
//                                         if (url.contains("hearts.com")) {
//                                             webView.loadUrl(url);
//                                         } else {
//                                             webView.stopLoading();
//                                              Toast.makeText(getApplicationContext(), "You Will not be redirected", Toast.LENGTH_LONG).show();
//
//                                         }
                                         return true;



                                     }

                                 }



                         
        );
        webView.setWebChromeClient(new WebChromeClient()


                                   {
                                       public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                                           callback.invoke(origin, true, false);
                                       }
                                   }

        );





        checkInternet();
        requestLocationPermission();




        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });


    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            back.setVisibility(View.INVISIBLE);


            webView.goBack();
        }
        else {
            new AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.super.onBackPressed();
                            //this mainactivity must be replaced with your activity
                        }

                    }).create().show();
        }

    }

    private class myWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {


           progress.setVisibility(View.GONE);
           swipe.setRefreshing(false);
        super.onPageFinished(view, url);
        }
    }

    private void checkInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnected()|| mobile.isConnected()){
            webView.setVisibility(View.VISIBLE);
            noInternet.setVisibility(View.INVISIBLE);
            webView.loadUrl(url);
        }else{
            webView.setVisibility(View.INVISIBLE);
            noInternet.setVisibility(View.VISIBLE);

        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if(EasyPermissions.hasPermissions(this, perms)) {
            checkInternet();
        }
        else {
            EasyPermissions.requestPermissions(this, "Please grant the location permis" +
                    "sion", REQUEST_LOCATION_PERMISSION, perms);
        }
    }







}
