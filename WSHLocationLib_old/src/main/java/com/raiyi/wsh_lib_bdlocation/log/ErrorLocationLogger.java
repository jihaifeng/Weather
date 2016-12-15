package com.raiyi.wsh_lib_bdlocation.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.os.Environment;

/**
 * @Description: 输出文件日志
 *
 * @author Yu Jingbo  
 *
 * @date 2014-10-29 下午5:20:25 
 *
 */
public class ErrorLocationLogger {
	
	private File logTxt;
	
	private static ErrorLocationLogger logger = new ErrorLocationLogger();
	
	private static final String DEFAULT_FILE_NAME = "raiyi_location.txt";
	
	private ErrorLocationLogger() {
		if (logTxt == null) {
			logTxt = new File(Environment.getExternalStorageDirectory(), DEFAULT_FILE_NAME);
		}
	}
	
	public static ErrorLocationLogger getLogger() {
		return logger;
	}
	
	public String getLog() {
		StringBuilder sb = new StringBuilder();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(logTxt);
			byte[] buf = new byte[4096];
			int len = -1;
			while ((len = fis.read(buf)) != -1) {
				sb.append(new String(buf, 0, len));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}
	
	@SuppressLint("SimpleDateFormat")
	public void log(String txt){
		doLog(txt);
	}
	
	public void doLog(String txt){
		FileWriter fw = null;
		try {
			fw = new FileWriter(logTxt, true);
			Date d = new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = format.format(d);
			fw.append(time + ": " + txt + "\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void clearAll() {
		FileWriter fw = null;
		try {
			fw = new FileWriter(logTxt, false);
			fw.write("");
		} catch (IOException e) {
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
