package com.softdesign.devintensive.ui.activities;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.storage.models.UserDTO;
import com.softdesign.devintensive.ui.adapters.UsersAdapter;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.LogUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private static final String TAG = ConstantManager.TAG_PREFIX + "UserListActivity";

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
    private UsersAdapter mUsersAdapter;
    private List<UserListRes.UserData> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        ButterKnife.bind(this);

        mDataManager = DataManager.getInstance();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        setupToolbar();
        setupDrawer();
        loadUsers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Загружает данные о пользователях
     */
    private void loadUsers() {
        Call<UserListRes> call = mDataManager.getUserList();

        call.enqueue(new Callback<UserListRes>() {
            @Override
            public void onResponse(Call<UserListRes> call, Response<UserListRes> response) {
                try {
                    mUsers = response.body().getData();
                    mUsersAdapter = new UsersAdapter(mUsers, getApplicationContext(), new UsersAdapter.UserViewHolder.CustomClickListener() {
                        @Override
                        public void onUserItemClickListener(int position) {
                            UserDTO userDTO = new UserDTO(mUsers.get(position));

                            Intent profileIntent = new Intent(UserListActivity.this, ProfileUserActivity.class);
                            profileIntent.putExtra(ConstantManager.PARCELABLE_KEY, userDTO);
                            startActivity(profileIntent);
                        }
                    });
                    mRecyclerView.setAdapter(mUsersAdapter);
                } catch (NullPointerException e) {
                    LogUtils.d(TAG, e.toString());
                }
            }

            @Override
            public void onFailure(Call<UserListRes> call, Throwable t) {
                //TODO
            }
        });
    }

    /**
     * инициализирует NavigationDrawer
     */
    private void setupDrawer() {
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setChecked(true);
                mNavigationDrawer.closeDrawer(GravityCompat.START);

                switch (item.getItemId()) {
                    case R.id.user_profile_menu:
                        Intent userProfileIntent = new Intent(UserListActivity.this, MainActivity.class);
                        startActivity(userProfileIntent);
                        break;
                    case R.id.team_menu:
                        break;
                    case R.id.auth_menu:
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(this, MainActivity.class)));
        searchView.setIconifiedByDefault(false);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mUsersAdapter.filter(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText != null && !newText.isEmpty()) {
            mUsersAdapter.filter(newText);
        }
        return true;
    }
}
