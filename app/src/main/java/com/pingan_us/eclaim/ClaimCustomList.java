package com.pingan_us.eclaim;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ClaimCustomList extends ArrayAdapter<String>{

    private final Activity context;
    private List<String> web;
    public ClaimCustomList(Activity context, List<String> web) {
        super(context, R.layout.claim_list_single, web);
        this.context = context;
        this.web = web;
    }
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.claim_list_single, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.claim_list_single_txt);
        txtTitle.setText(web.get(position));
        return rowView;
    }
}