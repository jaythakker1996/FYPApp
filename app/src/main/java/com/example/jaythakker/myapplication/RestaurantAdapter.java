package com.example.jaythakker.myapplication;

/**
 * Created by Kaushal on 15-01-2018.
 */

import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RestaurantAdapter extends ArrayAdapter<Restaurant> {
    ArrayList<Restaurant> restaurantList;
    LayoutInflater vi;
    int Resource;
    ViewHolder holder;

    public RestaurantAdapter(Context context, int resource, ArrayList<Restaurant> objects) {
        super(context, resource, objects);
        vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Resource = resource;
        restaurantList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // convert view = design
        View v = convertView;
        if (v == null) {
            holder = new ViewHolder();
            v = vi.inflate(Resource, null);
            //holder.imageview = (ImageView) v.findViewById(R.id.ivImage);
            holder.name = (TextView) v.findViewById(R.id.restaurantName);
            holder.area = (TextView) v.findViewById(R.id.restaurantArea);
            holder.location = (TextView) v.findViewById(R.id.restaurantLocation);
            holder.cuisine = (TextView) v.findViewById(R.id.restaurantCuisine);
            //holder.tvHeight = (TextView) v.findViewById(R.id.tvHeight);
            //holder.tvSpouse = (TextView) v.findViewById(R.id.tvSpouse);
            //holder.tvChildren = (TextView) v.findViewById(R.id.tvChildren);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        //holder.imageview.setImageResource(R.drawable.ic_launcher);
        //new DownloadImageTask(holder.imageview).execute(actorList.get(position).getImage());
        holder.name.setText(restaurantList.get(position).getName());
        holder.area.setText(restaurantList.get(position).getArea());
        holder.location.setText(restaurantList.get(position).getLatitude() + " : " + restaurantList.get(position).getLongitude());
        holder.cuisine.setText(restaurantList.get(position).getCuisine());
        //holder.tvHeight.setText("Height: " + actorList.get(position).getHeight());
        //holder.tvSpouse.setText("Spouse: " + actorList.get(position).getSpouse());
        //holder.tvChildren.setText("Children: " + restaurantList.get(position).getChildren());
        return v;

    }

    static class ViewHolder {
        //public ImageView imageview;
        public TextView name;
        public TextView area;
        public TextView location;
        public TextView cuisine;
        //public TextView tvHeight;
        //public TextView tvSpouse;
        //public TextView tvChildren;
    }

    /*private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }*/
}