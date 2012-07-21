package net.gamevue.GameVue;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class VideoListAdapter extends BaseAdapter {
	private Context mContext;
	public LayoutInflater li;

	public VideoListAdapter(Context c, LayoutInflater li) {
		this.li = li;
		mContext = c;
	}

	public int getCount() {
		return videos.size( );
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}
	
	public void loadVideos( int id )
	{
		DownloadVideoListTask dl = new DownloadVideoListTask(  );
		dl.execute( new String[]{ mContext.getString( R.string.server ) + "ajax.php?page=user-%3evideos&id=" + id } );
	}
	
	private class DownloadVideoListTask extends AsyncTask<String, Void, String> {
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
				for( int i = 0; i < jArray.length(); i++ )
				{
					try {
						JSONObject jObject = jArray.getJSONObject( i );
						
						Video video = new Video();

						video.md5	=	jObject.getString( "md5" );
						video.name	=	jObject.getString( "name" );
						video.thumb	=	jObject.getInt( "thumb" );
						video.id	=	jObject.getInt( "id" );
						
						videos.add( video );
						
						DownloadVideoTask dl = new DownloadVideoTask();
						dl.execute( new Video[] { video } );
					}
					catch( JSONException e )
					{
						Toast.makeText(mContext, "Error fetching Video Thumbnail, please check your internet connection", Toast.LENGTH_SHORT).show();
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

	private class DownloadVideoTask extends AsyncTask<Video, Void, Boolean>
	{
		@Override
		protected Boolean doInBackground( Video... videos )
		{
			for( Video video : videos )
			{
				try
				{
					video.image = BitmapFactory.decodeStream((InputStream)new URL(mContext.getString( R.string.server ) + video.getImageLocation()).getContent());
				} catch( Exception e )
				{
					
					Log.i( "Exception", "Error with fetching Video Thumbnail: " + e.getMessage() );
					return false;
				}
			}
			return true;
		}
		
		protected void onPostExecute( Boolean worked )
		{
			if( worked )
				notifyDataSetChanged(  );
		}
		
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		View v;
		if (convertView == null) {
			LayoutInflater li = this.li;
			v = li.inflate( R.layout.icon_with_text, null );
			TextView tv = (TextView)v.findViewById(R.id.icon_text);
			tv.setText( videos.get( position ).name );
			ImageView iv = (ImageView)v.findViewById(R.id.icon_image);
			iv.setImageBitmap( videos.get( position ).image );
		} else {
			v = convertView;
		}
		
		
		return v;
	}

	public ArrayList<Video> videos = new ArrayList<Video>();
}