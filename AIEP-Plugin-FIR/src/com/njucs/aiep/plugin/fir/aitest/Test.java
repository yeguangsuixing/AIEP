package com.njucs.aiep.plugin.fir.aitest;

import com.njucs.aiep.plugin.AILoader;
import com.njucs.aiep.plugin.fir.FIRLoader;
import com.njucs.aiep.plugin.fir.ai.FIR_AI;

public class Test {
	public static void main(String[] args){//*
		FIR_AI ai = new TestFIR_AI();
		AILoader<FIR_AI> aiLoader = new FIRLoader();
		aiLoader.setAI(ai);
		aiLoader.setHostIp("127.0.0.1"); 
		aiLoader.setHostPort( 8890 );
		aiLoader.initialize();
		aiLoader.execute();//*/
	}
}
