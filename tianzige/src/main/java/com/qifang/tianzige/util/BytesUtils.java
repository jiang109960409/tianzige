package com.qifang.tianzige.util;

/**
 * 关于字节数组编码转换的工具类
 *
 * 创建人：江亚宁
 * 创建日期：2018年12月28日
 */
public class BytesUtils {
	private final static byte[] hex = "0123456789ABCDEF".getBytes();  

	/**
	 * 字节数组转化16进制数
	 * @param b
	 * @return
	 */
	public static String bytes2HexString(byte[] b) {
		byte[] buff = new byte[2 * b.length];
		for (int i = 0; i < b.length; i++) {
			buff[2 * i] = hex[(b[i] >> 4) & 0x0f];
			buff[2 * i + 1] = hex[b[i] & 0x0f];
		}
		return new String(buff);
	}
}
