package org.kreal.ftpserver;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {
    final static String TAG = NewAppWidget.class.getSimpleName();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        if(FtpServerCC.getFtpServerState()==0) {
            views.setImageViewResource(R.id.ftpWidget,R.drawable.ftpoff);
            views.setOnClickPendingIntent(R.id.ftpWidget,PendingIntent.getBroadcast(context,0,new Intent(FtpServerCC.ACTION_START_FTPSERVER),PendingIntent.FLAG_UPDATE_CURRENT));
        }
        else {
            views.setImageViewResource(R.id.ftpWidget,R.drawable.ftpon);
            views.setOnClickPendingIntent(R.id.ftpWidget,PendingIntent.getBroadcast(context,0,new Intent(FtpServerCC.ACTION_STOP_FTPSERVER),PendingIntent.FLAG_UPDATE_CURRENT));
        }
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.v(TAG,"Update FTP Widget -- "+intent.getAction());
        if (intent.getAction().equals(FtpServerCC.ACTION_START_FTPSERVER)||intent.getAction().equals(FtpServerCC.ACTION_STOP_FTPSERVER)) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
            if(FtpServerCC.getFtpServerState()==0) {
                views.setImageViewResource(R.id.ftpWidget,R.drawable.ftpoff);
                views.setOnClickPendingIntent(R.id.ftpWidget,PendingIntent.getBroadcast(context,0,new Intent(FtpServerCC.ACTION_START_FTPSERVER),PendingIntent.FLAG_UPDATE_CURRENT));
            }
            else {
                views.setImageViewResource(R.id.ftpWidget,R.drawable.ftpon);
                views.setOnClickPendingIntent(R.id.ftpWidget,PendingIntent.getBroadcast(context,0,new Intent(FtpServerCC.ACTION_STOP_FTPSERVER),PendingIntent.FLAG_UPDATE_CURRENT));
            }
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(new ComponentName(context, NewAppWidget.class), views);
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

