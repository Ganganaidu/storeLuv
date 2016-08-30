package com.thisisswitch.storelove.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.thisisswitch.storelove.MyApplication;
import com.thisisswitch.storelove.models.ProdcutModel;
import com.thisisswitch.storelove.models.StoreModel;

public class SQLHelper extends SQLiteOpenHelper {

	//	private static final String TAG = "SQLHelper";

	private static final String DATABASE_NAME = "storluv.db";
	private static final int DATABASE_VERSION = 1;

	/**
	 * Singleton instance of {@link SQLHelper}.
	 */
	private static SQLHelper instance = null;

	/**
	 * @return the {@link SQLHelper} singleton.
	 */
	public static SQLHelper getInstance() {
		if (instance != null) {
			return instance;
		} else {
			return new SQLHelper();
		}
	}

	private SQLHelper() {
		super(MyApplication.getInstance().getApplicationContext(), DATABASE_NAME,
				null, DATABASE_VERSION);
	}

	//	public SQLHelper(Context context, String name,
	//			CursorFactory factory, int version) {
	//		super(context, name, factory, version);
	//	}
	//
	//	/** Helper to the database, manages versions and creation */
	//	public SQLHelper(Context context) {
	//		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	//
	//	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		//Sub Category NOT NULL UNIQUE ON CONFLICT IGNORE
		String prodcutTable = "CREATE TABLE product_table ( "
				+ "pr_row_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
				+ "product_url TEXT NOT NULL," 
				+ "product_desc TEXT,"
				+ "prodcut_name TEXT," 
				+ "product_type TEXT,"
				+ "product_price TEXT," 
				+ "product_id TEXT," 
				+ "device_like_status INTEGER,"
				+ "product_like_count INTEGER," 
				+ "store_name TEXT," 
				+ "store_id TEXT NOT NULL,"
				+ "prodcut_timestamp TEXT NOT NULL,"
				+ "image_width INTEGER NOT NULL,"
				+ "image_height INTEGER NOT NULL,"
				+ "image_loaded_flag INTEGER NOT NULL,"
				+ "keyword_name TEXT NOT NULL,"
				+ "extra1 TEXT NOT NULL,"
				+ "extra2 TEXT NOT NULL,"
				+ "extra3 TEXT);";
		db.execSQL(prodcutTable);

		//
		String storeTable = "CREATE TABLE store_table ( "
				+ "st_row_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
				+ "store_address TEXT NOT NULL," 
				+ "store_emid TEXT NOT NULL,"
				+ "store_phone_number TEXT," 
				+ "store_name TEXT,"
				+ "store_about TEXT,"
				+ "store_id TEXT NOT NULL,"
				+ "store_timestamp TEXT NOT NULL,"
				+ "extra1 TEXT NOT NULL,"
				+ "extra2 TEXT NOT NULL,"
				+ "extra3 TEXT);";
		db.execSQL(storeTable);

		//
		String likecountTable = "CREATE TABLE prodcut_like_table ( "
				+ "like_row_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
				+ "prodcut_id TEXT,"
				+ "device_like_status TEXT NOT NULL," 
				+ "product_like_count TEXT NOT NULL,"
				+ "like_timestamp TEXT NOT NULL,"
				+ "extra1 TEXT NOT NULL,"
				+ "extra2 TEXT NOT NULL,"
				+ "extra3 TEXT);";
		db.execSQL(likecountTable);

