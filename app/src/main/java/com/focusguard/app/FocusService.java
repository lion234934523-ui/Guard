package com.focusguard.app;
import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.content.SharedPreferences;
import android.os.SystemClock;
import java.util.Calendar;
import java.util.Locale;
public class FocusService extends AccessibilityService {
 private long lastBlock=0;private SharedPreferences prefs;
 @Override public void onServiceConnected(){super.onServiceConnected();prefs=getSharedPreferences("focus",MODE_PRIVATE);}
 @Override public void onAccessibilityEvent(AccessibilityEvent event){
  if(event==null||event.getPackageName()==null)return;
  if(prefs==null)prefs=getSharedPreferences("focus",MODE_PRIVATE);
  if(!withinHours())return;
  String pkg=event.getPackageName().toString();
  if(pkg.equals(getPackageName())||pkg.startsWith("com.android.systemui"))return;
  boolean block=false;
  if(pkg.equals("com.zhiliaoapp.musically")||pkg.equals("com.ss.android.ugc.trill"))block=prefs.getBoolean("tiktok",true);
  if(pkg.equals("com.instagram.android")){block=prefs.getBoolean("instagram",false);if(!block&&prefs.getBoolean("reels",true))block=matchesScreen(new String[]{"reels","reel","рилс","рилсы"});}
  if(pkg.equals("com.google.android.youtube")){block=prefs.getBoolean("youtube",false);if(!block&&prefs.getBoolean("shorts",true))block=matchesScreen(new String[]{"shorts","short","шортс","короткие видео"});}
  if(prefs.getBoolean("web",true)&&isBrowser(pkg)){
   AccessibilityNodeInfo node=getRootInActiveWindow();String content=collect(node,0,140).toLowerCase(Locale.ROOT);if(node!=null)node.recycle();
   String[] entries=prefs.getString("sites","tiktok.com,instagram.com/reels,youtube.com/shorts").toLowerCase(Locale.ROOT).split(",");
   for(String entry:entries){String rule=entry.trim();if(rule.length()>2&&content.contains(rule)){block=true;break;}}
  }
  if(block&&SystemClock.elapsedRealtime()-lastBlock>900){lastBlock=SystemClock.elapsedRealtime();performGlobalAction(GLOBAL_ACTION_HOME);}
 }
 private boolean isBrowser(String pkg){return pkg.equals("com.android.chrome")||pkg.equals("org.mozilla.firefox")||pkg.equals("com.sec.android.app.sbrowser")||pkg.equals("com.microsoft.emmx")||pkg.equals("com.brave.browser");}
 private boolean matchesScreen(String[] words){AccessibilityNodeInfo root=getRootInActiveWindow();if(root==null)return false;String content=collect(root,0,160).toLowerCase(Locale.ROOT);root.recycle();for(String w:words)if(content.contains(w))return true;return false;}
 private String collect(AccessibilityNodeInfo n,int depth,int budget){if(n==null||depth>12||budget<=0)return "";StringBuilder b=new StringBuilder();if(n.getText()!=null)b.append(n.getText()).append(' ');if(n.getContentDescription()!=null)b.append(n.getContentDescription()).append(' ');if(n.getViewIdResourceName()!=null)b.append(n.getViewIdResourceName()).append(' ');int count=Math.min(n.getChildCount(),budget);for(int i=0;i<count;i++){AccessibilityNodeInfo child=n.getChild(i);if(child!=null){b.append(collect(child,depth+1,budget-i-1));child.recycle();}}return b.toString();}
 private boolean withinHours(){String s=prefs.getString("start","00:00"),e=prefs.getString("end","00:00");int start=minutes(s),end=minutes(e);if(start==end)return true;Calendar c=Calendar.getInstance();int now=c.get(Calendar.HOUR_OF_DAY)*60+c.get(Calendar.MINUTE);return start<end?now>=start&&now<end:now>=start||now<end;}
 private int minutes(String s){try{String[] p=s.split(":");return Integer.parseInt(p[0])*60+Integer.parseInt(p[1]);}catch(Exception ex){return 0;}}
 @Override public void onInterrupt(){}
}