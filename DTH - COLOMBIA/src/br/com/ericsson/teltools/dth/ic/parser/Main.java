package br.com.ericsson.teltools.dth.ic.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class Main {
	private final static String FILE_PATH = "C:/development/workspaces/default/Layouts/src/files_in/";
	public final static String SEP_A = "<=>";
	public final static String SEP_B = "<[|]>";
	public final static String SEP_C = "<:>";
	public final static String SEP_D = "<;>";
	public final static String SEP_E = "<[.]>";

	public static void main(String[] args) throws IOException {
		File folder = new File(FILE_PATH);
		File[] allFiles = folder.listFiles();
		for(File file : allFiles) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(file));
				Container container = new Container(file.getName());
				createHeaders(reader.readLine(), container);
				String line;
				while((line = reader.readLine()) != null) {
					createContent(line, container);
				}
				Html.createHtml(container);
			} catch(Exception e) {
				System.out.println("Erro ao coletar arquivo '" + file.getName() + "'. " +e.getMessage());
			} finally {
				if(reader != null) {
					reader.close();
				}
			}
		}
	}

	private static void createHeaders(String line, Container container) {
		String layoutContent = line.substring(line.indexOf(SEP_A) + SEP_A.length());
		for(String table : layoutContent.split(SEP_B)) {
			String[] tableDef = table.split(SEP_C);
			if(tableDef.length == 1) {
				throw new RuntimeException("Definicao de layout mal formada ["+table+"]");
			}
			Table t = new Table(tableDef[0]);
			Layout l = new Layout();
			for(String field : tableDef[1].split(SEP_D)) {
				l.addField(field);
			}
			t.setLayout(l);
			container.addTable(t);
		}
	}

	private static void createContent(String line, Container container) {
		String[] tableDef = line.split(SEP_A);
		if(tableDef.length == 1) {
			return;
		}
		Table t = container.getTable(tableDef[0]);
		if(t == null) {
			System.out.println("Tabela [" + tableDef[0] + "] nao definida (nao ha informacoes sobre o layout)");
			t = new Table(tableDef[0]);
			t.setLayout(Layout.DEFAULT_LAYOUT);
			container.addTable(t);
		}
		for(String table : tableDef[1].split(SEP_B)) {
			String key = table.substring(0, table.indexOf(SEP_C));
			String values = table.substring(table.indexOf(SEP_C) + SEP_C.length());
			for (String record : values.split(SEP_E)) {
				Record r = new Record(key);
				for(String recordVal : record.split(SEP_D)) {
					r.addNextValue(recordVal);
				}
				t.addRecord(r);
			}
		}
	}
}