		//		String keyword_table = "CREATE TABLE keywords_table ( "
		//				+ "keyword_row_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
		//				+ "prodcut_id TEXT,"
		//				+ "keyowrd_name TEXT NOT NULL," 
		//				+ "keywird_id TEXT NOT NULL,"
		//				+ "store_name TEXT NOT NULL,"
		//				+ "extra1 TEXT NOT NULL,"
		//				+ "extra2 TEXT NOT NULL,"
		//				+ "extra3 TEXT);";
		//		db.execSQL(likecountTable);
	}

	////TODO/////////INSERT  advertaisement
	/** Insert Main category */
	public void insertinto_produtTable(ProdcutModel mProdcutModel){

		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();

		try{

			db.beginTransaction();
			values.put("product_url", mProdcutModel.product_url);
			values.put("product_desc", mProdcutModel.product_desc);
			values.put("prodcut_name", mProdcutModel.prodcut_name);
			values.put("product_type", mProdcutModel.product_type);
			values.put("store_name", mProdcutModel.store_name);
			values.put("product_price", mProdcutModel.product_price);
			values.put("product_id", mProdcutModel.product_id);
			values.put("product_like_count", mProdcutModel.product_like_count);
			values.put("device_like_status",  mProdcutModel.device_like_status);
			values.put("store_id", mProdcutModel.store_id);
			values.put("prodcut_timestamp", mProdcutModel.product_timeStamp);
			values.put("keyword_name", mProdcutModel.keyword_name);
			values.put("image_width", mProdcutModel.image_width);
			values.put("image_height", mProdcutModel.image_height);
			values.put("image_loaded_flag", mProdcutModel.image_loaded_flag);
			values.put("extra1", "");
			values.put("extra2", "");
			values.put("extra3", "");

			//db.insertWithOnConflict("citypedia_table",  null, values, SQLiteDatabase.CONFLICT_REPLACE);
			db.insert("product_table", null, values);
			db.setTransactionSuccessful();
		}catch (Exception e) {
		}
		finally{
			db.endTransaction();
			db.close();
		}
	}

	/**@param product_id 
	 * @param  product_like_count
	 * @param device_like_status
	 * @param sync_status*/
	public void updateLikeCount(String product_id,int product_like_count,int device_like_status,String sync_status){

		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		try {
			db.beginTransaction();
			//values.put("form_id", mFromDraftsModel.form_id);
			values.put("product_like_count",  product_like_count);
			values.put("device_like_status",  device_like_status);
			values.put("extra1", sync_status);
			db.update("product_table", values, " product_id = '"+product_id+"'", null);
			db.setTransactionSuccessful();

			//Log.e("db","like_count-->"+product_like_count);
			//Log.e("db","like_status-->"+device_like_status);
			//Log.e("db","product_id-->"+product_id);
		}catch (Exception e) {}
		finally {
			db.endTransaction();
			db.close();
		}
	}

	/** */
	public void updateProductLikeCount(String product_id,int product_like_count,
			int device_like_status,String sync_status){

		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		try {
			db.beginTransaction();
			values.put("prodcut_id", product_id);
			values.put("product_like_count",  product_like_count);
			values.put("device_like_status",  device_like_status);
			values.put("like_timestamp",  "");
			values.put("extra1", "");
			values.put("extra2", "");
			values.put("extra3", "");

			db.insert("prodcut_like_table", null, values);
			db.setTransactionSuccessful();

		}catch (Exception e) {}
		finally{
			db.endTransaction();
			db.close();
		}
	}

	/** */
	public void updateLikeSyncCount(int rowId){

		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		try {
			values.put("extra1", "0");
			db.update("product_table", values, " pr_row_id = "+rowId, null);
		}catch (Exception e) {}
		finally {
			db.close();
		}
	}

	/** Insert store details*/
	public void insertinto_storeTable(StoreModel mStoreModel){

		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();

		try{

			db.beginTransaction();
			values.put("store_address", mStoreModel.store_address);
			values.put("store_emid", mStoreModel.store_emailid);
			values.put("store_phone_number", mStoreModel.store_phone_number);
			values.put("store_name", mStoreModel.store_name);
			values.put("store_id", mStoreModel.store_id);
			values.put("store_about", mStoreModel.description);//
			values.put("store_timestamp", mStoreModel.store_timestamp);
			values.put("extra1", mStoreModel.description);//
			values.put("extra2", "");
			values.put("extra3", "");

			//db.insertWithOnConflict("citypedia_table",  null, values, SQLiteDatabase.CONFLICT_REPLACE);
			db.insert("store_table", null, values);
			db.setTransactionSuccessful();
		}catch (Exception e) {
		}
		finally{
			db.endTransaction();
			db.close();
		}
	}


	public void deleteProducts(){
		SQLiteDatabase db = getReadableDatabase();
		try{
			db.delete("product_table",null,null);
		} catch (Exception e){}
		finally{
			db.close();			
		}
	}

	public void deleteStore(){
		SQLiteDatabase db = getReadableDatabase();
		try {
			db.delete("store_table",null,null);
		} catch (Exception e){}
		finally {
			db.close();			
		}
	}

	public void deleteStoreId(String store_id){
		SQLiteDatabase db = getReadableDatabase();
		try{
			db.delete("store_table", "store_id = '"+store_id+"'", null);		
		} catch (Exception e){}
		finally{
			db.close();			
		}
	} 

	public void deleteProductIdBasedOnStoreId(String storeId){
		SQLiteDatabase db = getReadableDatabase();
		try { 
			db.delete("product_table", "store_id = '"+storeId+"'", null);		
		} catch (Exception e){}
		finally{
			db.close();			
		}
	}

	public void deleteProductId(String product_id){
		SQLiteDatabase db = getReadableDatabase();
		try { 
			db.delete("product_table", "product_id = '"+product_id+"'", null);		
		} catch (Exception e){}
		finally{
			db.close();			
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		//Log.v("New Version", "Datas can be upgraded"+oldVersion);
		//Log.v("New Version", "Datas can be upgraded"+newVersion);

		//Deletes the previous table if exists
		//db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);

		// Create tables again
		if (oldVersion >= newVersion)
			return;
		if (newVersion == 3) {

			//			db.delete("product_table", null, null);
			//			db.delete("store_table", null, null);
			//			db.delete("prodcut_like_table", null, null);
//			AppPreferences mAppPreferences = new AppPreferences(MyApplication.getInstance());
//			mAppPreferences.deletePref();
//
//			db.execSQL("DROP TABLE IF EXISTS product_table ");
//			db.execSQL("DROP TABLE IF EXISTS store_table ");
//			db.execSQL("DROP TABLE IF EXISTS prodcut_like_table ");

			//Log.v("New Version", "Datas can be upgraded");
		}
		//Log.d("Sample Data", "onUpgrade	: " + newVersion);
		onCreate(db);
	}
}
