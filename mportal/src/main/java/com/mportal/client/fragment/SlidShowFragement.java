package com.mportal.client.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mportal.client.widget.DateTime;

public class SlidShowFragement extends HomeFragment{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		DateTime date=new DateTime(getActivity());
		View v=date;
		return v;
	}
}
