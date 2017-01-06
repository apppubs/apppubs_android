package com.mportal.client.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mportal.client.R;
import com.mportal.client.adapter.LefZhutiAdapter;
import com.mportal.client.widget.commonlist.CommonListView;

public class LeftFragmentZhuanti extends HomeFragment {
	private CommonListView xlv;
	private LefZhutiAdapter adapter;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.left_zhuti, null);
		xlv=(CommonListView) view.findViewById(R.id.left_zhuti_xlv);
		adapter=new LefZhutiAdapter(getActivity());
		xlv.setAdapter(adapter);
		return view;
	}
    
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
    
}
