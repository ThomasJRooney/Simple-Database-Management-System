//Tia Hannes - Hanne123 - 5286175
//Thomas Rooney - Roone194 - 5364798

import java.io.File;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.FileNotFoundException;

public class Database {

  static Table[] tableArray;
  static int numTables;

  public Database() {
    tableArray = new Table[2];
    numTables = 0;
  }//Database constructor

  public static void resizeTableArray(){
    Table[] temp = new Table[(tableArray.length * 2)];
    for (int k = 0; k < tableArray.length; k++){
      temp[k] = tableArray[k];
    }
    tableArray = temp;
  }//method to resize tableArray

  public static Table[] getTableArray(){
    return tableArray;
  }//getTableArray Method

  public static int getNumTables(){
    return numTables;
  }//getNumTables Method

  public static void create(InterpretedQuery query) {
    Table newTable = new Table(query);
    boolean valid = true;

    //first check to see if a table with the same name already exists in the database

    if(valid){
      for (int i = 0; i < numTables; i++){
        if (tableArray[i].getTableName().equals(newTable.getTableName())){
          System.out.println(newTable.getTableName() + " already exists in the database.");
          valid = false;
        }
      }
    }

    //Next check that all column names are unique

    if(valid){
      for (int a = 0; a < (newTable.getColumnNames().length - 1); a++){
        for (int b = a + 1; b < (newTable.getColumnNames().length); b++){
          if (newTable.getColumnNames()[a].equals(newTable.getColumnNames()[b])) {
            System.out.println(newTable.getColumnNames()[a] + " is not a unique column name.");
            valid = false;
          }
        }
      }
    }

    /*
    *Lastly, check to see that all of the column types are valid. The valid types are:
    *int
    *double
    *String
    *boolean
    */

    if(valid){
      for (int c = 0; c < newTable.getColumnTypes().length; c++){
        if (!(newTable.getColumnTypes()[c].equals("int") || newTable.getColumnTypes()[c].equals("double") || newTable.getColumnTypes()[c].equals("String") || newTable.getColumnTypes()[c].equals("boolean"))) {
          System.out.println(newTable.getColumnTypes()[c] + " is not a valid column type");
          valid = false;
        }
      }
    }

     /*
     * if a table with that name does not already exist in the database,
     * all of the column names are unique,
     * and the column types are valid,
     * add it to the database.
     */

     if (numTables == tableArray.length - 1){
       resizeTableArray();
     }

     if (valid) {
       tableArray[numTables] = newTable;
       numTables++;
     }

   } //create method


  public static void insert(InterpretedQuery query) {
    Table table = tableArray[0];
    boolean inDatabase = true;

    /*
    * First, check and see which table, the user would like to insert
    * a row with the specified values into
    */

    for(int i = 0; i < numTables; i++){
      if (tableArray[i].getTableName().equals(query.getTableName())){
        table = tableArray[i];
      }
    }

    if(!table.getTableName().equals(query.getTableName())){
      System.out.println(query.getTableName() + " does not exist in the database.");
      inDatabase = false;
    }


    if(inDatabase){

    //create a new row object:

      Row row = new Row(query);

    //set values of the row:

      row.setObArrayValues();

    /*
    * Before you insert a new row into the row array, or the table object
    * check to see if you need to resize array
    */

      if (table.getNumRows() == table.getRowArrayLength() - 1){
        table.resizeRowArray();
      }

      //add row to the specified table

      table.addToRowArray(row);

    }//inDatabase if statement
  } //insert method

