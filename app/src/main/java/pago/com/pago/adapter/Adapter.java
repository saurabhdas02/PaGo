package pago.com.pago.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.net.Uri;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pago.com.pago.R;

public class Adapter extends BaseAdapter {
    private int[]mTo;
    private String[]mFrom;
    private ViewBinder mViewBinder;

    private ArrayList<HashMap<String, String>> mData;

    private int mResource;
    private int mDropDownResource;
    private LayoutInflater mInflater;

    //private SimpleFilter mFilter;
    private ArrayList<Map<String, ?>> mUnfilteredData;

    public Adapter(Context context, ArrayList<HashMap<String, String>> data,
                         int resource, String[] from, int[] to) {
        mData = data;
        mResource = mDropDownResource = resource;
        mFrom = from;
        mTo = to;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("Convert View","getView " + position + " " + convertView);

        ViewHolder holder = null;
        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.product_list, null);
            holder = new ViewHolder();

            holder.imgFavButton = (ImageView)convertView.findViewById(R.id.imgfavorite);
            holder.imgShareButton = (ImageView)convertView.findViewById(R.id.imgShare);
            convertView.setTag(holder);
            /* ImageView imgShareButton=(ImageView)v.findViewById(R.id.imgShare);
             ImageView imgFavButton=(ImageView)v.findViewById(R.id.imgfavorite);
             imgShareButton.setOnClickListener(imageClickListener);
             imgFavButton.setOnClickListener(imageClickListener);*/
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }

        //bindView(position, v);
        holder.imgFavButton.setOnClickListener(imageClickListener);
        holder.imgShareButton.setOnClickListener(imageClickListener);
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

     private View createViewFromResource(int position, View convertView,
               ViewGroup parent, int resource) {
         Log.d("Convert View","getView " + position + " " + convertView);

         ViewHolder holder = null;
         if (convertView == null) {

             convertView = mInflater.inflate(R.layout.product_list, null);
             holder = new ViewHolder();

             holder.imgFavButton = (ImageView)convertView.findViewById(R.id.imgfavorite);
             holder.imgShareButton = (ImageView)convertView.findViewById(R.id.imgShare);
            /* ImageView imgShareButton=(ImageView)v.findViewById(R.id.imgShare);
             ImageView imgFavButton=(ImageView)v.findViewById(R.id.imgfavorite);
             imgShareButton.setOnClickListener(imageClickListener);
             imgFavButton.setOnClickListener(imageClickListener);*/
         }
         else {
             holder = (ViewHolder)convertView.getTag();
         }

         //bindView(position, v);
         holder.imgFavButton.setOnClickListener(imageClickListener);
         holder.imgShareButton.setOnClickListener(imageClickListener);
         return convertView;
     }

    public static class ViewHolder {
        public ImageView imgShareButton;
        public ImageView imgFavButton;
    }

    OnClickListener imageClickListener =new OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.imgShare:
                    // your button is pressed on this view, position is stored in tag.
                    Integer positionPressed=(Integer)v.getTag();
                    String shareBody = "For more amazing offers, Click here http://bit.ly/1SuMkYc";
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Vito-");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    //startActivity(Intent.createChooser(sharingIntent, "Share via"));
                    break;

                case R.id.imgfavorite:
                    ImageView imgFav = (ImageView)v.findViewById(R.id.imgfavorite);
                    imgFav.setImageResource(R.drawable.ic_action_favorite_pinkfill);
                    // Toast.makeText(getActivity(),  + " connected!", Toast.LENGTH_LONG).show();
                   // Log.d("Position", String.valueOf(pos));
                    break;

                case R.id.imgLike:
                    break;

                case R.id.imgDislike:
                    break;
            }

        }
    };

    private void bindView(int position, View view) {
        final Map dataSet = mData.get(position);
        if (dataSet == null) {
            return;
        }

        final ViewBinder binder = mViewBinder;
        final String[] from = mFrom;
        final int[] to = mTo;
        final int count = to.length;

        for (int i = 0; i < count; i++) {
            final View v = view.findViewById(to[i]);
            if (v != null) {
                final Object data = dataSet.get(from[i]);
                String text = data == null ? "" : data.toString();
                if (text == null) {
                    text = "";
                }
                boolean bound = false;
                if (binder != null) {
                    bound = binder.setViewValue(v, data, text);
                }

                if (!bound) {
                    if (v instanceof Checkable) {
                        if (data instanceof Boolean) {
                            ((Checkable) v).setChecked((Boolean) data);
                        } else if (v instanceof TextView) {
                            // Note: keep the instanceof TextView check at the bottom of these
                            // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                            setViewText((TextView) v, text);
                        } else {
                            throw new IllegalStateException(v.getClass().getName() +
                                    " should be bound to a Boolean, not a " +
                                    (data == null ? "<unknown type>" : data.getClass()));
                        }
                    } else if (v instanceof TextView) {
                        // Note: keep the instanceof TextView check at the bottom of these
                        // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                        setViewText((TextView) v, text);
                    } else if (v instanceof ImageView) {
                        if (data instanceof Integer) {
                            setViewImage((ImageView) v, (Integer) data);
                        } else {
                            setViewImage((ImageView) v, text);
                        }
                    } else {
                        throw new IllegalStateException(v.getClass().getName() + " is not a " +
                                " view that can be bounds by this SimpleAdapter");
                    }
                }
            }
        }
    }

    public static interface ViewBinder {
        boolean setViewValue(View view, Object data, String textRepresentation);
    }

    public void setViewText(TextView v, String text) {
        v.setText(text);
    }

    public void setViewImage(ImageView v, int value) {
        v.setImageResource(value);
    }

    public void setViewImage(ImageView v, String value) {
        try {
            v.setImageResource(Integer.parseInt(value));
        } catch (NumberFormatException nfe) {
            v.setImageURI(Uri.parse(value));
        }
    }
}

