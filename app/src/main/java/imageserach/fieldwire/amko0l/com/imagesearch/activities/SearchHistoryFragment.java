package imageserach.fieldwire.amko0l.com.imagesearch.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import imageserach.fieldwire.amko0l.com.imagesearch.R;
import imageserach.fieldwire.amko0l.com.imagesearch.adapter.MyItemRecyclerViewAdapter;

import static android.content.Context.MODE_PRIVATE;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class SearchHistoryFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private String searchHistory = "SEARCH_HISTORY_FIELDWIRE";
    private String prefName = "SEARCH_HISTORY";
    private String searchString;
    private TextView emptyTextView;
    private MyItemRecyclerViewAdapter.OnListFragmentInteractionListener mListener;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    List<String> searchHistoryArray;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SearchHistoryFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MyItemRecyclerViewAdapter.OnListFragmentInteractionListener) {
            mListener = (MyItemRecyclerViewAdapter.OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException("Class Cast Excecption");
        }
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SearchHistoryFragment newInstance(int columnCount) {
        SearchHistoryFragment fragment = new SearchHistoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        sharedPref = getActivity().getSharedPreferences(prefName, MODE_PRIVATE);

        Set<String> historyset = sharedPref.getStringSet(searchHistory, new HashSet<String>());
        searchHistoryArray = new ArrayList<>();
        searchHistoryArray.addAll(historyset);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        emptyTextView = view.findViewById(R.id.empty);
        if (!searchHistoryArray.isEmpty()) {
            emptyTextView.setVisibility(View.GONE);
        }
        // Set the adapter
        if (view instanceof LinearLayout) {
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(new MyItemRecyclerViewAdapter(searchHistoryArray, mListener));
        }
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    // Handle query from seacrchWidget
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchString = intent.getStringExtra(SearchManager.QUERY);
            Set<String> set = sharedPref.getStringSet(searchHistory, new HashSet<String>());
            Set<String> newSet = new HashSet<>();
            newSet.addAll(set);
            newSet.add(searchString);
            Log.d("Amit  ", "set is " + newSet);
            editor = getActivity().getSharedPreferences(prefName, MODE_PRIVATE).edit();
            editor.putStringSet(searchHistory, newSet);
            editor.apply();
            emptyTextView.setVisibility(View.INVISIBLE);
        }
    }
}
