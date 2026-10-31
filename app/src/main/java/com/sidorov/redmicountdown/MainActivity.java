package com.sidorov.redmicountdown;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
public class MainActivity extends Activity {
 @Override public void onCreate(Bundle b) {
  super.onCreate(b);
  LinearLayout layout=new LinearLayout(this);
  layout.setOrientation(LinearLayout.VERTICAL);
  layout.setPadding(36,70,36,36);
  TextView info=new TextView(this);
  info.setText("Обратный отсчёт до 2 мая 2027\n\nНажмите кнопку ниже, чтобы добавить виджет на рабочий стол.");
  info.setTextSize(21);
  layout.addView(info);
  Button add=new Button(this);
  add.setText("ДОБАВИТЬ ВИДЖЕТ НА ЭКРАН");
  layout.addView(add);
  setContentView(layout);
  AppWidgetManager manager=AppWidgetManager.getInstance(this);
  ComponentName provider=new ComponentName(this,CountdownWidget.class);
  add.setOnClickListener(v->{
   if(manager.isRequestPinAppWidgetSupported()){
    boolean requested=manager.requestPinAppWidget(provider,null,null);
    if(!requested)Toast.makeText(this,"Не удалось отправить запрос. Откройте список виджетов.",Toast.LENGTH_LONG).show();
   } else {
    Toast.makeText(this,"Эта версия рабочего стола не поддерживает добавление кнопкой. Проверьте список виджетов.",Toast.LENGTH_LONG).show();
   }
  });
  int[] ids=manager.getAppWidgetIds(provider);
  new CountdownWidget().onUpdate(this,manager,ids);
 }
}