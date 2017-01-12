package com.example.shoppingtalk;

import java.util.HashMap;
import java.util.Map;

public class UtilMap {
	private static UtilMap utilMap = new UtilMap();

	private Map<String, String> map = new HashMap<String, String>();

	public static UtilMap getInstance() {
		return utilMap;
	}

	/* ˽�й��췽������ֹ��ʵ���� */
	private UtilMap() {

	}

	public Map<String, String> init() {

		return map;
	}
}
