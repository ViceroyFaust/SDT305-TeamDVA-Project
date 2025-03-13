package ua.edu.auk.dva;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing a table with a dynamic number of rows and a fixed number of columns
 *
 * @author Danylo Rybchynskyi
 */
public class Table {

  private final List<String> array;
  private final int cols;
  private final String name;

  /**
   * Instantiates a table with dynamic row count and a fixed number of columns. The table will be
   * given the name "unnamed".
   *
   * @param cols number of columns
   */
  public Table(int cols) {
    this(cols, "unnamed");
  }

  /**
   * Instantiates a table with dynamic row count and a fixed number of columns and a given name.
   *
   * @param cols number of columns required
   * @param name the name of the table
   */
  public Table(int cols, String name) {
    array = new ArrayList<>();
    this.cols = cols;
    this.name = name;
  }

  /**
   * Translates 2d coordinates to 1d coordinates
   *
   * @param row the row
   * @param col the column
   * @return 1d representation of 2d coordinates
   */
  private int translateCoordinates(int row, int col) {
    return col + row * cols;
  }

  /**
   * Returns whether we have a "rectangular" table
   *
   * @return true if the table is "rectangular"
   */
  public boolean isValid() {
    return array.size() % cols == 0;
  }

  /**
   * Adds a value to the table
   *
   * @param string value
   */
  public void add(String string) {
    array.add(string);
  }

  /**
   * Gets a value from a table cell
   *
   * @param row the row of the cell
   * @param col the column of the cell
   * @return the value found in the cell
   */
  public String get(int row, int col) {
    return array.get(translateCoordinates(row, col));
  }

  public int getCols() {
    return cols;
  }

  public int getRows() {
    return array.size() / cols;
  }

  public String getName() {
    return name;
  }
}
