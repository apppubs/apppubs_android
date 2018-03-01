package com.jelly.mango;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.jelly.mango.adapter.ViewPageAdapter;
import com.jelly.mango.presenter.ImageBrowsePresenter;
import com.jelly.mango.progressview.TextImageButton;
import com.jelly.mango.view.ImageBrowseView;

import java.util.List;

/**
 * Created by Jelly on 2016/9/3.
 */
public class ImageBrowseActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener,View.OnClickListener,ImageBrowseView {

    private static final String TAG = ImageBrowseActivity.class.getName();

    private ViewPager vp;
    private TextView hint;
    private TextImageButton save;
    private ViewPageAdapter adapter;
    ImageBrowsePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_image_browse);
        initView();
        loadPresenter();
        presenter.loadImage();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

    /**
     * init view
     */
    public void initView(){
        vp = (ViewPager) this.findViewById(R.id.viewPager);
        hint = (TextView) this.findViewById(R.id.hint);
        save = (TextImageButton) this.findViewById(R.id.save);
        save.setOnClickListener(this);
    }

    public void loadPresenter(){
        presenter = new ImageBrowsePresenter(this);
    }

    @Override
    public Intent getDataIntent() {
        return getIntent();
    }

    @Override
    public Context getMyContext() {
        return this;
    }

    @Override
    public int getPosition() {
        return adapter.getPosition();
    }

    @Override
    public void setImageBrowse(List<MultiplexImage> images,int position) {

        if(adapter == null && images != null && images.size() != 0){
            adapter = new ViewPageAdapter(this,images);
            vp.setAdapter(adapter);
            vp.setCurrentItem(position);
            vp.addOnPageChangeListener(this);
            hint.setText(position + 1 + "/" + images.size());
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        
    }


    @Override
    public void onPageSelected(int position) {
        adapter.setPosition(position);
        hint.setText(position + 1 + "/" + presenter.getImages().size());
        if(Mango.imageSelectListener != null){
            Mango.imageSelectListener.select(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if(state == 0 && adapter.getPrePosition() != vp.getCurrentItem()){
            adapter.updatePhotoView(adapter.getPrePosition());
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.save){
            presenter.saveImage();
        }

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Mango.imageSelectListener = null;
    }
}
