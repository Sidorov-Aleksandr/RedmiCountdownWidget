package com.sidorov.redmicountdown;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.os.SystemClock;
import java.time.*;
public class CountdownWidget extends AppWidgetProvider {
 public void onUpdate(Context c, AppWidgetManager m, int[] ids) {
  for(int id:ids) {
   long ms=Math.max(0,Duration.between(Instant.now(),LocalDate.of(2027,5,2).atStartOfDay(ZoneId.systemDefault()).toInstant()).toMillis());
   long days=ms/86400000L;
   long rest=ms%86400000L;
   RemoteViews v=new RemoteViews(c.getPackageName(),R.layout.widget);
   v.setTextViewText(R.id.days,days+" дней");
   v.setTextViewText(R.id.hours,String.format(java.util.Locale.getDefault(),"%02d ч  %02d мин",rest/3600000L,(rest/60000L)%60L));
   v.setOnClickPendingIntent(R.id.root,PendingIntent.getActivity(c,0,new Intent(c,MainActivity.class),PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT));
   m.updateAppWidget(id,v);
  }
 }
}