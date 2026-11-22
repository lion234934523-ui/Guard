package com.focusguard.app;
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.provider.Settings;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.*;
import android.content.SharedPreferences;
public class MainActivity extends Activity {
 SharedPreferences prefs; LinearLayout root;
 int white=Color.rgb(239,242,249),muted=Color.rgb(154,164,185),bg=Color.rgb(12,17,29);
 @Override public void onCreate(Bundle b){super.onCreate(b);prefs=getSharedPreferences("focus",MODE_PRIVATE);show();}
 TextView text(String s,int size,int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);t.setPadding(0,12,0,12);return t;}
 void show(){
  ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.setBackgroundColor(bg);
  root=new LinearLayout(this);root.setOrientation(1);root.setPadding(44,48,44,40);scroll.addView(root);setContentView(scroll);
  TextView title=text("FOCUSGUARD",29,white);title.setTypeface(null,Typeface.BOLD);root.addView(title);
  root.addView(text("Less scrolling. More living.",16,muted));
  root.addView(text("1. Enable Accessibility permission.\n2. Choose what to block.\n3. Keep FocusGuard enabled.",15,white));
  Button permission=new Button(this);permission.setText("ENABLE BLOCKING ACCESS");permission.setOnClickListener(v->startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));root.addView(permission);
  addSwitch("Block Instagram Reels (best effort)","reels",true);
  addSwitch("Block YouTube Shorts (best effort)","shorts",true);
  addSwitch("Block TikTok entirely","tiktok",true);
  addSwitch("Block Instagram entirely","instagram",false);
  addSwitch("Block YouTube entirely","youtube",false);
  addSwitch("Block distracting browser pages (best effort)","web",true);
  root.addView(text("Blocked website keywords (comma-separated)",16,white));
  EditText sites=new EditText(this);sites.setSingleLine(false);sites.setMinLines(2);sites.setTextColor(white);sites.setText(prefs.getString("sites","tiktok.com,instagram.com/reels,youtube.com/shorts"));root.addView(sites);
  Button save=new Button(this);save.setText("SAVE WEBSITE RULES");save.setOnClickListener(v->{prefs.edit().putString("sites",sites.getText().toString()).apply();Toast.makeText(this,"Saved",Toast.LENGTH_SHORT).show();});root.addView(save);
  root.addView(text("Focus hours (24-hour clock). 00:00 to 00:00 means all day.",14,muted));
  LinearLayout times=new LinearLayout(this);times.setOrientation(LinearLayout.HORIZONTAL);root.addView(times);
  EditText from=timeField("start","00:00"),to=timeField("end","00:00");times.addView(from,new LinearLayout.LayoutParams(0,-2,1));times.addView(to,new LinearLayout.LayoutParams(0,-2,1));
  Button saveTime=new Button(this);saveTime.setText("SAVE FOCUS HOURS");saveTime.setOnClickListener(v->{if(valid(from.getText().toString())&&valid(to.getText().toString())){prefs.edit().putString("start",from.getText().toString()).putString("end",to.getText().toString()).apply();Toast.makeText(this,"Saved",Toast.LENGTH_SHORT).show();}else Toast.makeText(this,"Use HH:mm",Toast.LENGTH_LONG).show();});root.addView(saveTime);
  root.addView(text("Privacy: checks run locally. Accessibility reads on-screen text. Android allows disabling this service.",13,muted));
 }
 EditText timeField(String key,String fallback){EditText e=new EditText(this);e.setSingleLine(true);e.setTextColor(white);e.setText(prefs.getString(key,fallback));e.setHint("HH:mm");return e;}
 boolean valid(String s){return s.matches("([01][0-9]|2[0-3]):[0-5][0-9]");}
 void addSwitch(String label,String key,boolean def){Switch sw=new Switch(this);sw.setText(label);sw.setTextColor(white);sw.setTextSize(15);sw.setPadding(0,18,0,18);sw.setChecked(prefs.getBoolean(key,def));sw.setOnCheckedChangeListener((b,checked)->prefs.edit().putBoolean(key,checked).apply());root.addView(sw);}
}