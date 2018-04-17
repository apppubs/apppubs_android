package com.apppubs.asytask;

public interface  AsyTaskCallback {

	Object onExecute (Integer tag,String[] params) throws Exception; 
	void onTaskSuccess(Integer tag,Object obj);
	void onTaskFail(Integer tag,Exception e);
}
