package com.merkoffice.app;

import android.app.*;
import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.print.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {
    EditText title, body;
    TextView status;
    static final int OPEN_FILE=10, EXPORT_FILE=11;

    @Override public void onCreate(Bundle b) { super.onCreate(b); buildScreen(); newDocument(); }

    void buildScreen() {
        LinearLayout root=new LinearLayout(this); root.setOrientation(1); root.setPadding(14,14,14,8); root.setBackgroundColor(0xFFF3F5F9);
        TextView brand=new TextView(this); brand.setText("MerkOffice"); brand.setTextSize(26); brand.setTypeface(null,1); brand.setTextColor(0xFF17365D); root.addView(brand);
        TextView sub=new TextView(this); sub.setText("Offline document studio"); sub.setTextColor(0xFF667085); root.addView(sub);
        title=new EditText(this); title.setSingleLine(true); title.setTextSize(18); title.setHint("Document title"); root.addView(title);
        LinearLayout r1=new LinearLayout(this); r1.setOrientation(0);
        button(r1,"New",v->newDocument()); button(r1,"Save",v->saveDocument()); button(r1,"Open",v->openPicker()); root.addView(r1);
        LinearLayout r2=new LinearLayout(this); r2.setOrientation(0);
        button(r2,"Export",v->exportPicker()); button(r2,"Share",v->shareDocument()); button(r2,"Print",v->printDocument()); root.addView(r2);
        body=new EditText(this); body.setGravity(Gravity.TOP|Gravity.START); body.setTextSize(16); body.setHint("Start writing here…"); body.setPadding(14,14,14,14); body.setBackgroundColor(-1); body.setMinLines(12);
        body.setInputType( android.text.InputType.TYPE_CLASS_TEXT|android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE|android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,0,1); p.setMargins(0,10,0,8); root.addView(body,p);
        status=new TextView(this); status.setTextColor(0xFF667085); root.addView(status); setContentView(root);
    }
    void button(LinearLayout row,String text,View.OnClickListener l) {
        Button b=new Button(this); b.setText(text); b.setTextSize(12); b.setOnClickListener(l); row.addView(b,new LinearLayout.LayoutParams(0,-2,1));
    }
    void newDocument(){ title.setText("Untitled"); body.setText(""); status.setText("New document • Files remain on this device"); }
    String safeName(){
        String n=title.getText().toString().trim(); if(n.isEmpty()) n="Untitled";
        n=n.replaceAll("[\\\\/:*?\"<>|]","_"); return n.endsWith(".txt")?n:n+".txt";
    }
    void saveDocument(){
        try { File d=new File(getFilesDir(),"documents"); if(!d.exists()) d.mkdirs();
            try(FileOutputStream o=new FileOutputStream(new File(d,safeName()))){o.write(body.getText().toString().getBytes(StandardCharsets.UTF_8));}
            status.setText("Saved locally: "+safeName()); Toast.makeText(this,"Saved",0).show();
        } catch(Exception e){error(e.getMessage());}
    }
    void openPicker(){ Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT); i.addCategory(Intent.CATEGORY_OPENABLE); i.setType("text/*"); startActivityForResult(i,OPEN_FILE); }
    void exportPicker(){ Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT); i.addCategory(Intent.CATEGORY_OPENABLE); i.setType("text/plain"); i.putExtra(Intent.EXTRA_TITLE,safeName()); startActivityForResult(i,EXPORT_FILE); }
    @Override protected void onActivityResult(int req,int result,Intent data){
        super.onActivityResult(req,result,data); if(result!=RESULT_OK||data==null||data.getData()==null)return;
        Uri u=data.getData();
        try {
            if(req==OPEN_FILE){
                StringBuilder s=new StringBuilder();
                try(BufferedReader r=new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(u),StandardCharsets.UTF_8))){String line;while((line=r.readLine())!=null)s.append(line).append("\n");}
                body.setText(s.toString()); String n=u.getLastPathSegment(); title.setText(n==null?"Opened":n.replaceFirst("\\.[^.]+$","")); status.setText("Document opened");
            } else if(req==EXPORT_FILE){
                try(OutputStream o=getContentResolver().openOutputStream(u)){o.write(body.getText().toString().getBytes(StandardCharsets.UTF_8));}
                status.setText("Export completed"); Toast.makeText(this,"Exported",0).show();
            }
        }catch(Exception e){error(e.getMessage());}
    }
    void shareDocument(){Intent i=new Intent(Intent.ACTION_SEND);i.setType("text/plain");i.putExtra(Intent.EXTRA_SUBJECT,title.getText().toString());i.putExtra(Intent.EXTRA_TEXT,body.getText().toString());startActivity(Intent.createChooser(i,"Share document"));}
    void printDocument(){
        WebView w=new WebView(this); String html="<meta charset='utf-8'><h2>"+esc(title.getText().toString())+"</h2><pre style='white-space:pre-wrap;font-family:sans-serif'>"+esc(body.getText().toString())+"</pre>";
        w.loadDataWithBaseURL(null,html,"text/html","UTF-8",null);
        w.setWebViewClient(new WebViewClient(){@Override public void onPageFinished(WebView v,String url){
            PrintManager pm=(PrintManager)getSystemService(PRINT_SERVICE);
            pm.print("MerkOffice document",v.createPrintDocumentAdapter("MerkOffice"),new PrintAttributes.Builder().build());
        }});
        status.setText("Preparing print preview…");
    }
    String esc(String s){return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;");}
    void error(String m){new AlertDialog.Builder(this).setTitle("MerkOffice").setMessage("Operation failed: "+m).setPositiveButton("OK",null).show();}
}
