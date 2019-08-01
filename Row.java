//Tia Hannes - Hanne123 - 5286175
//Thomas Rooney - Roone194 - 5364798

public class Row {

  Object[] obArray;
  InterpretedQuery query;
  Table table;

  public Row(InterpretedQuery Query){

    query = Query;
    obArray = new Object[query.getInsertValues().length];

    //initialize table to something, will get changed after loop
    table = Database.getTableArray()[0];

    for (int i = 0; i < Database.getNumTables(); i++){
      if (Database.getTableArray()[i].getTableName().equals(query.getTableName())){
        table = Database.getTableArray()[i];
      }
    }

  } //constructor

  public void setObArrayValues(){

    //First, change the types of the insert values if they are not of type String

    for (int k = 0; k < query.getInsertValues().length; k++) {
      if (table.getColumnTypes()[k].equals("int")){
        obArray[k] = Integer.parseInt(query.getInsertValues()[k]);
      }
      else if (table.getColumnTypes()[k].equals("double")){
        obArray[k] = Double.parseDouble(query.getInsertValues()[k]);
      }
      else if (table.getColumnTypes()[k].equals("boolean")){
        obArray[k] = Boolean.valueOf(query.getInsertValues()[k]);
      }
      else {
        obArray[k] = query.getInsertValues()[k];
      }
    }

  } //setRowArrayValues method

  public Object[] getObjectArray(){
    return obArray;
  }

} //class
