package com.bhsieh.imagesearchvolley;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

public class DetailFragment extends Fragment {

    private OnDetailFragmentListener mListener; // interface listener
    private String image;  // image URL
    private String name;  // image title
    private static final String TAG = "bth-detail";

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");

        // Retrieve data from Arguments
        if(getArguments()!= null){
            image = getArguments().getString(Constants.KEY_LINK);
            name = getArguments().getString(Constants.KEY_TITLE);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Log.d(TAG, "onAttach");

        if (context instanceof OnDetailFragmentListener) {
            mListener = (OnDetailFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() +
                    " must implement OnDetailFragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "onActivityCreate");
        ImageLoader imageLoader;

        //Display image and title
        imageLoader = CustomVolleyRequest.getInstance(getActivity()).getImageLoader();
        ImageView imageView = (ImageView)getActivity().findViewById(R.id.detail_image);
        TextView textView = (TextView)getActivity().findViewById(R.id.detail_title);
        Button button = (Button) getActivity().findViewById(R.id.f2_button);

        //Get image from web with Volley
        imageLoader.get(image,ImageLoader.getImageListener(imageView,
                R.mipmap.ic_launcher, android.R.drawable.ic_dialog_alert));
        textView.setText(name);

        // display title
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGoToGridFragment();
            }
        });
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
        super.onDetach();
        mListener = null;
    }

    public interface OnDetailFragmentListener {
        void onBackToGrid();
    }

    void onGoToGridFragment() {
        if(mListener != null) {
            Log.d(TAG, "Calling Activity interface");
            mListener.onBackToGrid();
        }
    }


}
