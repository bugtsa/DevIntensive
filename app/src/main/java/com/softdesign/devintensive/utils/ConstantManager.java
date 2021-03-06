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

    int USER_PHONE_INDEX_EDIT_TEXT = 0;
    int USER_EMAIL_INDEX_EDIT_TEXT = 1;
    int USER_VK_INDEX_EDIT_TEXT = 2;
    int USER_GIT_INDEX_EDIT_TEXT = 3;
    int USER_BIO_INDEX_EDIT_TEXT = 4;

    int USER_PHONE_INDEX_IMAGE_VIEW = 0;
    int USER_EMAIL_INDEX_IMAGE_VIEW = 1;
    int USER_VK_INDEX_IMAGE_VIEW = 2;
    int USER_GIT_INDEX_IMAGE_VIEW = 3;
    int USER_PROFILE_INDEX_IMAGE_VIEW = 4;

    int LOAD_PROFILE_PHOTO = 1;
    int REQUEST_CAMERA_PICTURE = 77;
    int REQUEST_GALLERY_PICTURE = 88;
    int PERMISSION_REQUEST_SETTINGS_CODE = 99;
    int CAMERA_REQUEST_PERMISSION_CODE = 111;
    int CALL_PHONE_REQUEST_PERMISSION_CODE = 122;

    String USER_RATING_VALUE = "USER_RATING_VALUE";
    String USER_CODE_LINES_VALUE = "USER_CODE_LINES_VALUE";
    String USER_PROJECT_VALUE = "USER_PROJECT_VALUE";
    String USER_AVATAR_KEY = "USER_AVATAR_KEY";
    String AUTH_TOKEN_KEY = "AUTH_TOKEN_KEY";
    String USER_ID_KEY = "USER_ID_KEY";
    String USER_FULL_NAME_KEY = "USER_FULL_NAME_KEY";
    String PARCELABLE_KEY = "PARCELABLE_KEY";
    String START_PROFILE_ACTIVITY_KEY = "START_PROFILE_ACTIVITY_KEY";
    String LIKE_USER_KEY = "LIKE_USER_KEY";

    String FORGOT_PASSWORD = "http://devintensive.softdesign-apps.ru/forgotpass";
    String PREFIX_URL_LINK = "http://";
    int SEARCH_DELAY = 1500;

    int SEARCH_WITHOUT_DELAY = 0;
    int RESPONSE_OK = 200;
    int USER_NOT_AUTHORIZED = 401;
    int LOGIN_OR_PASSWORD_INCORRECT = 404;
    int RESPONSE_NOT_OK = 666;
    int SERVER_ERROR = 677;
    int USER_LIST_LOADED_AND_SAVED = 688;
    int NETWORK_NOT_AVAILABLE = 699;
    int END_SHOW_USERS = 711;
    int EDITABLE_ERROR = 722;
    int USER_LIST_NOT_SAVED = 733;
}
