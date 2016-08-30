package com.thisisswitch.storelove;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.switchsoft.maplibrary.model.MarkerListModel;
import com.thisisswitch.storelove.adapter.ProductsListAdapter;
import com.thisisswitch.storelove.adapter.SlideMenuAdapter;
import com.thisisswitch.storelove.fragments.CategoriesFragment;
import com.thisisswitch.storelove.fragments.CategoryProductListFragment;
import com.thisisswitch.storelove.fragments.MyFeedDetailsFragment;
import com.thisisswitch.storelove.fragments.SearchFragment;
import com.thisisswitch.storelove.fragments.SettingsFragment;
import com.thisisswitch.storelove.fragments.StoreProductsListFragment;
import com.thisisswitch.storelove.fragments.StoresListFragment;
import com.thisisswitch.storelove.fragments.SubcategoriesFragment;
import com.thisisswitch.storelove.models.ProdcutModel;
import com.thisisswitch.storelove.models.StoreModel;
import com.thisisswitch.storelove.preferences.AppPreferences;
import com.thisisswitch.storelove.socialnetwork.StorluvLoginActvity;
import com.thisisswitch.storelove.utils.AppUrls;
import com.thisisswitch.storelove.utils.Globals;
import com.thisisswitch.storelove.utils.Networking;
import com.thisisswitch.storelove.widgets.CircleImageView;
import com.thisisswitch.storelove.widgets.FoldingCirclesDrawable;

