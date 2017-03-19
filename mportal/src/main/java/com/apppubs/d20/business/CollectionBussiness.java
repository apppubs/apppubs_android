package com.apppubs.d20.business;

import java.util.Date;

import android.content.Context;

import com.apppubs.d20.bean.Collection;
import com.orm.SugarRecord;

public class CollectionBussiness extends BaseBussiness{

	private static CollectionBussiness sCollectionBussiness;

	private CollectionBussiness() {

	}

	public static CollectionBussiness getInstance() {

		if (sCollectionBussiness == null) {
			synchronized (CollectionBussiness.class) {
				if(sCollectionBussiness==null){
					sCollectionBussiness = new CollectionBussiness();
				}
			}
			
		}

		return sCollectionBussiness;
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
