package br.com.ericsson.teltools.dth.ic.parser;

import java.util.LinkedList;
import java.util.List;

public class Layout {
	public static final Layout DEFAULT_LAYOUT = new Layout();
	static {
		Layout.DEFAULT_LAYOUT.addField("key");
	}

	private List<String> fields;

	public Layout() {
		this.fields = new LinkedList<String>();
	}

	public void addField(String field) {
		this.fields.add(field);
	}
	public List<String> getFields() {
		return this.fields;
	}

	public boolean match(Record record) {
		if(record.getValues().size() != this.getFields().size()) {
			System.err.println();
			System.err.println("Erro ao validar record. Campos definidos no layout: " + this.fields.size() + ". Campos encontrados: " + record.getValues().size());
			System.err.println("-- Layout: " + this);
			System.err.println("-- Record: " + record);
			return false;
		}
		return true;
	}

	public String toString() { 
		return this.fields.toString();
	}
}