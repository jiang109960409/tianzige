package com.qifang.tianzige.domain;

import java.util.LinkedList;

/**
 * 字符分解实体
 *
 * 创建人：江亚宁
 * 创建日期：2018年12月26日
 */
public class CharacterDecompositionDO {

	private String									word;
	/**字的组成结构**/
	private String									composition;
	private LinkedList<CharacterDecompositionDO>	components;

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getComposition() {
		return composition;
	}

	public void setComposition(String composition) {
		this.composition = composition;
	}

	public LinkedList<CharacterDecompositionDO> getComponents() {
		return components;
	}

	public void setComponents(LinkedList<CharacterDecompositionDO> components) {
		this.components = components;
	}

}
