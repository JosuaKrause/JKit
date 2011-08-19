/**
 * 
 */
package jkit.io.csv;

/**
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public interface CSVContext {

	CSVReader reader();

	String colName();

	String rowName();

	int row();

	int col();

}
