package com.example.photopicker;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * <p>Activity for fetching the images present in device</p>
 * 
 * @author Saurabh Jain. <saurabhjain.net@gmail.com>
 */
public class Photos extends Activity{

	private Button attachPhotos;
	private int count;
	private Bitmap[] imageBitmap;
	private String[] imagePath;
	private boolean[] imageSelected;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photos);
		initGridView();

		attachPhotos = (Button)findViewById(R.id.attach);

		attachPhotos.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(AppConstants.showDebugToasts) Toast.makeText(Photos.this, "Attach button clicked", Toast.LENGTH_SHORT).show();

					ArrayList<String> selectedImagesPath = new ArrayList<String>();
					for(int i=0 ; i<count ; i++) { //compiling list of selected images
						if(imageSelected[i]) {
							selectedImagesPath.add(imagePath[i]);
						}
					}

					final Intent intent = new Intent();
					intent.putStringArrayListExtra("selectedImagesPath", selectedImagesPath);
					setResult(RESULT_OK, intent); //setting result
					finish(); //finish activity after selection
				}
			});
	}

	private void initGridView() {
		final String[] columns = {MediaStore.Images.Media.DATA,MediaStore.Images.Media._ID};
		final String sortBy = MediaStore.Images.Media._ID;
		Cursor cursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, sortBy);
		int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
		this.count = cursor.getCount();
		this.imageBitmap = new Bitmap[this.count];
		this.imagePath = new String[this.count];
		this.imageSelected = new boolean[this.count];

		for(int i=0; i<this.count; i++) {
			cursor.moveToPosition(i);
			int id = cursor.getInt(columnIndex);
			int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
			imageBitmap[i] = MediaStore.Images.Thumbnails.getThumbnail(getApplicationContext().getContentResolver(), id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
			imagePath[i]= cursor.getString(dataColumnIndex);
		}

		GridView gridView = (GridView) findViewById(R.id.gridView);
		gridView.setAdapter(new ImageAdapter(getApplicationContext()));
		cursor.close();
	}

	private class ImageAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public ImageAdapter(Context c) {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return count;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.galleryitem, null);
				holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);
				holder.checkbox = (CheckBox) convertView.findViewById(R.id.itemCheckBox);

				convertView.setTag(holder);
			}
			else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.checkbox.setId(position);
			holder.imageview.setId(position);
			holder.checkbox.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					CheckBox cb = (CheckBox) v;
					int id = cb.getId();
					if (imageSelected[id]){
						cb.setChecked(false);
						imageSelected[id] = false;
					} else {
						cb.setChecked(true);
						imageSelected[id] = true;
					}
				}
			});
			holder.imageview.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// to prohibit clicking on images so keeping blank
				}
			});
			holder.imageview.setImageBitmap(imageBitmap[position]);
			holder.checkbox.setChecked(imageSelected[position]);
			holder.id = position;
			return convertView;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if((keyCode == KeyEvent.KEYCODE_BACK)) {
			final Intent intent = new Intent();
			setResult(RESULT_OK, intent); //setting result
			finish(); //finish activity after selection
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	class ViewHolder {
		ImageView imageview;
		CheckBox checkbox;
		int id;
	}

}
