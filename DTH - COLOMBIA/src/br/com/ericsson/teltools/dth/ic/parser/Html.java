package br.com.ericsson.teltools.dth.ic.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Html {
	private final static String FILE_PATH = "C:/development/workspaces/default/Layouts/src/files_out/";

	public static void createHtml(Container container) throws IOException {
		File f = new File(FILE_PATH, container.getFilename() + ".html");
		f.createNewFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		writer.append("<html><head><title>Teste</title><style type=\"text/css\">th{padding: 3px 10px;}</style></head><body><div id=\"container\">");
		appendContent(writer, container);
		writer.append("</div></body></html>");
	}

	private static void appendContent(BufferedWriter writer, Container container) throws IOException {
		for(Table t : container.getTables()) {
			writer.append("<h3>"+t.getName()+"</h3>");
			writer.append("<table border=\"1\">");
			writer.append("<tr>");
			writer.append("<th>Chave</th>");
			for(String field : t.getLayout().getFields()) {
				writer.append("<th>"+field+"</th>");	
			}
			writer.append("</tr>");
			
			for(Record r : t.getRecords()) {
				writer.append("<tr>");
				writer.append("<td>" + r.getKey() + "</td>");
				for(String value : r.getValues()) {
					writer.append("<td>"+value+"</td>");
				}
				writer.append("</tr>");
			}
			writer.append("</table>");
		}
	}
}