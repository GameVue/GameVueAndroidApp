package net.gamevue.GameVue;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class UserAvatarGallery extends Activity
{
	int id;
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.avatargallery );

		GridView gridview = (GridView) findViewById( R.id.userAvatarGrid );
		
		AvatarListAdapter adapter = new AvatarListAdapter( this );
		
		Bundle extras = getIntent().getExtras();

		id = extras.getInt( "id" );

		adapter.loadAvatars( id );
		
		gridview.setAdapter( adapter );
		gridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Toast.makeText(UserAvatarGallery.this, "" + position, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
