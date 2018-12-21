/*
 * This is  a part of the Video Resource System(VRS).
 * Copyright (C) 2010-2012 iqiyi.com Corporation
 * All rights reserved.
 *
 * Licensed under the iqiyi.com private License.
 */
package com.lesports.sms.data.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public abstract class MD5Util {

	private static final Logger logger = LoggerFactory.getLogger(MD5Util.class);

	private static ThreadLocal<MessageDigest> md5MessageDigestCache = new ThreadLocal<MessageDigest>() {
		@Override
		protected MessageDigest initialValue() {
			try {
				return MessageDigest.getInstance("md5");
			} catch (Exception ex) {
				logger.error("", ex);
			}
			return null;
		}
	};

	public static String md5(String str, String charset) {
		try {
			return bytes2Hex(md5(str.getBytes(charset)));
		} catch (Exception ex) {
			logger.error("", ex);
		}
		return null;
	}

	public static String md5_16(String str) {
		String md5_32 = bytes2Hex(md5(str.getBytes()));
		if (StringUtils.isBlank(md5_32)) {
			return null;
		}
		return md5_32.substring(8, 24);
	}

	/**
	 * 获得文件的md5值
	 * 
	 * @param file
	 * @return
	 */
	public static String fileMd5(File file) {
		if (file == null || !file.exists() || !file.isFile())
			return null;
		FileInputStream fis = null;
		byte[] buffer = new byte[1024];
		int numRead = 0;
		try {
			fis = new FileInputStream(file);
			MessageDigest md5 = md5MessageDigestCache.get();
			while ((numRead = fis.read(buffer)) > 0) {
				md5.update(buffer, 0, numRead);
			}
			return bytes2Hex(md5.digest());
		} catch (Exception ex) {
			logger.error("", ex);
			IOUtils.closeQuietly(fis);
		}
		return null;
	}

	public static String md5(String str) {
		return bytes2Hex(md5(str.getBytes()));
	}

	public static String md5Bytes2String(byte[] bytes) {
		return bytes2Hex(md5(bytes));
	}

	/**
	 * MD5加密
	 * 
	 * @param bytes
	 * @return
	 */
	private static byte[] md5(byte[] bytes) {
		try {
			MessageDigest md5 = md5MessageDigestCache.get();
			md5.update(bytes);
			return md5.digest();
		} catch (Exception ex) {
			// IGNORE exception
		}
		return null;
	}

	/**
	 * 将字节数组转换为16进制字符串表示
	 * 
	 * @param bytes
	 *            待转换的字节数组
	 * @return 16进制表示的字符串
	 */
	private static String bytes2Hex(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		String hex = null;
		for (int i = 0; i < bytes.length; i++) {
			hex = Integer.toHexString(bytes[i] & 0xFF);
			if (hex.length() == 1) {
				sb.append("0");
			}
			sb.append(hex);
		}
		return sb.toString();
	}

}
