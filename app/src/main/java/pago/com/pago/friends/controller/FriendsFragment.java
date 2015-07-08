package pago.com.pago.friends.controller;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import pago.com.pago.R;


public class FriendsFragment extends Fragment implements AdapterView.OnItemClickListener {


    private static final String FRIENDS_DETAIL_SCREEN_FRAGMENT = "FRIENDS_DETAIL_SCREEN_FRAGMENT";
    private ListView friendsListView;

    public static FriendsFragment newInstance() {
        return new FriendsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        friendsListView = (ListView) view.findViewById(R.id.restraunts_list_view);
        friendsListView.setAdapter(new FriendsAdapter());
        friendsListView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String name = "";
        String value = "";
        switch (position)
        {
            case 0:
                name = "Arun Patwardhan";
                value = "Arun123";
                break;
            case 1:
                name = "Govind Dholakiya";
                value = "Govind856";
                break;
            case 2:
                name = "Jaspreet Singh";
                value = "Jaspreet549";
                break;
        }

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fragment_slide_right_to_center,R.anim.fragment_slide_center_to_left,R.anim.fragment_slide_left_to_center,R.anim.fragment_slide_center_to_right);
        fragmentTransaction.replace(R.id.container, FriendDetailFragment.newInstance(name,value), FRIENDS_DETAIL_SCREEN_FRAGMENT);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private class FriendsAdapter extends BaseAdapter
    {

        private class ViewHolder
        {
            TextView friendName;
            TextView payruID;
        }
        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null)
            {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.friends_cell,null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.friendName = (TextView) convertView.findViewById(R.id.name_text_view);
                viewHolder.payruID = (TextView) convertView.findViewById(R.id.location_text_view);
                convertView.setTag(viewHolder);
            }
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            switch (position)
            {
                case 0:
                    viewHolder.friendName.setText("Arun Patwardhan");
                    viewHolder.payruID.setText("Arun123");
                    break;
                case 1:
                    viewHolder.friendName.setText("Govind Dholakiya");
                    viewHolder.payruID.setText("Govind856");
                    break;
                case 2:
                    viewHolder.friendName.setText("Jaspreet Singh");
                    viewHolder.payruID.setText("Jaspreet549");
                    break;
            }
            return convertView;
        }
    }

}
