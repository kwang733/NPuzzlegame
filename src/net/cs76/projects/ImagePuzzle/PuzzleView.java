package net.cs76.projects.ImagePuzzle;

import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.view.MotionEvent;
import android.view.View;

public class PuzzleView extends View
{
	GamePlay mGame;
	Bitmap mImage;
	boolean pieceInit;
	
	public PuzzleView( Context context )
	{
		super( context );
		mGame = (GamePlay) context;
		init();
	}

	void init()
	{
		pieceInit = true;
		mImage = BitmapFactory.decodeResource( getResources(), mGame.mImageID );
		
		mImage = Bitmap.createScaledBitmap( mImage, mGame.mLen, mGame.mLen, true );
		mGame.PuzzleMap = new int[mGame.mLevel][mGame.mLevel];
		mGame.vtPiece = new Vector< PuzzlePiece >();
		Bitmap tmpBit;
		boolean tag;
		for ( int i = 0; i < mGame.mLevel; i++ )
		{
			for ( int j = 0; j < mGame.mLevel; j++ )
			{
				if ( i * j == ( mGame.mLevel - 1 ) * ( mGame.mLevel - 1 ) )
				{
					tmpBit = null;
					tag = true;
				}
				else
				{
					tmpBit = Bitmap.createBitmap( mImage, j * mGame.blockSize, i * mGame.blockSize, mGame.blockSize, mGame.blockSize );
					tag = true;
				}
				mGame.vtPiece.add( ( i * mGame.mLevel ) + j, new PuzzlePiece( tmpBit, tag ) );
				mGame.PuzzleMap[i][j] = ( i * mGame.mLevel ) + j;
			}
		}
	}
	
	void pieceShuffle()
	{
		int index = mGame.mLevel;
		for ( int i = 0; i < index; i++ )
		{
			for ( int j = 0; j < index; j++ )
			{
				if ( i * j == ( index - 1 ) * ( index - 1 ) )
					mGame.PuzzleMap[i][j] = ( index ) * index - 1;
				else
					mGame.PuzzleMap[i][j] = ( index - i ) * index - j - 2; 
			}
		}
	}
	
	@Override
	public boolean onTouchEvent( MotionEvent event )
	{
		if ( event.getAction() == MotionEvent.ACTION_DOWN )
		{
			int y = (int) ( event.getX() / mGame.blockSize );
			int x = (int) ( event.getY() / mGame.blockSize );
			if ( mGame.moveNext( x, y ) )
			{
				mGame.mMove++;
			}
			else
			{
			}
			mGame.moveNext( x, y );
			invalidate();
			if ( mGame.checkGameOver() )
			{
				mGame.youWin();
			}
			return true;
		}
		return false;
	}

	@Override
	protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec )
	{
		if ( MeasureSpec.getMode( widthMeasureSpec ) != MeasureSpec.UNSPECIFIED )
		{
			setMeasuredDimension( getSuggestedMinimumWidth(), getSuggestedMinimumHeight() );
		}
		else
		{
			setMeasuredDimension( MeasureSpec.getSize( widthMeasureSpec ), MeasureSpec.getSize( heightMeasureSpec ) );
		}
	}
	
	@Override
	protected void onDraw( Canvas canvas )
	{
		canvas.drawColor( Color.BLACK );
		PuzzlePiece tmp;
		Bitmap tmpBit;
		Paint paint = new Paint();
		paint.setColor( Color.BLACK );
		paint.setStyle( Style.STROKE );
		paint.setStrokeWidth( 1 );
		
		for ( int i = 0; i < mGame.mLevel; i++ )
		{
			for ( int j = 0; j < mGame.mLevel; j++ )
			{
				tmp = mGame.vtPiece.get( mGame.PuzzleMap[i][j] );
				tmpBit = tmp.imagePiece;
				// tmpBit = Bitmap.createScaledBitmap(tmp.imagePiece,
				// mPuzzle.blockSize, mPuzzle.blockSize, true);
				if ( tmpBit != null )
				{
					canvas.drawBitmap( tmpBit, null, new Rect( j * mGame.blockSize, i * mGame.blockSize, ( j + 1 ) * mGame.blockSize, (i + 1) * mGame.blockSize ), null );
				}
			}
		}
		
		for ( int i = 0; i < mGame.mLevel; i++ )
		{
			for ( int j = 0; j < mGame.mLevel; j++ )
			{
				canvas.drawRect( i * mGame.blockSize, j * mGame.blockSize, i * mGame.blockSize + mGame.blockSize, j * mGame.blockSize + mGame.blockSize, paint );
			}
		}
		
		if ( !pieceInit )
		{
			pieceShuffle();
			pieceInit = true;
		}
	}

	class PuzzlePiece
	{
		PuzzlePiece( Bitmap bmp, boolean tag )
		{
			this.tag = tag;
			imagePiece = bmp;
		}

		boolean tag;
		Bitmap imagePiece;
	}

}
