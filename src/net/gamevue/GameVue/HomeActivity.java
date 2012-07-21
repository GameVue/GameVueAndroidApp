package net.gamevue.GameVue;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class HomeActivity extends Activity
{
	Activity MainActivity;
	private ListView listView;
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		MainActivity = this;
		super.onCreate( savedInstanceState );

		setContentView( R.layout.activity_hello_world );

		listView = (ListView) findViewById( R.id.mainlist );
		
		String[] values = new String[] {
				"List Users"
		};
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, android.R.id.text1, values );
		
		listView.setAdapter( adapter );
		listView.setOnItemClickListener( new AdapterView.OnItemClickListener(){

			public void onItemClick( AdapterView<?> adapterView, View arg1, int arg2, long arg3 )
			{
				Intent loadUserList = new Intent( adapterView.getContext( ), UserList.class );
				startActivityForResult( loadUserList, 0 );
			}
			
		} );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		getMenuInflater( ).inflate( R.menu.activity_hello_world, menu );
		return true;
	}

}
