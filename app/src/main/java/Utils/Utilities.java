package Utils;

/**
 * Created by Thanh on 01-Apr-15. <br/>
 * Class providing some utility methods regarding time and other stuff
 */
public class Utilities {

    public static void delay(long milli) {
        long now = System.currentTimeMillis();
        long end = now + milli;

        while (now < end) {
            now = System.currentTimeMillis();
        }
        return;
    }

    /**
     * DElay until the timeStamp
     * @param timeStamp some time in the future
     */
    public static void delayUntil(long timeStamp) {
        long now = System.currentTimeMillis();
        while (now < timeStamp) {
            now = System.currentTimeMillis();
        }
    }
 }
