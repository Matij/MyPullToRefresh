package com.martafoderaro.myswiperefresh;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.martafoderaro.myswiperefresh.RowElement;

import java.util.List;

/**
 * Created by MakeitappAndroid on 13/10/15.
 */
public class SwipeListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<RowElement> listElements;

    public SwipeListAdapter(Activity activity, List<RowElement> elementList){
        this.activity = activity;
        this.listElements = elementList;
    }

    @Override
    public int getCount() {
        return listElements.size();
    }

    @Override
    public Object getItem(int position) {
        return listElements.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.celltmplbase, null);

        TextView label1 = (TextView) convertView.findViewById(R.id.label1);
        TextView title = (TextView) convertView.findViewById(R.id.title);

        label1.setText(listElements.get(position).getTitle());
        title.setText(listElements.get(position).getSubtitle());

        return convertView;
    }
}
