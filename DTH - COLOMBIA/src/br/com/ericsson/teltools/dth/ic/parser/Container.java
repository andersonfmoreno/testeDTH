package br.com.ericsson.teltools.dth.ic.parser;

import java.util.LinkedList;
import java.util.List;

public class Container {
	private String fileName;
	private List<Table> tables;

	public Container(String fileName) {
		if(fileName == null) {
			throw new RuntimeException("Nome do arquivo nao pode ser nulo");
		}
		this.fileName = fileName;
		this.tables = new LinkedList<Table>();
	}

	public void addTable(Table table) {
		this.tables.add(table);
	}

	public String getFilename() {
		return this.fileName;
	}
	public List<Table> getTables() {
		return tables;
	}
	public Table getTable(String name) {
		for(Table t : this.tables) {
			if(t.getName().equalsIgnoreCase(name)) {
				return t;
			}
		}
		return null;
	}

	public String toString() { 
		return this.tables.toString();
	}
}