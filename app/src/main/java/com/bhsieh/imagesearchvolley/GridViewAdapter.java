package com.bhsieh.imagesearchvolley;

/**
 * Created by Brian on 5/22/2016.
 */

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;


public class GridViewAdapter extends BaseAdapter {

    private ImageLoader imageLoader;  // for Volley
    private Context context;

    //Array Lists of image urls and their names/titles
    private ArrayList<String> images;
    private ArrayList<String> names;

    // constructor with context and data sets
    public GridViewAdapter (Context context, ArrayList<String> images,
                            ArrayList<String> names){
        this.context = context;
        this.images = images;
        this.names = names;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // initializations for Volley
        NetworkImageView networkImageView = new NetworkImageView(context); // imageView
        imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();

        // get image from web, and load it to grid cell's image view
        imageLoader.get(images.get(position),
                ImageLoader.getImageListener(networkImageView, R.mipmap.ic_launcher,
                        android.R.drawable.ic_dialog_alert));
        networkImageView.setImageUrl(images.get(position),imageLoader);
        networkImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        networkImageView.setLayoutParams(new GridView.LayoutParams(200,200));

        // display title, shorten it if needed
        String formattedTitle = (names.get(position).length() < 14) ?
                names.get(position) :
                names.get(position).substring(0,9) + "...";
        TextView textView = new TextView(context);
        textView.setText(formattedTitle);

        // create dynamic layout for grid cell
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(textView);
        linearLayout.addView(networkImageView);

        //Return the view layout
        return linearLayout;
    }
}
