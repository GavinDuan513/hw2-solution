// package test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.ParseException;

import model.Filter.CategoryFilter;
import org.junit.Before;
import org.junit.Test;

import controller.ExpenseTrackerController;
import model.ExpenseTrackerModel;
import model.Transaction;
import view.ExpenseTrackerView;


public class TestExample {
  
  private ExpenseTrackerModel model;
  private ExpenseTrackerView view;
  private ExpenseTrackerController controller;

  @Before
  public void setup() {
    model = new ExpenseTrackerModel();
    view = new ExpenseTrackerView();
    controller = new ExpenseTrackerController(model, view);
  }

    public double getTotalCost() {
        double totalCost = 0.0;
        List<Transaction> allTransactions = model.getTransactions(); // Using the model's getTransactions method
        for (Transaction transaction : allTransactions) {
            totalCost += transaction.getAmount();
        }
        return totalCost;
    }


    public void checkTransaction(double amount, String category, Transaction transaction) {
	assertEquals(amount, transaction.getAmount(), 0.01);
        assertEquals(category, transaction.getCategory());
        String transactionDateString = transaction.getTimestamp();
        Date transactionDate = null;
        try {
            transactionDate = Transaction.dateFormatter.parse(transactionDateString);
        }
        catch (ParseException pe) {
            pe.printStackTrace();
            transactionDate = null;
        }
        Date nowDate = new Date();
        assertNotNull(transactionDate);
        assertNotNull(nowDate);
        // They may differ by 60 ms
        assertTrue(nowDate.getTime() - transactionDate.getTime() < 60000);
    }


    @Test
    public void testAddTransaction() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
    
        // Perform the action: Add a transaction
	double amount = 50.0;
	String category = "food";
        assertTrue(controller.addTransaction(amount, category));
    
        // Post-condition: List of transactions contains only
	//                 the added transaction	
        assertEquals(1, model.getTransactions().size());
    
        // Check the contents of the list
	Transaction firstTransaction = model.getTransactions().get(0);
	checkTransaction(amount, category, firstTransaction);
	
	// Check the total amount
        assertEquals(amount, getTotalCost(), 0.01);
    }


    @Test
    public void testRemoveTransaction() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
    
        // Perform the action: Add and remove a transaction
	double amount = 50.0;
	String category = "food";
        Transaction addedTransaction = new Transaction(amount, category);
        model.addTransaction(addedTransaction);
    
        // Pre-condition: List of transactions contains only
	//                the added transaction
        assertEquals(1, model.getTransactions().size());
	Transaction firstTransaction = model.getTransactions().get(0);
	checkTransaction(amount, category, firstTransaction);

	assertEquals(amount, getTotalCost(), 0.01);
	
	// Perform the action: Remove the transaction
        model.removeTransaction(addedTransaction);
    
        // Post-condition: List of transactions is empty
        List<Transaction> transactions = model.getTransactions();
        assertEquals(0, transactions.size());
    
        // Check the total cost after removing the transaction
        double totalCost = getTotalCost();
        assertEquals(0.00, totalCost, 0.01);
    }


    @Test
    public void testFilterByCategory() {
        assertEquals(0, model.getTransactions().size());

        Transaction addedTransaction1 = new Transaction(50.0, "food");
        Transaction addedTransaction2 = new Transaction(150.0, "food");
        Transaction addedTransaction3 = new Transaction(5.0, "travel");
        Transaction addedTransaction4 = new Transaction(33.0, "bills");
        model.addTransaction(addedTransaction1);
        model.addTransaction(addedTransaction2);
        model.addTransaction(addedTransaction3);
        model.addTransaction(addedTransaction4);
        CategoryFilter catFilter = new CategoryFilter("food");
        List<Transaction> filteredList = catFilter.filter(model.getTransactions());

        assertEquals(2, filteredList.size());
        for(Transaction transaction : filteredList){
            assertEquals("food", transaction.getCategory());
        }
    }

    @Test
    public void testUndoDisallowed() {
      assertTrue(model.getTransactions().size() == 0);
      assertTrue(!view.getUndoBtn().isEnabled());
    }

    @Test
    public void testUndoAllowed() {
        assertTrue(model.getTransactions().size() == 0);
        Transaction addedTransaction1 = new Transaction(45.0, "food");
        Transaction addedTransaction2 = new Transaction(72.0, "food");
        model.addTransaction(addedTransaction1);
        model.addTransaction(addedTransaction2);
        model.undoTransaction();
        assertEquals(1, model.getTransactions().size());
        double totalCost=0;
        for(Transaction transaction: model.getTransactions()){
            totalCost += transaction.getAmount();
        }
        assertEquals(45.0, totalCost, 0.01);

    }


}
