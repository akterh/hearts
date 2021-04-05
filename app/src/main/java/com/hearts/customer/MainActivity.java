package com.hearts.customer;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {
  ;
    private TextView back;
    private WebView webView;
    private SwipeRefreshLayout swipe;
    private ProgressBar progress;
    private LinearLayout noInternet;
    private Context context;
    private static final String TAG = "SingleDomain";
    String url ="https://hearts.com.bd";
    public String currentUrl;
    ArrayList<String> permissions = new ArrayList<>();
    ArrayList<String> permissionsToRequest;
    final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    public final static int ALL_PERMISSIONS_RESULT = 102;
    ArrayList<String> permissionsRejected = new ArrayList<>();
    boolean canGetLocation = true;
    String type="";






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
                                         currentUrl = view.getUrl();
//                                         URL myUrl = null;
//                                         try {
//                                             myUrl = new URL(currentUrl);
//                                         } catch (MalformedURLException e) {
//                                             e.printStackTrace();
//                                         }


                                         if (currentUrl.contains("hearts") && !url.contains("hearts")){

                                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                    builder.setIcon(R.drawable.ic_dialog_alert);
                                                    builder.setTitle("Will you go to this url?");
                                                    builder.setPositiveButton("Yes",
                                                            new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int id) {
                                                                    back.setVisibility(View.VISIBLE);


                                                                    if(url.contains("https://") || url.contains("http://")) {

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
//                                              Toast.makeText(getApplicationContext(), "You Will no
//                                              t be redirected", Toast.LENGTH_LONG).show();
//
//                                         }
                                         return true;



                                     }

                                 }



                         
        );
        webView.setWebChromeClient(new WebChromeClient()


                                   {
                                       public void onGeolocationPermissionsShowPrompt(String origin,
                                                                                      GeolocationPermissions.Callback callback) {
                                           callback.invoke(origin, true, false);
                                       }
                                   }

        );




        checkPer();
        checkInternet();





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



    public void checkPer() {
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);

        // check permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                        ALL_PERMISSIONS_RESULT);
                //Log.d(TAG, "Permission requests");
                canGetLocation = false;
            }
        }
    }

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean canUseExternalStorage = false;

        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                try {
                    //Log.d(TAG, "onRequestPermissionsResult");
                    for (String perms : permissionsToRequest) {
                        if (!hasPermission(perms)) {
                            permissionsRejected.add(perms);
                        }
                    }

                    if (permissionsRejected.size() > 0) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                                showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(permissionsRejected.toArray(
                                                            new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                }
                break;
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    canUseExternalStorage = true;
                }
                if (!canUseExternalStorage) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.cannot_use_save_permission), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("cancel", null)
                .create()
                .show();
    }
    }








