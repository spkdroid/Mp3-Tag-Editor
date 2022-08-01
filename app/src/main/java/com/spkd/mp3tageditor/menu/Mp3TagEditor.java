package com.spkd.mp3tageditor.menu;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.spkd.mp3tageditor.R;
import com.spkd.mp3tageditor.fileselection.RingCutterActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Mp3TagEditor extends Activity implements OnClickListener {
	
	Button launch;
	Button help;
	
	TextView tv;
	
	public void onCreate(Bundle savedBundle)
	{
		super.onCreate(savedBundle);
		setContentView(R.layout.menu);
	
	    launch=(Button) findViewById(R.id.launch);
	    launch.setOnClickListener(this);
	
	    help=(Button)findViewById(R.id.help);
	    help.setOnClickListener(this);
	
	    
	    tv=(TextView) findViewById(R.id.textView1);
	    Typeface tf = Typeface.createFromAsset(getAssets(), "font.ttf");
	    tv.setTypeface(tf);
	}

	@Override
	public void onClick(View a) {
		// TODO Auto-generated method stub
		if(a==launch)
		{
		finish();
		Intent i=new Intent(getApplicationContext(),RingCutterActivity.class);
		startActivity(i);
		}
		
		if(a==help)
		{
			new AlertDialog.Builder(this)
		    .setTitle("How to Use the Application")
		    .setMessage(R.string.FAQ)
		    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		        dialog.dismiss();
		        }
		     })
		    .setIcon(R.drawable.ic_launcher)
		    .show();
			
		}
		
		
	}
	
	public void onBackPressed()
	{
	 new AlertDialog.Builder(this)
     .setIcon(R.drawable.ic_launcher)
     .setTitle("Exit Application")
     .setMessage("Are you sure you want to exit this application?")
     .setPositiveButton("Yes", new DialogInterface.OnClickListener()
	    {
     public void onClick(DialogInterface dialog, int which) 
     {
     	System.exit(0);
     }

 })
 .setNeutralButton("Rate Us",new DialogInterface.OnClickListener() {
		
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			try {
			    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.spkd.mp3tageditor")));
			} catch (android.content.ActivityNotFoundException anfe) {
			    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.spkd.mp3tageditor")));
			}

			
		}
	})
 .setNegativeButton("No", null)
 .show();
	}
	
	
}
