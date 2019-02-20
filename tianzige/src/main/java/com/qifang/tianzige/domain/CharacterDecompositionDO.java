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

	public CharacterDecompositionDO() {
	}

	public CharacterDecompositionDO(String word) {
		this.word = word;
	}

	public CharacterDecompositionDO(String word, String composition, LinkedList<CharacterDecompositionDO> components) {
		this.word = word;
		this.composition = composition;
		this.components = components;
	}

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

	@Override
	public String toString() {
		return "CharacterDecompositionDO [word=" + word + ", composition=" + composition + ", components=" + components
				+ "]";
	}

}
