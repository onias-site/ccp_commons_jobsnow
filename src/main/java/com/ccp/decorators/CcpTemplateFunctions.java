package com.ccp.decorators;

import java.util.function.Supplier;

public enum CcpTemplateFunctions implements Supplier<String>{
	currentTimeMillis {
		public String get() {
			long currentTimeMillis = System.currentTimeMillis();
			String valorMais = "" + currentTimeMillis;
			return valorMais;
		}

	};
}
