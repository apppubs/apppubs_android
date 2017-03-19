package com.apppubs.d20.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apppubs.d20.adapter.LefZhutiAdapter;
import com.apppubs.d20.widget.commonlist.CommonListView;
import com.apppubs.d20.R;

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
