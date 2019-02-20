package com.qifang.tianzige.domain;

/**
 * 字符编码实体
 *
 * 创建人：江亚宁
 * 创建日期：2018年12月26日
 */
public class CharacterEncodingDO {

	private String	siJiao;
	private String	cangJie;
	private String	kangXiNo;
	private String	wuBi;
	private String	zhengMa;
	private String	dianMa;
	private String	quWei;
	private String	penOrderNo;
	private String	unicode;

	public String getSiJiao() {
		return siJiao;
	}

	public void setSiJiao(String siJiao) {
		this.siJiao = siJiao;
	}

	public String getCangJie() {
		return cangJie;
	}

	public void setCangJie(String cangJie) {
		this.cangJie = cangJie;
	}

	public String getKangXiNo() {
		return kangXiNo;
	}

	public void setKangXiNo(String kangXiNo) {
		this.kangXiNo = kangXiNo;
	}

	public String getWuBi() {
		return wuBi;
	}

	public void setWuBi(String wuBi) {
		this.wuBi = wuBi;
	}

	public String getZhengMa() {
		return zhengMa;
	}

	public void setZhengMa(String zhengMa) {
		this.zhengMa = zhengMa;
	}

	public String getDianMa() {
		return dianMa;
	}

	public void setDianMa(String dianMa) {
		this.dianMa = dianMa;
	}

	public String getQuWei() {
		return quWei;
	}

	public void setQuWei(String quWei) {
		this.quWei = quWei;
	}

	public String getPenOrderNo() {
		return penOrderNo;
	}

	public void setPenOrderNo(String penOrderNo) {
		this.penOrderNo = penOrderNo;
	}

	public String getUnicode() {
		return unicode;
	}

	public void setUnicode(String unicode) {
		this.unicode = unicode;
	}

	@Override
	public String toString() {
		return "CharacterEncodingDO [siJiao=" + siJiao + ", cangJie=" + cangJie + ", kangXiNo=" + kangXiNo + ", wuBi="
				+ wuBi + ", zhengMa=" + zhengMa + ", dianMa=" + dianMa + ", quWei=" + quWei + ", penOrderNo="
				+ penOrderNo + ", unicode=" + unicode + "]";
	}

}
