package com.softdesign.devintensive.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.events.ErrorEvent;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.res.LikeModelRes;
import com.softdesign.devintensive.data.storage.models.UserDTO;
import com.softdesign.devintensive.ui.adapters.RepositoriesAdapter;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.NetworkStatusChecker;
import com.softdesign.devintensive.utils.SnackBarUtils;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileUserActivity extends AppCompatActivity {

    @BindView(R.id.profile_coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.collapsing_toolbar_profile)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.toolbar_profile)
    Toolbar mToolBar;
    @BindView(R.id.user_profile_iv_profile)
    ImageView mProfileImage;
    @BindView(R.id.fab_user_profile)
    ImageView mUserLike;
    @BindView(R.id.about_me_et_profile)
    EditText mUserBio;
    @BindView(R.id.rating_quantity_static_tv)
    TextView mUserRating;
    @BindView(R.id.lines_code_quantity_static_tv)
    TextView mUserCodeLines;
    @BindView(R.id.project_quantity_static_tv)
    TextView mUserProjects;

    @BindView(R.id.repositories_list)
    ListView mRepoListView;

    private DataManager mDataManager;

    private Context mContext;

    private String mRemoteIdShowUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        ButterKnife.bind(this);

        mDataManager = DataManager.getInstance();
        mContext = mDataManager.getContext();

        setupToolBar();
        initProfileData();
    }

    /**
     * Устанавливает ToolBar
     */
    private void setupToolBar() {
        setSupportActionBar(mToolBar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Открывает ссылку
     * @param link ссылка на веб-адрес
     */
    public void browseLink(String link) {
        Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ConstantManager.PREFIX_URL_LINK + link));
        startActivity(browseIntent);
    }

    /**
     * Инициализирует данные пользователя
     */
    private void initProfileData() {
        UserDTO userDTO = getIntent().getParcelableExtra(ConstantManager.PARCELABLE_KEY);

        final List<String> repositories = userDTO.getRepositories();
        final RepositoriesAdapter repositoriesAdapter = new RepositoriesAdapter(this, repositories);
        mRepoListView.setAdapter(repositoriesAdapter);

        mRepoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                browseLink(repositories.get(position));
            }
        });

        mRemoteIdShowUser = userDTO.getRemoteId();
        mUserBio.setText(userDTO.getBio());
        mUserRating.setText(userDTO.getRating());
        mUserCodeLines.setText(userDTO.getCodeLines());
        mUserProjects.setText(userDTO.getProjects());

        mCollapsingToolbarLayout.setTitle(userDTO.getFullName());

        Picasso.with(this)
                .load(userDTO.getPhoto())
                .placeholder(R.drawable.user_bg)
                .error(R.drawable.user_bg)
                .into(mProfileImage);
    }

    @OnClick(R.id.fab_user_profile)
    protected void processLike() {
        if (mUserRating.equals(mUserRating)) {
            likeUser(mRemoteIdShowUser);
            mUserLike.setImageDrawable(getDrawable(R.drawable.ic_favorite_white_24dp));
        } else {
            unLikeUser(mRemoteIdShowUser);
            mUserLike.setImageDrawable(getDrawable(R.drawable.ic_favorite_border_white_24dp));
        }
    }

    /**
     * Ставит лайк пользователю
     * @param userRemoteId id пользователя, которому ставится лайк
     */
    private void likeUser(String userRemoteId) {
        if (NetworkStatusChecker.isNetworkAvailable(mContext)) {
            Call<LikeModelRes> call = mDataManager.likeUser(userRemoteId);
            call.enqueue(new Callback<LikeModelRes>() {
                @Override
                public void onResponse(Call<LikeModelRes> call, Response<LikeModelRes> response) {
                    if (response.code() == 200) {
                        SnackBarUtils.show(mCoordinatorLayout, getString(R.string.set_like));
                    } else {
                        SnackBarUtils.show(mCoordinatorLayout, getString(R.string.not_known_response));
                    }
                }

                @Override
                public void onFailure(Call<LikeModelRes> call, Throwable t) {
                    SnackBarUtils.show(mCoordinatorLayout, getString(R.string.error_response) + t.getMessage());
                    EventBus.getDefault().post(new ErrorEvent(ConstantManager.RESPONSE_NOT_OK));
                }
            });
        } else {
            SnackBarUtils.show(mCoordinatorLayout, getString(R.string.network_not_access_response));
            EventBus.getDefault().post(new ErrorEvent(ConstantManager.NETWORK_NOT_AVAILABLE));
        }
    }

    /**
     * Удаляет лайк у пользователя
     * @param userRemoteId id пользователя, у которого удаляется лайк
     */
    private void unLikeUser(String userRemoteId) {
        if (NetworkStatusChecker.isNetworkAvailable(mContext)) {
            Call<LikeModelRes> call = mDataManager.unLikeUser(userRemoteId);
            call.enqueue(new Callback<LikeModelRes>() {
                @Override
                public void onResponse(Call<LikeModelRes> call, Response<LikeModelRes> response) {
                    if (response.code() == 200) {
                        SnackBarUtils.show(mCoordinatorLayout, getString(R.string.set_like));
                    } else {
                        SnackBarUtils.show(mCoordinatorLayout, getString(R.string.not_known_response));
                    }
                }

                @Override
                public void onFailure(Call<LikeModelRes> call, Throwable t) {
                    SnackBarUtils.show(mCoordinatorLayout, getString(R.string.error_response) + t.getMessage());
                    EventBus.getDefault().post(new ErrorEvent(ConstantManager.RESPONSE_NOT_OK));
                }
            });
        } else {
            SnackBarUtils.show(mCoordinatorLayout, getString(R.string.network_not_access_response));
            EventBus.getDefault().post(new ErrorEvent(ConstantManager.NETWORK_NOT_AVAILABLE));
        }
    }
}
