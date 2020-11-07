package com.gm910.silvania.api.machinelearning;

import java.io.IOException;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;

public class Test {

	public static void main(String[] args) {

	}

	public Test() {

	}

	public static Dataset getDataSet(String filename) throws IOException {
		Dataset set = new DefaultDataset();
		return set;
	}

}
