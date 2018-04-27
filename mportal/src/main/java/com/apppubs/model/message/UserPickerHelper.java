package com.apppubs.model.message;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.apppubs.bean.TUser;
import com.apppubs.ui.activity.BaseActivity;
import com.apppubs.ui.message.activity.UserPickerActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwen on 2017/1/9.
 */

public class UserPickerHelper {

    /**
     * 最大讨论组人数
     */
    public static final int MAX_SELECTED_USER_NUM = 500;

    private static UserPickerHelper sUserPickerManager;

    private List<String> mUserIds;
    private List<String> mPreSelectedUserIds;//已经选中的用户
    private Context mContext;

    private UserPickerListener mUserPickerListener;
	private UserPickerMode mode;


    private UserPickerHelper(Context context){
        init(context);
    }

    public enum UserPickerMode{
		USER_PICKER_MODE_SINGLE,USER_PICKER_MODE_MULTI
	}

    private void init(Context context) {
        mUserIds = new ArrayList<String>();
        mPreSelectedUserIds = new ArrayList<String>();
        mContext = context;
    }

    public static synchronized UserPickerHelper getInstance(Context context){
        if (sUserPickerManager==null){
            sUserPickerManager = new UserPickerHelper(context);
        }
        return sUserPickerManager;
    }

    public boolean selectUser(String userId){
        if (!mUserIds.contains(userId)&&!mPreSelectedUserIds.contains(userId)){
            mUserIds.add(userId);
            return true;
        }
        return false;
    }

    public void selectUsers(List<String> userIds){
        for (String userId: userIds){
            selectUser(userId);
        }
    }

    public UserPickerMode getMode(){
		return mode;
	}

	public void setMode(UserPickerMode mode){
		this.mode = mode;
	}
    public void removeUser(String userId){
        mUserIds.remove(userId);
    }

    public void removeUsers(List<String> userIds){
        mUserIds.removeAll(userIds);
    }

    public void cancelSelect(){
        mUserIds.clear();
        mPreSelectedUserIds.clear();
    }

    public boolean isSelected(String userId){
        if (mUserIds.contains(userId)){
            return true;
        }
        return false;
    }

    public List<TUser> getSelectedUsers(){
        return UserBussiness.getInstance(mContext).getUsersByUserIds(mUserIds);
    }

    public List<String> getSelectedUserIds(){
        return mUserIds;
    }

    public void setPreSelectedUserIds(List<String> userIds){
        if (userIds==null){
            return ;
        }
        mPreSelectedUserIds = userIds;
    }

    public boolean isPreselected(String userId){
        if (mPreSelectedUserIds.contains(userId)){
            return true;
        }
        return false;
    }

    public int getRemainSelectionNum(){
        return MAX_SELECTED_USER_NUM - mPreSelectedUserIds.size();
    }

    public static void startActivity(Context context){
        startActivity(context,"选择人员",null,null);
    }

    public static void startActivity(Context context,String title,List<String> preSelectUserIds,UserPickerListener listener){

       startActivity(context,title,preSelectUserIds, UserPickerMode.USER_PICKER_MODE_MULTI,listener);
    }
    public static void startActivity(Context context,String title,List<String> preSelectUserIds,UserPickerMode mode,UserPickerListener listener){

        UserPickerHelper helper = UserPickerHelper.getInstance(context);
        helper.setPreSelectedUserIds(preSelectUserIds);
        helper.setUserPickerListener(listener);
		helper.setMode(mode);

        Intent startIntent = new Intent(context,UserPickerActivity.class);
        if (TextUtils.isEmpty(title)){
            startIntent.putExtra(BaseActivity.EXTRA_STRING_TITLE,"选择人员");
        }else{
            startIntent.putExtra(BaseActivity.EXTRA_STRING_TITLE,title);
        }
        context.startActivity(startIntent);
    }


    public void setUserPickerListener(UserPickerListener listener){
        mUserPickerListener = listener;
    }

    public UserPickerListener getUserPickerListener(){
        return mUserPickerListener;
    }

    public interface UserPickerListener {
        void onPickDone(List<String> userIds);
    }

}
