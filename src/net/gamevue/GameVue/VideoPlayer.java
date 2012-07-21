package net.gamevue.GameVue;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayer extends Activity
{
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		this.requestWindowFeature( Window.FEATURE_NO_TITLE );
		
		Bundle extras = getIntent().getExtras();
		
		String url = extras.getString( "url" );
		Uri uri = Uri.parse( url );
		
		setContentView( R.layout.video_player );
		
		VideoView video = (VideoView) findViewById( R.id.video_view );
		
		MediaController mediaController = new MediaController( this );
		mediaController.setAnchorView( video );
		video.setMediaController( mediaController );
		video.setVideoURI( uri );
		video.start();
	}
}
