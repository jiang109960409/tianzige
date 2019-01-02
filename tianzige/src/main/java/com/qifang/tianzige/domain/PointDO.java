package com.qifang.tianzige.domain;

/**
 * svg点的实体
 *
 * 创建人：江亚宁
 * 创建日期：2018年12月26日
 */
public class PointDO {

	private double	x;
	private double	y;

	public PointDO() {
	}

	public PointDO(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

}
