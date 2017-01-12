package com.example.shoppingtalk;

import java.util.HashMap;
import java.util.Map;

public class UtilMap {
	private static UtilMap utilMap = new UtilMap();

	private Map<String, String> map = new HashMap<String, String>();

	public static UtilMap getInstance() {
		return utilMap;
	}

	/* 私有构造方法，防止被实例化 */
	private UtilMap() {

	}

	public Map<String, String> init() {

		return map;
	}
}
