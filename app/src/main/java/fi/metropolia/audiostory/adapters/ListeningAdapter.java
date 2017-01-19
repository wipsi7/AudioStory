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
    private ColorPicker colorPicker;

    public ListeningAdapter(Context mContext, List<SearchResponse> list){
        this.mContext = mContext;
        this.list = list;
        colorPicker = new ColorPicker();
    }


    @Override
    public int getCount() {
        return list.size();
    }


    //not used
    @Override
    public Object getItem(int position) {
        Log.w(DEBUG_TAG, "called Object getItem(int position): " + position );
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        Log.w(DEBUG_TAG, "called getItemId(int position): " + position );
        return position;
    }


    private static class ViewHolder{
        private LinearLayout llTagsColorContainer;
        private TextView tvFeelings;
        private ImageView ivPlayStop;
        private TextView tvTitle;
        private TextView tvLength;

        ViewHolder(View v){
            llTagsColorContainer = (LinearLayout) v.findViewById(R.id.ll_item_visual_feelings);
            tvFeelings = (TextView)v.findViewById(R.id.tv_item_feelings);
            ivPlayStop = (ImageView)v.findViewById(R.id.iv_item_playstop);
            tvTitle = (TextView)v.findViewById(R.id.tv_item_title);
            tvLength = (TextView)v.findViewById(R.id.tv_item_length);
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(DEBUG_TAG, "getView called, position is " + position);
        View row = convertView;

        if(row == null){
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_item, parent, false);
            viewHolder = new ViewHolder(row);
            row.setTag(viewHolder); // Saves viewHolder

            Log.d(DEBUG_TAG, "Creating new row" );
        } else{
            viewHolder = (ViewHolder)row.getTag();
            Log.d(DEBUG_TAG, "Reusing old row");
        }
        viewHolder.ivPlayStop.setImageResource(R.drawable.play_stop_button);
        viewHolder.tvFeelings.setText(list.get(position).getFeelingsTags());
        processFeelingsTags(list.get(position).getFeelingsTags());
        viewHolder.tvTitle.setText(list.get(position).getTitle());
        viewHolder.tvLength.setText(list.get(position).getStoryDuration());

        return row;
    }

    /** Split String tag into separate tags */
    private void processFeelingsTags(String tags) {
        String[] splitTags = tags.split(" ");
        initVisualFeelings(splitTags);
    }


    /** sets colors of feelings for llTagsColorContainer childs */
    private void initVisualFeelings(String[] tags) {

        int childViewCount = viewHolder.llTagsColorContainer.getChildCount();
        int tagsCount = tags.length;

        //makes all childViews gone of VisualLayout
        for(int i = 0; i < childViewCount; i++){
            viewHolder.llTagsColorContainer.getChildAt(i).setVisibility(View.GONE);
        }

        //makes VisualLayout child visible by depending number of tags and assign feeling color
        for(int i = 0; i < tagsCount; i++){
            String feeling = tags[i];
            String feelingColor = colorPicker.getMatched(feeling);
            viewHolder.llTagsColorContainer.getChildAt(i).setBackgroundColor(Color.parseColor(feelingColor));
            viewHolder.llTagsColorContainer.getChildAt(i).setVisibility(View.VISIBLE);
        }
    }
}
