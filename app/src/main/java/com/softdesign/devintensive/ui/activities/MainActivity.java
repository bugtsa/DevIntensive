package com.softdesign.devintensive.ui.activities;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.jar.Manifest;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

import static java.util.jar.Manifest.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = ConstantManager.TAG_PREFIX + MainActivity.class.getSimpleName();

    @BindView(R.id.navigation_drawer)
    DrawerLayout mNavigationDrawer;
    @BindView(R.id.main_coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.profile_placeholder)
    RelativeLayout mProfilePlaceHolder;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.appbar_layout)
    AppBarLayout mAppBarLayout;

    @BindViews({ R.id.phone_et, R.id.email_et, R.id.vk_profile_et, R.id.repo_et, R.id.about_me_et})
    List<EditText> mUserInfoViews;

    @BindViews({R.id.call_phone_iv, R.id.send_email_iv, R.id.show_vk_iv, R.id.show_git_iv, R.id.user_avatar_iv, R.id.user_profile_iv})
    List<ImageView> mUserInfoImages;

    private DataManager mDataManager;

    private AppBarLayout.LayoutParams mAppBarParams = null;

    private int mCurrentEditMode = 0;

    private File mPhotoFile = null;

    private Uri mSelectedImage = null;

    /**
     * Обрабатывает событие onCreate жизненного цикла Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        LogUtils.d(TAG, "onCreate");

        mDataManager = DataManager.getInstance();

        mFab.setOnClickListener(this);
        mProfilePlaceHolder.setOnClickListener(this);

        mUserInfoImages.get(ConstantManager.USER_PHONE_INDEX_IMAGE_VIEW).setOnClickListener(this);
        mUserInfoImages.get(ConstantManager.USER_EMAIL_INDEX_IMAGE_VIEW).setOnClickListener(this);
        mUserInfoImages.get(ConstantManager.USER_VK_INDEX_IMAGE_VIEW).setOnClickListener(this);
        mUserInfoImages.get(ConstantManager.USER_GIT_INDEX_IMAGE_VIEW).setOnClickListener(this);
        mUserInfoImages.get(ConstantManager.USER_AVATAR_INDEX_IMAGE_VIEW).setImageBitmap(RoundedAvatarDrawable.getRoundedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.user_avatar)));

        setupToolBar();
        setupDrawer();
        if (!isLoadUserInfoValue()) {
            initUserInfoValue();
        }
        loadUserInfoValue();
        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserPhoto())
                .placeholder(R.drawable.user_profile)
                .into(mUserInfoImages.get(ConstantManager.USER_PROFILE_INDEX_IMAGE_VIEW));

        if (savedInstanceState == null) {

        } else {
            mCurrentEditMode = savedInstanceState.getInt(ConstantManager.EDIT_MODE_KEY, 0);
            changeEditMode(mCurrentEditMode);
        }
    }

    /**
     * Обрабатывает событие onOptionsItemSelected(выбор пункта меню из NavigationDrawer)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Обрабатывает событие onBackPressed(нажатие системной клавиши back)
     */
    @Override
    public void onBackPressed() {
        if (mNavigationDrawer.isDrawerOpen(GravityCompat.START)) {
            mNavigationDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Обрабатывает событие onClick
     */
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
            case R.id.profile_placeholder:
                showDialog(ConstantManager.LOAD_PROFILE_PHOTO);
                break;
            case R.id.call_phone_iv:
                callByPhoneNumber(mUserInfoViews.get(ConstantManager.USER_PHONE_INDEX_EDIT_TEXT).getText().toString());
                break;
            case R.id.send_email_iv:
                String [] addresses = {mUserInfoViews.get(ConstantManager.USER_EMAIL_INDEX_EDIT_TEXT).getText().toString()};
                sendEmail(addresses);
                break;
            case R.id.show_vk_iv:
                browseLink(mUserInfoViews.get(ConstantManager.USER_VK_INDEX_EDIT_TEXT).getText().toString());
                break;
            case R.id.show_git_iv:
                browseLink(mUserInfoViews.get(ConstantManager.USER_GIT_INDEX_EDIT_TEXT).getText().toString());
                break;
        }
    }

    /**
     * Обрабатывает событие onStart жизненного цикла Activity
     */
    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.d(TAG, "onStart");
    }

    /**
     * Обрабатывает событие onResume жизненного цикла Activity
     */
    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.d(TAG, "onResume");
    }

    /**
     * Обрабатывает событие onPause жизненного цикла Activity
     */
    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.d(TAG, "onPause");
        saveUserInfoValue();
    }

    /**
     * Обрабатывает событие onStop жизненного цикла Activity
     */
    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.d(TAG, "onStop");
    }

    /**
     * Обрабатывает событие onDestroy жизненного цикла Activity
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d(TAG, "onDestroy");
    }

    /**
     * получает результат из другой Activity (фото из камеры или из галлереи)
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ConstantManager.REQUEST_GALLERY_PICTURE:
                if(resultCode == RESULT_OK && data != null) {
                    mSelectedImage = data.getData();

                    insertProfileImage(mSelectedImage);
                }
                break;
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                if(resultCode ==RESULT_OK && mPhotoFile != null) {
                    mSelectedImage = Uri.fromFile(mPhotoFile);

                    insertProfileImage(mSelectedImage);
                }
        }
    }

    /**
     * Сохраняет состояние приложения
     * @param outState Bundle переменная
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        LogUtils.d(TAG, "onSaveInstanceState");
        outState.putInt(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);
    }

    /**
     * инициализирует ToolBar
     */
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

    /**
     * инициализирует NavigationDrawer
     */
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
            mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);
            setFocusForInputAtPhone(mUserInfoViews.get(ConstantManager.USER_PHONE_INDEX_EDIT_TEXT));
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
            mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white));
        }
    }

    /**
     * Певодит фокус на редактирование
     * @param field редактируемое поле
     */
    private void setFocusForInputAtPhone(final EditText field) {
        field.post(new Runnable() {
            @Override
            public void run() {
                field.requestFocus();
                field.onKeyUp(KeyEvent.KEYCODE_DPAD_CENTER, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_CENTER));
            }
        });
    }

    /**
     * Инициализирует данные пользователя из ресурсов приложения
     */
    private void initUserInfoValue() {
        List<String> userData = new ArrayList<>();
        userData.add(getResources().getString(R.string.phone_number_body));
        userData.add(getResources().getString(R.string.email_address_body));
        userData.add(getResources().getString(R.string.vk_profile_body));
        userData.add(getResources().getString(R.string.repo_body));
        userData.add(getResources().getString(R.string.about_me_body));
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

    /**
     * Проверяет, загруженны ли данные пользователя из SharedPreferences
     */
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

    /**
     * Загружает данные пользователя из SharedPreferences
     */
    private void loadUserInfoValue() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int index = 0; index < userData.size(); index++) {
            mUserInfoViews.get(index).setText(userData.get(index));
        }
    }

    /**
     * Сохраняет данные пользователя в SharedPreferences
     */
    private void saveUserInfoValue() {
        List<String> userData = new ArrayList<>();
        for (EditText userFieldView : mUserInfoViews) {
            userData.add(userFieldView.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

    /**
     * Создаёт intent загрузки фотографии из галлереи
     */
    private void loadPhotoFromGallery() {
        Intent takeGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        takeGalleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(takeGalleryIntent, getString(R.string.chose_gallery_photo)), ConstantManager.REQUEST_GALLERY_PICTURE);

    }

    /**
     * Создаёт intent загрузки фотографии с камеры
     */
    private void loadPhotoFromCamera() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent takeCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                mPhotoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (mPhotoFile != null) {
                takeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(takeCaptureIntent, ConstantManager.REQUEST_CAMERA_PICTURE);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, ConstantManager.CAMERA_REQUEST_PERMISSION_CODE);
            showGiveAllowPermissionSnackBar();
        }
    }

    /**
     * Показывает Snackbar, пересылающий на изменение настроек разрешений
     */
    private void showGiveAllowPermissionSnackBar() {
        Snackbar.make(mCoordinatorLayout, R.string.give_permission_message, Snackbar.LENGTH_LONG)
                .setAction(R.string.allow_message, new View.OnClickListener() {
                    @Override
                    public void onClick(View  v) {
                        openApplicationSettings();
                    }
                }).show();
    }

    /**
     * Создаёт intent звонка
     * @param phoneNumber номер телефона собеседника
     */
    private void callByPhoneNumber(String phoneNumber) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
            startActivity(dialIntent);
        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                    android.Manifest.permission.CALL_PHONE
            }, ConstantManager.CALL_PHONE_REQUEST_PERMISSION_CODE);
            showGiveAllowPermissionSnackBar();
        }
    }

    /**
     * Создаёт intent отправки сообщения
     * @param addresses e-mail адрес получателя
     */
    public void sendEmail(String[] addresses) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void browseLink(String link) {
        Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + link));
        startActivity(browseIntent);
    }

    /**
     * Проверяет полученные разрешения
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ConstantManager.CAMERA_REQUEST_PERMISSION_CODE && grantResults.length >= 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                int ii = 0;
            }
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                int ii = 0;
            }
        }

        if (requestCode == ConstantManager.CALL_PHONE_REQUEST_PERMISSION_CODE && grantResults.length >= 1) {
            int ii = 0;
        }
    }

    /**
     * Убирает с поля видимости PlaceHolder (поле изменения изображения профиля пользователя)
     */
    private void hideProfilePlaceHolder() {
        mProfilePlaceHolder.setVisibility(View.GONE);
    }

    /**
     * Отображает в приложении PlaceHolder
     */
    private void showProfilePlaceHolder() {
        mProfilePlaceHolder.setVisibility(View.VISIBLE);
    }

    /**
     * Блокирует Toolbar
     */
    private void lockToolbar() {
        mAppBarLayout.setExpanded(true, true);
        mAppBarParams.setScrollFlags(0);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    /**
     * Разблокирует Toolbar
     */
    private void unLockToolbar() {
        mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    /**
     * Создает диалог
     * @param id идентификатор создаваемого диалога
     * @return диалог
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case ConstantManager.LOAD_PROFILE_PHOTO:
                String[] selectItems = {getString(R.string.user_profile_dialog_gallery), getString(R.string.user_profile_dialog_camera), getString(R.string.user_profile_dialog_cancel)};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.user_profile_placeholder_image_title));
                builder.setItems(selectItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choiceItem) {
                        switch (choiceItem) {
                            case 0:
                                //загрузить из галлереи
                                loadPhotoFromGallery();
                                break;
                            case 1:
                                //загрузить из камеры
                                loadPhotoFromCamera();
                                break;
                            case 2:
                                //отмена
                                dialog.cancel();
                                break;
                        }
                    }
                });
                return builder.create();
            default:
                return null;
        }
    }

    /**
     * Создает изображение в памяти телефона и помещает его в галерею
     * @return файл изображения
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, image.getAbsolutePath());

        this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        return image;
    }

    /**
     * Добавляет изображение профиля пользователя и сохраняет его в SharedPreferences
     * @param selectedImage uri выбранного изображения
     */
    private void insertProfileImage(Uri selectedImage) {
        Picasso.with(this)
                .load(selectedImage)
                .into(mUserInfoImages.get(ConstantManager.USER_PROFILE_INDEX_IMAGE_VIEW));
        mDataManager.getPreferencesManager().saveUserPhoto(selectedImage);
    }

    /**
     * Открывает настройки приложения
     */
    public void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));

        startActivityForResult(appSettingsIntent, ConstantManager.PERMISSION_REQUEST_SETTINGS_CODE);
    }


}
