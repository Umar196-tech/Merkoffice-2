package com.merkoffice.app;

import android.app.*;
import android.os.*;
import android.content.*;
import android.net.Uri;
import android.print.*;
import android.webkit.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {
  WebView web;
  static final int EXPORT_FILE=41;
  String pendingExport="";
  @Override public void onCreate(Bundle b) {
    super.onCreate(b);
    web=new WebView(this);
    web.getSettings().setJavaScriptEnabled(true);
    web.getSettings().setDomStorageEnabled(true);
    web.addJavascriptInterface(new OfficeBridge(),"Office");
    web.setWebChromeClient(new WebChromeClient());
    setContentView(web);
    web.loadUrl("file:///android_asset/index.html");
  }
  class OfficeBridge {
    @JavascriptInterface public void save(String name,String html) {
      runOnUiThread(()->{
        try {
          File dir=new File(getFilesDir(),"documents"); if(!dir.exists())dir.mkdirs();
          File file=new File(dir,clean(name)+".html");
          try(FileOutputStream out=new FileOutputStream(file)){out.write(html.getBytes(StandardCharsets.UTF_8));}
          web.evaluateJavascript("saved("+JSONObjectQuote(file.getName())+")",null);
        } catch(Exception e){ fail(e.getMessage()); }
      });
    }
    @JavascriptInterface public void list() {
      runOnUiThread(()->{
        File dir=new File(getFilesDir(),"documents"); File[] files=dir.listFiles((d,n)->n.endsWith(".html"));
        if(files==null||files.length==0){new AlertDialog.Builder(MainActivity.this).setMessage("No saved documents yet.").setPositiveButton("OK",null).show();return;}
        String[] names=new String[files.length]; for(int i=0;i<files.length;i++)names[i]=files[i].getName();
        new AlertDialog.Builder(MainActivity.this).setTitle("Open saved document").setItems(names,(d,which)->{
          try {String html=new String(java.nio.file.Files.readAllBytes(files[which].toPath()),StandardCharsets.UTF_8);
            web.evaluateJavascript("loadSaved("+JSONObjectQuote(html)+","+JSONObjectQuote(names[which].replaceFirst("\\.html$",""))+")",null);
          }catch(Exception e){fail(e.getMessage());}
        }).setNegativeButton("Cancel",null).show();
      });
    }
    @JavascriptInterface public void export(String name,String html) {
      runOnUiThread(()->{pendingExport=html; Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("text/html");i.putExtra(Intent.EXTRA_TITLE,clean(name)+".html");
        startActivityForResult(i,EXPORT_FILE);});
    }
    @JavascriptInterface public void share(String name,String html) {
      runOnUiThread(()->{Intent i=new Intent(Intent.ACTION_SEND);i.setType("text/html");
        i.putExtra(Intent.EXTRA_SUBJECT,name);i.putExtra(Intent.EXTRA_TEXT,android.text.Html.fromHtml(html,android.text.Html.FROM_HTML_MODE_LEGACY).toString());
        startActivity(Intent.createChooser(i,"Share document"));});
    }
    @JavascriptInterface public void print(String name,String html) {
      runOnUiThread(()->{
        WebView printer=new WebView(MainActivity.this);
        printer.setWebViewClient(new WebViewClient(){@Override public void onPageFinished(WebView v,String url){
          PrintManager pm=(PrintManager)getSystemService(PRINT_SERVICE);
          pm.print(clean(name),v.createPrintDocumentAdapter(clean(name)),new PrintAttributes.Builder().build());
        }});
        printer.loadDataWithBaseURL(null,"<html><meta charset='utf-8'><style>body{font-family:Arial;padding:24px}img{max-width:100%}</style>"+html+"</html>","text/html","UTF-8",null);
      });
    }
    @JavascriptInterface public void toast(String msg){runOnUiThread(()->android.widget.Toast.makeText(MainActivity.this,msg,android.widget.Toast.LENGTH_SHORT).show());}
  }
  String clean(String s){if(s==null||s.trim().isEmpty())s="Untitled";return s.trim().replaceAll("[\\\\/:*?\"<>|]","_");}
  String JSONObjectQuote(String s){return org.json.JSONObject.quote(s==null?"":s);}
  void fail(String message){new AlertDialog.Builder(this).setTitle("MerkOffice").setMessage("Could not complete operation: "+message).setPositiveButton("OK",null).show();}
  @Override protected void onActivityResult(int request,int result,Intent data){
    super.onActivityResult(request,result,data);
    if(request==EXPORT_FILE&&result==RESULT_OK&&data!=null&&data.getData()!=null){
      try(OutputStream out=getContentResolver().openOutputStream(data.getData())){
        out.write(pendingExport.getBytes(StandardCharsets.UTF_8));
        web.evaluateJavascript("exported()",null);
      }catch(Exception e){fail(e.getMessage());}
    }
  }
  @Override public void onBackPressed(){new AlertDialog.Builder(this).setMessage("Exit MerkOffice? Save your document first.").setNegativeButton("Stay",null).setPositiveButton("Exit",(d,w)->finish()).show();}
}