  public static void load(InterpretedQuery query) throws FileNotFoundException{
    String fileName = query.getFileName();
    File file = new File(fileName);
    boolean inDatabase = false;
    Table table = tableArray[0];

    //cut off the .db from the table name
    String[] temp = fileName.split("\\.");
    String tableName = temp[0];


    //Check to see if the file we want to load already exists in database

    for(int i = 0; i < numTables; i++){
      if(tableArray[i].getTableName().equals(tableName)){
        System.out.println(tableName + " already exists in the database.");
        inDatabase = true;
      }
    }



    if(!inDatabase){
      try{
        Scanner s = new Scanner(file);

        //Get column names as a String[]
        String cNames = s.nextLine();
        String[] columnNames = cNames.split(",");

        //Get column Types as a String[]
        String cTypes = s.nextLine();
        String[] columnTypes = cTypes.split(",");

        //Make an interpreted Query to create a new table
        InterpretedQuery newQuery = new InterpretedQuery(QueryType.CREATE_STATEMENT, tableName, columnNames, columnTypes);
        create(newQuery);

        //While there is still rows, split the rows into String[]
        while(s.hasNextLine()){
          String nextLine = s.nextLine();
          String[] insertValues = nextLine.split(",");

          //make an Interpreted Query in order to make a row to be inserted
          InterpretedQuery insertRow = new InterpretedQuery(QueryType.INSERT_STATEMENT, tableName, insertValues);
          insert(insertRow);

        }
        s.close();
      } catch(FileNotFoundException ex){
        System.out.println(query.getFileName() + " is not a valid file.");
      }
    }
  }//load method

  public static void store(InterpretedQuery query) {

    //get the table name and name the file the same name as the table
    String fileName = query.getTableName() + ".db";
    PrintWriter p = null;
    boolean valid = true;
    boolean inDatabase = true;
    Table table = tableArray[0]; //initialize table object, will get reassigned after the for loop

    //First, check to see if the table exists in the database.

    for(int i = 0; i < numTables; i++){
      if (tableArray[i].getTableName().equals(query.getTableName())){
        table = tableArray[i];
      }
    }

    if(!table.getTableName().equals(query.getTableName())){
      System.out.println(query.getTableName() + " does not exist in the database.");
      inDatabase = false;
    }

    //Next check to see if the file name is valid

    if (inDatabase) {
      try {
        p = new PrintWriter(new File(fileName));
      } catch (Exception e) {
        System.out.println(query.getTableName() + " , file name not valid.");
        valid = false; //change valid to false if the file name is invalid
      }
    }

    //Lastly, store the table if it is a valid file name and exists in the database

    if(inDatabase && valid){

      //First write the Column Names to the File as the first line

      for (int i = 0; i < table.getColumnNames().length; i++){
        p.print(table.getColumnNames()[i] + ",");
      }
      p.println("");

      //Next write the Column Types to the File as the second nextLine

      for (int k = 0; k < table.getColumnTypes().length; k++){
        p.print(table.getColumnTypes()[k] + ",");
      }
      p.println("");


      //Lastly write all of the rows from the correct table to the file

      for (int a = 0; a < table.getNumRows(); a++){
        for(int b = 0; b < table.getColumnNames().length; b++){
          p.print(table.getRowArray()[a].getObjectArray()[b] + ",");
        }
        p.println("");
      }
      p.close();
    } //if valid && inDatabase
  } //store method

  public static void print(InterpretedQuery query) {

    Table table = tableArray[0]; //initialize a table object, will immediately get reassigned
    boolean inDatabase = true;

    //Check to see which table the user would like to print the contents of

    for(int i = 0; i < numTables; i++){
      if (tableArray[i].getTableName().equals(query.getTableName())){
        table = tableArray[i];
      }
    }

    //Check to see if the table exists in the Database

    if(!table.getTableName().equals(query.getTableName())){
      System.out.println(query.getTableName() + " does not exist in the database.");
      inDatabase = false;
    }

    if(inDatabase){

      //First print out the Column Names

      for (int i = 0; i < table.getColumnNames().length; i++){
        System.out.print(table.getColumnNames()[i] + ",");
      }
      System.out.println("");

      //Next print out the Column Types

      for (int k = 0; k < table.getColumnTypes().length; k++){
        System.out.print(table.getColumnTypes()[k] + ",");
      }
      System.out.println("");


      //Lastly print out all of the rows from the correct table to the file

      for (int a = 0; a < table.getNumRows(); a++){
        for(int b = 0; b < table.getColumnNames().length; b++){
          System.out.print(table.getRowArray()[a].getObjectArray()[b] + ",");
        }
        System.out.println("");
      }
    } //if valid
  }//print method

