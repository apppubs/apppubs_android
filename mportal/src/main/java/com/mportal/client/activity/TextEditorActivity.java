package com.mportal.client.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.mportal.client.R;

/**
 * Created by zhangwen on 2017/2/24.
 */

public class TextEditorActivity extends BaseActivity {

    private TextEditorListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.act_text_editor);
    }

    public static  void startActivity(Context context,TextEditorListener listener){
        Intent intent = new Intent(context,TextEditorListener.class);

    }

    public interface TextEditorListener{
        boolean shouldComplete(String text);
    }

}
