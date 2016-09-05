package messageit;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MsgLogger
{
	private static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm:ss.SSS");
	private static boolean showTime = false;
	
	public static void info( String s ) {
		if( isShowTime() )
			System.out.println("[" +
				sdf.format(new Date()) + "] " + s);
		else
			System.out.println( s );
	}

	public static void error( String s, Throwable t ) {
		info( s );
		t.printStackTrace(System.out);
	}

	public static void debug( String s ) {
		info( s );
	}

	public static void debug( String s, Throwable t ) {
		info( s );
		t.printStackTrace(System.out);
	}

    /**
     * @return
     */
    public static boolean isShowTime() {
        return showTime;
    }

    /**
     * @param b
     */
    public static void setShowTime(boolean b) {
        showTime = b;
    }

}
