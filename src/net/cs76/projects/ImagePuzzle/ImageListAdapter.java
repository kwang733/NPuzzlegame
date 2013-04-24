package net.cs76.projects.ImagePuzzle;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageListAdapter extends BaseAdapter
{
	private List< HashMap< String, Object >> list;
	private Context myContext;
	private ResizeImagesTask resizeImages;

	public ImageListAdapter( Context paramContext, ArrayList< HashMap< String, Object >> paramArrayList )
	{
		myContext = paramContext;

		// make thumbnail image
		resizeImages = new ResizeImagesTask();

		list = new ArrayList< HashMap< String, Object >>();
		
		//get file list of drawable
		Field[] arrayOfField = R.drawable.class.getFields();
		try
		{
			int nCount = arrayOfField.length;	//number of files on drawable
			for ( int i = 0; i < nCount; i++ )
			{
				Field localField = arrayOfField[i];
				String str = localField.getName();
				if ( str.startsWith( "puzzle_" ) )
				{
					Integer image_id = Integer.valueOf( localField.getInt( null ) );
					HashMap< String, Object > localHashMap = new HashMap< String, Object >();
					localHashMap.put( "name", str );
					localHashMap.put( "id", image_id );
					list.add( localHashMap );
				}
			}
		}
		catch ( IllegalArgumentException localIllegalArgumentException )
		{
			Log.d( "ImagePuzzle", "IllegalArgumentException while caching image list" );
		}
		catch ( IllegalAccessException localIllegalAccessException )
		{
			Log.d( "ImagePuzzle", "IllegalAccessException while caching image list" );
		}

		Void[] arrayOfVoid2 = new Void[0];
		resizeImages.execute( arrayOfVoid2 );
	}

	@Override
	public int getCount()
	{
		return list.size();
	}

	@Override
	public Object getItem( int arg0 )
	{
		return null;
	}

	@Override
	public long getItemId( int paramInt )
	{
		return ( (Integer) list.get( paramInt ).get( "id" ) ).longValue();
	}

	@Override
	public View getView( int paramInt, View paramView, ViewGroup paramViewGroup )
	{
		HashMap< String, Object > list_item = list.get( paramInt );
		ViewSet localViewSet = null;
		Bitmap list_item_bitmap;
		paramView = View.inflate( this.myContext, R.layout.imagelist_item, null );
		localViewSet = new ViewSet();

		localViewSet.txt = (TextView) paramView.findViewById( R.id.txt );
		localViewSet.img = (ImageView) paramView.findViewById( R.id.img );
		
		paramView.setTag( localViewSet );
		list_item_bitmap = (Bitmap) list_item.get( "thumb" );
		
		if ( list_item_bitmap != null )
			localViewSet.img.setImageBitmap( list_item_bitmap );
		else
			localViewSet.img.setImageResource( R.drawable.holder );

		ImageView list_item_imageview = localViewSet.img;
		list_item.put( "view", list_item_imageview );
		
		TextView list_item_filename = localViewSet.txt;
		String file_name = (String) list_item.get( "name" );
		list_item_filename.setText( file_name );
		
		return paramView;
	}

	private void setImage( ImageView paramImageView, Bitmap paramBitmap )
	{
		paramImageView.setImageBitmap( paramBitmap );
		notifyDataSetChanged();
	}

	private class ResizeImagesTask extends AsyncTask< Void, HashMap< String, Object >, Void >
	{
		@Override
		protected Void doInBackground( Void... params )
		{
			BitmapFactory.Options bitmap_option = new BitmapFactory.Options();
			
			Iterator< HashMap< String, Object >> list_iterator = list.iterator();
			
			while ( list_iterator.hasNext() )
			{
				HashMap< String, Object > list_item = list_iterator.next();

				if ( list_item.get( "thumb" ) != null )
				{
					HashMap[] arrayOfHashMap1 = new HashMap[1];
					arrayOfHashMap1[0] = list_item;
					publishProgress( arrayOfHashMap1 );
					continue;
				}
				
				int image_id = ( (Integer) list_item.get( "id" ) ).intValue();
				bitmap_option.inSampleSize = 4;		//decrease ratio. ( thumbnail image size = original image size / 4 )
				Bitmap thum_bitmap = BitmapFactory.decodeResource( myContext.getResources(), image_id, bitmap_option );				
				list_item.put( "thumb", thum_bitmap );
				
				HashMap[] arrayOfHashMap2 = new HashMap[1];
				arrayOfHashMap2[0] = list_item;
				publishProgress( arrayOfHashMap2 );
			}
			return null;
		}

		protected void onProgressUpdate( HashMap< String, Object >[] paramArrayOfHashMap )
		{
			ImageView list_item_imageview = (ImageView) paramArrayOfHashMap[0].get( "view" );
			if ( list_item_imageview == null )
				return;
			
			ImageListAdapter adapter = ImageListAdapter.this;
			Bitmap list_item_thumbImage = (Bitmap) paramArrayOfHashMap[0].get( "thumb" );
			adapter.setImage( list_item_imageview, list_item_thumbImage );
			paramArrayOfHashMap[0].remove( "view" );
		}
	}

	private class ViewSet
	{
		ImageView img;
		TextView txt;

		private ViewSet()
		{
		}
	}

}
