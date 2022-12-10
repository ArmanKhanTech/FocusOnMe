package com.android.focusonme.Service;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.android.focusonme.Activity.BlockAppActivity;
import com.android.focusonme.Activity.EnterPasswordActivity;
import com.android.focusonme.Activity.MainActivity;
import com.android.focusonme.DataBase.AnalysisDatabase;
import com.android.focusonme.DataBase.InternetBlockDatabase;
import com.android.focusonme.DataBase.SaveLimitPackages;
import com.android.focusonme.DataBase.SaveRestrictPackages;
import com.android.focusonme.R;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

public class ForegroundService extends Service {

    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    Context mContext;

    SaveRestrictPackages db = new SaveRestrictPackages(this);
    SaveLimitPackages db1=new SaveLimitPackages(this);
    AnalysisDatabase db2=new AnalysisDatabase(this);
    InternetBlockDatabase db3=new InternetBlockDatabase(this);

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        if(db.isDbEmpty()) {
            check.start();
        }

        if(db1.isDbEmpty()){
            check.start();
        }

        if(db3.isDbEmpty()){
            check.start();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        SharedPreferences sh = getSharedPreferences("PASS_CODE", Context.MODE_PRIVATE);
        int i = sh.getInt("pass",0 );
        Intent notificationIntent;
        if(i>0){
            notificationIntent = new Intent(this, EnterPasswordActivity.class);
        }
        else{
            notificationIntent = new Intent(this, MainActivity.class);
        }
        PendingIntent pendingIntent;
        pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("FocusOnMe")
                .setContentText(input)
                .setSmallIcon(R.drawable.notification_png)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected CountDownTimer check = new CountDownTimer(10, 10) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            String currentApp = "";
            UsageStatsManager usm = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                    time - 10 * 10, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(),
                            usageStats);
                }
                if (!mySortedMap.isEmpty()) {
                    currentApp = Objects.requireNonNull(mySortedMap.get(mySortedMap.lastKey())).getPackageName();
                }
            }

            ArrayList<String> packs= db.readRestrictPacks();
            ArrayList <String> packs1= db1.readLimitPacks();
            ArrayList <String> packs2= db3.readInternetPacks();

            if(packs.contains(currentApp)) {
                this.cancel();
                Intent lockIntent = new Intent(mContext, BlockAppActivity.class);
                lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                lockIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                lockIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                lockIntent.putExtra("PACK_NAME",currentApp);
                String msg="This App Is Blocked By FocusOnMe";
                lockIntent.putExtra("MSG",msg);
                db2.inAppBlocked(currentApp);
                startActivity(lockIntent);
            }

            else if(packs1.contains(currentApp)){
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                long startMillis;
                long endMillis;

                calendar.set(Calendar.HOUR_OF_DAY, 1);
                startMillis = calendar.getTimeInMillis();
                endMillis = System.currentTimeMillis();

                Map<String, UsageStats> lUsageStatsMap = usm.
                        queryAndAggregateUsageStats(startMillis, endMillis);

                String total = "";

                if (lUsageStatsMap.containsKey(currentApp)) {
                    long m=(Objects.requireNonNull(lUsageStatsMap.get(currentApp)).
                            getTotalTimeInForeground());
                    total=String.valueOf(m);
                }

                String compare=db1.readDuration(currentApp);

                long m1=Long.parseLong(total);
                long m2=Long.parseLong(compare);

                if(m1>m2) {
                    this.cancel();
                    Intent lockIntent = new Intent(mContext, BlockAppActivity.class);
                    lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    lockIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    lockIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    lockIntent.putExtra("PACK_NAME", currentApp);
                    String msg="This App Is Blocked By FocusOnMe";
                    lockIntent.putExtra("MSG",msg);
                    db2.inAppBlocked(currentApp);
                    startActivity(lockIntent);
                }
            }

            else if(packs2.contains(currentApp)){
                String temp=db3.readData(currentApp);
                float data=Float.parseFloat(temp);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                long startMillis;
                long endMillis;

                startMillis = calendar.getTimeInMillis();
                endMillis = System.currentTimeMillis();

                float currentData=getPkgInfo(startMillis, endMillis ,currentApp);

                if(data<currentData){
                    this.cancel();
                    Intent lockIntent = new Intent(mContext, BlockAppActivity.class);
                    lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    lockIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    lockIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    lockIntent.putExtra("PACK_NAME",currentApp);
                    String msg="This App Exceeds The Current Data Usage Limit";
                    lockIntent.putExtra("MSG",msg);
                    startActivity(lockIntent);
                }
            }
            this.start();
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    public float getPkgInfo(long startMillis, long endMillis, String pkgName){
        PackageManager packageManager = this.getPackageManager();
        ApplicationInfo info = null;
        int uid;
        try {
            info = packageManager.getApplicationInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        uid = Objects.requireNonNull(info).uid;
        return fetchNetworkStatsInfo(startMillis, endMillis, uid);
    }

    public float fetchNetworkStatsInfo(long startMillis, long endMillis, int uid) {
        NetworkStatsManager networkStatsManager;
        float total = 0.0f;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            float receivedWifi = 0;
            float sentWifi = 0;
            float receivedMobData = 0;
            float sentMobData = 0;

            networkStatsManager = (NetworkStatsManager) this.getSystemService(Context.NETWORK_STATS_SERVICE);
            NetworkStats nwStatsWifi = networkStatsManager.queryDetailsForUid(ConnectivityManager.TYPE_WIFI, null,
                    startMillis, endMillis, uid);
            NetworkStats.Bucket bucketWifi = new NetworkStats.Bucket();
            while (nwStatsWifi.hasNextBucket()) {
                nwStatsWifi.getNextBucket(bucketWifi);
                receivedWifi = receivedWifi + bucketWifi.getRxBytes();
                sentWifi = sentWifi + bucketWifi.getTxBytes();
            }

            NetworkStats nwStatsMobData = networkStatsManager.queryDetailsForUid(ConnectivityManager.TYPE_MOBILE, null,
                    startMillis, endMillis, uid);
            NetworkStats.Bucket bucketMobData = new NetworkStats.Bucket();
            while (nwStatsMobData.hasNextBucket()) {
                nwStatsMobData.getNextBucket(bucketMobData);
                receivedMobData = receivedMobData + bucketMobData.getRxBytes();
                sentMobData = sentMobData + bucketMobData.getTxBytes();
            }
            total = (receivedWifi + sentWifi + receivedMobData + sentMobData) / (1024 * 1024);
        }
        DecimalFormat df=new DecimalFormat("00000");
        df.setRoundingMode(RoundingMode.DOWN);
        total= Float.parseFloat(df.format(total));

        return total;
    }
}