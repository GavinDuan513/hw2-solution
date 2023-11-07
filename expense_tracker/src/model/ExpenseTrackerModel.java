package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExpenseTrackerModel {

  //encapsulation - data integrity
  private List<Transaction> transactions;

  private List<List<Transaction>> undoTransactionsList;

  public ExpenseTrackerModel() {
    transactions = new ArrayList<>();
    undoTransactionsList = new ArrayList<List<Transaction>>();
  }

  public void addTransaction(Transaction t) {
    // Perform input validation to guarantee that all transactions added are non-null.
    if (t == null) {
      throw new IllegalArgumentException("The new transaction must be non-null.");
    }
//    undoTransactions = new ArrayList<>(transactions);
    undoTransactionsList.add(new ArrayList<>(transactions));
    transactions.add(t);
  }

  public void removeTransaction(Transaction t) {
//    undoTransactions = new ArrayList<>(transactions);
    undoTransactionsList.add(new ArrayList<>(transactions));
    transactions.remove(t);
  }

  public List<Transaction> getTransactions() {
    //encapsulation - data integrity
    return Collections.unmodifiableList(new ArrayList<>(transactions));
  }

  public boolean undoAvailable(){
    if (undoTransactionsList.size() == 0) {
      return false;
    } else {
      return true;
    }
  }
  public void undoTransaction() {
    if (!undoAvailable()) {
      System.out.println("Undo Unavailable");
    } else {
      transactions = undoTransactionsList.remove(undoTransactionsList.size() - 1);
    }
  }

}
