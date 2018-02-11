package imageserach.fieldwire.amko0l.com.imagesearch.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import imageserach.fieldwire.amko0l.com.imagesearch.adapter.MyItemRecyclerViewAdapter;
import imageserach.fieldwire.amko0l.com.imagesearch.R;
import imageserach.fieldwire.amko0l.com.imagesearch.utils.FragmentManagerUtil;
import imageserach.fieldwire.amko0l.com.imagesearch.utils.Utils;

import static imageserach.fieldwire.amko0l.com.imagesearch.utils.AppConstants.IMAGE_LOAD_PAGE;
import static imageserach.fieldwire.amko0l.com.imagesearch.utils.AppConstants.IMAGE_SEARCH_PAGE;

public class MainActivity extends AppCompatActivity implements
        MyItemRecyclerViewAdapter.OnListFragmentInteractionListener, ImageHolderFragment.OnImageHolderIInteractionListener {
    private String TAG = "MainActivity";

    /*
        private String searchHistory = "SEARCH_HISTORY_FIELDWIRE";
        private ImageGridAdapter imageGridAdapter;
        private List<String> imageList;

        private TextView emptyTextView;
        private View loadingIndicator;
        // private String savedsearchString;

        SharedPreferences sharedPref;
        SharedPreferences.Editor editor;
        ListView listView;
        GridView gridView;
    */
    private SearchHistoryFragment searchHistoryFragment;
    private ImageHolderFragment imageHolderFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchHistoryFragment = SearchHistoryFragment.newInstance(0);
        FragmentManagerUtil.createFragment(R.id.imageSearchHolder, searchHistoryFragment,
                IMAGE_SEARCH_PAGE, this, false);
        /*
        imageList = new ArrayList<>(100);

        Utils.setContext(this);


        //set up gridView and it's empty case view
        gridView = (GridView) findViewById(R.id.gridview);
        emptyTextView = (TextView) findViewById(R.id.empty);
        if (!ConnectivityUtils.isConnected(this)) {
            emptyTextView.setText(R.string.no_internet);
        }
        gridView.setEmptyView(emptyTextView);

        loadingIndicator = findViewById(R.id.loading_indicator_main_grid);

        imageGridAdapter = new ImageGridAdapter(MainActivity.this, imageList);

        gridView.setAdapter(imageGridAdapter);


        gridView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                loadNextDataFromApi(page);
                return true;
            }
        });

        //listener for each item in gridView
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if ((!Utils.checkImageResource(MainActivity.this, (ImageView) view.findViewById(R.id.grid_item_image), R.drawable.ic_image_error)) && ((ProgressBar) view.findViewById(R.id.grid_item_loading_indicator)).getVisibility() == View.INVISIBLE) {
                    Intent intent = new Intent(MainActivity.this, ImageActivity.class);
                    intent.putExtra("imageuri", imageList.get(position));
                    startActivity(intent);
                } else if (Utils.checkImageResource(MainActivity.this, (ImageView) view.findViewById(R.id.grid_item_image), R.drawable.ic_image_error)) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.error_loading), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.image_loading), Toast.LENGTH_SHORT).show();
                }

            }
        });
*/
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

    // Receive query from searchWidget
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        if (searchHistoryFragment != null && searchHistoryFragment.isAdded()) {
            searchHistoryFragment.handleIntent(intent);
        }
        imageHolderFragment = ImageHolderFragment.newInstance(2, intent.getStringExtra(SearchManager.QUERY));
        FragmentManagerUtil.createFragment(R.id.imageSearchHolder, imageHolderFragment,
                IMAGE_LOAD_PAGE, this, true);
    }

    @Override
    public void onListFragmentInteraction(String item) {
        Log.i("Anshul", "Item Clicked=>" + item);
    }

    @Override
    public void onListImageHolderInteraction(String imageUrl, ImageView imageView, ProgressBar progressBar) {
        Log.i("Anshul", "Item Clicked from ImageHolder Fragment=>" + imageUrl);
        if ((!Utils.checkImageResource(MainActivity.this, imageView, R.drawable.ic_image_error)) && progressBar.getVisibility() == View.INVISIBLE) {
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
