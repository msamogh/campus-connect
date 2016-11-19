package com.msamogh.firstapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.msamogh.firstapp.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amogh on 7/8/15.
 */
public class SearchAdapter extends ArrayAdapter<ParseObject> {

    private final Context context;

    public SearchAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
        this.context = context;
    }

    private Filter filter;


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.search_item, parent, false);
        }

        final TextView venueAddress = (TextView) convertView
                .findViewById(R.id.search_item_venue_address);

        ImageView isSubscribed = (ImageView) convertView.findViewById(R.id.is_subscribed);

        final ParseObject community = this.getItem(position);


        convertView.setTag(community);
        CharSequence address = community.getString("communityId");

        venueAddress.setText(address);

        return convertView;

    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new VenueFilter(this);
        }
        return filter;
    }

    private class VenueFilter extends Filter {

        private final SearchAdapter searchAdapter;

        public VenueFilter(SearchAdapter adapter) {
            searchAdapter = adapter;
        }

        @Override
        protected FilterResults performFiltering(final CharSequence constraint) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Community").orderByAscending("communityId");
            final FilterResults result = new FilterResults();
            try {
                String constraintString = constraint.toString();
                List<ParseObject> list = query.find();
                // if no constraint is given, return the whole list

                // iterate over the list of venues and find if the venue matches the constraint. if it does, add to the result list
                final ArrayList<ParseObject> retList = new ArrayList();
                for (ParseObject community : list) {
                    if (community.getString("communityId").toLowerCase().contains(constraintString.toLowerCase())) {
                        retList.add(community);
                    }
                }
                result.values = retList;
                result.count = retList.size();


            } catch (ParseException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            try {
                ParseObject po = (ParseObject) resultValue;
                return po.getString("communityId");
            } catch (ClassCastException cce) {
                return resultValue.toString();
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // we clear the adapter and then populate it with the new results
            searchAdapter.clear();
            if (results.count > 0) {

                for (ParseObject o : (ArrayList<ParseObject>) results.values) {
                    searchAdapter.add(o);
                }
            }
        }

    }

}
