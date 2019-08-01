//Tia Hannes - Hanne123 - 5286175
//Thomas Rooney - Roone194 - 5364798

import java.util.Scanner;
import java.io.FileNotFoundException;

public class Main{

  public static void main(String[] args) throws FileNotFoundException{
    Scanner s = new Scanner(System.in);
    String next = "";
    InterpretedQuery iQ;
    Database database = new Database();
    boolean done = false;

    while(!done){
      next = s.nextLine();
      iQ = QueryEvaluator.evaluateQuery(next);
      if(iQ.getQueryType() == QueryType.CREATE_STATEMENT){
        database.create(iQ);
      } else if(iQ.getQueryType() == QueryType.INSERT_STATEMENT){
        database.insert(iQ);
      } else if(iQ.getQueryType() == QueryType.LOAD_STATEMENT){
        try{
          database.load(iQ);
        } catch(FileNotFoundException ex){
          System.out.println(iQ.getFileName() + " is not a valid file.");
        }
      } else if(iQ.getQueryType() == QueryType.STORE_STATEMENT){
        database.store(iQ);
      } else if(iQ.getQueryType() == QueryType.PRINT_STATEMENT){
        database.print(iQ);
      } else if(iQ.getQueryType() == QueryType.SELECT_STATEMENT){
        database.select(iQ);
      } else if(iQ.getQueryType() == QueryType.EXIT_STATEMENT){
        done = true;
      }
    }
  }
}
