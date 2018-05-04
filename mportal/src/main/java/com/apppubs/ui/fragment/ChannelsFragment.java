package com.apppubs.ui.fragment;

import java.util.List;

import android.os.Bundle;

import com.apppubs.bean.TMenuItem;
import com.apppubs.bean.TNewsChannel;
import com.apppubs.model.NewsBiz;
import com.orm.SugarRecord;
/**
 * 资讯列表容器,需要传入频道所属的类别
 * @author Administrator
 *
 */
public class ChannelsFragment extends TitleMenuFragment{
	
	public static final String ARGUMENT_NAME_CHANNELTYPEID = "channel_type_id";
	
	protected String mChannelTypeId;//频道所属类别id
	/**
	 * 本类型下所有的频道信息
	 */
	protected List<TNewsChannel> mChannelList;
	/**
	 * 本类型下已选择的频道信息
	 */
	protected List<TNewsChannel> mChannelSelectedList;
	
	protected NewsBiz mNb;
	
	//是否允许更改频道
	protected boolean mAllowConfig = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		Bundle args = getArguments();
		mChannelTypeId = args.getString(ARGUMENT_NAME_CHANNELTYPEID);
		mNb = NewsBiz.getInstance(mContext);
		mChannelList = SugarRecord.find(TNewsChannel.class,  "TYPE_ID=?", new String[]{mChannelTypeId+""}, null, "DISPLAY_ORDER", null);
		mChannelSelectedList = SugarRecord.find(TNewsChannel.class,  "TYPE_ID=? and DISPLAY_ORDER != 0", new String[]{mChannelTypeId+""}, null, "DISPLAY_ORDER", null);
//		mAllowConfig = SugarRecord.findByProperty(TMenuItem.class, "CHANNEL_TYPE_ID", mChannelTypeId+"").getAllowConfigFlag()==0?false:true;
	}
	
	protected void refreshSelectedList() {
		mChannelSelectedList = SugarRecord.find(TNewsChannel.class,  "TYPE_ID=? and DISPLAY_ORDER != 0", new String[]{mChannelTypeId+""}, null, "DISPLAY_ORDER", null);
	}
	protected void refreshChannelList () {
		mChannelList = SugarRecord.find(TNewsChannel.class,  "TYPE_ID=?", new String[]{mChannelTypeId+""}, null, "DISPLAY_ORDER", null);
	}
	

	
	
	
}
