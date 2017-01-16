package fi.metropolia.audiostory.museum;


public final class Constant {
    /** Constants for retrieving data from nfc tag **/
    public static final int USERNAME_INDEX = 0;
    public static final int PASSWORD_INDEX = 1;
    public static final int COLLECTION_ID_INDEX = 2;
    public static final int ARTIFACT_INDEX = 3;

    /** Constants for passing and retrieving data from bundle */
    public static final String EXTRA_BUNDLE_DATA = "ExtraBundleData";
    public static final String BUNDLE_USER = "BundleUser";
    public static final String BUNDLE_PASS = "BundlePass";
    public static final String BUNDLE_ID = "BundleId";
    public static final String BUNDLE_API = "BundleApi";
    public static final String BUNDLE_ARTIFACT = "BundleArtifact";
    public static final String BUNDLE_STORY_TITLE = "BundleStoryTitle";
    public static final String BUNDLE_WAV_PATH = "BundleWavPath";
    public static final String BUNDLE_FEELINGS = "BundleFeelings";
    public static final String BUNDLE_TYPE = "BundleType";
    public static final int BUNDLE_RECORD = 1343252;
    public static final int BUNDLE_LISTEN = 1232523;


    public static final int MESSAGE_PLAY_FINISH = 34531;

    /** File names **/
    public static final String CACHE_FILE = "cacheImageFile.jpg";
}
