package com.apppubs.d20.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.apppubs.d20.util.LogM;
import com.apppubs.d20.fragment.BaseFragment;

public class PageFragmentPagerAdapter extends FragmentStatePagerAdapter {
	private List<BaseFragment> mlist;

	public PageFragmentPagerAdapter(FragmentManager fm) {
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
		return mlist.size();
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		super.destroyItem(container, position, object);
		LogM.log(this.getClass(), "销毁fragment：" + position);

	}

	public void setData(List<BaseFragment> list) {
		this.mlist = list;
	}

}
