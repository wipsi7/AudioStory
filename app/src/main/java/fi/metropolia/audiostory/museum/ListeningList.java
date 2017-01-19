package fi.metropolia.audiostory.museum;


import android.util.Log;

import java.util.ArrayList;

import fi.metropolia.audiostory.search.SearchResponse;


/** Class for creating list that matches selected tags of artifact **/
public class ListeningList {

    private static final String DEBUG_TAG = "ListeningList";

    private SearchResponse[][] searchResponses;
    private String tags[];
    private ArrayList<SearchResponse> playList;

    private int numberOfTags;
    private int numberOfStories;

    private String tempTags;


    public ListeningList(SearchResponse[][] searchResponses, String[] tags){
        this.searchResponses = searchResponses;
        this.tags = tags;
        numberOfTags = tags.length;
        numberOfStories = searchResponses.length;
        playList = new ArrayList<>();

        addMatchedToPlayList();

        Log.d(DEBUG_TAG, "Printing matched ");
        for(int i= 0; i < playList.size(); i++){
            Log.d(DEBUG_TAG, "Matched: " + playList.get(i).getTitle());
        }
    }

    private void addMatchedToPlayList() {
        switch (numberOfTags){
            case 1:
                for(int i = 0; i < numberOfStories; i++){
                    tempTags = searchResponses[i][0].getFeelingsTags();
                    if(tempTags.contains(tags[0])){
                        playList.add(searchResponses[i][0]);
                    }
                }
                break;

            case 2:
                for(int i = 0; i < numberOfStories; i++){
                    tempTags = searchResponses[i][0].getFeelingsTags();
                    if(tempTags.contains(tags[0]) || tempTags.contains(tags[1])){
                        playList.add(searchResponses[i][0]);
                    }
                }
                break;

            case 3:
                for(int i = 0; i < numberOfStories; i++){
                    tempTags = searchResponses[i][0].getFeelingsTags();
                    if(tempTags.contains(tags[0]) || tempTags.contains(tags[1]) || tempTags.contains(tags[2])){
                        playList.add(searchResponses[i][0]);
                    }
                }
                break;
        }
    }

    public ArrayList<SearchResponse> returnFilteredList(){
        return playList;
    }

}
