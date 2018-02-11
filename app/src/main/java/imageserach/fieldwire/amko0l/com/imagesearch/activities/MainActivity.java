package imageserach.fieldwire.amko0l.com.imagesearch.activities;

import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import imageserach.fieldwire.amko0l.com.imagesearch.adapter.MyItemRecyclerViewAdapter;
import imageserach.fieldwire.amko0l.com.imagesearch.R;
import imageserach.fieldwire.amko0l.com.imagesearch.utils.FragmentManagerUtil;
import imageserach.fieldwire.amko0l.com.imagesearch.utils.Utils;

import static imageserach.fieldwire.amko0l.com.imagesearch.utils.AppConstants.IMAGE_LOAD_PAGE;
import static imageserach.fieldwire.amko0l.com.imagesearch.utils.AppConstants.IMAGE_SEARCH_PAGE;

public class MainActivity extends AppCompatActivity implements
        MyItemRecyclerViewAdapter.OnListFragmentInteractionListener, ImageHolderFragment.OnImageHolderIInteractionListener {
    private String TAG = "MainActivity";
    private SearchHistoryFragment searchHistoryFragment;
    private ImageHolderFragment imageHolderFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchHistoryFragment = SearchHistoryFragment.newInstance(0);
        FragmentManagerUtil.createFragment(R.id.imageSearchHolder, searchHistoryFragment,
                IMAGE_SEARCH_PAGE, this, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();

        // Set current activity as searchable activity
        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.setIconifiedByDefault(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Receive query from searchWidget
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        if (searchHistoryFragment != null && searchHistoryFragment.isAdded()) {
            searchHistoryFragment.handleIntent(intent);
        }
        imageHolderFragmentCreation(intent.getStringExtra(SearchManager.QUERY));
    }

    private void imageHolderFragmentCreation(String query) {
        imageHolderFragment = ImageHolderFragment.newInstance(2, query);
        FragmentManagerUtil.createFragment(R.id.imageSearchHolder, imageHolderFragment,
                IMAGE_LOAD_PAGE, this, true);
    }

    @Override
    public void onListFragmentInteraction(String item) {
        Log.i("Anshul", "Item Clicked=>" + item);
        if (imageHolderFragment == null) {
            imageHolderFragmentCreation(item);
        }
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        Log.i("Anshul", "Count value" + count + ",,imageHolderFragment" + imageHolderFragment);
        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
            imageHolderFragment = null;
        }
        Log.i("Anshul", "Count value end" + count + ",,imageHolderFragment" + imageHolderFragment);
    }

    @Override
    public void onListImageHolderInteraction(String imageUrl, ImageView imageView, ProgressBar progressBar) {
        Log.i("Anshul", "Item Clicked from ImageHolder Fragment=>" + imageUrl);
        if ((!Utils.checkImageResource(MainActivity.this, imageView, R.drawable.ic_image_error)) && progressBar.getVisibility() == View.GONE) {
            Intent intent = new Intent(MainActivity.this, ImageActivity.class);
            intent.putExtra("imageuri", imageUrl);
            startActivity(intent);
        } else if (Utils.checkImageResource(MainActivity.this, imageView, R.drawable.ic_image_error)) {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.error_loading), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.image_loading), Toast.LENGTH_SHORT).show();
        }

    }
}
