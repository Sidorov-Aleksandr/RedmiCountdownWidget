package com.sidorov.redmicountdown;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.*;
import java.time.LocalDate;
public class ConfigActivity extends Activity {
 @Override public void onCreate(Bundle b){
  super.onCreate(b);
  int id=getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
  if(id==AppWidgetManager.INVALID_APPWIDGET_ID){finish();return;}
  setResult(RESULT_CANCELED);
  SharedPreferences prefs=getSharedPreferences(CountdownWidget.PREF,MODE_PRIVATE);
  LocalDate d;
  try{d=LocalDate.parse(prefs.getString("date_"+id,"2027-05-02"));}catch(Exception e){d=LocalDate.of(2027,5,2);}
  LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(30,40,30,30);
  TextView heading=new TextView(this);heading.setText("Настройка обратного отсчёта");heading.setTextSize(22);root.addView(heading);
  EditText title=new EditText(this);title.setSingleLine(true);title.setHint("Название события");title.setText(prefs.getString("title_"+id,"До 2 мая 2027"));root.addView(title);
  DatePicker picker=new DatePicker(this);picker.init(d.getYear(),d.getMonthValue()-1,d.getDayOfMonth(),null);root.addView(picker,new LinearLayout.LayoutParams(-1,0,1));
  Spinner colors=new Spinner(this);String[] options={"Тёмный","Синий","Зелёный","Фиолетовый"};colors.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,options));colors.setSelection(prefs.getInt("theme_"+id,0));root.addView(colors);
  Button save=new Button(this);save.setText("СОХРАНИТЬ И ДОБАВИТЬ");root.addView(save);
  setContentView(root);
  save.setOnClickListener(v->{
   String date=String.format(java.util.Locale.ROOT,"%04d-%02d-%02d",picker.getYear(),picker.getMonth()+1,picker.getDayOfMonth());
   prefs.edit().putString("date_"+id,date).putString("title_"+id,title.getText().toString().trim().isEmpty()?"Моё событие":title.getText().toString().trim()).putInt("theme_"+id,colors.getSelectedItemPosition()).apply();
   CountdownWidget.refresh(this,id);
   Intent result=new Intent();result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,id);setResult(RESULT_OK,result);finish();
  });
 }
}