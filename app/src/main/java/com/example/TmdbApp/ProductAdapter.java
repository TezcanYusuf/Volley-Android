package com.example.TmdbApp;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class ProductAdapter extends ArrayAdapter<Product> {

    private View view;
    private LayoutInflater inflater;
    private ArrayList< Product > objects;
    private Context context;

    public static class ViewHolder {
        public TextView textView1;
        public ImageView imageView;
        public ImageView background;
        public RatingBar score;

    }

    //Constructor
    public ProductAdapter(Context context, ArrayList < Product > objects) {
        super(context, -1, objects);
        this.objects = objects;
        this.context=context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList < Product > getItems() {
        return objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        view = convertView;

        if (view == null) {
            view = inflater.inflate(R.layout.card_detail, parent, false);

            holder = new ViewHolder();
            holder.textView1 = (TextView) view.findViewById(R.id.team_name);
            holder.imageView = view.findViewById(R.id.team_icon);
            holder.background = view.findViewById(R.id.imageView2);
            holder.score = view.findViewById(R.id.ratingBar);
            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        Object item = getItem(position);
        populateForPerson(holder, (Product) item, position);

        return view;
    }

    private static final float BLUR_RADIUS = 25f;

    public Bitmap blur(Bitmap image) {
        if (null == image) return null;

        Bitmap outputBitmap = Bitmap.createBitmap(image);
        final RenderScript renderScript = RenderScript.create(context);
        Allocation tmpIn = Allocation.createFromBitmap(renderScript, image);
        Allocation tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap);

//Intrinsic Gausian blur filter
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);
        return outputBitmap;
    }

    public void populateForPerson(ViewHolder holder, Product person, int position) {
        String productName = person.getProductName();
        holder.textView1.setText(productName);
        Glide.with(context)
                .load("http://image.tmdb.org/t/p/w440_and_h660_face" + person.getProductPicture())
                .into(holder.imageView);
        Glide.with(context)
                .load("http://image.tmdb.org/t/p/w440_and_h660_face" + person.getProductPicture())
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(70, 5)))
                .into(holder.background);

        String voteText = person.getVote_average();
        if (voteText.equals("0"))
            holder.score.setVisibility(View.INVISIBLE);
        else
            holder.score.setRating(Float.parseFloat(voteText)/2);
    }



}