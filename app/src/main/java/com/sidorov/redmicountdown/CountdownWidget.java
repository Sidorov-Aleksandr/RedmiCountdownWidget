package com.sidorov.redmicountdown;
import android.appwidget.*;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.widget.*;
import android.os.SystemClock;
import java.time.*;
import java.io.*;
public class CountdownWidget extends AppWidgetProvider {
 public static final String PREF="countdowns";
 public static final int[] COLORS={0xFF151B29,0xFF16233E,0xFF123A32,0xFF352044,0xFF802F3F,0xFF9C5B2C,0xFF273B57,0xFF42352B,0xFF233B3B,0xFF4A264C,0xFF303030,0xFFE9D9C1};
 public static void refresh(Context c,int id){
  SharedPreferences p=c.getSharedPreferences(PREF,Context.MODE_PRIVATE);
  int font=Math.max(0,Math.min(3,p.getInt("font_"+id,0)));
  int[] layouts={R.layout.widget,R.layout.widget_font1,R.layout.widget_font2,R.layout.widget_font3};
  RemoteViews v=new RemoteViews(c.getPackageName(),layouts[font]);
  String date=p.getString("date_"+id,"2027-05-02");
  long remaining;
  try{remaining=Math.max(0,Duration.between(Instant.now(),LocalDate.parse(date).atStartOfDay(ZoneId.systemDefault()).toInstant()).toMillis());}
  catch(Exception e){remaining=0;}
  int theme=Math.max(0,Math.min(COLORS.length-1,p.getInt("theme_"+id,0)));
  Bitmap bg=Bitmap.createBitmap(720,300,Bitmap.Config.ARGB_8888);
  Canvas canvas=new Canvas(bg);
  Paint paint=new Paint(3);
  paint.setColor(COLORS[theme]);canvas.drawRoundRect(0,0,720,300,35,35,paint);
  String image=p.getString("photo_"+id,"");
  if(!image.isEmpty()){
   try{
    BitmapFactory.Options o=new BitmapFactory.Options();o.inSampleSize=2;
    Bitmap photo=BitmapFactory.decodeFile(image,o);
    if(photo!=null){
     canvas.save();
     Path clip=new Path();clip.addRoundRect(0,0,720,300,35,35,Path.Direction.CW);canvas.clipPath(clip);
     float scale=Math.max(720f/photo.getWidth(),300f/photo.getHeight());
     float w=photo.getWidth()*scale,h=photo.getHeight()*scale;
     paint.setColor(Color.WHITE);canvas.drawBitmap(photo,null,new RectF((720-w)/2,(300-h)/2,(720+w)/2,(300+h)/2),paint);
     paint.setColor(0x77000000);canvas.drawRect(0,0,720,300,paint);
     canvas.restore();photo.recycle();
    }
   }catch(Exception ignored){}
  }
  v.setImageViewBitmap(R.id.backdrop,bg);
  int textColor=p.getInt("textcolor_"+id,Color.WHITE);
  v.setTextColor(R.id.title,textColor);v.setTextColor(R.id.days,textColor);v.setTextColor(R.id.timer,textColor);
  v.setTextViewText(R.id.title,p.getString("title_"+id,"До 2 мая 2027"));
  v.setTextViewText(R.id.days,(remaining/86400000L)+" дней");
  v.setChronometerCountDown(R.id.timer,true);
  v.setChronometer(R.id.timer,SystemClock.elapsedRealtime()+remaining,"%s",remaining>0);
  Intent intent=new Intent(c,ConfigActivity.class);intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,id);
  v.setOnClickPendingIntent(R.id.root,PendingIntent.getActivity(c,id,intent,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE));
  AppWidgetManager.getInstance(c).updateAppWidget(id,v);
 }
 @Override public void onUpdate(Context c,AppWidgetManager m,int[] ids){for(int id:ids)refresh(c,id);}
 @Override public void onDeleted(Context c,int[] ids){
  SharedPreferences p=c.getSharedPreferences(PREF,Context.MODE_PRIVATE);
  SharedPreferences.Editor e=p.edit();
  for(int id:ids){
   String file=p.getString("photo_"+id,"");if(!file.isEmpty())new File(file).delete();
   for(String key:new String[]{"date_","title_","theme_","font_","photo_","textcolor_"})e.remove(key+id);
  }e.apply();
 }
}