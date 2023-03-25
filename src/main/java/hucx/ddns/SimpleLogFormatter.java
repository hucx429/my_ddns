package hucx.ddns;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * 版权声明：本文为CSDN博主「10km」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
 * 原文链接：https://blog.csdn.net/10km/article/details/127761415
 */
public class SimpleLogFormatter extends Formatter {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

	@Override
	public String format(LogRecord record) {
		String message = formatMessage(record);
		String throwable = "";
		if (record.getThrown() != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			pw.println();
			record.getThrown().printStackTrace(pw);
			pw.close();
			throwable = "\n" + sw;
		}
		Thread currentThread = Thread.currentThread();
		StackTraceElement stackTrace = currentThread.getStackTrace()[8];
		return String.format("%s - [%s] (%22s) %s%s\n",
				sdf.format(new Date()),
				Thread.currentThread().getName(),
				stackTrace.getClassName(),
				message,
				throwable);
	}
}
