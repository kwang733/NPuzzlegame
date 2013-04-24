package net.cs76.projects.ImagePuzzle;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ImagePuzzleActivity extends Activity implements OnItemClickListener
{
	ImageListAdapter adapter;
	static int mWidth, mHeight, mLength;

	/** Called when the activity is first created. */
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.image_selection );

		ListView imageList = (ListView) findViewById( R.id.listview );
		ArrayList< HashMap< String, Object >> imageArrayList = new ArrayList< HashMap< String, Object >>();

		adapter = new ImageListAdapter( this, imageArrayList );
		imageList.setAdapter( adapter );
		imageList.setOnItemClickListener( this );
		
		WindowManager wm = (WindowManager) getSystemService( Context.WINDOW_SERVICE );
		Display d = wm.getDefaultDisplay();
		mHeight = d.getHeight();
		mWidth = d.getWidth();
		mLength = mWidth > mHeight ? mHeight : mWidth;

	}

	@Override
	public void onItemClick( AdapterView< ? > paramAdapterView, View paramView, int paramInt, long paramLong )
	{
		Intent intent = new Intent( this, GamePlay.class );
		int imageId = Long.valueOf( paramLong ).intValue();
		intent.putExtra( "image", imageId );
		startActivity( intent );

	}
}
