package com.apppubs.ui.news;

/**
 * Channel fragment 构造工厂
 * 
 * Copyright (c) heaven Inc.
 *
 * Original Author: zhangwen
 *
 * ChangeLog:
 * 2015年1月25日 by zhangwen create
 *
 */
public class ChannelFragmentFactory{
	
	public static final int TYPE_CHANNEL_HEADLINE = 1;//头条类型的新闻fragment
	public static final int CHANNEL_TYPE_SPECIALS = 2;
	public static final int CHANNEL_TYPE_PIC = 3;
	public static final int CHANNEL_TYPE_AUDIO = 4;
	public static final int CHANNEL_TYPE_VIDEO = 5;
	public static final int CHANNEL_TYPE_WEBPAGE = 6;
	
	
	
	public static ChannelFragment getChannelFragment(int channelType){
		switch (channelType) {
		case TYPE_CHANNEL_HEADLINE:
			return new ChannelDefaultFragment();
		case CHANNEL_TYPE_PIC:
			return new ChannelPictureFragment();
		case CHANNEL_TYPE_VIDEO:
			return new ChannelVideoFragment();
		case CHANNEL_TYPE_AUDIO:
			return new ChannelAudioFragment();
		case CHANNEL_TYPE_WEBPAGE:
			return new ChannelWebAppFragment();
		case CHANNEL_TYPE_SPECIALS:
			return new ChannelSpecialsFragment();
		default:
			return new ChannelDefaultFragment();
		}
	}
}
