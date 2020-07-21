package com.social.videofilter.base;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.social.R;
import com.social.videofilter.helper.ToolbarHelper;

import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @author LLhon
 * @Project Android-Video-Editor
 * @Package com.marvhong.videoeditor.base
 * @Date 2018/8/23 11:01
 * @description
 */
public abstract class BaseActivity extends AppCompatActivity {

    private CompositeDisposable mDisposables = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            //默认不显示原生标题
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            initToolbar(new ToolbarHelper(toolbar, getSupportActionBar()));
        }

        initView();
        initData();
    }

    protected void init() {

    }

    protected abstract int getLayoutId();

    protected void initToolbar(ToolbarHelper toolbarHelper) {

    }

    protected void initView() {

    }

    protected void initData() {

    }

    protected void setupToolbar(Toolbar toolBar, String title) {
        toolBar.setTitle(title);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.this.onBackPressed();
            }
        });
    }

    protected void addFragment(int containerViewId, Fragment fragment) {
        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        transaction.add(containerViewId, fragment);
        transaction.commit();
    }

    public void subscribe(Disposable disposable) {
        mDisposables.add(disposable);
    }

    public void unsubscribe() {
        if (mDisposables != null && !mDisposables.isDisposed()) {
            mDisposables.dispose();
            mDisposables.clear();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                FragmentManager fm = getSupportFragmentManager();
                if (fm != null && fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } else {
                    onBackPressed();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unsubscribe();
    }
}
