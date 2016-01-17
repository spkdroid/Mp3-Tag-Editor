package com.spkd.mp3tageditor.fileselection;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v1Tag;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.spkd.mediascanner.SingleMediaScanner;
import com.spkd.mp3tageditor.R;
import com.spkd.mp3tageditor.menu.Mp3TagEditor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FileEdit extends Activity implements OnClickListener
{
	private static final int SELECT_PHOTO = 0;
	String FileName;
	Button Save;
	EditText title,artist,album,track;
	Spinner genre;
	TextView file;
	Mp3File mp3file;
	int flag=0;
	ProgressDialog progress;
	ImageView ir;
	
	public void onCreate(Bundle savedinBundle)
	{
		super.onCreate(savedinBundle);
		setContentView(R.layout.tag_view);
		Intent i=getIntent();
		FileName=i.getStringExtra("filename");
		
		
		title=(EditText) findViewById(R.id.title_edit);
		artist=(EditText) findViewById(R.id.artist_edit);
		album=(EditText) findViewById(R.id.album_edit);
		track=(EditText) findViewById(R.id.track_edit);
		
		genre=(Spinner)findViewById(R.id.spinner1);
		
		file=(TextView)findViewById(R.id.file);
		file.setText(FileName);
		
		ir=(ImageView)findViewById(R.id.pictitle);
		//ir.setOnClickListener(this);
		
		Save=(Button) findViewById(R.id.save);
		Save.setOnClickListener(this);
		
		
		
		try {
			mp3file = new Mp3File(FileName);
		} catch (UnsupportedTagException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (mp3file.hasId3v1Tag()) {
			Toast.makeText(getApplicationContext(),"IDv1",Toast.LENGTH_LONG).show();
		
			ID3v1 id3v1Tag = mp3file.getId3v1Tag();
			title.setText(id3v1Tag.getTitle());
			album.setText(id3v1Tag.getAlbum());
			artist.setText(id3v1Tag.getArtist());
			track.setText(id3v1Tag.getTrack());
			genre.setSelection(id3v1Tag.getGenre());
			Toast.makeText(getApplicationContext(),id3v1Tag.getGenreDescription(),Toast.LENGTH_LONG).show();		
			flag=1;
		} else
		if (mp3file.hasId3v2Tag()) {
			
			Toast.makeText(getApplicationContext(),"IDv2",Toast.LENGTH_LONG).show();		
	        
			ID3v2 id3v2Tag = mp3file.getId3v2Tag();
			
	        title.setText(id3v2Tag.getTitle());
			
	        album.setText(id3v2Tag.getAlbum());
			
	        artist.setText(id3v2Tag.getArtist());
			
	        track.setText(id3v2Tag.getTrack());
			
	        genre.setSelection(id3v2Tag.getGenre());
			
			byte[] imageData = id3v2Tag.getAlbumImage();
			
			Bitmap bmp = BitmapFactory.decodeByteArray(imageData, 0,imageData.length);
			ir.setImageBitmap(bmp);
			
			flag=2;
		} else if (mp3file.hasCustomTag()) {
			Toast.makeText(getApplicationContext(),"Custom",Toast.LENGTH_LONG).show();		
			flag=3;
		}
		
		
		
		Toast.makeText(getApplicationContext(),FileName,Toast.LENGTH_LONG).show();		
		
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
	    super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 

	    switch(requestCode) { 
	    case SELECT_PHOTO:
	        if(resultCode == RESULT_OK){  
	            Uri selectedImage = imageReturnedIntent.getData();
	            InputStream imageStream;
				try {
					imageStream = getContentResolver().openInputStream(selectedImage);
		            Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
		            ir.setImageBitmap(yourSelectedImage);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	    }
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		
		if(v==ir)
		{
			Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
			photoPickerIntent.setType("image/*");
			startActivityForResult(photoPickerIntent, SELECT_PHOTO);    
		}
		
		if(v==Save)
		{
			if (mp3file.hasId3v1Tag() || mp3file.hasCustomTag()) {
			
			new AsyncTask<Void,Void,Void>() {

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
			progress=new ProgressDialog(FileEdit.this);
			progress.setMessage("Please wait the application is editing the id3 tag");
			progress.show();
			}
			

				@Override
				protected Void doInBackground(Void... params) {
					// TODO Auto-generated method stub
			
					try {
						EditId3V1();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
				}
				
				protected void onPostExecute(Void result) {
					
					progress.dismiss();
					new AlertDialog.Builder(FileEdit.this)
					.setTitle("ID3TAG Edited")
					.setMessage("The ID3 Tag of your music file has been edited.The Song can be found in the same location in the sdcard.Please press the yes button for going back to menu screen")
					.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog, int which) { 
					    	onBackPressed();
					    }
					 })
					.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog, int which) { 
					        // do nothing
					dialog.dismiss();
					    }
					 })
					 .setIcon(R.drawable.ic_launcher)
					 .show();
					
				};
			}.execute();
			}
			else if (mp3file.hasId3v2Tag()) {
		
				new AsyncTask<Void,Void,Void>()
				{

					@Override
					protected void onPreExecute() {
						// TODO Auto-generated method stub
						progress=new ProgressDialog(FileEdit.this);
						progress.setMessage("Please wait the application is editing the id3 tag");
						progress.show();
						
					}
			
					@Override
					protected Void doInBackground(Void... params) {
						// TODO Auto-generated method stub
						try {
							EditId3V2();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return null;
					}
					
					@Override
					protected void onPostExecute(Void result) {
						// TODO Auto-generated method stub
					
						new AlertDialog.Builder(FileEdit.this)
					    .setTitle("ID3TAG Edited")
					    .setMessage("The ID3 Tag of your music file has been edited.The Song can be found in the same location in the sdcard.Please press the yes button for going back to menu screen")
					    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					        public void onClick(DialogInterface dialog, int which) { 
					        	onBackPressed();
					        }
					     })
					    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					        public void onClick(DialogInterface dialog, int which) { 
					            // do nothing
					    dialog.dismiss();
					        }
					     })
					     .setIcon(R.drawable.ic_launcher)
					     .show();
					
					}
					
				}.execute();
					
				}
		}		
	}

	private void EditId3V2() throws IOException {
		// TODO Auto-generated method stub
		
		
	    ID3v2 id3v2Tag;
	    if (mp3file.hasId3v2Tag()) {
		  id3v2Tag =  mp3file.getId3v2Tag();
			id3v2Tag.setTrack(track.getText()+"");
			id3v2Tag.setArtist(artist.getText()+"");
			id3v2Tag.setTitle(title.getText()+"");
			id3v2Tag.setAlbum(album.getText()+"");
			id3v2Tag.setGenre(genre.getSelectedItemPosition());
			
	//		Bitmap bm=ir.getDrawingCache();
	//		ByteArrayOutputStream st=new ByteArrayOutputStream();
	//		bm.compress(Bitmap.CompressFormat.PNG,100,st);
	//		byte[] bA=st.toByteArray();
			
	//		id3v2Tag.setAlbumImage(bA,null);
	    }
		    
		
		try {
			String arg=Environment.getExternalStorageState()+"/sample.mp3";
			mp3file.save("/sdcard/sample.mp3");
		} catch (NotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//return null;	
		File fp=new File(FileName);
		fp.delete();
		
		FileInputStream inStream = new FileInputStream("/sdcard/sample.mp3");
	    FileOutputStream outStream = new FileOutputStream(FileName);
	    FileChannel inChannel = inStream.getChannel();
	    FileChannel outChannel = outStream.getChannel();
	    inChannel.transferTo(0, inChannel.size(), outChannel);
	    inStream.close();
	    outStream.close();
	    
	    new SingleMediaScanner(this,new File(FileName));
	    
	    File ff=new File("/sdcard/sample.mp3");
	    ff.delete();
	}

	private void EditId3V1() throws IOException {
		// TODO Auto-generated method stub
				ID3v1 id3v1Tag;
			    if (mp3file.hasId3v1Tag()) {
				  id3v1Tag =  mp3file.getId3v1Tag();
				} else {
				  // mp3 does not have an ID3v1 tag, let's create one..
				  id3v1Tag = new ID3v1Tag();
				  mp3file.setId3v1Tag(id3v1Tag);
				}
				id3v1Tag.setTrack(track.getText()+"");
				id3v1Tag.setArtist(artist.getText()+"");
				id3v1Tag.setTitle(title.getText()+"");
				id3v1Tag.setAlbum(album.getText()+"");
				//id3v1Tag.setYear("2001");
				id3v1Tag.setGenre(genre.getSelectedItemPosition());
			//	id3v1Tag.setComment("Some comment");
				    
				
				try {
					String arg=Environment.getExternalStorageState()+"/sample.mp3";
					mp3file.save("/sdcard/sample.mp3");
				} catch (NotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//return null;	
				File fp=new File(FileName);
				fp.delete();
				
				FileInputStream inStream = new FileInputStream("/sdcard/sample.mp3");
			    FileOutputStream outStream = new FileOutputStream(FileName);
			    FileChannel inChannel = inStream.getChannel();
			    FileChannel outChannel = outStream.getChannel();
			    inChannel.transferTo(0, inChannel.size(), outChannel);
			    inStream.close();
			    outStream.close();
			    
			    new SingleMediaScanner(this,new File(FileName));
			    
			    File ff=new File("/sdcard/sample.mp3");
			    ff.delete();		
	}
	
	public void onBackPressed()
	{
		finish();
		Intent i=new Intent(getApplicationContext(),Mp3TagEditor.class);
		startActivity(i);
	}
	
}
