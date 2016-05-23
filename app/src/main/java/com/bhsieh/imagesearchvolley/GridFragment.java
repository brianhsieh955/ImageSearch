package com.bhsieh.imagesearchvolley;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Brian on 5/22/2016.
 */
public class GridFragment extends Fragment {

    private OnGridFragmentListener mListener; // interface listener
    private GridView gridView;  //GridView Object
    private ArrayList<String> images; // image URLs
    private ArrayList<String> names;  // image titles
    private String image;  // image URL
    private String name;  // image title
    private static final String TAG = "bth-grid";

    public GridFragment() {
        // Required empty public constructor
    }

    // check if Activity implemented the interface
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnGridFragmentListener) {
            mListener = (OnGridFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() +
                    " must implement OnDetailFragmentListener");
        }
    }

    // inflate layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grid, container, false);
    }

    // where actions take place
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // initiations
        if(names == null ) {  // instantiate data lists if does not exist yet
            images = new ArrayList<>();
            names = new ArrayList<>();
        }
        gridView = (GridView) getActivity().findViewById(R.id.gridView); // the grid
        Button queryButton = (Button) getActivity().findViewById(R.id.queryButton); // search button

        // if not a new search, retrieve old search term to refresh grid display
        if (names.size()>0 ) {// means this is not a brand new search
            String queryString = null;
            if(mListener != null) {
                queryString= mListener.onGetQueryTermFromActivity();
            }
            getDataAndDisplay(queryString);  // download images and draw the grid
        }

        // handle click event on search button
        // read in query term, and load data from internet
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();  // hide keyboard
                String queryString = getQueryString(); // build query
                if (queryString == "") {return;}  // empty entry, toast shown already
                getDataAndDisplay(queryString); // get data from web and display
            }
        });

        // handle item click event on gridView
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mListener != null) { // send image URL and title to MainActivity
                    mListener.onSendDataToMain(images.get(position), names.get(position));
                }
            }
        });
    }

    // clear interface listener
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // build search query from user input
    private String getQueryString() {

        // read user input
        EditText queryText = (EditText) getActivity().findViewById(R.id.queryText);
        String queryString = queryText.getText().toString()
                .trim(); // trim heading and trailing spaces
        if(queryString.length()==0) { // no search term entered
            Toast.makeText(getActivity(),"Please enter search term",
                    Toast.LENGTH_SHORT).show();
            return "";
        }

        // save query term to Activity, to persist it
        if(mListener != null) {
            mListener.onSaveQueryTermToActivity(queryString);
        }

        return queryString;
    }

    // hide soft keyboard
    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // get data from web, show on gridView
    private void getDataAndDisplay(String query) {
        // clear data lists before a new search
        images.clear();
        names.clear();
        // showing a progress dialog while our app fetches the data from url
        final ProgressDialog loading = ProgressDialog.show(getActivity(),
                "Please wait...", "Fetching data...", false, false);

        // build a queryString
        String queryEncoded = Uri.encode(query);
        String queryString = "https://www.googleapis.com/customsearch/v1?q=" + queryEncoded +
                "&cx=" + Constants.SEARCH_ENGINE_ID + "&searchType=image&key=" +
                Constants.API_KEY;
//        String queryString = "https://www.googleapis.com/customsearch/v1?q=Santa+Monica&cx=016375031836508228577%3An9k0fgafjxg&searchType=image&key=AIzaSyBrVW94yNV07IeA10kOBA-MYrbed091ElU";

        // prepare Volley request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                queryString, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss(); // cancel progress dialog
                        parseResponse(response); // parse JSON
                        displayGrid();  // display grid view
                    }
                },
                new Response.ErrorListener() { // no action
                    @Override
                    public void onErrorResponse(VolleyError error) {}
                }
        );

        // prepare Volley request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonObjectRequest);
    }

    // parse JSON to grid data lists,
    private void parseResponse(JSONObject jObjResult) {
        try {
            JSONArray jArrItems = jObjResult.getJSONArray(Constants.KEY_ITEMS);
            if(jArrItems.length()==0) {
                Toast.makeText(getActivity(), "No Item Found", Toast.LENGTH_SHORT).show();
            }
            for (int i = 0; i < jArrItems.length(); i++) {
                JSONObject jObjItem = jArrItems.getJSONObject(i);
                names.add(jObjItem.getString(Constants.KEY_TITLE));
                images.add(jObjItem.getString(Constants.KEY_LINK));
            }
        } catch (Exception e) {e.printStackTrace();}
    }

    // Draw gridView with data
    private void displayGrid() {
        GridViewAdapter gridViewAdapter = new GridViewAdapter(getActivity(), images, names);
        gridView.setAdapter(gridViewAdapter);
    }

    // fragment interface method declarations
    public interface OnGridFragmentListener {
        void onSendDataToMain(String image, String name);
        void onSaveQueryTermToActivity(String query);
        String onGetQueryTermFromActivity();
    }



}