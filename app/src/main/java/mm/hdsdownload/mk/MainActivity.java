package mm.hdsdownload.mk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.htetznaing.xgetter.Model.XModel;
import com.htetznaing.xgetter.XGetter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    XGetter xGetter;
    ProgressDialog progressDialog;
    String org;
    XDownloader xDownloader;
    XModel current_Xmodel =null;
    WebView webView;
    String url="";

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();


        webView = (WebView)findViewById(R.id.webview);
        if(checkConnection(this)) {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.addJavascriptInterface((Object) MainActivity.this, "FBDownloader");
            myclient var13_12 = new myclient(MainActivity.this);
            webView.setWebViewClient((WebViewClient) var13_12);
            CookieSyncManager.createInstance((Context) MainActivity.this);
            CookieManager.getInstance().setAcceptCookie(true);
            CookieSyncManager.getInstance().startSync();
            webView.loadUrl("https://m.facebook.com/");
            webView.setOnKeyListener(new View.OnKeyListener()
            {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event)
                {
                    if(event.getAction() == KeyEvent.ACTION_DOWN)
                    {
                        WebView webView = (WebView) v;

                        switch(keyCode)
                        {
                            case KeyEvent.KEYCODE_BACK:
                                if(webView.canGoBack())
                                {
                                    webView.goBack();
                                    return true;
                                }
                                break;
                        }
                    }

                    return false;
                }
            });
        }else {
            webView.loadUrl("file:///android_asset/error.html");
        }

    }
    public static boolean checkConnection(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {

            return false;
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @JavascriptInterface
    public void processVideo(String link, String sid) {
        Intent intent = new Intent(MainActivity.this, HdSdActivity.class);
        intent.putExtra("url", sid);
        startActivity(intent);
    }

    class myclient
            extends WebViewClient {
        private final MainActivity this$0;

        myclient(MainActivity mainActivity) {
            this.this$0 = mainActivity;
        }
        @Override
        public void onLoadResource(WebView webView, String string2) {
            WebView webView2 = webView;
            StringBuffer stringBuffer = new StringBuffer();
            StringBuffer stringBuffer2 = new StringBuffer();
            StringBuffer stringBuffer3 = new StringBuffer();
            StringBuffer stringBuffer4 = new StringBuffer();
            StringBuffer stringBuffer5 = new StringBuffer();
            StringBuffer stringBuffer6 = new StringBuffer();
            StringBuffer stringBuffer7 = new StringBuffer();
            StringBuffer stringBuffer8 = new StringBuffer();
            StringBuffer stringBuffer9 = new StringBuffer();
            StringBuffer stringBuffer10 = new StringBuffer();
            StringBuffer stringBuffer11 = new StringBuffer();
            StringBuffer stringBuffer12 = new StringBuffer();
            webView2.loadUrl(stringBuffer.append(stringBuffer2.append(stringBuffer3.append(stringBuffer4.append(stringBuffer5.append(stringBuffer6.append(stringBuffer7.append(stringBuffer8.append(stringBuffer9.append(stringBuffer10.append(stringBuffer11.append(stringBuffer12.append("javascript:(function prepareVideo() { ").append("var el = document.querySelectorAll('div[data-sigil]');").toString()).append("for(var i=0;i<el.length; i++)").toString()).append("{").toString()).append("var sigil = el[i].dataset.sigil;").toString()).append("if(sigil.indexOf('inlineVideo') > -1){").toString()).append("delete el[i].dataset.sigil;").toString()).append("console.log(i);").toString()).append("var jsonData = JSON.parse(el[i].dataset.store);").toString()).append("el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\",\"'+jsonData['videoID']+'\");');").toString()).append("}").toString()).append("}").toString()).append("})()").toString());
            WebView webView3 = webView;
            StringBuffer stringBuffer13 = new StringBuffer();
            webView3.loadUrl(stringBuffer13.append("javascript:( window.onload=prepareVideo;").append(")()").toString());
        }

        @Override
        public void onPageFinished(WebView webView, String string2) {
            WebView webView2 = webView;
            StringBuffer stringBuffer = new StringBuffer();
            StringBuffer stringBuffer2 = new StringBuffer();
            StringBuffer stringBuffer3 = new StringBuffer();
            StringBuffer stringBuffer4 = new StringBuffer();
            StringBuffer stringBuffer5 = new StringBuffer();
            StringBuffer stringBuffer6 = new StringBuffer();
            StringBuffer stringBuffer7 = new StringBuffer();
            StringBuffer stringBuffer8 = new StringBuffer();
            StringBuffer stringBuffer9 = new StringBuffer();
            StringBuffer stringBuffer10 = new StringBuffer();
            StringBuffer stringBuffer11 = new StringBuffer();
            webView2.loadUrl(stringBuffer.append(stringBuffer2.append(stringBuffer3.append(stringBuffer4.append(stringBuffer5.append(stringBuffer6.append(stringBuffer7.append(stringBuffer8.append(stringBuffer9.append(stringBuffer10.append(stringBuffer11.append("javascript:(function() { ").append("var el = document.querySelectorAll('div[data-sigil]');").toString()).append("for(var i=0;i<el.length; i++)").toString()).append("{").toString()).append("var sigil = el[i].dataset.sigil;").toString()).append("if(sigil.indexOf('inlineVideo') > -1){").toString()).append("delete el[i].dataset.sigil;").toString()).append("var jsonData = JSON.parse(el[i].dataset.store);").toString()).append("el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\");');").toString()).append("}").toString()).append("}").toString()).append("})()").toString());

        }


    }

    private boolean checkPermissions() {
        int storage = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        final List<String> listPermissionsNeeded = new ArrayList<>();
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1000);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==1000){
            if (grantResults.length > 0&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
              //  downloadFile(current_Xmodel);
            } else {
                checkPermissions();
                Toast.makeText(this, "You need to allow this permission!", Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }


}
