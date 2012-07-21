package net.gamevue.GameVue;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class UserProfile extends Activity
{
	TextView usernameTextView;
	int id;
	String username = null;
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.profile );
		ListView options = (ListView) findViewById( R.id.userProfileListView );
		
		usernameTextView = (TextView) findViewById( R.id.userProfileName );
		
		String[] newValues = new String[] {
				"Friends",
				"Avatars",
				"Videos"
		};
		
		ArrayAdapter<String> newAdapter = new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, android.R.id.text1, newValues );
		
		options.setAdapter( newAdapter );
		
		DownloadUserTask task = new DownloadUserTask();
		
		Bundle extras = getIntent().getExtras();

		id = extras.getInt( "id" );
		task.execute(new String[] { getString( R.string.server ) + "ajax.php?page=user-%3einfo&id=" + id });
		
		options.setOnItemClickListener( new AdapterView.OnItemClickListener(){
			public void onItemClick( AdapterView<?> adapterView, View view, int index, long arg3 )
			{
				switch( index )
				{
					case 0:
						//
						// Friends
						//
						
						Intent loadUserList = new Intent( adapterView.getContext( ), UserList.class );
						loadUserList.putExtra( "friends", id );
						loadUserList.putExtra( "username", username );
						startActivityForResult( loadUserList, 0 );
					break;
					
					case 1:
						//
						// Avatars
						//
						
						Intent loadUserAvatarGallery = new Intent( adapterView.getContext(  ), UserAvatarGallery.class );
						loadUserAvatarGallery.putExtra( "id", id );
						startActivityForResult( loadUserAvatarGallery, 0 );
					break;
					
					case 2:
						//
						// Videos
						//
						
						Intent loadUserVideoGallery = new Intent( adapterView.getContext(  ), UserVideoGallery.class );
						loadUserVideoGallery.putExtra( "id", id );
						startActivityForResult( loadUserVideoGallery, 0 );
					break;
				}
			}
		});
	}
	
	public void completeUserLoad(User user) {
		username = user.username;
		usernameTextView.setText( "  " + user.username );
		DownloadAvatarTask task = new DownloadAvatarTask();
		task.execute( new String[] { user.avatar } );
		
		setTitle( user.username );
	}
	
	public void completeAvatarLoad( Bitmap bitmap )
	{
		ImageView avatar = (ImageView) findViewById( R.id.userProfileAvatarView );
		avatar.setImageBitmap( bitmap );
	}
	
	private class DownloadAvatarTask extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground( String... urls )
		{
			Bitmap avatar = null;
			for( String url : urls )
			{
				try
				{
					avatar = BitmapFactory.decodeStream((InputStream)new URL(url).getContent());
				} catch( Exception e )
				{
					e.printStackTrace();
				}
			}
			return avatar;
		}
		
		protected void onPostExecute( Bitmap avatar )
		{
			completeAvatarLoad( avatar );
		}
		
	}
	
	private class DownloadUserTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			for (String url : urls) {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);
				try {
					HttpResponse execute = client.execute(httpGet);
					InputStream content = execute.getEntity().getContent();

					BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
					String s = "";
					while ((s = buffer.readLine()) != null) {
						response += s;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			User user = new User(  );
			try{
				JSONObject u = new JSONObject(result);
				try {
					user.id = Integer.valueOf( (String) u.get("id") );
					user.username = (String) u.get("username");
					user.avatar = (String) u.get("img");
				}
				catch( Exception e )
				{
					Log.i( "Exception1", "Exception : " + e.getMessage() );
				}
			}
			catch(JSONException e)
			{
				Log.i( "Exception2", "Exception : " + e.getMessage());
			}
			completeUserLoad(user);
		}
	}
}
