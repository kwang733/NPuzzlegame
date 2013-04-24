package net.cs76.projects.ImagePuzzle;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class YouWin extends Activity implements OnClickListener
{

	/** Called when the activity is first created. */
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		setContentView( R.layout.you_win );

		Bundle localBundle = getIntent().getExtras();
		int i = localBundle.getInt( "puzzle" );
		int j = localBundle.getInt( "moves" );
		( (ImageView) findViewById( R.id.won_image ) ).setImageResource( i );
		TextView localTextView = (TextView) findViewById( R.id.moves );
		String str = "Incredible, you did it in " + j + " moves!";
		localTextView.setText( str );
		( (Button) findViewById( R.id.cool ) ).setOnClickListener( this );
	}

	@Override
	public void onClick( View v )
	{
		finish();
	}

}
