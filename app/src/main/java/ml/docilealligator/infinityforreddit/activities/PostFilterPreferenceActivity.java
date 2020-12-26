package ml.docilealligator.infinityforreddit.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import ml.docilealligator.infinityforreddit.Infinity;
import ml.docilealligator.infinityforreddit.R;
import ml.docilealligator.infinityforreddit.RedditDataRoomDatabase;
import ml.docilealligator.infinityforreddit.adapters.FilterFragmentPostFilterRecyclerViewAdapter;
import ml.docilealligator.infinityforreddit.bottomsheetfragments.PostFilterOptionsBottomSheetFragment;
import ml.docilealligator.infinityforreddit.customtheme.CustomThemeWrapper;
import ml.docilealligator.infinityforreddit.postfilter.DeletePostFilter;
import ml.docilealligator.infinityforreddit.postfilter.PostFilter;
import ml.docilealligator.infinityforreddit.postfilter.PostFilterViewModel;

public class PostFilterPreferenceActivity extends BaseActivity {

    @BindView(R.id.coordinator_layout_post_filter_preference_activity)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.appbar_layout_post_filter_preference_activity)
    AppBarLayout appBarLayout;
    @BindView(R.id.toolbar_post_filter_preference_activity)
    Toolbar toolbar;
    @BindView(R.id.recycler_view_post_filter_preference_activity)
    RecyclerView recyclerView;
    @BindView(R.id.fab_post_filter_preference_activity)
    FloatingActionButton fab;
    @Inject
    @Named("default")
    SharedPreferences sharedPreferences;
    @Inject
    RedditDataRoomDatabase redditDataRoomDatabase;
    @Inject
    CustomThemeWrapper customThemeWrapper;
    @Inject
    Executor executor;
    public PostFilterViewModel postFilterViewModel;
    private FilterFragmentPostFilterRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((Infinity) getApplication()).getAppComponent().inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_filter_preference);

        ButterKnife.bind(this);

        applyCustomTheme();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(PostFilterPreferenceActivity.this, CustomizePostFilterActivity.class);
            intent.putExtra(CustomizePostFilterActivity.EXTRA_FROM_SETTINGS, true);
            startActivity(intent);
        });

        adapter = new FilterFragmentPostFilterRecyclerViewAdapter(postFilter -> {
            PostFilterOptionsBottomSheetFragment postFilterOptionsBottomSheetFragment = new PostFilterOptionsBottomSheetFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(PostFilterOptionsBottomSheetFragment.EXTRA_POST_FILTER, postFilter);
            postFilterOptionsBottomSheetFragment.setArguments(bundle);
            postFilterOptionsBottomSheetFragment.show(getSupportFragmentManager(), postFilterOptionsBottomSheetFragment.getTag());
        });

        recyclerView.setAdapter(adapter);

        postFilterViewModel = new ViewModelProvider(this,
                new PostFilterViewModel.Factory(redditDataRoomDatabase)).get(PostFilterViewModel.class);

        postFilterViewModel.getPostFilterListLiveData().observe(this, postFilters -> adapter.setPostFilterList(postFilters));
    }

    public void editPostFilter(PostFilter postFilter) {
        Intent intent = new Intent(PostFilterPreferenceActivity.this, CustomizePostFilterActivity.class);
        intent.putExtra(CustomizePostFilterActivity.EXTRA_POST_FILTER, postFilter);
        intent.putExtra(CustomizePostFilterActivity.EXTRA_FROM_SETTINGS, true);
        startActivity(intent);
    }

    public void applyPostFilterTo(PostFilter postFilter) {
        Intent intent = new Intent(this, PostFilterUsageListingActivity.class);
        intent.putExtra(PostFilterUsageListingActivity.EXTRA_POST_FILTER, postFilter);
        startActivity(intent);
    }

    public void deletePostFilter(PostFilter postFilter) {
        DeletePostFilter.deletePostFilter(redditDataRoomDatabase, executor, postFilter);
    }

    @Override
    protected SharedPreferences getDefaultSharedPreferences() {
        return sharedPreferences;
    }

    @Override
    protected CustomThemeWrapper getCustomThemeWrapper() {
        return customThemeWrapper;
    }

    @Override
    protected void applyCustomTheme() {
        applyAppBarLayoutAndToolbarTheme(appBarLayout, toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }
}