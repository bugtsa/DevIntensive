package com.softdesign.devintensive.ui.activities;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
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
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.LogUtils;
import com.softdesign.devintensive.utils.RoundedAvatarDrawable;
import com.softdesign.devintensive.utils.RoundedImageTransformation;
import com.softdesign.devintensive.utils.ToastUtils;
import com.softdesign.devintensive.utils.ValidatorUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.jar.Manifest;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = ConstantManager.TAG_PREFIX + MainActivity.class.getSimpleName();

//    private ImageView drawerUsrAvatar;
//    private TextView drawerUserFullName;
//    private TextView drawerUserEmail;

    @BindView(R.id.navigation_drawer)
    DrawerLayout mNavigationDrawer;
    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;
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

    @BindViews({R.id.phone_et, R.id.email_et, R.id.vk_profile_et, R.id.repo_et, R.id.about_me_et})
    List<EditText> mUserFieldsViews;

    @BindViews({R.id.rating_title_tv, R.id.lines_code_title_tv, R.id.project_title_tv})
    List<TextView> mUserValueViews;

    @BindViews({R.id.phone_til, R.id.email_til, R.id.vk_profile_til, R.id.repo_til})
    List<TextInputLayout> mUserInfoTil;

    @BindViews({R.id.call_phone_iv, R.id.send_email_iv, R.id.show_vk_iv, R.id.show_git_iv, R.id.drawer_user_avatar_iv, R.id.user_profile_iv})
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


        mUserFieldsViews.get(ConstantManager.USER_PHONE_INDEX_EDIT_TEXT).addTextChangedListener(new MyTextWatcher(mUserFieldsViews.get(ConstantManager.USER_PHONE_INDEX_EDIT_TEXT), this, getWindow()));
        mUserFieldsViews.get(ConstantManager.USER_EMAIL_INDEX_EDIT_TEXT).addTextChangedListener(new MyTextWatcher(mUserFieldsViews.get(ConstantManager.USER_EMAIL_INDEX_EDIT_TEXT), this, getWindow()));
        mUserFieldsViews.get(ConstantManager.USER_VK_INDEX_EDIT_TEXT).addTextChangedListener(new MyTextWatcher(mUserFieldsViews.get(ConstantManager.USER_VK_INDEX_EDIT_TEXT), this, getWindow()));
        mUserFieldsViews.get(ConstantManager.USER_GIT_INDEX_EDIT_TEXT).addTextChangedListener(new MyTextWatcher(mUserFieldsViews.get(ConstantManager.USER_GIT_INDEX_EDIT_TEXT), this, getWindow()));

        setupToolBar();
        setupDrawer();
