package com.sidorov.redmicountdown;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.os.Bundle;
import android.widget.TextView;
public class MainActivity extends Activity {
 @Override public void onCreate(Bundle b){super.onCreate(b);TextView t=new TextView(this);t.setText("Отсчёт до 2 мая 2027\n\nДобавьте виджет на рабочий стол Redmi через меню «Виджеты».\n\nНажмите на виджет для обновления.");t.setTextSize(20);t.setPadding(40,70,40,40);setContentView(t);AppWidgetManager m=AppWidgetManager.getInstance(this);int[] ids=m.getAppWidgetIds(new ComponentName(this,CountdownWidget.class));new CountdownWidget().onUpdate(this,m,ids);}
}