package com.hearts.customer;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

public class MainActivity extends AppCompatActivity {
    private TextView back;
    private WebView webView;
    private SwipeRefreshLayout swipe;
    private ProgressBar progress;
    private LinearLayout noInternet;
    private Context context;
    String url ="https://hearts.com.bd";
    public String currentUrl;
    ArrayList<String> permissions = new ArrayList<>();
    ArrayList<String> permissionsToRequest;
    final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    public final static int ALL_PERMISSIONS_RESULT = 102;
    ArrayList<String> permissionsRejected = new ArrayList<>();
    boolean canGetLocation = true;
    String type="";
    private final static int file_perm = 2;





    private static final String TAG = MainActivity.class.getSimpleName();
    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;
    private final static int FCR=1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= 21) {
            Uri[] results = null;
            //Check if response is positive
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == FCR) {
                    if (null == mUMA) {
                        return;
                    }
                    if (intent == null) {
                        //Capture Photo if no image available
                        if (mCM != null) {
                            results = new Uri[]{Uri.parse(mCM)};
                        }
                    } else {
                        String dataString = intent.getDataString();
                        if (dataString != null) {
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }
            }
            mUMA.onReceiveValue(results);
            mUMA = null;
        } else {
            if (requestCode == FCR) {
                if (null == mUM) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                mUM.onReceiveValue(result);
                mUM = null;
            }
        }
    }







    @SuppressLint("SetJavaScriptEnabled")
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
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
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


//

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

                                       public boolean onShowFileChooser(
                                               WebView webView, ValueCallback<Uri[]> filePathCallback,
                                               WebChromeClient.FileChooserParams fileChooserParams){
                                           if(mUMA != null){
                                               mUMA.onReceiveValue(null);
                                           }
                                           mUMA = filePathCallback;
                                           Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                           if(takePictureIntent.resolveActivity(MainActivity.this.getPackageManager()) != null){
                                               File photoFile = null;
                                               try{
                                                   photoFile = createImageFile();
                                                   takePictureIntent.putExtra("PhotoPath", mCM);
                                               }catch(IOException ex){
                                                   Log.e(TAG, "Image file creation failed", ex);
                                               }
                                               if(photoFile != null){
                                                   mCM = "file:" + photoFile.getAbsolutePath();
                                                   takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                                               }else{
                                                   takePictureIntent = null;
                                               }
                                           }
                                           Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                           contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                                           contentSelectionIntent.setType("*/*");
                                           Intent[] intentArray;
                                           if(takePictureIntent != null){
                                               intentArray = new Intent[]{takePictureIntent};
                                           }else{
                                               intentArray = new Intent[0];
                                           }

                                           Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                                           chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                                           chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                                           chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                                           startActivityForResult(chooserIntent, FCR);
                                           return true;
                                       }







                                   }



        );



       webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {

                if(!check_permission(2)){
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, file_perm);
                }else {
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                    request.setMimeType(mimeType);
                    String cookies = CookieManager.getInstance().getCookie(url);
                    request.addRequestHeader("cookie", cookies);
                    request.addRequestHeader("User-Agent", userAgent);
                    request.setDescription(getString(R.string.dl_downloading));
                    request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType));
                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    assert dm != null;
                    dm.enqueue(request);
                    Toast.makeText(getApplicationContext(), getString(R.string.dl_downloading2), Toast.LENGTH_LONG).show();
                }
            }
        });




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



    private File createImageFile() throws IOException{
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_"+timeStamp+"_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName,".jpg",storageDir);
    }

    public boolean check_permission(int permission){
        switch(permission){
            case 1:
                return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

            case 2:
                return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

            case 3:
                return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

        }
        return false;
    }






}