public class StoreLoveActivity extends SherlockFragmentActivity 
implements StoresListFragment.StoreListOnclickListener,
CategoriesFragment.CategoryListOnclickListener,
SubcategoriesFragment.SubCategoryListOnclickListener,
MyFeedDetailsFragment.MyFeedOnclickListener{

	protected static final String TAG = "StoreLoveActivity";

	private  FrameLayout fragment_container;
	private  ListView stag_listview;
	private  PullToRefreshListView mPullToRefreshListView;
	private  ProgressBar customProgressBar1;
	private  RelativeLayout prodcut_rellayout;
	//private View footerView = null;

	//slide menu views
	//private LinearLayout profile_layout;
	private CircleImageView slidemenu_imageView1;
	private TextView slidemenu_username_textView1;
	private ListView slidemenu_listView1;

	private ProductsListAdapter adapter = null;
	private  ArrayList<ProdcutModel> productList = null;
	private  AppPreferences mAppPreferences = null;
	private  ProdcutModel mProdcutModel = null;

	//Fragment
	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	private StoresListFragment mStoresListFragment;

	private MixpanelAPI mMixpanel;
	//slide menu
	private MenuDrawer mMenuDrawer;
	//https://github.com/coswind/mvnRepoDemo
	//https://github.com/mobiquestdeveloper/ActionBarPullToRefreshAndEndlessAdapter

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//mMenuDrawer = MenuDrawer.attach(this);
		mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, Position.LEFT, MenuDrawer.MENU_DRAG_WINDOW);
		mMenuDrawer.setMenuView(R.layout.slide_menu_layout);
		mMenuDrawer.setContentView(R.layout.activity_store_love);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setTitle("Featured");

		getSupportActionBar().setIcon(android.R.color.transparent);
		// The drawable that replaces the up indicator in the action bar
		mMenuDrawer.setSlideDrawable(R.drawable.home_icon_1);
		// Whether the previous drawable should be shown
		mMenuDrawer.setDrawerIndicatorEnabled(true);

		productList = new ArrayList<ProdcutModel>();
		mAppPreferences = new AppPreferences(this);
		mProdcutModel = new ProdcutModel();

		//slide menu views
		slidemenu_imageView1 = (CircleImageView)findViewById(R.id.slidemenu_imageView1);
		slidemenu_username_textView1 = (TextView)findViewById(R.id.slidemenu_username_textView1);
		slidemenu_listView1 = (ListView) findViewById(R.id.slidemenu_listView1);
		//profile_layout = (LinearLayout)findViewById(R.id.profile_layout);

		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.stag_listview);
		prodcut_rellayout = (RelativeLayout) findViewById(R.id.prodcut_rellayout);
		fragment_container = (FrameLayout) findViewById(R.id.fragment_container);
		customProgressBar1 = (ProgressBar)findViewById(R.id.customProgressBar1);

		pullToRefreshChangedView();
		stag_listview.setVisibility(View.GONE);
		customProgressBar1.setVisibility(View.VISIBLE);
		customProgressBar1.setIndeterminateDrawable(new FoldingCirclesDrawable());
		//customProgressBar1.execute();

		loadStoreProducts();
		slidemenuClick();
		mixPanelTrack();
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateSlideMenuLayout();
	}

	@Override
	public void onPause() {
		super.onPause();
	}
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this); 
	}
	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);  
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Share")
		.setIcon(R.drawable.ic_social_share_)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		menu.add("Search")
		.setIcon(R.drawable.ic_storluv_search)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getTitle().equals("Share")) {
			// Sending a screen view in v3 using MapBuilder.
			Tracker tracker = GoogleAnalytics.getInstance(this).getTracker(Globals.gTracker_id);
			tracker.set(Fields.SCREEN_NAME, "Home Screen");
			tracker.send(MapBuilder.createAppView().set(Fields.customDimension(1), "share") .build());

			shareDetails();

		}  else if (item.getItemId() == android.R.id.home) { 

			updateSlideMenuLayout();
			mMenuDrawer.toggleMenu();

		}  else if(item.getTitle().equals("Search")) {
			// Sending a screen view in v3 using MapBuilder.
			Tracker tracker = GoogleAnalytics.getInstance(this).getTracker(Globals.gTracker_id);
			tracker.set(Fields.SCREEN_NAME, "Search Screen");
			tracker.send(MapBuilder.createAppView().set(Fields.customDimension(1), "Search") .build());

			navigateTosearchPage();
		}  
		return false;
	}

	//loading product details 
	private void loadStoreProducts() {

		if(Networking.isNetworkAvailable(this)) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new  LoadProductsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new  LoadProductsTask().execute();
			}
		} else {
			Toast.makeText(this, Networking.mNewtWorkState, Toast.LENGTH_SHORT).show();
		}
	}
	//TODO 
	private void updateSlideMenuLayout() {
		//setting slide menu adapter
		slidemenu_listView1.setAdapter(new SlideMenuAdapter(this,mAppPreferences));
		if(mAppPreferences.getUserImageUrl().length()>2){

			MyApplication.imageLoader.loadImage(mAppPreferences.getUserImageUrl(), MyApplication.options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					// Do whatever you want with Bitmap
					slidemenu_imageView1.setImageBitmap(loadedImage);
				}
			});
			slidemenu_username_textView1.setText(mAppPreferences.getUserName());
		} else {
			slidemenu_imageView1.setImageResource(R.drawable.storluv_sidebar_profile);
		}
		//		profile_layout.setOnClickListener(new OnClickListener() {
		//			@Override
		//			public void onClick(View v) {
		//				mMenuDrawer.toggleMenu();
		//				if(mAppPreferences.getUserid().equals("") || mAppPreferences.getUserid().equals("0")) {
		//					navigateToLoginPage();
		//				} else {
		//					navigateToProfileViewPage();
		//				}
		//			}
		//		});
	}


	private void slidemenuClick() {
		slidemenu_listView1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int count,long arg3) {
				mMenuDrawer.toggleMenu();
				switch (count) {
				case 0:

					getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
					prodcut_rellayout.setVisibility(View.VISIBLE);
					fragment_container.setVisibility(View.GONE);
					getSupportActionBar().setTitle("Featured");

					if(!mAppPreferences.getRegionId().equals(mAppPreferences.getPreviousRegionId())) {
						mAppPreferences.savePreviousRegionId(mAppPreferences.getRegionId());
						//Log.d(TAG,"region id "+mAppPreferences.getRegionId());
						//Log.d(TAG,"previous region id "+mAppPreferences.getPreviousRegionId());
						//
						if(Networking.isNetworkAvailable(StoreLoveActivity.this)) {
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
								new  LoadOnRegionChangeProductsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							} else {
								new  LoadOnRegionChangeProductsTask().execute();
							}
						} else {
							Toast.makeText(StoreLoveActivity.this, Networking.mNewtWorkState, Toast.LENGTH_SHORT).show();
						}
					}
					break;

				case 1:

					if(mAppPreferences.getUserid().equals("") || mAppPreferences.getUserid().equals("0")) {
						navigateToLoginPage();
					} else {
						navigateToMyfeedPage(); 
					}
					break;

				case 2:
					navigateToStoreListpage();
					break;

				case 3:
					navigateToCategoryPage(false);
					break;

				case 4:
					navigateToCategoryPage(true);
					break;

				case 5:

					prodcut_rellayout.setVisibility(View.GONE);
					fragment_container.setVisibility(View.VISIBLE);

					getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
					//Replace the existing fragment with home fragment
					fragmentManager = getSupportFragmentManager();
					fragmentTransaction = fragmentManager.beginTransaction();
					SettingsFragment settingsFragment = new SettingsFragment();

					fragmentTransaction.replace(R.id.fragment_container, settingsFragment);
					fragmentTransaction.addToBackStack("SettingsFragment");
					fragmentTransaction.commit();

					break;
				default:
					break;
				}
			}
		});
	}

	private void navigateToLoginPage() {
		Intent in = new Intent(StoreLoveActivity.this,StorluvLoginActvity.class);
		startActivity(in);
	}

	// TODO ======================= App fragment navigations =========================
	private void navigateTosearchPage() {

		getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		prodcut_rellayout.setVisibility(View.GONE);
		fragment_container.setVisibility(View.VISIBLE);

		//Replace the existing fragment with home fragment
		fragmentManager = getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();
		SearchFragment searchFragment = new SearchFragment();

		fragmentTransaction.replace(R.id.fragment_container, searchFragment);
		fragmentTransaction.addToBackStack("SearchFragment");
		fragmentTransaction.commit();
		overridePendingTransition (R.anim.open_next, R.anim.close_main);
	}

	private void navigateToMyfeedPage() {

		getSupportActionBar().setTitle("My Feed");
		getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		prodcut_rellayout.setVisibility(View.GONE);
		fragment_container.setVisibility(View.VISIBLE);

		//Replace the existing fragment with home fragment
		fragmentManager = getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();
		MyFeedDetailsFragment myfeedPage = new MyFeedDetailsFragment();

		fragmentTransaction.replace(R.id.fragment_container, myfeedPage);
		fragmentTransaction.addToBackStack("MyFeedDetailsFragment");
		fragmentTransaction.commit();
	}

	private void navigateToStoreListpage() {

		if(mStoresListFragment != null && mStoresListFragment.isVisible()) {
			final int drawerState = mMenuDrawer.getDrawerState();
			if (drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING) {
				mMenuDrawer.closeMenu();
			}
		} else {

			getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			prodcut_rellayout.setVisibility(View.GONE);
			fragment_container.setVisibility(View.VISIBLE);

			//Replace the existing fragment with home fragment
			fragmentManager = getSupportFragmentManager();
			fragmentTransaction = fragmentManager.beginTransaction();
			mStoresListFragment = new StoresListFragment();

			fragmentTransaction.replace(R.id.fragment_container, mStoresListFragment);
			fragmentTransaction.addToBackStack("StoresListFragment");
			fragmentTransaction.commit();

		}
	}	
	//	private void navigateToProfileViewPage() {
	//
	//		getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
	//		prodcut_rellayout.setVisibility(View.GONE);
	//		fragment_container.setVisibility(View.VISIBLE);
	//
	//		//Replace the existing fragment with home fragment
	//		fragmentManager = getSupportFragmentManager();
	//		fragmentTransaction = fragmentManager.beginTransaction();
	//		MyProfileFragment profielFragment = new MyProfileFragment();
	//
	//		fragmentTransaction.replace(R.id.fragment_container, profielFragment);
	//		fragmentTransaction.addToBackStack("MyProfileFragment");
	//		fragmentTransaction.commit();
	//	}

	private void navigateToCategoryPage(boolean isSpecial){

		getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		prodcut_rellayout.setVisibility(View.GONE);
		fragment_container.setVisibility(View.VISIBLE);

		//Replace the existing fragment with home fragment
		fragmentManager = getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();
		CategoriesFragment catFragment = new CategoriesFragment();

		Bundle storeBundle = new Bundle();
		storeBundle.putBoolean("isSpecial",isSpecial);
		catFragment.setArguments(storeBundle);

		fragmentTransaction.replace(R.id.fragment_container, catFragment);
		fragmentTransaction.addToBackStack("CategoriesFragment");
		fragmentTransaction.commit();
	}

	//==== Mix panel tracking details =======
	/** using this for tracking visits of this page(like google analytics) */
	private void mixPanelTrack() {
		mMixpanel = MixpanelAPI.getInstance(this, Globals.MIXPANEL_TOKEN);
		JSONObject props = new JSONObject();
		try {
			props.put("HOME", "");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mMixpanel.track("HOME", props);
	}


	//TODO====== Pull to refresh listview details ==============
	public void pullToRefreshChangedView() {

		// disabling scroll when pull
		mPullToRefreshListView.setScrollingWhileRefreshingEnabled(!mPullToRefreshListView.isScrollingWhileRefreshingEnabled());
		mPullToRefreshListView.setSelected(true);
		mPullToRefreshListView.setMode(mPullToRefreshListView.getMode() == Mode.BOTH ?Mode.PULL_FROM_START: Mode.BOTH);
		mPullToRefreshListView.setShowIndicator(false);

		stag_listview = mPullToRefreshListView.getRefreshableView();
		// Set a listener to be invoked when the list should be refreshed.
		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

				if(Networking.isNetworkAvailable(StoreLoveActivity.this)) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new LoadProductsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}
					else {
						new LoadProductsTask().execute();
					}
				} else {
					mPullToRefreshListView.onRefreshComplete();
					Toast.makeText(StoreLoveActivity.this, Globals.mTimeOutConnection, Toast.LENGTH_SHORT).show();
				}
			}
			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

				if(Networking.isNetworkAvailable(StoreLoveActivity.this)) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new LoadMoreProductsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}
					else {
						new LoadMoreProductsTask().execute();
					}
				} else {
					mPullToRefreshListView.onRefreshComplete();
					Toast.makeText(StoreLoveActivity.this, Globals.mTimeOutConnection, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	// =========== Fragments call back methods =============
	@Override
	public void onStoreSelected(StoreModel mStoreModel) {

		//Replace the existing fragment with home fragment
		fragmentManager = getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();

		StoreProductsListFragment storePage = new StoreProductsListFragment();
		Bundle storeBundle = new Bundle();
		storeBundle.putSerializable("storelist", mStoreModel);
		storePage.setArguments(storeBundle);	

		fragmentTransaction.replace(R.id.fragment_container, storePage);
		fragmentTransaction.addToBackStack("StoreProductsListFragment");
		fragmentTransaction.commit();
	}

	@Override
	public void onCateogrySelected(String mainCatId) {
		//Replace the existing fragment with home fragment
		fragmentManager = getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();

		SubcategoriesFragment moviesPage = new SubcategoriesFragment();
		Bundle storeBundle = new Bundle();
		storeBundle.putString("maincatId",mainCatId);
		moviesPage.setArguments(storeBundle);	

		fragmentTransaction.replace(R.id.fragment_container, moviesPage);
		fragmentTransaction.addToBackStack("SubcategoriesFragment");
		fragmentTransaction.commit();
	}

	@Override
	public void onSubCateogrySelected(String subcatid) {
		//Replace the existing fragment with home fragment
		fragmentManager = getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();

		CategoryProductListFragment moviesPage = new CategoryProductListFragment();
		Bundle storeBundle = new Bundle();
		storeBundle.putString("subcatId",subcatid);
		moviesPage.setArguments(storeBundle);	

		fragmentTransaction.replace(R.id.fragment_container, moviesPage);
		fragmentTransaction.addToBackStack("CategoryProductListFragment");
		fragmentTransaction.commit();
	}

	@Override
	public void onMyFeedSelected(String storeId) {

	}

	@Override
	public void onMapEnabled(boolean enbale, MarkerListModel markers) {

	}

	//TODO================= Loading details from server ====================
	//pull up load 
	private class LoadMoreProductsTask extends AsyncTask<String, String, Boolean> {
		@Override
		protected void onPreExecute() {

		}
		@Override
		protected Boolean doInBackground(String... params) {

			if(mAppPreferences.getUserid().equals("")){
				mAppPreferences.saveUserid("0");
			}
			//checking count for taking last value in the list for paging count
			int count = productList.size();
			if(count != 0){
				mAppPreferences.saveFromCount(productList.get(count-1).getProduct_id());		
			} 
			return getProdcutsResponse(AppUrls.getProductsUrl(Globals.PAGE_COUNT_VALUE, mAppPreferences.getFromCount(), 
					mAppPreferences.getRegionId(), mAppPreferences.getUserid(),"10001"),10001);
		}
		@Override
		protected void onPostExecute(Boolean result) {
			//Log.d(TAG,"resulkt-->"+result);
			mPullToRefreshListView.onRefreshComplete();

			if(result) {
				try {
					if(adapter != null) {
						adapter.notifyDataSetChanged();
						PauseOnScrollListener listener = new PauseOnScrollListener(MyApplication.imageLoader, true, true);
						stag_listview.setOnScrollListener(listener);
					}
				} catch(Exception e){}
			} else {
				mPullToRefreshListView.onRefreshComplete();
			}
		}
	}
	//pull listview down details
	private class LoadProductsTask extends AsyncTask<String, String, Boolean> {

		String fromCount = "0";
		@Override
		protected void onPreExecute() {

		}
		@Override
		protected Boolean doInBackground(String... params) {
			if(mAppPreferences.getUserid().equals("")){
				mAppPreferences.saveUserid("0");
			}
			//taking product id for checking latest updates
			if(productList.size() != 0){
				fromCount = productList.get(0).getProduct_id();
			}
			return getProdcutsResponse(AppUrls.getProductsUrl(Globals.PAGE_COUNT_VALUE, fromCount, 
					mAppPreferences.getRegionId(), mAppPreferences.getUserid(),"10002"),10002);
		}
		@Override
		protected void onPostExecute(Boolean result) {
			mPullToRefreshListView.onRefreshComplete();

			stag_listview.setVisibility(View.VISIBLE);
			customProgressBar1.setVisibility(View.GONE);

			if(result) {
				try {
					if(!productList.isEmpty()) {
						adapter = new ProductsListAdapter(StoreLoveActivity.this, productList,mAppPreferences);
						stag_listview.setAdapter(adapter);

						PauseOnScrollListener listener = new PauseOnScrollListener(MyApplication.imageLoader, true, true);
						stag_listview.setOnScrollListener(listener);
					} else {
						if(adapter != null) {
							adapter.notifyDataSetChanged();						
						}
					}
				} catch(Exception e){}
			} else {
				Toast.makeText(StoreLoveActivity.this, "No Products to display", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/** parsing data and saving in local arraylist
	 * @param url URL of the products
	 * @param 10001 for pulldown and 10002 for pull up side*/
	private boolean getProdcutsResponse(String url,int isfromUpside){

		Log.e(TAG," url-->"+ url);
		mProdcutModel = new ProdcutModel();

		//Declaring the JSON object
		JSONObject jsonObject;
		//Declaring the URL connection object
		URLConnection tc1;

		try{

			URL tableConnect = new URL(url);
			tc1 = tableConnect.openConnection(); 
			tc1.setReadTimeout(Globals.readTimeOut);
			tc1.setConnectTimeout(Globals.readTimeOut);
			BufferedReader input = new BufferedReader(new InputStreamReader(tc1.getInputStream()));
			String line2;
			String mydata = "";
			//reading the content of the result
			while ((line2 = input.readLine()) != null) {
				//Gets all the characters and adding them into myData string
				mydata = mydata + line2;
			}
			if(mydata.length() != 0) {

				jsonObject = new JSONObject(mydata);	
				//JSONObject subcat_object = jsonObject.getJSONObject("GET_PRODUCTPageResult");
				JSONArray productArrayObject = jsonObject.getJSONArray("GetFeaturedProductsResult");
				int length = productArrayObject.length();

				Log.e("TAG","getProdcutsResponse mydata-->"+length);
				if(length != 0) {
					try {
						for (int i = 0; i <length; i++) {
							//image_width  = Integer.parseInt(sub_mainArray.getJSONObject(i).getString("Image1_Width"));
							//image_height  = Integer.parseInt(sub_mainArray.getJSONObject(i).getString("Image1_Height"));
							mProdcutModel.product_like_count  = productArrayObject.getJSONObject(i).getInt("ProductLikes");
							mProdcutModel.product_desc = productArrayObject.getJSONObject(i).getString("Product_Description");
							mProdcutModel.product_id  = productArrayObject.getJSONObject(i).getString("Product_Id");
							mProdcutModel.product_url = productArrayObject.getJSONObject(i).getString("Product_Image1");
							mProdcutModel.prodcut_name  = productArrayObject.getJSONObject(i).getString("Product_Name");
							mProdcutModel.product_price  = productArrayObject.getJSONObject(i).getString("Product_Price");
							mProdcutModel.store_name  = productArrayObject.getJSONObject(i).getString("Store_Name");
							mProdcutModel.device_like_status = productArrayObject.getJSONObject(i).getInt("UserLike");

							if(isfromUpside == 10001) {
								productList.add(mProdcutModel.addProdcuts(mProdcutModel));
							} else {
								//saving values at the top of the list
								productList.add(i,mProdcutModel.addProdcuts(mProdcutModel));
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					return false;
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	//TODO =========== share app link ===============
	/**social network share app link*/
	private void shareDetails() {

		List<ApplicationInfo> packages = getPackageManager().getInstalledApplications(PackageManager.MATCH_DEFAULT_ONLY);
		Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("text/plain");
		for (ApplicationInfo appInfo : packages) {

			//Log.v(TAG,"appInfo-->"+appInfo.packageName);
			if ("com.facebook.katana".equals(appInfo.packageName)){

				//Log.e(TAG,"Facebook");
				intent.putExtra(Intent.EXTRA_TEXT,  Globals.appLink);
				intent.putExtra(Intent.EXTRA_TITLE, getResources().getString(R.string.app_name));
			}
			else if ("com.twitter.android".equals(appInfo.packageName)) {

				//Log.e(TAG,"Twitter");
				intent.putExtra(Intent.EXTRA_TEXT,   Globals.appLink);
				intent.putExtra(Intent.EXTRA_SUBJECT,   getResources().getString(R.string.app_name));
			} 
			else if ("com.android.email".equals(appInfo.packageName)) {				

				//Log.e(TAG,"Mail");
				//Log.v(TAG,"appInfo-->"+appInfo.packageName);
				intent.putExtra(Intent.EXTRA_TEXT,    Globals.appLink);
				intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
			}
		}
		startActivity(Intent.createChooser(intent, "Choice App to send email:"));
	}

	//TODO
	@Override
	public void onBackPressed() {

		//closing slide menu 
		final int drawerState = mMenuDrawer.getDrawerState();
		if (drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING) {
			mMenuDrawer.closeMenu();
			return;
		} else {
			//cloasing active framents
			FragmentManager fm = getSupportFragmentManager();
			if (fm.getBackStackEntryCount() > 1) {

				Log.i("MainActivity", "popping backstack::"+fm.getBackStackEntryCount());
				fm.popBackStack();

				//if we dnt have any actvie fragments, then opening home feed
			} else if(prodcut_rellayout.getVisibility() != View.VISIBLE) {

				getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
				prodcut_rellayout.setVisibility(View.VISIBLE);
				fragment_container.setVisibility(View.GONE);

			}  else { //if main is in actvie state giving alert to user .
				// final exit of application
				AlertDialog.Builder builder = new AlertDialog.Builder(StoreLoveActivity.this);
				builder.setTitle(getResources().getString(R.string.app_name));
				builder.setMessage("Are you sure you want to quit the application?");
				builder.setPositiveButton("Quit",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						finish();
						dialog.dismiss();
					}
				});
				builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
		}
	}

	@Override
	protected void onDestroy() {
		mMixpanel.flush();
		super.onDestroy();
	}

	//using this for changing products when regoin changed
	private class LoadOnRegionChangeProductsTask extends AsyncTask<String, String, Boolean> {

		String fromCount = "0";
		@Override
		protected void onPreExecute() {
			stag_listview.setVisibility(View.GONE);
			customProgressBar1.setVisibility(View.VISIBLE);
		}
		@Override
		protected Boolean doInBackground(String... params) {
			if(mAppPreferences.getUserid().equals("")){
				mAppPreferences.saveUserid("0");
			}
			//taking product id for checking latest updates
			if(productList.size() != 0){
				fromCount = productList.get(0).getProduct_id();
			}
			return getProdcutsResponse(AppUrls.getProductsUrl(Globals.PAGE_COUNT_VALUE, fromCount, 
					mAppPreferences.getRegionId(), mAppPreferences.getUserid(),"10002"),10002);
		}
		@Override
		protected void onPostExecute(Boolean result) {
			mPullToRefreshListView.onRefreshComplete();

			stag_listview.setVisibility(View.VISIBLE);
			customProgressBar1.setVisibility(View.GONE);

			if(result) {
				if(!productList.isEmpty()) {
					adapter = new ProductsListAdapter(StoreLoveActivity.this, productList,mAppPreferences);
					stag_listview.setAdapter(adapter);

					PauseOnScrollListener listener = new PauseOnScrollListener(MyApplication.imageLoader, true, true);
					stag_listview.setOnScrollListener(listener);
				} else {
					mPullToRefreshListView.setAdapter(null);
					mPullToRefreshListView.setVisibility(View.VISIBLE);
					customProgressBar1.setVisibility(View.GONE);
				}
			} else {
				productList.clear();
				stag_listview.setAdapter(null);
				stag_listview.setVisibility(View.VISIBLE);
				customProgressBar1.setVisibility(View.GONE);
				Toast.makeText(StoreLoveActivity.this, "No Products to display", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
