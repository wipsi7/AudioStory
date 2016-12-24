package fi.metropolia.audiostory.adapters;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import fi.metropolia.audiostory.R;
import fi.metropolia.audiostory.museum.ColorPicker;
import fi.metropolia.audiostory.search.SearchResponse;

public class ListeningAdapter extends BaseAdapter{

    private static final String DEBUG_TAG = "ListeningAdapter";
    private Context mContext;
    private List<SearchResponse> list;
    private ViewHolder viewHolder;

    public ListeningAdapter(Context mContext, List<SearchResponse> list){
        this.mContext = mContext;
        this.list = list;

    }



    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    private static class ViewHolder{
        private LinearLayout llVisualFeelings;
        private TextView tvFeelings;
        private ImageView ivPlayStop;
        private TextView tvTitle;
        private TextView tvLength;
        private int position;

        ViewHolder(View v){
            llVisualFeelings = (LinearLayout)v.findViewById(R.id.ll_item_visual_feelings);
            tvFeelings = (TextView)v.findViewById(R.id.tv_item_feelings);
            ivPlayStop = (ImageView)v.findViewById(R.id.iv_item_playstop);
            tvTitle = (TextView)v.findViewById(R.id.tv_item_title);
            tvLength = (TextView)v.findViewById(R.id.tv_item_length);

        }

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {



        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, parent, false);

            viewHolder = new ViewHolder(convertView);

            Log.d(DEBUG_TAG, "Creating new row, row id is " + position );
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
            Log.d(DEBUG_TAG, "Reusing old row, row id is" + position);
        }

        viewHolder.position = position;
        viewHolder.ivPlayStop.setImageResource(R.drawable.play_stop_button);
        viewHolder.tvFeelings.setText(list.get(position).getTags());
        processTags(list.get(position).getTags());
        viewHolder.tvTitle.setText(list.get(position).getTitle());
        viewHolder.tvLength.setText("03:21:00");


        return convertView;
    }

    private void processTags(String tags) {
        String[] splitTags = tags.split(" ");
        initVisualFeelings(splitTags);
    }


    private void initVisualFeelings(String[] tags) {


        ColorPicker colorPicker = new ColorPicker();

        int childViewCount = viewHolder.llVisualFeelings.getChildCount();
        int tagsCount = tags.length;


        //makes all childViews gone of VisualLayout
        for(int i = 0; i < childViewCount; i++){
            viewHolder.llVisualFeelings.getChildAt(i).setVisibility(View.GONE);
        }

        //makes VisualLayout child visible by depending number of tags and assign feeling color
        for(int i = 0; i < tagsCount; i++){
            String color = tags[i];
            color = colorPicker.getMatched(color);
            viewHolder.llVisualFeelings.getChildAt(i).setBackgroundColor(Color.parseColor(color));
            viewHolder.llVisualFeelings.getChildAt(i).setVisibility(View.VISIBLE);
        }
    }
}
