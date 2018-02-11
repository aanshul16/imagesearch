package imageserach.fieldwire.amko0l.com.imagesearch.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.List;
import java.util.Map;

import imageserach.fieldwire.amko0l.com.imagesearch.R;
import imageserach.fieldwire.amko0l.com.imagesearch.adapter.MyImageHoldingRecyclerViewAdapter;
import imageserach.fieldwire.amko0l.com.imagesearch.utils.ConnectivityUtils;
import imageserach.fieldwire.amko0l.com.imagesearch.utils.Utils;
import imageserach.fieldwire.amko0l.com.imagesearch.utils.VolleySingleton;

import static imageserach.fieldwire.amko0l.com.imagesearch.utils.AppConstants.ARG_COLUMN_COUNT;
import static imageserach.fieldwire.amko0l.com.imagesearch.utils.AppConstants.SEARCH_STRING;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class ImageHolderFragment extends Fragment {

    private static final String TAG = "ImageHolderFragment";
    private int mColumnCount = 2;
    private OnImageHolderIInteractionListener mListener;
    private String searchString;
    BroadcastReceiver mReceiver;
    private IntentFilter intentFilter;
    private ProgressBar loadingIndicator;
    private RecyclerView recyclerView;
    private MyImageHoldingRecyclerViewAdapter myImageHoldingRecyclerViewAdapter;
    private TextView emptyTextView;
    private List<String> imageList;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ImageHolderFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ImageHolderFragment newInstance(int columnCount, String searchQuery) {
        ImageHolderFragment fragment = new ImageHolderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(SEARCH_STRING, searchQuery);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            searchString = getArguments().getString(SEARCH_STRING);
        }

        Utils.setContext(getActivity());

        imageList = new ArrayList<>();
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "   network status on receive");
                Toast.makeText(getActivity(), "network status changes" + ConnectivityUtils.isConnected(getActivity()), Toast.LENGTH_SHORT).show();
                updateUI();

            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list2, container, false);

        loadingIndicator = (ProgressBar) view.findViewById(R.id.loadIndicator);
        emptyTextView = (TextView) view.findViewById(R.id.empty);

        if (!ConnectivityUtils.isConnected(getActivity())) {
            emptyTextView.setText(R.string.no_internet);
        }

        // Set the adapter
        if (view instanceof LinearLayout) {
            Context context = view.getContext();
            recyclerView = view.findViewById(R.id.recyclerView);
            myImageHoldingRecyclerViewAdapter = new MyImageHoldingRecyclerViewAdapter(imageList, mListener, getActivity());
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            recyclerView.setAdapter(myImageHoldingRecyclerViewAdapter);
        }
        volleyRequest(getUri(), 0);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnImageHolderIInteractionListener) {
            mListener = (OnImageHolderIInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

        VolleySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(mReceiver, intentFilter);
    }

    public void updateUIPostExecute(List<String> response, int addFlag) {

        if (addFlag == 0) {
            imageList.clear();
        }
        imageList.addAll(response);
        myImageHoldingRecyclerViewAdapter.notifyDataSetChanged();
    }

    public String getSearchString() {
        return searchString;
    }

    public String getUri() {
        return Utils.getUri(getSearchString()).toString();
    }

    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        Uri.Builder uriBuilder = Utils.getUri(getSearchString());
        uriBuilder.appendQueryParameter("start", "" + offset);
        volleyRequest(uriBuilder.toString(), 1);
    }

    private void updateUI() {
        if (ConnectivityUtils.isConnected(getActivity())) {
            emptyTextView.setText(R.string.no_images);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText(R.string.no_internet);
            recyclerView.setVisibility(View.GONE);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnImageHolderIInteractionListener {
        void onListImageHolderInteraction(String imageUrl, ImageView imageView, ProgressBar progressBar);
    }
}
