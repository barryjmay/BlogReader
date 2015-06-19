package com.barrymay.blogreader;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class BlogWebViewActivity extends Activity {
	
	protected String mUrl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blog_web_view);
		//Here we get the Intent that was passed into this activity
		Intent intent = getIntent();
		//Get Uri from intent
		Uri blogUri = intent.getData();
		mUrl = blogUri.toString();
		//Get webview from layout
		WebView webView = (WebView) findViewById(R.id.webView1);
		webView.loadUrl(mUrl);
	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.blog_web_view, menu);
		return true;
	}
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_share) {
			sharePost();
		}
		return super.onOptionsItemSelected(item);
	}

	private void sharePost() {
		//Declare an intent to say we have something to share
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		//Define type of data to share. Send url as plaintext - to keep it simple
		shareIntent.setType("text/plain");
		//Now we have to add string data as an extra to the intent. Intents can have multiple extras
		//And they have to be added as key/value pairs
		shareIntent.putExtra(Intent.EXTRA_TEXT, mUrl);
		//Use createChooser method to offer sharing options
		startActivity(Intent.createChooser(shareIntent, getString(R.string.share_chooser_title)));		
	}
}
