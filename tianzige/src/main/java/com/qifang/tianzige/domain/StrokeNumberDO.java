package com.qifang.tianzige.domain;

import java.util.LinkedList;

/**
 * 笔画数实体
 * 
 * 创建人：江亚宁
 * 创建日期：2018年12月26日
 */
public class StrokeNumberDO {

	/**总笔画数**/
	private int 				totalNumber;
	/**除去偏旁部首后的笔画数**/
	private int 				exceptRadicalNumber;
	/**字的结构**/
	private String 				composition;
	/**偏方部首**/
	private String 				radical;
	/**笔画顺序**/
	private LinkedList<String> 	strokes;

	public int getTotalNumber() {
		return totalNumber;
	}

	public void setTotalNumber(int totalNumber) {
		this.totalNumber = totalNumber;
	}

	public int getExceptRadicalNumber() {
		return exceptRadicalNumber;
	}

	public void setExceptRadicalNumber(int exceptRadicalNumber) {
		this.exceptRadicalNumber = exceptRadicalNumber;
	}

	public String getComposition() {
		return composition;
	}

	public void setComposition(String composition) {
		this.composition = composition;
	}

	public String getRadical() {
		return radical;
	}

	public void setRadical(String radical) {
		this.radical = radical;
	}

	public LinkedList<String> getStrokes() {
		return strokes;
	}

	public void setStrokes(LinkedList<String> strokes) {
		this.strokes = strokes;
	}

}
