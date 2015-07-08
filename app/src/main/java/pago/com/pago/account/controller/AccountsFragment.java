package pago.com.pago.account.controller;



import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import pago.com.pago.R;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class AccountsFragment extends Fragment {


    private ListView banksListView;

    public static AccountsFragment newInstance()
    {
        return new AccountsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accounts, container, false);
        // Inflate the layout for this fragment
        banksListView = (ListView) view.findViewById(R.id.restraunts_list_view);
        banksListView.setAdapter(new BanksAdapter());
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private class BanksAdapter extends BaseAdapter
    {

        private class ViewHolder
        {
            TextView bankName;
            TextView location;
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
                convertView = getActivity().getLayoutInflater().inflate(R.layout.banks_cell,null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.bankName = (TextView) convertView.findViewById(R.id.name_text_view);
                viewHolder.location = (TextView) convertView.findViewById(R.id.location_text_view);
                convertView.setTag(viewHolder);
            }
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            switch (position)
            {
                case 0:
                    viewHolder.bankName.setText("HDFC");
                    viewHolder.location.setText("Powai");
                    break;
                case 1:
                    viewHolder.bankName.setText("ICICI");
                    viewHolder.location.setText("Ghatkopar");
                    break;
                case 2:
                    viewHolder.bankName.setText("SBI");
                    viewHolder.location.setText("Andheri");
                    break;
            }
            return convertView;
        }
    }
}