//        if (!isLoadUserInfoValue()) {
//            initUserInfoValue();
//        }
//        loadUserInfoValue();
//        Picasso.with(this)
//                .load(mDataManager.getPreferencesManager().loadUserPhoto())
//                .placeholder(R.drawable.user_profile)
//                .into(mUserInfoImages.get(ConstantManager.USER_PROFILE_INDEX_IMAGE_VIEW));

        initUserFields();
        initUserInfoValue();

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
                callByPhoneNumber(mUserFieldsViews.get(ConstantManager.USER_PHONE_INDEX_EDIT_TEXT).getText().toString());
                break;
            case R.id.send_email_iv:
                String[] addresses = {mUserFieldsViews.get(ConstantManager.USER_EMAIL_INDEX_EDIT_TEXT).getText().toString()};
                sendEmail(addresses);
                break;
            case R.id.show_vk_iv:
                browseLink(mUserFieldsViews.get(ConstantManager.USER_VK_INDEX_EDIT_TEXT).getText().toString());
                break;
            case R.id.show_git_iv:
                browseLink(mUserFieldsViews.get(ConstantManager.USER_GIT_INDEX_EDIT_TEXT).getText().toString());
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
//        saveUserInfoValue();
        saveUserFields();
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
                if (resultCode == RESULT_OK && data != null) {
                    mSelectedImage = data.getData();

                    mDataManager.getPreferencesManager().saveUserPhoto(mSelectedImage);
                    insertProfileImage(mSelectedImage);
                }
                break;
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                if (resultCode == RESULT_OK && mPhotoFile != null) {
                    mSelectedImage = Uri.fromFile(mPhotoFile);

                    mDataManager.getPreferencesManager().saveUserPhoto(mSelectedImage);
                    insertProfileImage(mSelectedImage);
                }
        }
    }

    /**
     * Сохраняет состояние приложения
     *
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

        mCollapsingToolbar.setTitle(mDataManager.getPreferencesManager().getUserFullName());
        insertProfileImage(mDataManager.getPreferencesManager().loadUserPhoto());
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

//        drawerUsrAvatar = (ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.drawer_user_avatar_iv);
//        drawerUserFullName = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.drawer_user_name_tv);
//        drawerUserEmail = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.drawer_user_email_tv);

//        drawerUserFullName.setText(mDataManager.getPreferencesManager().getUserFullName());
//        drawerUserEmail.setText(mDataManager.getPreferencesManager().getUserEmail());

        insertDrawerAvatar(mDataManager.getPreferencesManager().loadUserAvatar());

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setChecked(true);
                mNavigationDrawer.closeDrawer(GravityCompat.START);

                if (item.getItemId() == R.id.auth_menu) {
                    Intent authIntent = new Intent(MainActivity.this, AuthActivity.class);
                    startActivity(authIntent);
                }
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
            for (EditText userValue : mUserFieldsViews) {
                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);
            }
            showProfilePlaceHolder();
            lockToolbar();
            mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);
            setFocusForInputAtPhone(mUserFieldsViews.get(ConstantManager.USER_PHONE_INDEX_EDIT_TEXT));
//            onChangeTextListener(mUserInfoViews.get(ConstantManager.USER_EMAIL_INDEX_EDIT_TEXT));
        } else {
            mFab.setImageResource(R.drawable.ic_create_black_24dp);
            for (EditText userValue : mUserFieldsViews) {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);

//                saveUserInfoValue();
            }
            hideProfilePlaceHolder();
            unLockToolbar();
            mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white));
            saveUserFields();
        }
    }

    /**
     * Певодит фокус на редактирование
     *
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
//    private void initUserInfoValue() {
//        List<String> userData = new ArrayList<>();
//        userData.add(getResources().getString(R.string.phone_number_body));
//        userData.add(getResources().getString(R.string.email_address_body));
//        userData.add(getResources().getString(R.string.vk_profile_body));
//        userData.add(getResources().getString(R.string.repo_body));
//        userData.add(getResources().getString(R.string.about_me_body));
//        mDataManager.getPreferencesManager().saveUserProfileData(userData);
//    }

    /**
     * Проверяет, загруженны ли данные пользователя из SharedPreferences
     */
//    private boolean isLoadUserInfoValue() {
//        boolean isLoadUserInfo = false;
//        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
//        for (int index = 0; index < userData.size(); index++) {
//            if (!userData.get(index).isEmpty() && !userData.get(index).equals("null")) {
//                isLoadUserInfo = true;
//            }
//        }
//        return isLoadUserInfo;
//    }

    /**
     * Загружает данные пользователя из SharedPreferences
     */
//    private void loadUserInfoValue() {
//        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
//        for (int index = 0; index < userData.size(); index++) {
//            mUserInfoViews.get(index).setText(userData.get(index));
//        }
//    }
    private void initUserFields() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int i = 0; i < userData.size(); i++) {
            mUserFieldsViews.get(i).setText(userData.get(i));
        }
    }

    /**
     * Сохраняет данные пользователя в SharedPreferences
     */
