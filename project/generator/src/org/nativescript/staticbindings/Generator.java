package org.nativescript.staticbindings;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Generator {
	
	public static void main(String[] args) {
		try {
			new Generator().go(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void go(String[] args) throws IOException {
		if (args.length != 2) {
			throw new IllegalArgumentException("Expects two arguments");
		}
		String filename = args[0];
		String outputDir = args[1];
		
		List<DataRow> rows = getRows(filename);
		
		processRows(rows, outputDir);
	}
	
	private List<DataRow> getRows(String filename) throws IOException {
		List<DataRow> rows = new ArrayList<DataRow>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			String line;
			while ((line = br.readLine()) != null) {
				DataRow row = new DataRow(line);
				rows.add(row);
			}

		} finally {
			if (br != null) {
				br.close();
			}
		}
		return rows;
	}
	
	private void processRows(List<DataRow> rows, String outputDir) {
		for (DataRow r: rows) {
			System.out.println(r.getBaseClassname());
		}
	}
}
