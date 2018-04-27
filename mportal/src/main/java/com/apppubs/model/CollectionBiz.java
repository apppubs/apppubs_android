package com.apppubs.model;

import java.util.Date;

import android.content.Context;

import com.apppubs.bean.Collection;
import com.orm.SugarRecord;

public class CollectionBiz extends BaseBiz {

	private static CollectionBiz sCollectionBiz;

	public CollectionBiz(Context context) {
		super(context);
	}

	public static CollectionBiz getInstance(Context context) {

		if (sCollectionBiz == null) {
			synchronized (CollectionBiz.class) {
				if(sCollectionBiz ==null){
					sCollectionBiz = new CollectionBiz(context);
				}
			}
			
		}

		return sCollectionBiz;
	}
	
	

	// 收藏操作
	public static void toggleCollect(int type, Context context, boolean isCollected, String mInfoId, String title, String summy) {
		/**
		 * 收藏开关 
		 */
		if (!isCollected) {
			Collection c = new Collection();
			c.setAddTime(new Date());
			c.setInfoId(mInfoId);
			c.setTitle(title);
			c.setContentAbs(summy);
			c.setType(type);
			c.save();
		} else {
			SugarRecord.deleteAll(Collection.class, "INFO_ID=?", mInfoId);
		}
	}
	
	
	
}
