package com.qifang.tianzige.domain;

import java.util.LinkedList;
import java.util.List;

/**
 * 字的实体
 *
 * 创建人：江亚宁
 * 创建日期：2018年12月26日
 */
public class WordDO {

	/**字的Unicode值变为10进制**/
	private int								id;
	/**字的utf8的值**/
	private String							idUTF8;
	private String							wordName;
	private StrokeNumberDO					strokeNumber;
	private LinkedList<LinkedList<PointDO>>	strokeOrderSvg;
	private List<PronunciationDO>			pronunciation;
	private CharacterEncodingDO				characterEncoding;
	private CharacterDecompositionDO		characterDecomposition;
	private BasicExplainDO					basicExplain;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getWordName() {
		return wordName;
	}

	public void setWordName(String wordName) {
		this.wordName = wordName;
	}

	public StrokeNumberDO getStrokeNumber() {
		return strokeNumber;
	}

	public void setStrokeNumber(StrokeNumberDO strokeNumber) {
		this.strokeNumber = strokeNumber;
	}

	public LinkedList<LinkedList<PointDO>> getStrokeOrderSvg() {
		return strokeOrderSvg;
	}

	public void setStrokeOrderSvg(LinkedList<LinkedList<PointDO>> strokeOrderSvg) {
		this.strokeOrderSvg = strokeOrderSvg;
	}

	public List<PronunciationDO> getPronunciation() {
		return pronunciation;
	}

	public void setPronunciation(List<PronunciationDO> pronunciation) {
		this.pronunciation = pronunciation;
	}

	public CharacterEncodingDO getCharacterEncoding() {
		return characterEncoding;
	}

	public void setCharacterEncoding(CharacterEncodingDO characterEncoding) {
		this.characterEncoding = characterEncoding;
	}

	public CharacterDecompositionDO getCharacterDecomposition() {
		return characterDecomposition;
	}

	public void setCharacterDecomposition(CharacterDecompositionDO characterDecomposition) {
		this.characterDecomposition = characterDecomposition;
	}

	public BasicExplainDO getBasicExplain() {
		return basicExplain;
	}

	public void setBasicExplain(BasicExplainDO basicExplain) {
		this.basicExplain = basicExplain;
	}

	public String getIdUTF8() {
		return idUTF8;
	}

	public void setIdUTF8(String idUTF8) {
		this.idUTF8 = idUTF8;
	}

}
