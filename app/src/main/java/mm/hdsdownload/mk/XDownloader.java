package mm.hdsdownload.mk;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;
import com.htetznaing.xgetter.Model.XModel;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.os.Environment.getExternalStorageDirectory;

public class XDownloader {
    private Activity activity;
    private String mBaseFolderPath;
    private DownloadManager mDownloadManager;
    private long mDownloadedFileID;
    private DownloadManager.Request mRequest;
    private OnDownloadFinished onDownloadFinished;

    public XDownloader(Activity activity){
        this.activity = activity;
        mDownloadManager = (DownloadManager) activity.getSystemService(DOWNLOAD_SERVICE);
        mBaseFolderPath  =getExternalStorageDirectory()+"/VideoDownloadFB/";
    }

    public void download(XModel xModel,String f){
        try {
            String fileName =f+".mp4";
            if (!new File(mBaseFolderPath).exists()) {
                new File(mBaseFolderPath).mkdir();
            }
            String mFilePath = mBaseFolderPath + fileName;
            Uri downloadUri = Uri.parse(xModel.getUrl());
            mRequest = new DownloadManager.Request(downloadUri);
            mRequest.setDestinationUri(Uri.parse(mFilePath));

            if (xModel.getCookie()!=null){
                mRequest.addRequestHeader("cookie", xModel.getCookie());
            }

            mRequest.setMimeType("video/*");
            mRequest.setTitle(fileName);
            mRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            mRequest.setDestinationInExternalPublicDir(getExternalStorageDirectory()+"/VideoDownloadFB/",fileName);
            mDownloadedFileID = mDownloadManager.enqueue(mRequest);
            IntentFilter downloaded = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            activity.registerReceiver(downloadCompletedReceiver, downloaded);
            Toast.makeText(activity, "Starting Download : " + fileName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            try {
                intent.setDataAndType(Uri.parse(URLDecoder.decode(xModel.getUrl(), "UTF-8")), "video/mp4");
                activity.startActivity(Intent.createChooser(intent, "Download with..."));
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
        }
    }

    private BroadcastReceiver downloadCompletedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Uri uri = mDownloadManager.getUriForDownloadedFile(mDownloadedFileID);
            onDownloadFinished.onCompleted(getRealPathFromURI(uri));
        }
    };

    private String getRealPathFromURI (Uri contentUri) {
        String path = null;
        String[] proj = { MediaStore.MediaColumns.DATA };
        Cursor cursor = activity.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            path = cursor.getString(column_index);
        }
        cursor.close();
        return path;
    }

    public void OnDownloadFinishedListerner(OnDownloadFinished onDownloadFinished){
        this.onDownloadFinished=onDownloadFinished;
    }

    public interface OnDownloadFinished {
        void onCompleted(String path);
    }
}