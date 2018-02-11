package imageserach.fieldwire.amko0l.com.imagesearch.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import imageserach.fieldwire.amko0l.com.imagesearch.R;
import imageserach.fieldwire.amko0l.com.imagesearch.adapter.ImageGridAdapter;
import imageserach.fieldwire.amko0l.com.imagesearch.utils.ConnectivityUtils;
import imageserach.fieldwire.amko0l.com.imagesearch.utils.Utils;
import imageserach.fieldwire.amko0l.com.imagesearch.utils.VolleySingleton;
import imageserach.fieldwire.amko0l.com.imagesearch.views.EndlessScrollListener;

public class MainActivity extends AppCompatActivity {
    private String prefName = "SEARCH_HISTORY";
    private String TAG = "MainActivity";

    private String searchHistory = "SEARCH_HISTORY_FIELDWIRE";
    private ImageGridAdapter imageGridAdapter;
    private List<String> imageList;

    private String searchString;
    private TextView emptyTextView;
    private View loadingIndicator;
    // private String savedsearchString;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    ListView listView;
    GridView gridView;

    BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = this.getSharedPreferences(prefName, Context.MODE_PRIVATE);

        Set<String> historyset = sharedPref.getStringSet(searchHistory, new HashSet<String>());
        String[] searchHistoryArray = new String[historyset.size()];
        int i = 0;
        Log.d("Amit  ", "  " + historyset.size());
        for (String history : historyset) {
            searchHistoryArray[i++] = history;
        }


        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.search_listview, searchHistoryArray);

        listView = findViewById(R.id.search_list);
        listView.setVisibility(View.VISIBLE);
        listView.setAdapter(adapter);



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

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "   network status on receive");
                Toast.makeText(getApplicationContext(), "network status changes" + ConnectivityUtils.isConnected(getApplicationContext()), Toast.LENGTH_SHORT).show();
                updateUI();

            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(mReceiver, intentFilter);



      /*
       TODO: Rotation Fix Volley

       if (savedInstanceState != null) {
            savedsearchString = savedInstanceState.getString("searchStringKey");
            if (savedsearchString!=null && !savedsearchString.isEmpty()) {
                searchString = savedsearchString;
                loadingIndicator.setVisibility(View.VISIBLE);
                emptyTextView.setVisibility(View.INVISIBLE);
                volleyRequest(getSearchString(),0);
            }
        }*/


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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                searchString = ((TextView)view).getText().toString();

                Log.d(TAG, "item clicked is " + searchString);

                loadingIndicator.setVisibility(View.VISIBLE);
                emptyTextView.setVisibility(View.INVISIBLE);
                listView.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "item clicked is " + searchString, Toast.LENGTH_SHORT).show();
                volleyRequest(getUri(), 0);
            }
        });

    }

    private void updateUI() {
        if (ConnectivityUtils.isConnected(this)) {
            emptyTextView.setText(R.string.no_images);
            gridView.setVisibility(View.VISIBLE);
        } else {
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText(R.string.no_internet);
            gridView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        Uri.Builder uriBuilder = Utils.getUri(getSearchString());
        uriBuilder.appendQueryParameter("start", "" + offset);
        volleyRequest(uriBuilder.toString(), 1);

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
        handleIntent(intent);
    }

    // Handle query from seacrchWidget
    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchString = intent.getStringExtra(SearchManager.QUERY);
            Set<String> set = sharedPref.getStringSet(searchHistory, new HashSet<String>());
            Set<String> newSet = new HashSet<>();
            newSet.addAll(set);
            newSet.add(searchString);
            Log.d("Amit  ", "set is " + newSet);
            editor = getSharedPreferences(prefName, MODE_PRIVATE).edit();
            editor.putStringSet(searchHistory, newSet);
            editor.apply();
            loadingIndicator.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.INVISIBLE);
            volleyRequest(getUri(), 0);

        }
    }

    //Volley request for json string
    public void volleyRequest(String volleysearchString, final int addFlag) {

        /*
         * @params {Request type, url to be searched, responseHandler, errorHandler}
         */

        StringRequest stringRequest = new StringRequest(Request.Method.GET, volleysearchString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadingIndicator.setVisibility(View.GONE);
                        updateUIPostExecute(Utils.extractImages(response), addFlag);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loadingIndicator.setVisibility(View.GONE);
                //Toast.makeText(MainActivity.this, getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                imageGridAdapter.clear();
                emptyTextView.setVisibility(View.VISIBLE);

                String message = null;
                if (error instanceof NetworkError) {
                    message = getResources().getString(R.string.connection_error);
                } else if (error instanceof ServerError) {
                    message = getResources().getString(R.string.server_error);
                } else if (error instanceof AuthFailureError) {
                    message = getResources().getString(R.string.connection_error);
                } else if (error instanceof ParseError) {
                    message = getResources().getString(R.string.parse_error);
                } else if (error instanceof TimeoutError) {
                    message = getResources().getString(R.string.timeout_error);
                }

                emptyTextView.setText(message);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Client-ID 41a1c63ff97b89d");
                return headers;
            }
        };

        VolleySingleton.getInstance(this.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public void updateUIPostExecute(List<String> response, int addFlag) {


        if (addFlag == 0) {
            imageGridAdapter.clear();
            imageList.clear();
        }
        imageList.addAll(response);
        imageGridAdapter.notifyDataSetChanged();

    }

    /*
    TODO: Rotation Fix Volley

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        if (searchString != null && !searchString.isEmpty()) {
            outState.putString("searchStringKey", searchString);
        } else if(savedsearchString != null && !savedsearchString.isEmpty()){
                outState.putString("searchStringKey", savedsearchString);
        }
        super.onSaveInstanceState(outState);
    }*/


    public String getSearchString() {
        return searchString;
    }

    public String getUri() {
        return Utils.getUri(getSearchString()).toString();
    }
}
