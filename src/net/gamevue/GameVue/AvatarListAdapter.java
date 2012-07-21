package net.gamevue.GameVue;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class AvatarListAdapter extends BaseAdapter {
	private Context mContext;

	public AvatarListAdapter(Context c) {
		mContext = c;
	}

	public int getCount() {
		return images.size( );
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}
	
	public void loadAvatars( int id )
	{
		DownloadAvatarListTask dl = new DownloadAvatarListTask(  );
		dl.execute( new String[]{ mContext.getString( R.string.server ) + "ajax.php?page=user-%3eavatars&id=" + id } );
	}
	
	public void completeAvatarLoad( Bitmap avatar )
	{
		if( avatar != null )
		{
			images.add( avatar );
			this.notifyDataSetChanged( );
		}
	}
	
	private class DownloadAvatarListTask extends AsyncTask<String, Void, String> {
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
				JSONArray jArray = new JSONArray(result);
				String avatar;
				for( int i = 0; i < jArray.length(); i++ )
				{
					try {
						avatar = jArray.getString( i );
						
						DownloadAvatarTask dl = new DownloadAvatarTask();
						dl.execute( new String[] { avatar } );
					}
					catch( JSONException e )
					{
						Toast.makeText(mContext, "Error fetching avatar, please check your internet connection", Toast.LENGTH_SHORT).show();
						Log.i( "Exception", "Exception : " + e.getMessage() );
					}
				}
			}
			catch(JSONException e)
			{
				Log.i( "Exception", "Exception : " + e.getMessage());
			}
		}
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
					
					Log.i( "Exception", "Error with fetching avatar: " + e.getMessage() );
				}
			}
			return avatar;
		}
		
		protected void onPostExecute( Bitmap avatar )
		{
			completeAvatarLoad( avatar );
		}
		
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) {
			imageView = new ImageView(mContext);
			imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(8, 8, 8, 8);
		} else {
			imageView = (ImageView) convertView;
		}
		
		imageView.setImageBitmap( images.get( position ) );
		return imageView;
	}

	private ArrayList<Bitmap> images = new ArrayList<Bitmap>();
}