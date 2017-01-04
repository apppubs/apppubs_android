package com.mportal.lame;

public class LameUtils {
	static {

		System.loadLibrary("mp3lame");

	}
	
	public native String getVersion();

	public native void convert(String input, String output);
}
