package com.sidorov.redmicountdown;
import android.app.*;
import android.appwidget.*;
import android.os.*;
import android.content.*;
import android.graphics.*;
import android.net.Uri;
import android.widget.*;
import java.io.*;
import java.time.*;
public class ConfigActivity extends Activity {
 int id; SharedPreferences prefs; String chosenPhoto; TextView photoLabel;
 @Override public void onCreate(Bundle b){
  super.onCreate(b);
  id=getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
  if(id==AppWidgetManager.INVALID_APPWIDGET_ID){finish();return;}
  prefs=getSharedPreferences(CountdownWidget.PREF,MODE_PRIVATE);
  chosenPhoto=prefs.getString("photo_"+id,"");
  setResult(RESULT_CANCELED);
  LocalDate d;try{d=LocalDate.parse(prefs.getString("date_"+id,"2027-05-02"));}catch(Exception e){d=LocalDate.of(2027,5,2);}
  ScrollView scroll=new ScrollView(this);
  LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(24,28,24,28);
  scroll.addView(root);
  TextView heading=new TextView(this);heading.setText("Оформление события");heading.setTextSize(24);root.addView(heading);
  EditText title=new EditText(this);title.setSingleLine(true);title.setHint("Название");title.setText(prefs.getString("title_"+id,"До 2 мая 2027"));root.addView(title);
  DatePicker picker=new DatePicker(this);picker.init(d.getYear(),d.getMonthValue()-1,d.getDayOfMonth(),null);root.addView(picker);
  Spinner colors=new Spinner(this);
  String[] options={"Графит","Ночной синий","Изумруд","Фиолетовый","Бордовый","Медный","Стальной","Кофейный","Бирюзовый","Сливовый","Угольный","Песочный"};
  colors.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,options));colors.setSelection(prefs.getInt("theme_"+id,0));root.addView(colors);
  Spinner fonts=new Spinner(this);
  fonts.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,new String[]{"Обычный","Узкий современный","С засечками","Моноширинный"}));
  fonts.setSelection(prefs.getInt("font_"+id,0));root.addView(fonts);
  Spinner textColors=new Spinner(this);
  textColors.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,new String[]{"Белый текст","Чёрный текст","Золотой текст","Голубой текст"}));
  int old=prefs.getInt("textcolor_"+id,0xFFFFFFFF);
  int[] textValues={0xFFFFFFFF,0xFF101010,0xFFFFD580,0xFFBDE8FF};
  for(int i=0;i<textValues.length;i++)if(textValues[i]==old)textColors.setSelection(i);
  root.addView(textColors);
  Button photo=new Button(this);photo.setText("ВЫБРАТЬ ФОТО ИЗ ГАЛЕРЕИ");root.addView(photo);
  photoLabel=new TextView(this);photoLabel.setText(chosenPhoto.isEmpty()?"Фото не выбрано":"Фото выбрано");root.addView(photoLabel);
  photo.setOnClickListener(v->{Intent pick=new Intent(Intent.ACTION_OPEN_DOCUMENT);pick.setType("image/*");pick.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(pick,42);});
  Button clear=new Button(this);clear.setText("УБРАТЬ ФОТО");root.addView(clear);
  clear.setOnClickListener(v->{chosenPhoto="";photoLabel.setText("Фото не выбрано");});
  Button save=new Button(this);save.setText("СОХРАНИТЬ ОФОРМЛЕНИЕ");root.addView(save);
  setContentView(scroll);
  save.setOnClickListener(v->{
   String date=String.format(java.util.Locale.ROOT,"%04d-%02d-%02d",picker.getYear(),picker.getMonth()+1,picker.getDayOfMonth());
   prefs.edit().putString("date_"+id,date).putString("title_"+id,title.getText().toString().trim().isEmpty()?"Моё событие":title.getText().toString().trim()).putInt("theme_"+id,colors.getSelectedItemPosition()).putInt("font_"+id,fonts.getSelectedItemPosition()).putInt("textcolor_"+id,textValues[textColors.getSelectedItemPosition()]).putString("photo_"+id,chosenPhoto).apply();
   CountdownWidget.refresh(this,id);
   Intent result=new Intent();result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,id);setResult(RESULT_OK,result);finish();
  });
 }
 @Override protected void onActivityResult(int request,int result,Intent data){
  super.onActivityResult(request,result,data);
  if(request==42&&result==RESULT_OK&&data!=null){
   try{
    Uri uri=data.getData();
    BitmapFactory.Options opts=new BitmapFactory.Options();opts.inJustDecodeBounds=true;
    try(InputStream input=getContentResolver().openInputStream(uri)){BitmapFactory.decodeStream(input,null,opts);}
    int sample=1;while(opts.outWidth/sample>1200||opts.outHeight/sample>1200)sample*=2;
    opts.inJustDecodeBounds=false;opts.inSampleSize=sample;
    Bitmap bitmap;
    try(InputStream input=getContentResolver().openInputStream(uri)){bitmap=BitmapFactory.decodeStream(input,null,opts);}
    if(bitmap==null)throw new IOException("Не удалось прочитать изображение");
    File file=new File(getFilesDir(),"widget_photo_"+id+".jpg");
    try(FileOutputStream output=new FileOutputStream(file)){bitmap.compress(Bitmap.CompressFormat.JPEG,85,output);}
    bitmap.recycle();chosenPhoto=file.getAbsolutePath();photoLabel.setText("Фото выбрано");
   }catch(Exception e){Toast.makeText(this,"Ошибка загрузки фото",Toast.LENGTH_LONG).show();}
  }
 }
}