package com.qifang.tianzige.domain;

import java.util.List;

/**
 * 字的基本解释实体
 *
 * 创建人：江亚宁
 * 创建日期：2018年12月26日
 */
public class BasicExplainDO {

	private String			basic;
	private List<String>	chineseEnglishTranslate;
	private List<String>	relatedWords;
	private String			formationOfCharacter;
	private String			english;

	public String getBasic() {
		return basic;
	}

	public void setBasic(String basic) {
		this.basic = basic;
	}

	public List<String> getChineseEnglishTranslate() {
		return chineseEnglishTranslate;
	}

	public void setChineseEnglishTranslate(List<String> chineseEnglishTranslate) {
		this.chineseEnglishTranslate = chineseEnglishTranslate;
	}

	public List<String> getRelatedWords() {
		return relatedWords;
	}

	public void setRelatedWords(List<String> relatedWords) {
		this.relatedWords = relatedWords;
	}

	public String getFormationOfCharacter() {
		return formationOfCharacter;
	}

	public void setFormationOfCharacter(String formationOfCharacter) {
		this.formationOfCharacter = formationOfCharacter;
	}

	public String getEnglish() {
		return english;
	}

	public void setEnglish(String english) {
		this.english = english;
	}
}
