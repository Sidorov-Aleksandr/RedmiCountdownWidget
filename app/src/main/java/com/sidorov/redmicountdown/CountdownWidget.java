package com.sidorov.redmicountdown;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.os.SystemClock;
import java.time.*;
public class CountdownWidget extends AppWidgetProvider {
 public static final String PREF="countdowns";
 public static void refresh(Context c,int id){
  SharedPreferences p=c.getSharedPreferences(PREF,Context.MODE_PRIVATE);
  String date=p.getString("date_"+id,"2027-05-02");
  String title=p.getString("title_"+id,"До 2 мая 2027");
  int theme=p.getInt("theme_"+id,0);
  long remaining;
  try { remaining=Math.max(0,Duration.between(Instant.now(),LocalDate.parse(date).atStartOfDay(ZoneId.systemDefault()).toInstant()).toMillis()); }
  catch(Exception e){remaining=0;}
  RemoteViews v=new RemoteViews(c.getPackageName(),R.layout.widget);
  v.setInt(R.id.root,"setBackgroundResource",theme==1?R.drawable.bg_blue:theme==2?R.drawable.bg_green:theme==3?R.drawable.bg_purple:R.drawable.bg);
  v.setTextViewText(R.id.title,title);
  v.setTextViewText(R.id.days,(remaining/86400000L)+" дней");
  v.setChronometerCountDown(R.id.timer,true);
  v.setChronometer(R.id.timer,SystemClock.elapsedRealtime()+remaining,"%s",remaining>0);
  Intent intent=new Intent(c,ConfigActivity.class);
  intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,id);
  PendingIntent pi=PendingIntent.getActivity(c,id,intent,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
  v.setOnClickPendingIntent(R.id.root,pi);
  AppWidgetManager.getInstance(c).updateAppWidget(id,v);
 }
 @Override public void onUpdate(Context c,AppWidgetManager m,int[] ids){for(int id:ids)refresh(c,id);}
 @Override public void onDeleted(Context c,int[] ids){
  SharedPreferences.Editor e=c.getSharedPreferences(PREF,Context.MODE_PRIVATE).edit();
  for(int id:ids){e.remove("date_"+id).remove("title_"+id).remove("theme_"+id);}e.apply();
 }
}