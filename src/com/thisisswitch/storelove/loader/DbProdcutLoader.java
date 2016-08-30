package com.thisisswitch.storelove.loader;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.AsyncTaskLoader;

import com.thisisswitch.storelove.database.SQLHelper;
import com.thisisswitch.storelove.models.ProdcutModel;

/**loader asyanctasks for loading values from local db 
 * this class will load list using call back methods*/
public  class DbProdcutLoader extends AsyncTaskLoader<ArrayList<ProdcutModel>>  {

	ArrayList<ProdcutModel> productList = null;
	ProdcutModel mProdcutModel = null;
	boolean isLoading = false;

	public DbProdcutLoader(Context context) {
		super(context);
		mProdcutModel = new ProdcutModel();
	}

	@Override
	public void forceLoad() {
		if (!isLoading) {
			super.forceLoad();
			isLoading = !isLoading;
		}
	}

	@Override
	public ArrayList<ProdcutModel> loadInBackground() {

		SQLiteDatabase db = null ;
		Cursor cursor =  null ;
		try{
			productList = new ArrayList<ProdcutModel>();
			String query = "SELECT product_url,prodcut_name,product_desc,product_price,product_like_count,store_id,product_id,store_name FROM prodcut_table";

			db = SQLHelper.getInstance().getReadableDatabase();
			cursor = db.rawQuery(query, null);

			if(cursor.getCount() != 0){
				while (cursor.moveToNext()) {

					//mActivityListModel.activity_internal_id =   cursor.getInt(cursor.getColumnIndex("activity_internal_id"));
					mProdcutModel.product_url = cursor.getString(cursor.getColumnIndex("product_url"));
					mProdcutModel.prodcut_name = cursor.getString(cursor.getColumnIndex("prodcut_name"));
					mProdcutModel.product_desc = cursor.getString(cursor.getColumnIndex("product_desc"));
					mProdcutModel.product_price = cursor.getString(cursor.getColumnIndex("product_price"));
					mProdcutModel.product_like_count = cursor.getInt(cursor.getColumnIndex("product_like_count"));
					mProdcutModel.store_id = cursor.getString(cursor.getColumnIndex("store_id"));
					mProdcutModel.product_id = cursor.getString(cursor.getColumnIndex("product_id"));
					mProdcutModel.store_name = cursor.getString(cursor.getColumnIndex("store_name"));

					productList.add(mProdcutModel.addProdcuts(mProdcutModel));
				}
				//Collections.shuffle(productList);
			}
		}catch (Exception e) {
		} finally {

			db.close();
			cursor.close();
		}
		isLoading = false;
		return productList;
	}

	@Override
	public void deliverResult(ArrayList<ProdcutModel> data) {
		if (isReset()) {
			// The Loader has been reset; ignore the result and invalidate the data.
			// onReleaseResources(data);
			return;
		}
		// Hold a reference to the old data so it doesn't get garbage collected.
		// We must protect it until the new data has been delivered.
		ArrayList<ProdcutModel> oldData = productList;
		productList = data;

		if (isStarted()) {
			// If the Loader is in a started state, deliver the results to the
			// client. The superclass method does this for us.
			super.deliverResult(data);
		}
		// Invalidate the old data as we don't need it any more.
		if (oldData != null && oldData != data) {
			//onReleaseResources(oldData);
		}
	}

	@Override
	protected void onStartLoading() {
		forceLoad();
	}

	/**
	 * Handles a request to stop the Loader.
	 */
	@Override
	protected void onStopLoading() {
		// Attempt to cancel the current load task if possible.
		cancelLoad();
	}
	/**
	 * Handles a request to completely reset the Loader.
	 */
	@Override 
	protected void onReset() {
		super.onReset();
		// Ensure the loader is stopped
		onStopLoading();
	}
	@Override
	protected void onForceLoad() {
		super.onForceLoad();
	}
	/**
	 * Helper function to take care of releasing resources associated
	 * with an actively loaded data set.
	 */
	protected void onReleaseResources(ArrayList<ProdcutModel> apps) {
		// For a simple List<> there is nothing to do.  For something
		// like a Cursor, we would close it here.
	}
}
