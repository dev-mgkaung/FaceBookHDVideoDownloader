package mm.hdsdownload.mk;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

import java.io.File;
import java.util.EmptyStackException;

public class HomeActivity extends AppCompatActivity {
    Button facebookbtn;
    int count;
    private InterstitialAd interstitialAd ;
    private int video_column_index;
    RelativeLayout relativelayout;
    LinearLayout linearLayout;
    private Cursor videocursor;

    private AdapterView.OnItemClickListener videogridlistener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView adapterView, View view, final int i, long j) {
            PopupMenu popupMenu = new PopupMenu(HomeActivity.this, view);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                final int pos = i;

                public boolean onMenuItemClick(MenuItem menuItem) {
                    int itemId = menuItem.getItemId();
                    if (itemId == R.id.MPlay) {
                        System.gc();
                        video_column_index = videocursor.getColumnIndexOrThrow("_data");
                        videocursor.moveToPosition(this.pos);
                        streamFB(videocursor.getString(video_column_index));
                        return true;
                    } else if (itemId == R.id.MDelete) {
                        System.gc();
                        video_column_index = videocursor.getColumnIndexOrThrow("_data");
                        videocursor.moveToPosition(this.pos);
                        File file = new File(videocursor.getString(video_column_index));
                        String[] strArr = new String[]{file.getAbsolutePath()};
                        ContentResolver contentResolver = HomeActivity.this.getContentResolver();
                        Uri contentUri = MediaStore.Files.getContentUri("external");
                        contentResolver.delete(contentUri, "_data=?", strArr);
                        if (file.exists()) {
                            contentResolver.delete(contentUri, "_data=?", strArr);
                              Toast.makeText(HomeActivity.this, "Delete Successfully!",pos);
                        }
                        init_phone_video_grid();
                        return file.exists();
                    } else if (itemId == R.id.MCancel) {
                        return true;
                    } else {
                        return onMenuItemClick(menuItem);
                    }
                }
            });
        }
    };
    private void init_phone_video_grid() {
        System.gc();
        videocursor =HomeActivity.this.managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{"_id", "_data", "_display_name", "_size"}, "_data like?", new String[]{"%VideoDownloadFB%"}, "datetaken DESC");
        count = this.videocursor.getCount();
         if(count==0){
             //nativeAdShow();
             linearLayout.setVisibility(View.VISIBLE);
             relativelayout.setVisibility(View.GONE);
         }else{ linearLayout.setVisibility(View.GONE);
             relativelayout.setVisibility(View.VISIBLE);}
        videolist.setAdapter(new VideoAdapter(HomeActivity.this));
        videolist.setOnItemClickListener(this.videogridlistener);
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(HomeActivity.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.show();

    }

    public void streamFB(String str) {
        try {
            Intent intent = new Intent(HomeActivity.this, StreamVideo.class);
            intent.putExtra("video_url", str);
            startActivity(intent);
            //     Toast.makeText(this, "Streaming Started", 0).show();
        } catch (EmptyStackException e) {
            Log.e("Error", e.getMessage());
            //    Toast.makeText(this, "Streaming Failed", 0).show();
        }
    }
    ListView videolist;
    public class VideoAdapter extends BaseAdapter {
        private Context vContext;
        public long getItemId(int i) {
            return (long) i;
        }
        public VideoAdapter(Context context) {
            this.vContext = context;
        }
        public int getCount() {
            return count;
        }
        public Object getItem(int i) {
            return Integer.valueOf(i);
        }
        @SuppressLint("WrongViewCast")
        public View getView(int i, View view, ViewGroup viewGroup) {
            System.gc();
            view = LayoutInflater.from(this.vContext).inflate(R.layout.listitem, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.listItem=(ImageView)view.findViewById(R.id.menuid);
            viewHolder.txtTitle = (TextView) view.findViewById(R.id.txtTitle);
            viewHolder.txtSize = (TextView) view.findViewById(R.id.textSize);
            viewHolder.thumbImage = (ImageView) view.findViewById(R.id.imgIcon);
            video_column_index =videocursor.getColumnIndexOrThrow("_display_name");
            videocursor.moveToPosition(i);
            String string = videocursor.getString(video_column_index);
            video_column_index = videocursor.getColumnIndexOrThrow("_size");
            videocursor.moveToPosition(i);

            long value1 = Long.parseLong(videocursor.getString(video_column_index));
            String s=getSize(value1);
            viewHolder.txtTitle.setText(string);

            viewHolder.txtSize.setText("File Size= "+s);
            Cursor managedQuery =HomeActivity.this.managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{"_id", "_display_name", "_data"}, "_display_name=?", new String[]{string}, null);
            managedQuery.moveToFirst();
            long j = managedQuery.getLong(managedQuery.getColumnIndex("_id"));
            ContentResolver contentResolver =HomeActivity.this.getContentResolver();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            viewHolder.thumbImage.setImageBitmap(MediaStore.Video.Thumbnails.getThumbnail(contentResolver, j, 3, options));
            return view;
        }
    }
    static class ViewHolder {
        ImageView thumbImage;
        TextView txtSize;
        TextView txtTitle;
        ImageView listItem;
        ViewHolder() {
        }
    }
    private AdView adView;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }



    public static String getSize(long size) {
        long n = 1000;
        String s = "";
        double kb = size / n;
        double mb = kb / n;
        double gb = mb / n;
        double tb = gb / n;
        if(size < n) {
            s = size + " Bytes";
        } else if(size >= n && size < (n * n)) {
            s =  String.format("%.2f", kb) + " KB";
        } else if(size >= (n * n) && size < (n * n * n)) {
            s = String.format("%.2f", mb) + " MB";
        } else if(size >= (n * n * n) && size < (n * n * n * n)) {
            s = String.format("%.2f", gb) + " GB";
        } else if(size >= (n * n * n * n)) {
            s = String.format("%.2f", tb) + " TB";
        }
        return s;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        getSupportActionBar().setIcon(R.drawable.logo);
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
        relativelayout=(RelativeLayout)findViewById(R.id.relativelayout);
        linearLayout=(LinearLayout)findViewById(R.id.linerlayout);

        linearLayout.setVisibility(View.VISIBLE);
        relativelayout.setVisibility(View.GONE);
        videolist = (ListView) findViewById(R.id.latest_grid);

        if(AndroidPermission.checkPermission(HomeActivity.this)) {
            init_phone_video_grid();

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Environment.getExternalStorageDirectory());
            stringBuilder.append("/VideoDownloadFB/");
            MediaScannerConnection.scanFile(HomeActivity.this, new String[]{new File(stringBuilder.toString()).getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String str, Uri uri) {
                }
            });
        }
        facebookbtn=(Button)findViewById(R.id.browse_btn);
        facebookbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HomeActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onDestroy() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }

}