  public static void select(InterpretedQuery query) {

    boolean inDatabase = true;
    Table table = tableArray[0]; //initialize a table object, will get reassigned to the table that corresponds with the query
    String conditional;
    String conditionType = "String"; //will get reassigned
    String[] conditionArray;

    //First get the table

    for (int z = 0; z < numTables; z++){
      if (tableArray[z].getTableName().equals(query.getTableName())){
        table = tableArray[z];
      }
    }

    if (!(table.getTableName().equals(query.getTableName()))){
      inDatabase = false; //if the table is not in the database, print out a message indicating so
    }

    if (inDatabase) {

      conditional = query.getConditional();
      /*
      *create a condition Array delimited by empty spaces.
      * the 0 index will be the column names
      * the 1 index will be the conditional, ex.) "=", "<="
      *the 2 index will be the condition
      */

      conditionArray = conditional.split(" ");

      //Next find what type the condition is

      for (int y = 0; y < table.getColumnNames().length; y++) {
        if (table.getColumnNames()[y].equals(conditionArray[0])){
           conditionType = table.getColumnTypes()[y];
        }
      }

      //First print out the the column Names
      for (int e = 0; e < query.getColumnNames().length; e++){
        System.out.print(query.getColumnNames()[e] + ",");
      }
      System.out.println("");

      //Next print out all of the column types
      for (int f = 0; f < query.getColumnNames().length; f++){
        for (int k =0; k < table.getColumnNames().length; k++) {
          if (query.getColumnNames()[f].equals(table.getColumnNames()[k])) {
            System.out.print(table.getColumnTypes()[k] + ",");
          }
        }
      }
      System.out.println("");

      //Run different switch statements depending on the conditionType

      switch (conditionType) {

        case "int" : switch (conditionArray[1]) {

          case "<=" :

            for (int a = 0; a < table.getNumRows(); a++) { //loop through the number of rows in the table
              for (int b = 0; b < table.getRowArray()[a].getObjectArray().length; b++){ //loop through a single row
                if (table.getRowArray()[a].getObjectArray()[b] instanceof Integer){ //check if the object is an instance of the desired type
                  if (table.getColumnNames()[b].equals(conditionArray[0]) && //check if the column name at the index of the object meets the condition
                  (((table.getRowArray()[a].getObjectArray()[b]).equals(Integer.valueOf(conditionArray[2]))) || (((int)table.getRowArray()[a].getObjectArray()[b])) < (Integer.valueOf(conditionArray[2])))) {
                    for (int c = 0; c < query.getColumnNames().length; c++){ //loop through the query column names
                      for (int d = 0; d < table.getColumnNames().length; d++){ //loop through table column names to get index of the specified columns
                        if (query.getColumnNames()[c].equals(table.getColumnNames()[d])){
                          System.out.print(table.getRowArray()[a].getObjectArray()[d] + ","); //print out the desired column value at that row
                        }
                      }
                    }
                    System.out.println(""); //new line
                  }
                }
              }
            }
          break;

          case "<" :

            for (int a = 0; a < table.getNumRows(); a++) {
              for (int b = 0; b < table.getRowArray()[a].getObjectArray().length; b++){
                if (table.getRowArray()[a].getObjectArray()[b] instanceof Integer){
                  if (table.getColumnNames()[b].equals(conditionArray[0]) &&
                  (((int)table.getRowArray()[a].getObjectArray()[b]) < (Integer.valueOf(conditionArray[2])))) {
                    for (int c = 0; c < query.getColumnNames().length; c++){
                      for (int d = 0; d < table.getColumnNames().length; d++){
                        if (query.getColumnNames()[c].equals(table.getColumnNames()[d])){
                          System.out.print(table.getRowArray()[a].getObjectArray()[d] + ",");
                        }
                      }
                    }
                    System.out.println("");
                  }
                }
              }
            }
          break;

          case "=" :

            for (int a = 0; a < table.getNumRows(); a++) {
              for (int b = 0; b < table.getRowArray()[a].getObjectArray().length; b++){
                if (table.getColumnNames()[b].equals(conditionArray[0]) &&
                table.getRowArray()[a].getObjectArray()[b].equals(Integer.valueOf(conditionArray[2]))) {
                //^^Checks to see if the condition is met for that row

                  for (int c = 0; c < query.getColumnNames().length; c++){
                    for (int d = 0; d < table.getColumnNames().length; d++){
                      if (query.getColumnNames()[c].equals(table.getColumnNames()[d])){
                        System.out.print(table.getRowArray()[a].getObjectArray()[d] + ",");
                      }
                    }
                  }
                  System.out.println("");
                }
              }
            }
          break;

          case "!=" :

            for (int a = 0; a < table.getNumRows(); a++) {
              for (int b = 0; b < table.getRowArray()[a].getObjectArray().length; b++){
                if (table.getColumnNames()[b].equals(conditionArray[0]) &&
                !(table.getRowArray()[a].getObjectArray()[b].equals(Integer.valueOf(conditionArray[2])))) {
                  for (int c = 0; c < query.getColumnNames().length; c++){
                    for (int d = 0; d < table.getColumnNames().length; d++){
                      if (query.getColumnNames()[c].equals(table.getColumnNames()[d])){
                        System.out.print(table.getRowArray()[a].getObjectArray()[d] + ",");
                      }
                    }
                  }
                  System.out.println("");
                }
              }
            }
          break;

          case ">" :

            for (int a = 0; a < table.getNumRows(); a++) {
              for (int b = 0; b < table.getRowArray()[a].getObjectArray().length; b++){
                if (table.getRowArray()[a].getObjectArray()[b] instanceof Integer){
                  if (table.getColumnNames()[b].equals(conditionArray[0]) &&
                  (((int)table.getRowArray()[a].getObjectArray()[b]) > (Integer.valueOf(conditionArray[2])))) {
                    for (int c = 0; c < query.getColumnNames().length; c++){
                      for (int d = 0; d < table.getColumnNames().length; d++){
                        if (query.getColumnNames()[c].equals(table.getColumnNames()[d])){
                          System.out.print(table.getRowArray()[a].getObjectArray()[d] + ",");
                        }
                      }
                    }
                    System.out.println("");
                  }
                }
              }
            }
          break;

          case ">=" :

            for (int a = 0; a < table.getNumRows(); a++) {
              for (int b = 0; b < table.getRowArray()[a].getObjectArray().length; b++){
                if (table.getRowArray()[a].getObjectArray()[b] instanceof Integer){
                  if (table.getColumnNames()[b].equals(conditionArray[0]) &&
                  (((table.getRowArray()[a].getObjectArray()[b]).equals(Integer.valueOf(conditionArray[2]))) || (((int)table.getRowArray()[a].getObjectArray()[b])) > (Integer.valueOf(conditionArray[2])))) {
                    for (int c = 0; c < query.getColumnNames().length; c++){
                      for (int d = 0; d < table.getColumnNames().length; d++){
                        if (query.getColumnNames()[c].equals(table.getColumnNames()[d])){
                          System.out.print(table.getRowArray()[a].getObjectArray()[d] + ",");
                        }
                      }
                    }
                    System.out.println("");
                  }
                }
              }
            }
          break;
          } //int
        break;

        case "boolean" : switch (conditionArray[1]) {

          case "=" :

            for (int a = 0; a < table.getNumRows(); a++) {
              for (int b = 0; b < table.getRowArray()[a].getObjectArray().length; b++){
                if (table.getColumnNames()[b].equals(conditionArray[0]) &&
                (table.getRowArray()[a].getObjectArray()[b].equals(Boolean.valueOf(conditionArray[2])))){
                  for (int c = 0; c < query.getColumnNames().length; c++){
                    for (int d = 0; d < table.getColumnNames().length; d++){
                      if (query.getColumnNames()[c].equals(table.getColumnNames()[d])){
                        System.out.print(table.getRowArray()[a].getObjectArray()[d] + ",");
                      }
                    }
                  }
                  System.out.println("");
                }
              }
            }
          break;


          case "!=" :

            for (int a = 0; a < table.getNumRows(); a++) {
              for (int b = 0; b < table.getRowArray()[a].getObjectArray().length; b++){
                if (table.getColumnNames()[b].equals(conditionArray[0]) &&
                !(table.getRowArray()[a].getObjectArray()[b].equals(Boolean.valueOf(conditionArray[2])))) {
                  for (int c = 0; c < query.getColumnNames().length; c++){
                    for (int d = 0; d < table.getColumnNames().length; d++){
                      if (query.getColumnNames()[c].equals(table.getColumnNames()[d])){
                        System.out.print(table.getRowArray()[a].getObjectArray()[d] + ",");
                      }
                    }
                  }
                  System.out.println("");
                }
              }
            }
          break;
          }//boolean
        break;

        case "double" : switch (conditionArray[1]) {

          case "<=" :

            for (int a = 0; a < table.getNumRows(); a++) {
              for (int b = 0; b < table.getRowArray()[a].getObjectArray().length; b++){
                if (table.getRowArray()[a].getObjectArray()[b] instanceof Double){
                  if (table.getColumnNames()[b].equals(conditionArray[0]) &&
                  (((table.getRowArray()[a].getObjectArray()[b]).equals(Double.valueOf(conditionArray[2]))) || (((double)table.getRowArray()[a].getObjectArray()[b])) < (Double.valueOf(conditionArray[2])))) {
                    for (int c = 0; c < query.getColumnNames().length; c++){
                      for (int d = 0; d < table.getColumnNames().length; d++){
                        if (query.getColumnNames()[c].equals(table.getColumnNames()[d])){
                          System.out.print(table.getRowArray()[a].getObjectArray()[d] + ",");
                        }
                      }
                    }
                    System.out.println("");
                  }
                }
              }
            }
          break;

          case "<" :

            for (int a = 0; a < table.getNumRows(); a++) {
              for (int b = 0; b < table.getRowArray()[a].getObjectArray().length; b++){
                if (table.getRowArray()[a].getObjectArray()[b] instanceof Double){
                  if (table.getColumnNames()[b].equals(conditionArray[0]) &&
                  (((double)table.getRowArray()[a].getObjectArray()[b]) < (Double.valueOf(conditionArray[2])))) {
                    for (int c = 0; c < query.getColumnNames().length; c++){
                      for (int d = 0; d < table.getColumnNames().length; d++){
                        if (query.getColumnNames()[c].equals(table.getColumnNames()[d])){
                          System.out.print(table.getRowArray()[a].getObjectArray()[d] + ",");
                        }
                      }
                    }
                    System.out.println("");
                  }
                }
              }
            }
          break;

          case "=" :

            for (int a = 0; a < table.getNumRows(); a++) {
              for (int b = 0; b < table.getRowArray()[a].getObjectArray().length; b++){
                if (table.getColumnNames()[b].equals(conditionArray[0]) &&
                table.getRowArray()[a].getObjectArray()[b].equals(Double.valueOf(conditionArray[2]))) {
                  for (int c = 0; c < query.getColumnNames().length; c++){
                    for (int d = 0; d < table.getColumnNames().length; d++){
                      if (query.getColumnNames()[c].equals(table.getColumnNames()[d])){
                        System.out.print(table.getRowArray()[a].getObjectArray()[d] + ",");
                      }
                    }
                  }
                  System.out.println("");
                }
              }
            }
          break;

          case "!=" :

          for (int a = 0; a < table.getNumRows(); a++) {
            for (int b = 0; b < table.getRowArray()[a].getObjectArray().length; b++){
              if (table.getColumnNames()[b].equals(conditionArray[0]) &&
              !(table.getRowArray()[a].getObjectArray()[b].equals(Double.valueOf(conditionArray[2])))) {
                for (int c = 0; c < query.getColumnNames().length; c++){
                  for (int d = 0; d < table.getColumnNames().length; d++){
                    if (query.getColumnNames()[c].equals(table.getColumnNames()[d])){
                      System.out.print(table.getRowArray()[a].getObjectArray()[d] + ",");
                    }
                  }
                }
                System.out.println("");
              }
            }
          }
        break;

            case ">=" :

              for (int a = 0; a < table.getNumRows(); a++) {
                for (int b = 0; b < table.getRowArray()[a].getObjectArray().length; b++){
                  if (table.getRowArray()[a].getObjectArray()[b] instanceof Double) {
                    if (table.getColumnNames()[b].equals(conditionArray[0]) &&
                    (((table.getRowArray()[a].getObjectArray()[b]).equals(Double.valueOf(conditionArray[2]))) || (((double)table.getRowArray()[a].getObjectArray()[b])) > (Double.valueOf(conditionArray[2])))) {
                      for (int c = 0; c < query.getColumnNames().length; c++){
                        for (int d = 0; d < table.getColumnNames().length; d++){
                          if (query.getColumnNames()[c].equals(table.getColumnNames()[d])){
                            System.out.print(table.getRowArray()[a].getObjectArray()[d] + ",");
                          }
                        }
                      }
                      System.out.println("");
                    }
                  }
                }
              }
            break;

            case ">" :

              for (int a = 0; a < table.getNumRows(); a++) {
                for (int b = 0; b < table.getRowArray()[a].getObjectArray().length; b++){
                  if (table.getRowArray()[a].getObjectArray()[b] instanceof Double){
                    if (table.getColumnNames()[b].equals(conditionArray[0]) &&
                    (((double)table.getRowArray()[a].getObjectArray()[b]) > (Double.valueOf(conditionArray[2])))) {
                      for (int c = 0; c < query.getColumnNames().length; c++){
                        for (int d = 0; d < table.getColumnNames().length; d++){
                          if (query.getColumnNames()[c].equals(table.getColumnNames()[d])){
                            System.out.print(table.getRowArray()[a].getObjectArray()[d] + ",");
                          }
                        }
                      }
                      System.out.println("");
                    }
                  }
                }
              }
            break;
          }//double
        break;

        case "String" : switch (conditionArray[1]) {

          case "=" :

            for (int a = 0; a < table.getNumRows(); a++) {
              for (int b = 0; b < table.getRowArray()[a].getObjectArray().length; b++){
                if (table.getColumnNames()[b].equals(conditionArray[0]) &&
                table.getRowArray()[a].getObjectArray()[b].equals(conditionArray[2])) {
                  for (int c = 0; c < query.getColumnNames().length; c++){
                    for (int d = 0; d < table.getColumnNames().length; d++){
                      if (query.getColumnNames()[c].equals(table.getColumnNames()[d])){
                        System.out.print(table.getRowArray()[a].getObjectArray()[d] + ",");
                      }
                    }
                  }
                  System.out.println("");
                }
              }
            }
          break;

          case "!=" :

            for (int a = 0; a < table.getNumRows(); a++) {
              for (int b = 0; b < table.getRowArray()[a].getObjectArray().length; b++){
                if (table.getColumnNames()[b].equals(conditionArray[0]) &&
                !table.getRowArray()[a].getObjectArray()[b].equals(conditionArray[2])) {
                  for (int c = 0; c < query.getColumnNames().length; c++){
                    for (int d = 0; d < table.getColumnNames().length; d++){
                      if (query.getColumnNames()[c].equals(table.getColumnNames()[d])){
                        System.out.print(table.getRowArray()[a].getObjectArray()[d] + ",");
                      }
                    }
                  }
                  System.out.println("");
                }
              }
            }
          break;
          }
        break;
      } //switch statement
    } //inDatabase
  } //select
} //class
