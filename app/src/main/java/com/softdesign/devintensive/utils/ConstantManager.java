package com.softdesign.devintensive.utils;

public interface ConstantManager {
    String TAG_PREFIX = "DEV";

    String EDIT_MODE_KEY = "EDIT_MODE_KEY";

    String USER_PHONE_KEY = "USER_PHONE_KEY";
    String USER_EMAIL_KEY = "USER_EMAIL_KEY";
    String USER_VK_KEY = "USER_VK_KEY";
    String USER_GIT_KEY = "USER_GIT_KEY";
    String USER_BIO_KEY = "USER_BIO_KEY";
    String USER_PHOTO_KEY = "USER_PHOTO_KEY";

    int LOAD_PROFILE_PHOTO = 1;
    int REQUEST_CAMERA_PICTURE = 77;
    int REQUEST_GALLERY_PICTURE = 88;
    int PERMISSION_REQUEST_SETTINGS_CODE = 99;
    int CAMERA_REQUEST_PERMISSION_CODE = 111;
    int CALL_PHONE_REQUEST_PERMISSION_CODE = 122;
}
