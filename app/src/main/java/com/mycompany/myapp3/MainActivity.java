package com.mycompany.myapp3;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

	ImageView img;
	final private int REQUEST_INTERNET =123;
	
	private InputStream OpenHttpConnection(String urlString) throws IOException{
		InputStream in = null;
		int response = -1;
		
		URL url = new URL(urlString);
		
		URLConnection conn = url.openConnection();
		
		if(!(conn instanceof HttpURLConnection))
			throw new IOException("Not an HTTP connection");
		try{
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setAllowUserInteraction(false);
			httpConn.setInstanceFollowRedirects(true);
			httpConn.setRequestMethod("GET");
			httpConn.connect();
			response = httpConn.getResponseCode();
			if(response == HttpURLConnection.HTTP_OK)
				in = httpConn.getInputStream();
		}catch(Exception ex){
			Log.d("Networking", ex.getLocalizedMessage());
			throw new IOException("Error connecting");
		}
		return in;
	}
	
	private Bitmap DownloadImage(String URL){
		Bitmap bitmap = null;
		InputStream in = null;
		try{
			in = OpenHttpConnection(URL);
			bitmap = BitmapFactory.decodeStream(in);
			in.close();
		}catch(Exception el){
			Log.d("NetworkingActivity", el.getLocalizedMessage());
		}
		return bitmap;
	}
	
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap>{
		protected Bitmap doInBackground(String... urls){
			return DownloadImage(urls[0]);
		}
		
		protected void onPostExecute(Bitmap result){
			ImageView img = findViewById(R.id.imageView);
			img.setImageBitmap(result);
		}
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		
		Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		if(ContextCompat.checkSelfPermission(this,
		   Manifest.permission.INTERNET)
		   != PackageManager.PERMISSION_GRANTED){
		    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.INTERNET},
			REQUEST_INTERNET);
		}else{
			new DownloadImageTask().execute("https://avatars.githubusercontent.com/u/69393826?s=400&u=19b4b510181d262782d56ea5ee7c45a4d2da7d40&v=4");
		}
		
    }

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		//super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	
		switch(requestCode){
			case REQUEST_INTERNET:
				if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
					new DownloadImageTask().execute("https://avatars.githubusercontent.com/u/69393826?s=400&u=19b4b510181d262782d56ea5ee7c45a4d2da7d40&v=4");
				}else{
					Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
			    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

}
