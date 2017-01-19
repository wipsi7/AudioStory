package fi.metropolia.audiostory.museum;


import java.util.HashMap;

public class ColorPicker {

    private final static String DEBUG_TAG = "ColorPicker";

    private String interest;
    private String attraction;
    private String surprise;
    private String hope;
    private String gratitude;
    private String joy;
    private String relief;
    private String pride;
    private String love;

    private HashMap<String, String> colorsList;


    public ColorPicker(){
        init();
    }

    private void init() {

        colorsList = new HashMap<>();

        //good feeleings colors and values
        colorsList.put("interest", "#8393c7");
        colorsList.put("attraction", "#f48372");
        colorsList.put("surprise", "#f7902e");
        colorsList.put("hope", "#f7db8d");
        colorsList.put("gratitude", "#4bc0b3");
        colorsList.put("joy", "#fdcf07");
        colorsList.put("relief", "#abd68e");
        colorsList.put("pride", "#8a2994");
        colorsList.put("love", "#f8b4cb");

        //bad feelings colors and values
        colorsList.put("panic", "#ff3e3e");
        colorsList.put("disgust", "#7c683a");
        colorsList.put("indifference", "#a9a9a9");
        colorsList.put("fear", "#0f3350");
        colorsList.put("anger", "#a92045");
        colorsList.put("sorrow", "#585858");
        colorsList.put("shame", "#2a853b");
        colorsList.put("frustration", "#e58c00");
        colorsList.put("hate", "#000000");

    }



    /** Returns color hex as String */
    public String getMatched(String colorFeeling){
        colorFeeling = colorFeeling.toLowerCase();
        return colorsList.get(colorFeeling);
    }
}
