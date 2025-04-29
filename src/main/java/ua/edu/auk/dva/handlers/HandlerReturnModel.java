package ua.edu.auk.dva.handlers;

import ua.edu.auk.dva.Table;

public class HandlerReturnModel {

  Table table;
  boolean success;

  public Table getTable() {
    return table;
  }

  public boolean isSuccess() {
    return success;
  }

  HandlerReturnModel(Table table) {
    this.table = table;
    this.success = true;
  }

  HandlerReturnModel(Table table, boolean success) {
    this.table = table;
    this.success = success;
  }

  HandlerReturnModel(boolean success) {
    this.success = success;
  }

}