//    private void saveUserInfoValue() {
//        List<String> userData = new ArrayList<>();
//        for (EditText userFieldView : mUserInfoViews) {
//            userData.add(userFieldView.getText().toString());
//        }
//        mDataManager.getPreferencesManager().saveUserProfileData(userData);
//    }
    private void saveUserFields() {
        List<String> userData = new ArrayList<>();
        for (EditText userFieldView : mUserFieldsViews) {
            userData.add(userFieldView.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

    private void initUserInfoValue() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileValues();
        for (int i = 0; i < userData.size(); i++) {
            mUserValueViews.get(i).setText(userData.get(i));
        }
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

    private void insertDrawerAvatar(Uri selectedImage) {
//        Picasso.with(this)
//                .load(selectedImage)
//                .resize(getResources().getDimensionPixelSize(R.dimen.drawer_header_avatar_size),
//                        getResources().getDimensionPixelSize(R.dimen.drawer_header_avatar_size))
//                .centerCrop()
//                .transform(new RoundedImageTransformation())
//                .placeholder(R.drawable.avatar_bg)
//                .into(drawerUsrAvatar);
    }

    /**
     * Показывает Snackbar, пересылающий на изменение настроек разрешений
     */
    private void showGiveAllowPermissionSnackBar() {
        Snackbar.make(mCoordinatorLayout, R.string.give_permission_message, Snackbar.LENGTH_LONG)
                .setAction(R.string.allow_message, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openApplicationSettings();
                    }
                }).show();
    }

    /**
     * Создаёт intent звонка
     *
     * @param phoneNumber номер телефона собеседника
     */
    private void callByPhoneNumber(String phoneNumber) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
            startActivity(dialIntent);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.CALL_PHONE
            }, ConstantManager.CALL_PHONE_REQUEST_PERMISSION_CODE);
            showGiveAllowPermissionSnackBar();
        }
    }

    /**
     * Создаёт intent отправки сообщения
     *
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
        Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ConstantManager.PREFIX_URL_LINK + link));
        startActivity(browseIntent);
    }

    /**
     * Проверяет полученные разрешения
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
     *
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
     *
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
     *
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

    /**
     * класс слушатель изменений строк в TextWatcher`ах
     */
    private class MyTextWatcher implements TextWatcher {

        private View view;
        boolean skipOnChange = false;
        private ValidatorUtils validateUtils;

        private MyTextWatcher(View view, Context context, Window window) {
            this.view = view;
            validateUtils = new ValidatorUtils(mUserFieldsViews, mUserInfoTil, context, window);
        }

        /**
         * Отрезает ненужную часть от url строки
         *
         * @param url введённая строка
         * @return
         */
        private String cutNoNeedPathAtUrl(String url) {
            String myUrl = url;
            return myUrl.replace("http://", "").replace("https://", "").replace("http:// www.", "").replace("www.", "");
        }

        /**
         * Проверяет поля на ненужную часть url строки
         *
         * @param indexFieldAtUserInfoViews индекс поля
         */
        private void checkUrlFieldOnNoNeedPath(int indexFieldAtUserInfoViews) {
            String originString = mUserFieldsViews.get(indexFieldAtUserInfoViews).getText().toString();
            String cutString = cutNoNeedPathAtUrl(originString);
            if (originString != cutString) {
                mUserFieldsViews.get(indexFieldAtUserInfoViews).setText(cutString);
            }
        }

        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        }

        /**
         * Вызывает код после изменения строки
         *
         * @param editable
         */
        public void afterTextChanged(Editable editable) {
            if (skipOnChange)
                return;

            skipOnChange = true;
            try {
                int indexFieldAtUserInfoViews = 0;
                switch (view.getId()) {
                    case R.id.phone_et:
                        indexFieldAtUserInfoViews = ConstantManager.USER_PHONE_INDEX_EDIT_TEXT;
                        break;
                    case R.id.email_et:
                        indexFieldAtUserInfoViews = ConstantManager.USER_EMAIL_INDEX_EDIT_TEXT;
                        break;
                    case R.id.vk_profile_et:
                        indexFieldAtUserInfoViews = ConstantManager.USER_VK_INDEX_EDIT_TEXT;
                        checkUrlFieldOnNoNeedPath(indexFieldAtUserInfoViews);
                        break;
                    case R.id.repo_et:
                        indexFieldAtUserInfoViews = ConstantManager.USER_GIT_INDEX_EDIT_TEXT;
                        checkUrlFieldOnNoNeedPath(indexFieldAtUserInfoViews);
                        break;
                }
                validateUtils.isValidate(indexFieldAtUserInfoViews);
            } catch (NumberFormatException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                skipOnChange = false;
            }
        }
    }
}
