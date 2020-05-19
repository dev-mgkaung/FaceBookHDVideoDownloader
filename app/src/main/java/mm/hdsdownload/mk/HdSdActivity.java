package mm.hdsdownload.mk;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.htetznaing.xgetter.Model.XModel;
import com.htetznaing.xgetter.XGetter;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HdSdActivity extends AppCompatActivity {
    XGetter xGetter;
    SimpleArcDialog dialog;
    String org;
    XDownloader xDownloader;
    XModel current_Xmodel = null;
    String urls = "";
    Button sdBtn;
    Button hdBtn;
    EditText editText;
    TextView filepath;
    private AdView adView;
    ArrayList<XModel> Mymodel = new ArrayList<>();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getCurrentLocalDateTimeStamp() {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
        Date now = new Date();
        String fileName = formatter.format(now);
        return fileName;
    }

    public void facebookAds() {
        adView = new AdView(this, "IMG_16_9_APP_INSTALL#966313450451266_966314493784495", AdSize.BANNER_HEIGHT_50);
        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container2);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        adView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback

            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad loaded callback
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        });

        // Request an ad
        adView.loadAd();
    }
    private InterstitialAd interstitialAd ;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_link);
        AudienceNetworkAds.initialize(this);
        interstitialAd = new InterstitialAd(this, "966313450451266_973175566431721");
        interstitialAd.setAdListener(new InterstitialAdListener() {

            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }

            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {

            }
        });
        // load the ad
        interstitialAd.loadAd();
        Intent intent = getIntent();
        urls = intent.getStringExtra("url");

        editText = (EditText) findViewById(R.id.filename_edittext);
        filepath = (TextView) findViewById(R.id.filepath);
        sdBtn = (Button) findViewById(R.id.sdBtn);
        hdBtn = (Button) findViewById(R.id.hdBtn);
        String fp = Environment.getExternalStorageDirectory() + "/VideoDownloadFB/";
        filepath.setText(fp);
        editText.setText(getCurrentLocalDateTimeStamp());
        sdBtn.setVisibility(View.GONE);
        hdBtn.setVisibility(View.GONE);
        generateDownloadLink(urls);
        sdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                done(Mymodel.get(0));
            }
        });
        hdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                done(Mymodel.get(1));
            }
        });
        facebookAds();
    }
    @Override
    protected void onDestroy() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }
    public void generateDownloadLink(String strUrl) {
        this.dialog = new SimpleArcDialog(this);
        this.dialog.setConfiguration(new ArcConfiguration(this, SimpleArcLoader.STYLE.COMPLETE_ARC));
        this.dialog.setCancelable(false);

        this.dialog.show();
        xGetter = new XGetter(HdSdActivity.this);
        xGetter.find(strUrl);
        xGetter.onFinish(new XGetter.OnTaskCompleted() {

            @Override
            public void onTaskCompleted(ArrayList<XModel> vidURL, boolean multiple_quality) {
                dialog.dismiss();
                if (multiple_quality) {
                    if (vidURL != null) {

                        for (XModel model : vidURL) {
                            String url = model.getUrl();
                            String cookie = model.getCookie();
                            if (url == null) {
                                sdBtn.setVisibility(View.VISIBLE);
                                hdBtn.setVisibility(View.GONE);
                            }
                        }
                        multipleQualityDialog(vidURL);
                        Mymodel = vidURL;

                    } else done(null);
                } else {
                    done(vidURL.get(0));
                    Mymodel = vidURL;

                }
            }

            @Override
            public void onError() {
                dialog.dismiss();
                done(null);
            }
        });

        xDownloader = new XDownloader(HdSdActivity.this);
        xDownloader.OnDownloadFinishedListerner(new XDownloader.OnDownloadFinished() {
            @Override
            public void onCompleted(String path) {
            }
        });
    }

    private void multipleQualityDialog(final ArrayList<XModel> model) {
        CharSequence[] name = new CharSequence[model.size()];

        for (int i = 0; i < model.size(); i++) {
            name[i] = model.get(i).getQuality();
        }

        for (XModel models : model) {
            String url = models.getUrl();
            String cookie = models.getCookie();
            if (url == null) {
                sdBtn.setVisibility(View.VISIBLE);
                hdBtn.setVisibility(View.GONE);
            } else {
                sdBtn.setVisibility(View.VISIBLE);
                hdBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    private void done(final XModel xModel) {
        String url = null;
        if (xModel != null) {
            url = xModel.getUrl();
        }
        MaterialStyledDialog.Builder builder = new MaterialStyledDialog.Builder(HdSdActivity.this);
        if (url != null) {
            String finalUrl = url;
            builder.setTitle("Congratulations!")
                    .setDescription("Now,you can stream or download.")
                    .setStyle(Style.HEADER_WITH_ICON)
                    .setIcon(R.drawable.ic_beenhere_black_24dp)
                    .withDialogAnimation(true)
                    .setPositiveText("Stream")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            watchDialog(xModel);
                        }
                    })
                    .setNegativeText("Download")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            downloadDialog(xModel);
                        }
                    });
        } else {
            builder.setTitle("Sorry!")
                    .setDescription("Video Not Found")
                    .setStyle(Style.HEADER_WITH_ICON)
                    .setIcon(R.drawable.ic_error_black_24dp)
                    .withDialogAnimation(true)
                    .setPositiveText("OK")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    });
        }
        MaterialStyledDialog dialog = builder.build();
        dialog.show();
    }

    private void watchDialog(final XModel xModel) {
        MaterialStyledDialog.Builder builder = new MaterialStyledDialog.Builder(this);
        builder.setTitle("Notice!")
                .setDescription("Choose your player")
                .setStyle(Style.HEADER_WITH_ICON)
                .setIcon(R.drawable.ic_movie_filter_black_24dp)
                .withDialogAnimation(true)
                .setPositiveText("Simple Exoplayer")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(getApplicationContext(), SimpleVideoPlayer.class);
                        intent.putExtra("url", xModel.getUrl());
                        intent.putExtra("fname", editText.getText().toString());
                        if (xModel.getCookie() != null) {
                            intent.putExtra("cookie", xModel.getCookie());
                        }
                        startActivity(intent);
                    }
                })

                .setNeutralText("MXPlayer")
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        openWithMXPlayer(xModel);
                    }
                });
        MaterialStyledDialog dialog = builder.build();
        dialog.show();
    }

    //Example Open Google Drive Video with MX Player
    private void openWithMXPlayer(XModel xModel) {
        boolean appInstalledOrNot = appInstalledOrNot("com.mxtech.videoplayer.ad");
        boolean appInstalledOrNot2 = appInstalledOrNot("com.mxtech.videoplayer.pro");
        String str2;
        if (appInstalledOrNot || appInstalledOrNot2) {
            String str3;
            if (appInstalledOrNot2) {
                str2 = "com.mxtech.videoplayer.pro";
                str3 = "com.mxtech.videoplayer.ActivityScreen";
            } else {
                str2 = "com.mxtech.videoplayer.ad";
                str3 = "com.mxtech.videoplayer.ad.ActivityScreen";
            }
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(xModel.getUrl()), "application/x-mpegURL");
                intent.setPackage(str2);
                intent.setClassName(str2, str3);
                if (xModel.getCookie() != null) {
                    intent.putExtra("headers", new String[]{"cookie", xModel.getCookie()});
                    intent.putExtra("secure_uri", true);
                }
                startActivity(intent);
                return;
            } catch (Exception e) {
                e.fillInStackTrace();
                Log.d("errorMx", e.getMessage());
                return;
            }
        }
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.mxtech.videoplayer.ad")));
        } catch (ActivityNotFoundException e2) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.mxtech.videoplayer.ad")));
        }
    }

    public boolean appInstalledOrNot(String str) {
        try {
            getPackageManager().getPackageInfo(str, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void downloadDialog(final XModel xModel) {
        MaterialStyledDialog.Builder builder = new MaterialStyledDialog.Builder(this);
        builder.setTitle("Download Details")
                .setDescription("File Name :" + editText.getText().toString() + ".mp4")
                .setStyle(Style.HEADER_WITH_ICON)
                .setIcon(R.drawable.ic_cloud_download_black_24dp)
                .withDialogAnimation(true)
                .setPositiveText("Download")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        downloadFile(xModel, editText.getText().toString());
                    }
                });

        MaterialStyledDialog dialog = builder.build();
        dialog.show();
    }

    private void downloadFile(XModel xModel, String filename) {
        current_Xmodel = xModel;
        xDownloader.download(current_Xmodel, filename);
    }

}
