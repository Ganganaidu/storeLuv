package com.thisisswitch.storelove.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.thisisswitch.storelove.fragments.ProductDetailsFragment;
import com.thisisswitch.storelove.models.ProdcutModel;

public class ProductsDetailsViewPageAdapter  extends FragmentPagerAdapter {

	private ArrayList<String> productUrlsList;
	private ProdcutModel mProdcutModel;
	
	public ProductsDetailsViewPageAdapter(FragmentManager fm,ArrayList<String> productUrlsList,ProdcutModel mProdcutModel) {
		super(fm);
		this.productUrlsList = productUrlsList;
		this.mProdcutModel = mProdcutModel;
	}

	@Override
	public Fragment getItem(int position) {
		return ProductDetailsFragment.newInstance(position,productUrlsList,mProdcutModel);
	}

	@Override
	public int getCount() {
		return productUrlsList.size();
	}
	
//	@Override
//	public void destroyItem(ViewGroup container, int position, Object object) {
//		if (position >= getCount()) {
//			FragmentManager manager = ((Fragment) object).getFragmentManager();
//			FragmentTransaction trans = manager.beginTransaction();
//			trans.remove((Fragment) object);
//			trans.commit();
//		}
//	}
}
