package org.kreal.ftpserver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class FTPAppWidget extends AppWidgetProvider {
    final static String TAG = FTPAppWidget.class.getSimpleName();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = createview(context,true);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
    static public  RemoteViews createview(Context context , boolean isoff){
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        views.setImageViewResource(R.id.ftpWidget,isoff ? R.drawable.ftpoff : R.drawable.ftpon);
        Intent intent = new Intent(isoff ? FtpServerAndroid.ACTION_START_FTPSERVER : FtpServerAndroid.ACTION_STOP_FTPSERVER );
        views.setOnClickPendingIntent(R.id.ftpWidget,PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT));
        return views;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.v(TAG,"Update FTP Widget -- "+intent.getAction());
        if (intent.getAction().equals(FtpServerAndroid.FTPSERVER_STARTED)||intent.getAction().equals(FtpServerAndroid.FTPSERVER_STOPED)) {
            RemoteViews views = createview(context,intent.getAction().equals(FtpServerAndroid.FTPSERVER_STOPED));
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(new ComponentName(context, FTPAppWidget.class), views);
        }
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }


}

