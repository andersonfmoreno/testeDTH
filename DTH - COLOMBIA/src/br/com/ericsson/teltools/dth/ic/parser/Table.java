package br.com.ericsson.teltools.dth.ic.parser;

import java.util.LinkedList;
import java.util.List;

public class Table {
	private String name;
	private Layout layout;
	private List<Record> records;

	public Table(String name) {
		if(name == null) {
			throw new RuntimeException("Nome da tabela nao pode ser nulo");
		}
		this.name = name;
		this.records = new LinkedList<Record>();
	}
	public void setLayout(Layout layout) {
		this.layout = layout;
	}
	public void addRecord(Record record) {
		if(this.layout == null) {
			throw new RuntimeException("Layout nao definido para esta tabela");
		}
		if(!this.layout.match(record)) {
			//throw new RuntimeException("Record mal formado");
		}
		this.records.add(record);
	}

	public String getName() {
		return this.name;
	}
	public Layout getLayout() {
		return this.layout;
	}
	public List<Record> getRecords() {
		return this.records;
	}
	public String toString() { 
		return this.name + "(" + this.layout + ")";
	}
}