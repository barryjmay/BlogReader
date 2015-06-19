package com.barrymay.blogreader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

//MainListActivity is created as a subclass of the ListActivity class
public class MainListActivity extends ListActivity {
	
	
	//The following variables are typed in ALL CAPS to indicate these variables hold constant values
	//Set a new variable to hold the number of requests we want to hold from the blog
	public static final int NUMBER_OF_POSTS = 20;
	//Set a TAG variable to use for logging. This will print the name of the class (without its package name)
	public static final String TAG = MainListActivity.class.getSimpleName();
	//This MVariable has been added to return the JSON object obtained in doInBackground to the Main Thread.
	protected JSONObject mBlogData;
	//The Progressbar has to be added manually in the layout file
	protected ProgressBar mProgressBar;
	//These variables represent the key and author vales in the posts array or the treehouseblog URL
	private final String KEY_TITLE = "title";
	private final String KEY_AUTHOR = "author";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_list);
		
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		
		if (isNetworkAvailable()){		
			mProgressBar.setVisibility(View.VISIBLE);
			//Create & execute the Async Task that is outlined below
			GetBlogPostsTask getBlogPostsTask = new GetBlogPostsTask();
			getBlogPostsTask.execute();
		}
		else {
			Toast.makeText(this, "Network is unavailable!", Toast.LENGTH_LONG).show();
		};
		
		//Toast.makeText(this, getString(R.string.no_items), Toast.LENGTH_LONG).show();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		//Automatically generated. This line overrides the methods from the superclass
		super.onListItemClick(l, v, position, id);
		//Get the URL from the item that was tapped. This is already in the JSON Array named JSON Posts.
		
		try {
			JSONArray jsonPosts = mBlogData.getJSONArray("posts");
			JSONObject jsonPost = jsonPosts.getJSONObject(position);
			String blogURL = jsonPost.getString("url");
			//Declare Intent. This is an explicit intent calling the BlogWebViewActivity Class.
			Intent intent = new Intent(this, BlogWebViewActivity.class);
			//Attach our data to the Intent. The URL needs to be set as the data for the intent as a Uri object
			intent.setData(Uri.parse(blogURL));
			//Express this intention
			startActivity(intent);
			
		} catch (JSONException e) {
			logException(e);
		}
		
		
	}

	private void logException(Exception e) {
		Log.e(TAG, "Exception caught: ", e);
	}
	
	
	
	//Here is a method to check if the network exists & if its connected	
	private boolean isNetworkAvailable() {
		//Use the android ConnectivityManager class & cast getSystemService
		ConnectivityManager manager = (ConnectivityManager) 
				getSystemService(Context.CONNECTIVITY_SERVICE);
		//Instantiate a NetworkInfoObject so we can analyse the active networkInfo to see if its present and active
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		//Initialise isAvailable to false & then check if isAvailable matches the true condition
		boolean isAvailable = false;
		//if we are not connected to the internet, networkInfo will be null & isConnected is false
		if (networkInfo != null && networkInfo.isConnected()) {
			isAvailable = true;
		}
		return isAvailable;
	}
		
	//This method was generated automatically from onPostExecute and then cut & pasted up here
	public void handleBlogResponse() {
		mProgressBar.setVisibility(View.INVISIBLE);
		
		// MBlogData was originally set to null. It will stay null if something goes wrong. Here we check if something went wrong
		if (mBlogData == null) {
			updateDisplayForError();
			
		}
		else {
			try {
				//Here we initialise MBlogPostTitles to be same length as the list of items in or JSONArray of blog posts
				JSONArray jsonPosts = mBlogData.getJSONArray("posts");
				//Here we create an ArrayList object to hold the data & specify its structure i.e. a Hashmap containing strings
				ArrayList<HashMap<String, String>> blogPosts = 
						new ArrayList<HashMap<String, String>>();
				//Loop through jsonPosts, get the title for each post and add it to MBlogPostTitles
				for (int i=0; i<jsonPosts.length(); i++) {
					//JSONObject refers to each object in the array
					JSONObject post = jsonPosts.getJSONObject(i);
					//Title is the property of the object called title
					String title = post.getString(KEY_TITLE);
					//This line converts HTML containing special characters to normal text - Important when getting data from the web
					title = Html.fromHtml(title).toString();
					//Grab the author from the posts
					String author = post.getString(KEY_AUTHOR);
					author = Html.fromHtml(author).toString();
					
					//Create a HashMap object, add title & author to it and add it to our ArrayList
					HashMap<String, String> blogPost = new HashMap<String, String>();
					//Set HashMap variable
					blogPost.put(KEY_TITLE, title);
					blogPost.put(KEY_AUTHOR, author);
					blogPosts.add(blogPost);
				}
				//Add the two array variables and use them in a new Adapter called SimpleAdapter for our ListView
				//This array holds the keys of the info in the HashMap
				String[] keys = {KEY_TITLE, KEY_AUTHOR};
				//This array holds the ids of the TextViews that holds the values for these keys in the layout
				//The ids are ints
				int[] ids = {android.R.id.text1, android.R.id.text2};
				SimpleAdapter adapter = new SimpleAdapter(this, blogPosts, 
						android.R.layout.simple_list_item_2, keys, ids);
				
				setListAdapter(adapter);
				
			} catch (JSONException e) {
				logException(e);
			}
		}
	}
	private void updateDisplayForError() {
		//Create an AlterDialog to tell the user some action is needed. AlertDialogs are constructed differently from other code 
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		//Now configure the AlertDialog to show an error message title and message - set in strings.xml
		builder.setTitle(getString(R.string.title));
		builder.setMessage(getString(R.string.error_message));
		builder.setPositiveButton(android.R.string.ok, null);
		//Now create a Dialog & present it to the user
		AlertDialog dialog = builder.create();
		dialog.show();
		
		//Here we set the text for the Empty text view i.e. if something goes wrong
		//Empty text view is  a special case so we have to use a special method instead of findViewByID
		TextView emptyTextView = (TextView) getListView().getEmptyView();
		emptyTextView.setText(getString(R.string.no_items));
	}
	
	//Create a subclass of AsyncTask class to create a 2nd thread to carry out web requests asynchronously
	private class GetBlogPostsTask extends AsyncTask<Object, Void, JSONObject> {		
		//doInBackground gets added automatically when we click Intellisense for GetBlogPostsTask Error
		//... mean that we can pass 0 or more arguments of type objects to the method
		@Override
		protected JSONObject doInBackground(Object... arg0) {
			//Here we throw an exception. Try to create a URL object. If it fails with a malformed url exception then run this extra code to throw the exception
			//Initialise the response code here so if something goes wrong -1 will appear as the responseCode
			int responseCode = -1;
			//Set jsonObject variable & initialise
			JSONObject jsonResponse = null;			
			//the entire try/catch method to connect to the blog has been pasted in here
			try {
				URL blogFeedUrl = new URL("http://blog.teamtreehouse.com/api/get_recent_summary/?count=" + NUMBER_OF_POSTS);
				HttpURLConnection connection = (HttpURLConnection) blogFeedUrl.openConnection();
				connection.connect();
				
				responseCode = connection.getResponseCode();
				
				//Make sure request was successful
				if(responseCode ==HttpURLConnection.HTTP_OK){
					//When a successful request has been made the data is stored in an input stream object inside the connection object
					//Get the input stream object from the connection we just made
					InputStream inputStream = connection.getInputStream();
					//Use a reader object to read the data. Read from input stream character by character
					Reader reader = new InputStreamReader(inputStream);					
					//Determine how many characters to read in
					int nextCharacter; // read() returns an int, we cast it to char later
				    String responseData = "";
				    while(true){ // Infinite loop, can only be stopped by a "break" statement
				        nextCharacter = reader.read(); // read() without parameters returns one character
				        if(nextCharacter == -1) // A return value of -1 means that we reached the end
				            break;
				        responseData += (char) nextCharacter; // The += operator appends the character to the end of the string
				    }					
					jsonResponse = new JSONObject(responseData);					
					}
				else {
					Log.i(TAG, "Unsuccessful HTTP Response Code: "+ responseCode);
					}	
					}
							 
			catch (MalformedURLException e) {
				logException(e);
			}
			catch (IOException e) {
				logException(e);
			}
			catch (Exception e){
				logException(e);
			}
			return jsonResponse;
		}
		
		@Override
		protected void onPostExecute(JSONObject result) {
			//This puts the data obtained from doInBackground into the Activity on the main thread
			mBlogData = result;
			//This new method lets our activity know that the list has been updated
			handleBlogResponse();
		}		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
