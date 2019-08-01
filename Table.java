//Tia Hannes - Hanne123 - 5286175
//Thomas Rooney - Roone194 - 5364798

public class Table {

  Row[] rowArray;
  String tableName;
  String[] columnNames;
  String[] columnTypes;
  int numRows;

  public Table(InterpretedQuery query){
    numRows = 0;
    rowArray = new Row[2];
    tableName = query.getTableName();
    columnNames = query.getColumnNames();
    columnTypes = query.getColumnTypes();
  }

  public String getTableName(){
    return tableName;
  }

  public String[] getColumnNames(){
    return columnNames;
  }

  public String[] getColumnTypes(){
    return columnTypes;
  }

  public Row[] getRowArray(){
    return rowArray;
  }

  public int getNumRows(){
    return numRows;
  }

  public int getRowArrayLength(){
    return rowArray.length;
  }

  public void addToRowArray(Row row){
    rowArray[numRows] = row;
    numRows++;
  }

  public void resizeRowArray(){
    Row[] temp = new Row[(rowArray.length * 2)];
    for (int k = 0; k < rowArray.length; k++){
      temp[k] = rowArray[k];
      }
    rowArray = temp;
    }//method to resize rowArray

  } //class
