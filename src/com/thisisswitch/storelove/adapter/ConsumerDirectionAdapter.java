package com.thisisswitch.storelove.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.switchsoft.maplibrary.model.DirectionModel;
import com.thisisswitch.storelove.R;

public class ConsumerDirectionAdapter extends BaseAdapter {

	private LayoutInflater inflate;
	private DirectionModel mDirectionModel;
	//private int endPosition = 0;

	public ConsumerDirectionAdapter(Context context, DirectionModel directionModel) {
		inflate = LayoutInflater.from(context);
		this.mDirectionModel = directionModel;
		//endPosition = mDirectionModel.getinstructionsArray().size() -1;
	}

	//returns the size of the array list
	public int getCount() {
		return mDirectionModel.getinstructionsArray().size();
	}

	public Object getItem(int arg0) {
		return mDirectionModel.getinstructionsArray().get(arg0);
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// A ViewHolder keeps references to children views to avoid unnecessary calls
		// to findViewById() on each row.
		ViewHolder holder = null;

		// When convertView is not null, we can reuse it directly, there is no need
		// to re inflate it. We only inflate a new View when the convertView supplied
		// by ListView is null.
		if(convertView == null){
			convertView = inflate.inflate(R.layout.row_map_direction, null); 

			holder = new ViewHolder();

			//holder.mDirection_Imageview = (ImageView)convertView.findViewById(R.id.direction_imageview);
			holder.mDirection_Textview = (TextView)convertView.findViewById(R.id.direction_textview);
			holder.mDistance_Textview = (TextView)convertView.findViewById(R.id.distance_textview);

			convertView.setTag(holder);	
		}
		else{
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder=(ViewHolder)convertView.getTag();
		}

		//Setting the data
		holder.mDirection_Textview.setText(Html.fromHtml(mDirectionModel.getinstructionsArray().get(position)));
		holder.mDistance_Textview.setText(mDirectionModel.getdistanceArray().get(position));
		
		/*if(position == 0){
			holder.mDirection_Imageview.setImageResource(R.drawable.ic_resume);
		} else if(position == endPosition){
			holder.mDirection_Imageview.setImageResource(R.drawable.da_turn_arrive);
		} else {
			holder.mDirection_Imageview.setImageResource(0);
		}*/

		return convertView;	
	}

	//Holds the views
	private class ViewHolder{
		//ImageView mDirection_Imageview;
		TextView mDirection_Textview, mDistance_Textview;

	}

}
