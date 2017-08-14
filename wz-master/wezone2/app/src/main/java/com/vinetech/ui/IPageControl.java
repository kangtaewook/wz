package com.vinetech.ui;

//PageControl �⺻Ʋ
public interface IPageControl {
	/**
	 * �������� ��ü ũ�⸦ �����մϴ�.
	 * @param size
	 */
	void setPageSize(int size);
	/**
	 * ������ ��ü ũ�⸦ �����մϴ�.
	 * @return
	 */
	int getPageSize();
	
	/**
	 * �������� ȣ���մϴ�.
	 * @param index
	 */
	void setPageIndex(int index);
	/**
	 * ���� �������� �����մϴ�.
	 * @return
	 */
	int getCurrentPageIndex();
}
