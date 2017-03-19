package com.apppubs.d20.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.apppubs.d20.fragment.ChannelFragment;
import com.apppubs.d20.util.LogM;

public class NewsFragmentPagerAdapter extends FragmentStatePagerAdapter{
	private List<ChannelFragment> mlist;
	public NewsFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int pos) {
		return mlist.get(pos);
	}
	@Override
	public int getItemPosition(Object object) {
		
		return POSITION_NONE;
	}
	@Override
	public int getCount() {
//		Log.v("newsFragment","getCount"+mFrgList.size()+this);
		return mlist.size();
	}
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		super.destroyItem(container, position, object);
		LogM.log(this.getClass(),"销毁fragment："+position);
		
	}
	
	public void setData(List<ChannelFragment> list){
		this.mlist = list;
	}
	
	
}