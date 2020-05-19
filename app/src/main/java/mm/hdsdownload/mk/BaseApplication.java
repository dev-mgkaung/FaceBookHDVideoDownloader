package mm.hdsdownload.mk;

import android.app.Application;

import com.facebook.ads.AudienceNetworkAds;


public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(this);
    }
}
