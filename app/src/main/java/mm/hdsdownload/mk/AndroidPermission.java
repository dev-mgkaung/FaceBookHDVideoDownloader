package mm.hdsdownload.mk;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;

public class AndroidPermission {
    public static int REQUEST_CODE = 1000;

    public static String[] ALL_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    public static boolean checkPermission(Activity activity){
        if (!hasPermissions(activity, ALL_PERMISSIONS)) {
            ActivityCompat.requestPermissions(activity, ALL_PERMISSIONS, REQUEST_CODE);
            return false;
        }else{
            return true;
        }
    }

    public static boolean checkPermission(Activity activity, String[] permissions,int requestCode){
        if (!hasPermissions(activity, permissions)) {
            REQUEST_CODE = requestCode;
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE);
            return false;
        }else{
            return true;
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isSuccessRequestPermission(int requestCode, String[] permissions, int[] grantResults){
        int count = 0;
        if(requestCode == REQUEST_CODE){
           for(int grantResult:grantResults){
               if(grantResult == PackageManager.PERMISSION_GRANTED){
                   count++;
               }
           }
           if(grantResults.length==count){
               return true;
           }
           return false;
       }
       return false;
    }
}
