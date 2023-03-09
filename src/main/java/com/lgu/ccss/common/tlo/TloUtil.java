package com.lgu.ccss.common.tlo;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.MDC;

import com.lgu.common.tlo.TloWriter;

public class TloUtil {
	
	private static final Map<String, String> svcClassMap;
	
	static {
		svcClassMap = getSvcClassMap();
	}
	
	public static void setTloData(Map<String, String> tloMap) {
		
		if (TloWriter.getTloFieldSize() == 0) {
			TloWriter.setTloFieldMap(TloData.class.getDeclaredFields());
		}
		
		String exist = null;
		for (String key : tloMap.keySet()) {
			try {
				exist = TloWriter.getTloFieldValue(key);
			} catch (NullPointerException e) {
				
			}
			
			if (exist != null) {
				MDC.put(key, tloMap.get(key));
			}
		}
	}
	
	public static Map<String, String> getTloData() {
		Map<String, String> tloMap = new HashMap<String, String>();
		
		@SuppressWarnings("unchecked")
		Map<String, String> mdcMap = MDC.getCopyOfContextMap();
		for (String key : mdcMap.keySet()) {
			tloMap.put(key, mdcMap.get(key));
		}
		
		return tloMap;
	}
	
	public static Map<String, String> getSvcClassMap() {
		Map<String, String> svcClassMap = new HashMap<String, String>();
		
		svcClassMap.put(TloConst.PROCESS_SMSGW, TloConst.P001);

		return svcClassMap;
	}
	
	public static String getSvcClass(String key) {
		return svcClassMap.get(key);
	}
}