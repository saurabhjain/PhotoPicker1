package com.example.photopicker;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * <p>Activity for displaying selected images</p>
 * 
 * @author Saurabh Jain. <saurabhjain.net@gmail.com>
 */

public class PhotoPickerActivity extends Activity {

	private Button mPickPhotos;
	final Context context = this;
	int requestCode;

	Object[] imagePath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Attaching Pick Photos button' View
		mPickPhotos = (Button) findViewById(R.id.pickPhotos);

		// OnClickListener for Pick Photos button
		mPickPhotos.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent intent = new Intent(context, Photos.class);
				startActivityForResult(intent, requestCode);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Bundle bundle = data.getExtras();
		if(bundle != null) {
			ArrayList<String> selectedImagesPath = bundle.getStringArrayList("selectedImagesPath");

			if(selectedImagesPath != null && selectedImagesPath.size() > 0) {
				imagePath = selectedImagesPath.toArray();
				initGridView();
			}
		}
	}

	private void initGridView() {
		GridView gridView = (GridView) findViewById(R.id.gridView);
		gridView.setAdapter(new DisplayImageAdapter(getApplicationContext()));
	}

	private class DisplayImageAdapter extends BaseAdapter {
		private Context mContext;

		public DisplayImageAdapter(Context c) {
			mContext = c;
		}

		@Override
		public int getCount() {
			return imagePath.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		// create a new ImageView for each item referenced by the Adapter
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			if (convertView == null) { // if it's not recycled, initialize some attributes
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new GridView.LayoutParams(100, 100));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(2,2,2,2);
			} else {
				imageView = (ImageView) convertView;
			}

			try {
				Bitmap bitmap = getPreview(URI.create("file://" + ((String)imagePath[position]).replaceAll(" ","%20") ));
				imageView.setImageBitmap(bitmap);
			} catch (IllegalArgumentException e) {
				Toast.makeText(PhotoPickerActivity.this, "Skipping image '" + (String)imagePath[position] + "' as error occurred in fetching it...",
						Toast.LENGTH_SHORT).show();
			}
			return imageView;
		}

	}

	Bitmap getPreview(URI uri) {
	    File image = new File(uri);

	    BitmapFactory.Options bounds = new BitmapFactory.Options();
	    bounds.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(image.getPath(), bounds);
	    if ((bounds.outWidth == -1) || (bounds.outHeight == -1))
	        return null;

	    BitmapFactory.Options opts = new BitmapFactory.Options();
	    opts.inSampleSize = 100 / MediaStore.Images.Thumbnails.MICRO_KIND;
	    return BitmapFactory.decodeFile(image.getPath(), opts);
	}
}