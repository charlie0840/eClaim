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

public class CustomListt extends ArrayAdapter<Bitmap>{

    private final Activity context;
    private List<Bitmap> imageId;
    public CustomListt(Activity context, List<Bitmap> imageId) {
        super(context, R.layout.list_single, imageId);
        this.context = context;
        this.imageId = imageId;
    }
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        imageView.setImageBitmap(imageId.get(position));

        return rowView;
    }
}
