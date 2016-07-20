package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.redmadrobot.chronos.ChronosConnector;
import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.events.ErrorEvent;
import com.softdesign.devintensive.data.events.TimeEvent;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.data.storage.tasks.SaveUsersListOperation;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.LogUtils;
import com.softdesign.devintensive.utils.NetworkStatusChecker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends BaseActivity {
    private static final String TAG = ConstantManager.TAG_PREFIX + AuthActivity.class.getSimpleName();

    private DataManager mDataManager;

    private ChronosConnector mConnector;

    @BindView(R.id.login_email_et)
    EditText mAuthLogin;
    @BindView(R.id.login_password_et)
    EditText mAuthPass;
    @BindView(R.id.login_btn)
    Button mAuthLoginBtn;
    @BindView(R.id.remember_tv)
    TextView mRememberPass;
    @BindView(R.id.main_coordinator_auth)
    CoordinatorLayout mCoordinatorLayout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);

        mDataManager = DataManager.getInstance();

        mConnector = new ChronosConnector();
        mConnector.onCreate(this, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mConnector.onResume();
        hideSplash();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mConnector.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mConnector.onSaveInstanceState(outState);
    }

    public void onOperationFinished(final SaveUsersListOperation.Result result) {
        Intent loginIntent = new Intent(AuthActivity.this, MainActivity.class);
        startActivity(loginIntent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onErrorEvent(ErrorEvent event) {
        hideSplash();
        hideProgress();

        switch (event.getErrorCode()) {
            case ConstantManager.NETWORK_NOT_AVAILABLE:
                startUserMainActivity();
                break;
            case ConstantManager.USER_LIST_LOADED_AND_SAVED:
                startUserMainActivity();
                break;
            case ConstantManager.USER_NOT_AUTHORIZED:
                showSnackBar("Необходима авторизация");
                break;
            case ConstantManager.LOGIN_OR_PASSWORD_INCORRECT:
                showSnackBar("Неверный логин или пароль");
                break;
            case ConstantManager.RESPONSE_NOT_OK:
                startUserMainActivity();
                break;
            case ConstantManager.SERVER_ERROR:
                startUserMainActivity();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimeEvent(TimeEvent timeEvent) {
        switch(timeEvent.getTimeCode()) {

            case ConstantManager.END_SHOW_USERS:
                hideSplash();
                break;
        }
    }

    private void startUserMainActivity() {
        Intent loginIntent = new Intent(AuthActivity.this, MainActivity.class);
        finish();
        startActivity(loginIntent);
    }

    private void showSnackBar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @OnClick(R.id.remember_tv)
    protected void rememberPass() {
        Intent rememberIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ConstantManager.FORGOT_PASSWORD));
        startActivity(rememberIntent);
    }

    @OnClick(R.id.login_btn)
    protected void signIn() {
        showSplash();
        if (NetworkStatusChecker.isNetworkAvailable(this)) {
            Call<UserModelRes> call = mDataManager.loginUser(new UserLoginReq(mAuthLogin.getText().toString(), mAuthPass.getText().toString()));
            call.enqueue(new Callback<UserModelRes>() {
                @Override
                public void onResponse(Call<UserModelRes> call, Response<UserModelRes> response) {
                    if (response.code() == ConstantManager.RESPONSE_OK) {
                        loginSuccess(response.body());
                    } else if(response.code() == ConstantManager.LOGIN_OR_PASSWORD_INCORRECT ||
                            response.code() == ConstantManager.USER_NOT_AUTHORIZED){
                        EventBus.getDefault().post(new ErrorEvent(response.code()));
                    } else {
                        EventBus.getDefault().post(new ErrorEvent(ConstantManager.RESPONSE_NOT_OK));
                    }
                }
                @Override
                public void onFailure(Call<UserModelRes> call, Throwable t) {
                    showSnackBar(getString(R.string.error_response) + t.getMessage());
                    EventBus.getDefault().post(new ErrorEvent(ConstantManager.SERVER_ERROR));
                }
            });
        } else {
            showSnackBar(getString(R.string.network_not_access_response));
            EventBus.getDefault().post(new ErrorEvent(ConstantManager.NETWORK_NOT_AVAILABLE));
        }
    }

    protected void loginSuccess(UserModelRes userModel) {
        mDataManager.getPreferencesManager().saveAuthToken(userModel.getData().getToken());
        mDataManager.getPreferencesManager().saveUserId(userModel.getData().getUser().getId());
        mDataManager.getPreferencesManager().saveUserPhoto(Uri.parse(userModel.getData().getUser().getPublicInfo().getPhoto()));
        mDataManager.getPreferencesManager().saveUserAvatar(Uri.parse(userModel.getData().getUser().getPublicInfo().getAvatar()));
        mDataManager.getPreferencesManager().saveUserFullName(userModel.getData().getUser().getFirstName() + " " + userModel.getData().getUser().getSecondName());
        saveUserValues(userModel);
        saveUserData(userModel);
        saveUserInDb();
    }

    private void saveUserValues(UserModelRes userModel) {
        int[] userValues = {
                userModel.getData().getUser().getProfileValues().getRating(),
                userModel.getData().getUser().getProfileValues().getLinesCode(),
                userModel.getData().getUser().getProfileValues().getProjects()
        };

        mDataManager.getPreferencesManager().saveUserProfileValues(userValues);
    }

    private void saveUserData(UserModelRes userModel) {
        List<String> userData = new ArrayList<>();
        userData.add(userModel.getData().getUser().getContacts().getPhone());
        userData.add(userModel.getData().getUser().getContacts().getEmail());
        userData.add(userModel.getData().getUser().getContacts().getVk());
        userData.add(userModel.getData().getUser().getRepositories().getRepo().get(0).getGit());
        userData.add(userModel.getData().getUser().getPublicInfo().getBio());

        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

    private void saveUserInDb() {
        Call<UserListRes> call = mDataManager.getUserListFromNetwork();
        call.enqueue(new Callback<UserListRes>() {
            @Override
            public void onResponse(Call<UserListRes> call, final Response<UserListRes> response) {
                try {
                    if (response.code() == ConstantManager.RESPONSE_OK) {
                        mConnector.runOperation(new SaveUsersListOperation(response), false);
                    } else {
                        LogUtils.d(TAG, "onResponse: " + String.valueOf(response.errorBody().source()));
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<UserListRes> call, Throwable t) {
            }
        });
    }
}
