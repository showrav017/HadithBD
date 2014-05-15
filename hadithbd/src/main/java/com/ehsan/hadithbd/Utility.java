package com.ehsan.hadithbd;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.Html.ImageGetter;

public class Utility {
	
	public Activity activity;
	
	
	public Utility(Activity _activity) {
		this.activity = _activity;
    }

	public String readTxtFromFile(String filename) throws IOException{
    	
	   	 InputStream inputStream = this.activity.getAssets().open(filename);
	   	 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	   	 
	   	  int i;
	   	  try {
	   		 i = inputStream.read();
		    	  while (i != -1)
		    	  {
		    	   byteArrayOutputStream.write(i);
		    	   i = inputStream.read();
		    	  }
		    	  inputStream.close();
	   	  } catch (IOException e) {
	   		  // TODO Auto-generated catch block
	   		  e.printStackTrace();
	   	  }
	   	  return byteArrayOutputStream.toString();
	 }
	
	public ImageGetter imgGetter = new ImageGetter() {

        public Drawable getDrawable(String source) {
                HttpGet get = new HttpGet(source);
                DefaultHttpClient client = new DefaultHttpClient();
                Drawable drawable = null;
                
                try {
					drawable = Drawable.createFromStream(activity.getAssets().open(source), null);
				} catch (IOException e){
					e.printStackTrace();
				}

                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
                
                return drawable;
        }
    };
}
