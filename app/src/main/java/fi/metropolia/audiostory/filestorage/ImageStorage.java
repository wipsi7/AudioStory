package fi.metropolia.audiostory.filestorage;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import fi.metropolia.audiostory.museum.Constant;

public class ImageStorage {

    private static final String DEBUG_TAG  = "ImageStorage";

    private Context context;
    private Bitmap image;
    private File file, dir;



    public ImageStorage(Context context){
        this.context = context;

        init();
    }

    private void init() {

        dir = context.getCacheDir();
        file = new File(dir, Constant.CACHE_FILE);

    }

    public void store(Bitmap bitmap){
        image = bitmap;

        if(file.exists()){
            Log.d(DEBUG_TAG, "File exists");
            file.delete();

        }

        try {
            file.createNewFile();
            FileOutputStream fileOutputStream = fileOutputStream = new FileOutputStream(file);
            if(bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)){
                Log.d(DEBUG_TAG, "Successfully stored file");
                fileOutputStream.close();
            }
        } catch (FileNotFoundException e) {
            Log.e(DEBUG_TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(DEBUG_TAG, e.getMessage());;
        }


        Log.d(DEBUG_TAG, "Listing cache files" + Arrays.toString(dir.list()));
    }

    public Bitmap loadImage(){
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

        return bitmap;
    }


}
