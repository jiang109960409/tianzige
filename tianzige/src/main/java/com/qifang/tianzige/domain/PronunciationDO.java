package com.qifang.tianzige.domain;

/**
 * 发音实体
 *
 * 创建人：江亚宁
 * 创建日期：2018年12月26日
 */
public class PronunciationDO {

	/**普通话声调标记**/
	private String	mandarinToneMark;
	/**普通话声调数字**/
	private String	mandarinToneNumber;
	/**广东话发音**/
	private String	jyutpingCantonese;

	public PronunciationDO() {
	}

	public PronunciationDO(String mandarinToneMark, String mandarinToneNumber) {
		this.mandarinToneMark = mandarinToneMark;
		this.mandarinToneNumber = mandarinToneNumber;
	}

	public String getMandarinToneMark() {
		return mandarinToneMark;
	}

	public void setMandarinToneMark(String mandarinToneMark) {
		this.mandarinToneMark = mandarinToneMark;
	}

	public String getMandarinToneNumber() {
		return mandarinToneNumber;
	}

	public void setMandarinToneNumber(String mandarinToneNumber) {
		this.mandarinToneNumber = mandarinToneNumber;
	}

	public String getJyutpingCantonese() {
		return jyutpingCantonese;
	}

	public void setJyutpingCantonese(String jyutpingCantonese) {
		this.jyutpingCantonese = jyutpingCantonese;
	}

}
