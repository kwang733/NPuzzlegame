package net.cs76.projects.ImagePuzzle;

import java.util.Vector;

import net.cs76.projects.ImagePuzzle.PuzzleView.PuzzlePiece;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GamePlay extends Activity
{
	int mImageID;
	int mLevel;
	int blockSize;
	int mWid, mHei, mLen;
	int mCountDown = 0;
	
	int[][] PuzzleMap;
	BackThread mThread;
	Vector< PuzzlePiece > vtPiece;
	int mTime, mMove;
	RelativeLayout puzzleLinear;
	PuzzleView mPuzzle;
	boolean isRestart = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		
		initVariables();
		
		setContentView( R.layout.game_play );
		
		mPuzzle = new PuzzleView( this );
		mPuzzle.setMinimumHeight( mLen );
		mPuzzle.setMinimumWidth( mLen );
		
		puzzleLinear = (RelativeLayout) findViewById( R.id.puzzleLayout );
		puzzleLinear.addView( mPuzzle, 0 );

		mThread = new BackThread( mHandler );
		mThread.setDaemon( true );
		mThread.start();

	}

	private void initVariables()
	{
		Intent intent = getIntent();
		mImageID = intent.getIntExtra( "image", R.drawable.puzzle_0 );
		mLevel = intent.getIntExtra( "level", 4 );
		mHei = ImagePuzzleActivity.mHeight;
		mWid = ImagePuzzleActivity.mWidth;
		mLen = ImagePuzzleActivity.mLength;
		blockSize = mLen / mLevel;
		mMove = 0;
	}

	@Override
	public boolean onCreateOptionsMenu( Menu paramMenu )
	{
		getMenuInflater().inflate( R.menu.options_menu, paramMenu );
		return true;
	}
	
	private void setLevel( int nLevel )
	{
		mLevel = nLevel;
		blockSize = mLen / mLevel;
		mMove = 0;
		mCountDown = 0;
		isRestart = true;
		
		( ( TextView ) findViewById( R.id.countdown ) ).bringToFront();
		( ( TextView ) findViewById( R.id.countdown ) ).setVisibility( View.VISIBLE );
		
		mPuzzle.init();
		mPuzzle.postInvalidate();

		mThread = new BackThread( mHandler );
		mThread.setDaemon( true );
		mThread.start();

	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		switch ( item.getItemId() )
		{
			case R.id.menu_shuffle:
				mPuzzle.pieceShuffle();
				mPuzzle.postInvalidate();
				break;
			case R.id.menu_easy:
				setLevel( 3 );
				break;
			case R.id.menu_medium:
				setLevel( 4 );
				break;
			case R.id.menu_hard:
				setLevel( 5 );
				break;
			case R.id.menu_quit:
				finish();
				break;
		}
		return super.onOptionsItemSelected( item );
	}
	
	public boolean canMove( int x, int y, int sx, int sy )
	{
		if ( x < 0 || sx < 0 || y < 0 || sy < 0 )
			return false;
		if ( mLevel <= x || mLevel <= sx || mLevel <= y || mLevel <= sy )
			return false;
		int blank = mLevel * mLevel - 1;
		int a = PuzzleMap[sx][sy];
		if ( blank == a )
		{
			PuzzleMap[sx][sy] = PuzzleMap[x][y];
			PuzzleMap[x][y] = blank;
			return true;
		}
		return false;

	}

	public boolean moveNext( int x, int y )
	{
		if ( true == canMove( x, y, x - 1, y ) )
			return true;
		if ( true == canMove( x, y, x + 1, y ) )
			return true;
		if ( true == canMove( x, y, x, y - 1 ) )
			return true;
		if ( true == canMove( x, y, x, y + 1 ) )
			return true;
		return false;
	}

	public boolean checkGameOver()
	{
		int cnt = 0;
		for ( int i = 0; i < mLevel; i++ )
		{
			for ( int j = 0; j < mLevel; j++ )
			{
				if ( cnt++ != PuzzleMap[i][j] )
					return false;
			}
		}
		return true;
	}

	public void youWin()
	{
		Intent intent = new Intent( this, YouWin.class );
		intent.putExtra( "puzzle", mImageID );
		intent.putExtra( "moves", mMove );
		startActivity( intent );
		finish();

	}

	Handler mHandler = new Handler()
	{
		public void handleMessage( Message msg )
		{
			switch ( msg.what )
			{
				case 0:
					( ( TextView ) findViewById( R.id.countdown ) ).setText( Integer.toString( mCountDown + 1 ) );
					if ( mCountDown == 2 )
					{
						( ( TextView ) findViewById( R.id.countdown ) ).setVisibility( View.GONE );
						if ( isRestart == true )
						{
							mPuzzle.pieceShuffle();
							mPuzzle.postInvalidate();
							isRestart = false;
						}
						else
						{
							mPuzzle.pieceInit = false;
							mPuzzle.postInvalidate();
						}
					}
					// tvTime.setText(Integer.toString(msg.arg1));
					break;
			}
		}
	};

	class BackThread extends Thread
	{
		Handler mHandler;
		

		public BackThread( Handler handler )
		{
			mHandler = handler;
		}

		public void run()
		{
			while ( mCountDown < 3 )
			{
				Message msg = Message.obtain();
				msg.what = 0;
				mHandler.sendMessage( msg );
				try
				{
					Thread.sleep( 1000 );
					mCountDown++;
				}
				catch ( Exception e )
				{
				}
			}
		}
	}

}
