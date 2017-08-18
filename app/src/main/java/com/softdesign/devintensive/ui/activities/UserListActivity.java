package com.softdesign.devintensive.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.redmadrobot.chronos.ChronosConnector;
import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.events.ErrorEvent;
import com.softdesign.devintensive.data.events.TimeEvent;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.res.LikeModelRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.data.storage.models.Like;
import com.softdesign.devintensive.data.storage.models.LikeDao;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDTO;
import com.softdesign.devintensive.data.storage.tasks.LoadUserByNameOperation;
import com.softdesign.devintensive.data.storage.tasks.LoadUsersListOperation;
import com.softdesign.devintensive.ui.activities.interfaces.CustomClickListener;
import com.softdesign.devintensive.ui.adapters.UsersAdapter;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.NetworkStatusChecker;
import com.softdesign.devintensive.utils.RoundedAvatarDrawable;
import com.softdesign.devintensive.utils.SnackBarUtils;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserListActivity extends BaseActivity {
    private static final String TAG = ConstantManager.TAG_PREFIX + UserListActivity.class.getSimpleName();

    private ImageView drawerUserAvatar;
    private TextView drawerUserFullName;
    private TextView drawerUserEmail;

    @BindView(R.id.coordinator_layout_users_list)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.navigation_drawer_users_list)
    DrawerLayout mNavigationDrawer;
    @BindView(R.id.navigation_view_users_list)
    NavigationView mNavigationView;
    @BindView(R.id.toolbar_users_list)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view_user_list)
    RecyclerView mRecyclerView;

    private DataManager mDataManager;

    private Context mContext;
    private UsersAdapter mUsersAdapter;
    private List<User> mUsers;

    private MenuItem mSearchItem;

    private ChronosConnector mConnector;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        ButterKnife.bind(this);

        showSplash();

        mConnector = new ChronosConnector();
        mConnector.onCreate(this, savedInstanceState);

        mDataManager = DataManager.getInstance();
        mContext = mDataManager.getContext();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mHandler = new Handler();
        setAllowSwipeUser();
        setupToolbar();
        setupDrawer();
        loadUsersListFromDb();
    }

    /**
     * Обрабатывает событие onResume жизненного цикла Activity
     */
    @Override
    protected void onResume() {
        super.onResume();
        mConnector.onResume();
    }

    /**
     * Обрабатывает событие onPause жизненного цикла Activity
     */
    @Override
    protected void onPause() {
        super.onPause();
        mConnector.onPause();
    }

    /**
     * Обрабатывает событие onStart жизненного цикла Activity
     */
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    /**
     * Обрабатывает событие onStop жизненного цикла Activity
     */
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /**
     * Обрабатывает событие onDestroy жизненного цикла Activity
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mConnector.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Загружает данные о пользователях из БД
     */
    private void loadUsersListFromDb() {
        try {
            mConnector.runOperation(new LoadUsersListOperation(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Загружает пользователей по имени из БД
     *
     * @param queryFullName
     */
    private void loadUsersByNameFromDb(final String queryFullName) {
        try {
            mConnector.runOperation(new LoadUserByNameOperation(queryFullName), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Устанавливает аватар пользователя со скруглёнными краями в Navigation Drawer
     *
     * @param selectedImage
     */
    private void insertDrawerAvatar(Uri selectedImage) {
        Picasso.with(this)
                .load(selectedImage)
                .fit()
                .centerCrop()
                .transform(new RoundedAvatarDrawable())
                .placeholder(R.drawable.avatar_bg)
                .into(drawerUserAvatar);
    }

    /**
     * инициализирует NavigationDrawer
     */
    private void setupDrawer() {
        View headerLayout = mNavigationView.getHeaderView(0);

        drawerUserAvatar = (ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.drawer_user_avatar_iv);

        drawerUserFullName = (TextView) headerLayout.findViewById(R.id.drawer_user_name_tv);
        drawerUserEmail = (TextView) headerLayout.findViewById(R.id.drawer_user_email_tv);

        drawerUserFullName.setText(mDataManager.getPreferencesManager().getUserFullName());
        drawerUserEmail.setText(mDataManager.getPreferencesManager().getUserEmail());

        insertDrawerAvatar(mDataManager.getPreferencesManager().loadUserAvatar());

        mNavigationView.setCheckedItem(R.id.team_menu);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setChecked(true);
                mNavigationDrawer.closeDrawer(GravityCompat.START);

                switch (item.getItemId()) {
                    case R.id.user_profile_menu:
                        showSplash();
                        Intent userProfileIntent = new Intent(UserListActivity.this, LoginUserActivity.class);
                        startActivity(userProfileIntent);
                        break;
                    case R.id.team_menu:

                        break;
                    case R.id.auth_menu:
                        showSplash();
                        Intent authIntent = new Intent(UserListActivity.this, AuthActivity.class);
                        startActivity(authIntent);
                        break;
                }

                return false;
            }
        });
    }

    /**
     * инициализирует ToolBar
     */
    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(R.string.my_name);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Устанавливает обработку поиска по имени пользователя в списке
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        mSearchItem = menu.findItem(R.id.search_action);
        android.widget.SearchView searchView = (android.widget.SearchView) MenuItemCompat.getActionView(mSearchItem);
        searchView.setQueryHint(getString(R.string.search_input_name_user));
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                showUserByQuery(newText);
                return false;
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Обрабатывает результат операции LoadUsersListOperation
     *
     * @param result результат операции
     */
    public void onOperationFinished(final LoadUsersListOperation.Result result) {
        mUsers = result.getOutput();
        showUsers();
    }

    /**
     * Обрабатывает результат операции LoadUserByNameOperation
     *
     * @param result результат операции
     */
    public void onOperationFinished(final LoadUserByNameOperation.Result result) {
        mUsers = result.getOutput();
        showUsers();
    }

    /**
     * Отображет список пользователей
     */
    private void showUsers() {
        if (mUsers.size() == 0) {
            SnackBarUtils.show(mCoordinatorLayout, getString(R.string.error_load_users_list));
        } else {
            mUsersAdapter = new UsersAdapter(mUsers, getApplicationContext(), new CustomClickListener() {
                @Override
                public void onUserItemClickListener(String action, int position) {
                    if (action.equals(ConstantManager.START_PROFILE_ACTIVITY_KEY)) {
                        UserDTO userDTO = new UserDTO(mUsers.get(position));

                        Intent profileIntent = new Intent(UserListActivity.this, ProfileUserActivity.class);
                        profileIntent.putExtra(ConstantManager.PARCELABLE_KEY, userDTO);
                        startActivity(profileIntent);
                    } else if (action.equals(ConstantManager.LIKE_USER_KEY)) {
                        if (mUsersAdapter.isUserLiked(mUsers.get(position))) {
                            unLikeUser(position);
                        } else {
                            likeUser(position);
                        }
                    }
//                    } else if (action.equals(ConstantManager.UNLIKE_USER_KEY)) {
//                    }
                }
            });
            mRecyclerView.swapAdapter(mUsersAdapter, false);
        }
        EventBus.getDefault().post(new TimeEvent(ConstantManager.END_SHOW_USERS));
    }

    /**
     * Отображает список пользователей при поиске по имени
     *
     * @param query строка поиска
     */
    private void showUserByQuery(final String query) {
        Runnable searchUsers = new Runnable() {
            @Override
            public void run() {
                if (!query.isEmpty()) {
                    loadUsersByNameFromDb(query);
                } else {
                    loadUsersListFromDb();
                }
            }
        };

        mHandler.removeCallbacks(searchUsers);
        if (!query.isEmpty()) {
            mHandler.postDelayed(searchUsers, ConstantManager.SEARCH_DELAY);
        } else {
            mHandler.postDelayed(searchUsers, ConstantManager.SEARCH_WITHOUT_DELAY);
        }
    }

    /**
     * Слушает событие TimeEvent
     *
     * @param timeEvent слушает окончание отображение списка пользователей
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimeEvent(TimeEvent timeEvent) {
        switch (timeEvent.getTimeCode()) {

            case ConstantManager.END_SHOW_USERS:
                hideSplash();
                break;
        }
    }

    /**
     * Разрешает осуществлять swipe элементов списка пользователей
     */
    private void setAllowSwipeUser() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                final int startPosition = viewHolder.getAdapterPosition();
                final int targetPosition = target.getAdapterPosition();

                mUsers.add(targetPosition, mUsers.remove(startPosition));
                mUsersAdapter.notifyItemMoved(startPosition, targetPosition);

                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                mUsers.remove(position);
                mUsersAdapter.notifyDataSetChanged();
            }
        });
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    /**
     * Вызывает call запрос и обрабатывает callBack от него для установки или удаления лайка
     * @param call вызов
     * @param user пользователь
     * @param position позиция в RecycleView
     * @param isLikeUser true - установка лайка, false - удаление
     */
    private void callWithLikeModel(Call call, final User user, final int position, final boolean isLikeUser) {
        call.enqueue(new Callback<LikeModelRes>() {
            @Override
            public void onResponse(Call<LikeModelRes> call, Response<LikeModelRes> response) {
                if (response.code() == 200) {
                    UserModelRes.ProfileValues userData = response.body().getData();
                    user.setRait(userData.getRait());
                    user.setCodeLines(userData.getLinesCode());
                    user.setProjects(userData.getProjects());

                    if (isLikeUser) {
                        mDataManager.getDaoSession().getLikeDao().insert(
                                new Like(mDataManager.getPreferencesManager().getUserId(), user.getRemoteId())
                        );
                    } else {
                        List<Like> likes = mDataManager.getDaoSession().queryBuilder(Like.class).where(
                                LikeDao.Properties.UserRemoteId.eq(user.getRemoteId()),
                                LikeDao.Properties.LikeUserId.eq(mDataManager.getPreferencesManager().getUserId())
                        ).list();
                        for (Like like : likes) {
                            like.delete();
                        }
                    }
                    user.resetLikes();
                    user.setRating(userData.getRating());
                    mDataManager.getDaoSession().getUserDao().insertOrReplace(user);

                    mUsersAdapter.notifyItemChanged(position);
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
    }

    /**
     * Ставит лайк пользователю
     *
     * @param position позиция пользователя, которому ставится лайк
     */
    private void likeUser(final int position) {
        final User user = mUsers.get(position);
        if (NetworkStatusChecker.isNetworkAvailable(mContext)) {
            Call<LikeModelRes> call = mDataManager.likeUser(user.getRemoteId());
            callWithLikeModel(call, user, position, true);
        } else {
            SnackBarUtils.show(mCoordinatorLayout, getString(R.string.network_not_access_response));
            EventBus.getDefault().post(new ErrorEvent(ConstantManager.NETWORK_NOT_AVAILABLE));
        }
    }

    /**
     * Удаляет лайк у пользователя
     *
     * @param position позиция пользователя, у которого удаляется лайк
     */
    private void unLikeUser(final int position) {
        final User user = mUsers.get(position);
        if (NetworkStatusChecker.isNetworkAvailable(mContext)) {
            Call<LikeModelRes> call = mDataManager.unLikeUser(user.getRemoteId());
            callWithLikeModel(call, user, position, false);
        } else {
            SnackBarUtils.show(mCoordinatorLayout, getString(R.string.network_not_access_response));
            EventBus.getDefault().post(new ErrorEvent(ConstantManager.NETWORK_NOT_AVAILABLE));
        }
    }
}
