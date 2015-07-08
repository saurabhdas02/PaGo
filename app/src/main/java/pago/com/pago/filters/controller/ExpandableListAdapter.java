package pago.com.pago.filters.controller;

/**
 * Created by Saurabh on 3/19/2015.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import pago.com.pago.R;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;
    private String str;

    private HashMap<Integer, boolean[]> mChildCheckStates;

    MainFilter mf = new MainFilter();

    public List<String> catList = new ArrayList<String>();
    public List<String> brandList = new ArrayList<String>();
    public List<String> merchantList = new ArrayList<String>();
    public List<String> couponTypeList = new ArrayList<String>();
    public List<String> bankList = new ArrayList<String>();

    private ChildViewHolder childViewHolder;
    private GroupViewHolder groupViewHolder;

    /*
          *  For the purpose of this document, I'm only using a single
     *	textview in the group (parent) and child, but you're limited only
     *	by your XML view for each group item :)
    */
    private String groupText;
    private String childText;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData, String cat) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this.str = cat;

        // Initialize our hashmap containing our check states here
        mChildCheckStates = new HashMap<Integer, boolean[]>();
    }

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final int mGroupPosition = groupPosition;
        final int mChildPosition = childPosition;

        childText = (String) getChild(mGroupPosition, mChildPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);

            childViewHolder = new ChildViewHolder();

            childViewHolder.mChildText = (TextView) convertView
                    .findViewById(R.id.childTextView);

            childViewHolder.mCheckBox = (CheckBox) convertView
                    .findViewById(R.id.checkBox);

            convertView.setTag(R.layout.list_item, childViewHolder);

        }
        else {

            childViewHolder = (ChildViewHolder) convertView
                    .getTag(R.layout.list_item);
        }

        childViewHolder.mChildText.setText(childText);


        /*
		 * You have to set the onCheckChangedListener to null
		 * before restoring check states because each call to
		 * "setChecked" is accompanied by a call to the
		 * onCheckChangedListener
		*/
        childViewHolder.mCheckBox.setOnCheckedChangeListener(null);

        if (mChildCheckStates.containsKey(mGroupPosition)) {
			/*
			 * if the hashmap mChildCheckStates<Integer, Boolean[]> contains
			 * the value of the parent view (group) of this child (aka, the key),
			 * then retrive the boolean array getChecked[]
			*/
            boolean getChecked[] = mChildCheckStates.get(mGroupPosition);

            // set the check state of this position's checkbox based on the
            // boolean value of getChecked[position]
            childViewHolder.mCheckBox.setChecked(getChecked[mChildPosition]);

        } else {

			/*
			 * if the hashmap mChildCheckStates<Integer, Boolean[]> does not
			 * contain the value of the parent view (group) of this child (aka, the key),
			 * (aka, the key), then initialize getChecked[] as a new boolean array
			 *  and set it's size to the total number of children associated with
			 *  the parent group
			*/
            boolean getChecked[] = new boolean[getChildrenCount(mGroupPosition)];

            // add getChecked[] to the mChildCheckStates hashmap using mGroupPosition as the key
            mChildCheckStates.put(mGroupPosition, getChecked);

            // set the check state of this position's checkbox based on the
            // boolean value of getChecked[position]
            childViewHolder.mCheckBox.setChecked(false);
            if (childText.equalsIgnoreCase(str)){
                childViewHolder.mCheckBox.setChecked(getChecked[mChildPosition]);
            }
        }

        childViewHolder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
                    getChecked[mChildPosition] = isChecked;
                    mChildCheckStates.put(mGroupPosition, getChecked);
                    if ( _listDataHeader.get(mGroupPosition) == "Category" ) {
                        catList.add(_listDataChild.get(_listDataHeader.get(mGroupPosition)).get(mChildPosition));
                        MainFilter obj = new MainFilter();
                        obj.GetBrands(_listDataHeader,_listDataChild,catList);
                    }
                    if ( _listDataHeader.get(mGroupPosition) == "Brand" ) {
                        brandList.add(_listDataChild.get(_listDataHeader.get(mGroupPosition)).get(mChildPosition));
                    }
                    if ( _listDataHeader.get(mGroupPosition) == "Merchant" ) {
                        merchantList.add(_listDataChild.get(_listDataHeader.get(mGroupPosition)).get(mChildPosition));
                    }
                    if ( _listDataHeader.get(mGroupPosition) == "Coupon Type" ) {
                        couponTypeList.add(_listDataChild.get(_listDataHeader.get(mGroupPosition)).get(mChildPosition));
                    }
                    if ( _listDataHeader.get(mGroupPosition) == "Bank" ) {
                        bankList.add(_listDataChild.get(_listDataHeader.get(mGroupPosition)).get(mChildPosition));
                    }


                } else {

                    boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
                    getChecked[mChildPosition] = isChecked;
                    mChildCheckStates.put(mGroupPosition, getChecked);
                    if ( _listDataHeader.get(mGroupPosition) == "Category" ) {
                        catList.remove(_listDataChild.get(_listDataHeader.get(mGroupPosition)).get(mChildPosition));
                    }
                    if ( _listDataHeader.get(mGroupPosition) == "Brand" ) {
                        brandList.remove(_listDataChild.get(_listDataHeader.get(mGroupPosition)).get(mChildPosition));
                    }
                    if ( _listDataHeader.get(mGroupPosition) == "Merchant" ) {
                        merchantList.remove(_listDataChild.get(_listDataHeader.get(mGroupPosition)).get(mChildPosition));
                    }
                    if ( _listDataHeader.get(mGroupPosition) == "Coupon Type" ) {
                        couponTypeList.remove(_listDataChild.get(_listDataHeader.get(mGroupPosition)).get(mChildPosition));
                    }
                    if ( _listDataHeader.get(mGroupPosition) == "Bank" ) {
                        bankList.remove(_listDataChild.get(_listDataHeader.get(mGroupPosition)).get(mChildPosition));
                    }

                }
            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        groupText = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater Inflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = Inflater.inflate(R.layout.list_group, null);

            // Initialize the GroupViewHolder defined at the bottom of this document
            groupViewHolder = new GroupViewHolder();

            groupViewHolder.mGroupText = (TextView) convertView.findViewById(R.id.groupTextView);

            convertView.setTag(groupViewHolder);
        }
        else {

            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        groupViewHolder.mGroupText.setText(groupText);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void notifyDataSetChanged()
    {
        // Refresh List rows
        super.notifyDataSetChanged();
    }

    public final class GroupViewHolder {

        TextView mGroupText;
    }

    public final class ChildViewHolder {

        TextView mChildText;
        CheckBox mCheckBox;
    }
    public void update(List _GroupStrings, HashMap<String, List<String>> _ChildStrings) {
        _listDataHeader = (ArrayList<String>) _GroupStrings;
        _listDataChild =  _ChildStrings;
    }
}

