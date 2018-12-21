package com.lesports.sms.download.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * Created by gengchengliang on 2015/4/27.
 */
public class FileUtil {

	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
	private static FileInputStream fis = null;
	private static FileChannel fc = null;
	private static FileLock flock = null;

	public static void lock(File xmlFile){
		try {
			fis = new FileInputStream(xmlFile);
			fc = fis.getChannel();
			flock = fc.tryLock();
			if(flock.isValid()){
				logger.info(xmlFile.getName()+ " is locked");
			}
		} catch (Exception e){
			logger.error(xmlFile.getName()+" lock error");
		}
	}

	public static void unlock(File xmlFile){
		try {
			flock.release();
			System.out.println(xmlFile.getName()+ "is released");
			fc.close();
			fis.close();
		} catch (Exception e){
			logger.error(xmlFile.getName()+" unlock error");
		}
	}
}
