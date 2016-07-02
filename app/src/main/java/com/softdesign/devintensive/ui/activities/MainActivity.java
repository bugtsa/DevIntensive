package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.LogUtils;
import com.softdesign.devintensive.utils.RoundedAvatarDrawable;
import com.softdesign.devintensive.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = ConstantManager.TAG_PREFIX + MainActivity.class.getSimpleName();

    private CoordinatorLayout mCoordinatorLayout;

    private DataManager mDataManager;

    private EditText mUserPhone, mUserEmail, mUserVK, mUserGit, mUserBio;

    private List<EditText> mUserInfoViews;

    private Toolbar mToolbar;

    private DrawerLayout mNavigationDrawer;

    private FloatingActionButton mFab;

    private ImageView mUserAvatar;

    private RelativeLayout mProfilePlaceHolder;

    private CollapsingToolbarLayout mCollapsingToolbar;

    private AppBarLayout mAppBarLayout;

    private AppBarLayout.LayoutParams mAppBarParams = null;

    private int mCurrentEditMode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogUtils.d(TAG, "onCreate");

        mDataManager = DataManager.getInstance();

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mNavigationDrawer = (DrawerLayout) findViewById(R.id.navigation_drawer);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mProfilePlaceHolder = (RelativeLayout) findViewById(R.id.profile_placeholder);
        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);

        mUserAvatar = (ImageView) findViewById(R.id.user_avatar);
        mUserAvatar.setImageBitmap(RoundedAvatarDrawable.getRoundedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.user_avatar)));

        mUserPhone = (EditText) findViewById(R.id.phone_et);
        mUserEmail = (EditText) findViewById(R.id.email_et);
        mUserVK = (EditText) findViewById(R.id.vk_profile_et);
        mUserGit = (EditText) findViewById(R.id.repo_et);
        mUserBio = (EditText) findViewById(R.id.about_me_et);

        mUserInfoViews = new ArrayList<>();
        mUserInfoViews.add(mUserPhone);
        mUserInfoViews.add(mUserEmail);
        mUserInfoViews.add(mUserVK);
        mUserInfoViews.add(mUserGit);
        mUserInfoViews.add(mUserBio);
        if (!isLoadUserInfoValue()) {
            initUserInfoValue();
        }
        loadUserInfoValue();

        mFab.setOnClickListener(this);
        setupToolBar();
        setupDrawer();

        List<String> test = mDataManager.getPreferencesManager().loadUserProfileData();

        if (savedInstanceState == null) {

        } else {
            mCurrentEditMode = savedInstanceState.getInt(ConstantManager.EDIT_MODE_KEY, 0);
            changeEditMode(mCurrentEditMode);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawer.isDrawerOpen(GravityCompat.START)) {
            mNavigationDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (mCurrentEditMode == 0) {
                    changeEditMode(1);
                    mCurrentEditMode = 1;
                } else {
                    changeEditMode(0);
                    mCurrentEditMode = 0;
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.d(TAG, "onPause");
        saveUserInfoValue();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d(TAG, "onDestroy");
    }

    /**
     * получает результат из другой Activity (фото из камеры или из галлереи)
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        LogUtils.d(TAG, "onSaveInstanceState");
        outState.putInt(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);
    }

    private void setupToolBar() {
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();

        mAppBarParams = (AppBarLayout.LayoutParams) mCollapsingToolbar.getLayoutParams();
        if (actionBar != null) {
            actionBar.setTitle(R.string.my_name);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigator_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                ToastUtils.showShortMessage(item.toString(), getApplicationContext());
                item.setCheckable(true);
                mNavigationDrawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    /**
     * переключает режим редактирования
     *
     * @param mode если 1 - режим редактирования, если 0 - режим просмотра
     * @return
     */
    private void changeEditMode(int mode) {
        if (mode == 1) {
            mFab.setImageResource(R.drawable.ic_done_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);
            }
            showProfilePlaceHolder();
            lockToolbar();
        } else {
            mFab.setImageResource(R.drawable.ic_create_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);

                saveUserInfoValue();
            }
            hideProfilePlaceHolder();
            unLockToolbar();
        }
    }

    private void initUserInfoValue() {
        List<String> userData = new ArrayList<>();
        userData.add(getResources().getString(R.string.phone_number_body));
        userData.add(getResources().getString(R.string.email_address_body));
        userData.add(getResources().getString(R.string.vk_profile_body));
        userData.add(getResources().getString(R.string.repo_body));
        userData.add(getResources().getString(R.string.about_me_body));
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

    private boolean isLoadUserInfoValue() {
        boolean isLoadUserInfo = false;
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int index = 0; index < userData.size(); index++) {
           if (!userData.get(index).isEmpty() && !userData.get(index).equals("null")) {
               isLoadUserInfo = true;
           }
        }
        return isLoadUserInfo;
    }

    private void loadUserInfoValue() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int index = 0; index < userData.size(); index++) {
            mUserInfoViews.get(index).setText(userData.get(index));
        }
    }

    private void saveUserInfoValue() {
        List<String> userData = new ArrayList<>();
        for (EditText userFieldView : mUserInfoViews) {
            userData.add(userFieldView.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

    private void loadPhotoFromGallery() {

    }

    private void loadPhotoFromCamera() {

    }

    private void hideProfilePlaceHolder() {
        mProfilePlaceHolder.setVisibility(View.GONE);
    }

    private void showProfilePlaceHolder() {
        mProfilePlaceHolder.setVisibility(View.VISIBLE);
    }

    private void lockToolbar() {
        mAppBarLayout.setExpanded(true, true);
        mAppBarParams.setScrollFlags(0);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    private void unLockToolbar() {
        mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL| AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }
}
