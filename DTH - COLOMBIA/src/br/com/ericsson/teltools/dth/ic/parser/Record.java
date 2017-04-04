package br.com.ericsson.teltools.dth.ic.parser;

import java.util.LinkedList;
import java.util.List;

public class Record {
	private String key;
	private List<String> values;

	public Record(String key) {
		if(key == null) {
			throw new RuntimeException("A chave do record nao pode ser nula");
		}
		this.key = key;
		this.values = new LinkedList<String>();
	}

	public void addNextValue(String value) {
		this.values.add(value);
	}

	public String getKey() {
		return this.key;
	}
	public List<String> getValues() {
		return values;
	}
	public String getValue(int index) {
		return values.get(index);
	}

	public String toString() { 
		return this.values.toString();
	}
}