package net.gamevue.GameVue;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class UserVideoGallery extends Activity
{
	int id;
	VideoListAdapter adapter;
	
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.videosgallery );

		GridView gridview = (GridView) findViewById( R.id.videosGrid );
		
		adapter = new VideoListAdapter( this, getLayoutInflater() );
		
		Bundle extras = getIntent().getExtras();

		id = extras.getInt( "id" );

		adapter.loadVideos( id );
		
		gridview.setAdapter( adapter );
		gridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Intent loadVideo = new Intent( parent.getContext(  ), VideoPlayer.class );
				loadVideo.putExtra( "url", "http://beta.gamevue.net/uploads/videos/" + adapter.videos.get( position ).md5 + ".mp4" );
				startActivityForResult( loadVideo, 0 );
			}
		});
	}
}
