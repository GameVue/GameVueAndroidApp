package net.gamevue.GameVue;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class UserList extends Activity
{
	ListView listView;
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_hello_world );
		
		listView = (ListView) findViewById( R.id.mainlist );
		
		String[] values = new String[] {
				"Loading user list..."
		};
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, android.R.id.text1, values );
		listView.setAdapter( adapter );
		
		DownloadUsersTask task = new DownloadUsersTask();
		
		Bundle extras = getIntent().getExtras();
		
		String url = getString( R.string.server ) + "ajax.php?page=user-%3elist";
		
		if( extras != null )
		{
			int friends = extras.getInt( "friends" );
			if( friends > 0 )
			{
				String username = extras.getString( "username" );
				if( username != null )
				{
					setTitle( username + "'s friends" );
				}
				url += "&friends=" + friends;
			}
		}
		
		task.execute(new String[] { url });
	}
	
	public void completeUserLoad(final DownloadUsersTask task) {
		ArrayList<String> values = task.values;
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
		listView.setAdapter( adapter );
		listView.setOnItemClickListener( new AdapterView.OnItemClickListener(){
			public void onItemClick( AdapterView<?> adapterView, View view, int index, long arg3 )
			{
				Intent loadUserProfile = new Intent( adapterView.getContext( ), UserProfile.class );
				loadUserProfile.putExtra( "id", task.userid.get( listView.getItemAtPosition( index ).toString( ) ) );
				startActivityForResult( loadUserProfile, 0 );
			}
		} );
	}
	
	private class DownloadUsersTask extends AsyncTask<String, Void, String> {
		public ArrayList<String> values = new ArrayList<String>();
		public HashMap<String,Integer> userid = new HashMap<String,Integer>();
		
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
			try{
				JSONObject jObject = new JSONObject(result);
				Iterator<String> keys = jObject.keys(  );
				String username;
				while( keys.hasNext() )
				{
					try {
						String key = keys.next(  );
						username = jObject.getString( key );
						values.add( username );
						userid.put( username, Integer.valueOf( key ) );
					}
					catch( Exception e )
					{
						Log.i( "Exception", "Exception : " + e.getMessage() );
					}
				}
			}
			catch(JSONException e)
			{
				Log.i( "Exception", "Exception : " + e.getMessage());
			}
			Collections.sort( values );
			completeUserLoad(this);
		}
	}
}
