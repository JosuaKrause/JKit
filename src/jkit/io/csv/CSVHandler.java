/**
 * 
 */
package jkit.io.csv;

/**
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public interface CSVHandler {

	void start(CSVContext ctx);

	void colTitle(CSVContext ctx, String title);

	void rowTitle(CSVContext ctx, String title);

	void cell(CSVContext ctx, String content);

	void row(CSVContext ctx);

	void end(CSVContext ctx);

}
