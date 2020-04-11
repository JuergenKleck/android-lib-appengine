package info.simplyapps.appengine;

public class ScreenKit {

    // assume this is the 100% value
    public final static int height = 960;
    public final static int width = 540;

    public static int scaleWidth(int value, int screen) {
        return value * screen / width;
    }

    public static int scaleHeight(int value, int screen) {
        return value * screen / height;
    }

    public static int unscaleWidth(int value, int screen) {
        return Float.valueOf((Float.valueOf(value) / Float.valueOf(screen)) * Float.valueOf(width)).intValue();
    }

    public static int unscaleHeight(int value, int screen) {
        return Float.valueOf((Float.valueOf(value) / Float.valueOf(screen)) * Float.valueOf(height)).intValue();
    }

}
