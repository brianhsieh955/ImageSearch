package com.bhsieh.imagesearchvolley;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity
        implements DetailFragment.OnDetailFragmentListener,
        GridFragment.OnGridFragmentListener {
    private static final String TAG = "bth-Main";
    private String queryTerm;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();

        // show grid fragment as the main entry point
        if (savedInstanceState == null) {
            Log.d(TAG, "main onCreate");
            fragmentManager.beginTransaction()
                    .add(R.id.container_1, new GridFragment())
                    .commit();
        }
    }

    // interface method from GridFragment
    // pass selected image item data to DetailFragment
    @Override
    public void onSendDataToMain(String image, String name) {

        Log.d(TAG, "ready to show detail: URL= " + image + " Title= " + name);
        DetailFragment detailFragment = new DetailFragment();

        // pass data in DetailFragment's Arguments
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_LINK, image);
        bundle.putString(Constants.KEY_TITLE, name);
        detailFragment.setArguments(bundle);

        // display DetailFragment with animation
        FragmentTransaction fragTrans = fragmentManager.beginTransaction();
        fragTrans.setCustomAnimations(
                android.R.anim.slide_in_left,android.R.anim.slide_out_right,
                android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        fragTrans.replace(R.id.container_1, detailFragment);
        fragTrans.addToBackStack(null);
        fragTrans.commit();
    }

    // interface method from DetailFragment
    // pop DetailFragment and re-display GridFragment
    @Override
    public void onBackToGrid() {
        // delete detail fragment, display grid fragment
        Log.d(TAG, "ready to close detail and back to grid");
        fragmentManager.popBackStack();
        // re-draw grid
    }

    // interface method from GridFragment
    // persist/save fragment data in this activity
    @Override
    public void onSaveQueryTermToActivity(String query) {
        queryTerm = query; // current query term
    }

    // interface method from GridFragment
    // send persisted data back to GridFragment
    @Override
    public String onGetQueryTermFromActivity() {
        return queryTerm; // last query term
    }
}
