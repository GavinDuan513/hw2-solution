// package test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.ParseException;
import java.util.Vector;

import model.Filter.AmountFilter;
import model.Filter.CategoryFilter;
import org.junit.Before;
import org.junit.Test;

import controller.ExpenseTrackerController;
import model.ExpenseTrackerModel;
import model.Transaction;
import view.ExpenseTrackerView;

import javax.swing.*;


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
    public void testAddTransactionViewUpdated() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());

        // Perform the action: Add a transaction
        double amount = 50.0;
        String category = "food";
        assertTrue(controller.addTransaction(amount, category));

        // Post-condition: List of transactions contains only
        //                 the added transaction
        assertEquals(1, model.getTransactions().size());

        // Check the contents of the display list
        JTable table = view.getTransactionsTable();
        Object rowAmount = table.getValueAt(0, 1);
        assertEquals(rowAmount, 50.0);
        Object rowCategory = table.getValueAt(0, 2);
        assertEquals(rowCategory, "food");

        Object totalRowAmount = table.getValueAt(1, 3);
        assertEquals(totalRowAmount, 50.0);
    }

    @Test
    public void testAddInvalidTransactionViewUpdated() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
        boolean messageShown = false;

        // Perform the action: Add a transaction
        double amount = -100.0;
        String category = "food";
        controller.addTransaction(amount, category);

        // Post-condition: List of transactions still empty
        assertEquals(0, model.getTransactions().size());

        // Check error message displayed

        JDialog dialog = view.getDialog();

        Timer timer = new Timer(10, e -> dialog.dispose());
        timer.setRepeats(false);
        timer.start();
        view.showInvalidInput();

        // Check the message shown is as expected.
        String errormsg = view.getErrorMessage();
        String msgExp = "Invalid amount or category entered";
        assertEquals(msgExp, errormsg);

    }

    @Test
    public void testFilterByAmountAndHighlight(){
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
        boolean messageShown = false;

        // Perform the action: Add a transaction
        String category = "food";
        controller.addTransaction(100, category);
        controller.addTransaction(50, category);
        controller.addTransaction(100, category);
        controller.addTransaction(50, category);

        // Perform the action: filter by amount = 50
        AmountFilter amountFilter = new AmountFilter(50);
        controller.setFilter(amountFilter);
        controller.applyFilter();

        // Post-condition: List of transactions contains only
        //                 the added transaction
        assertEquals(4, model.getTransactions().size());

        // Check the color of each rows
        Color rowColor = view.getTransactionsTable().getBackground();
        Color highlightColor = new Color(173, 255, 168);

        JTable transactionsTable = view.getTransactionsTable();

        // Check row 0 --> not highlighted
        Component normal_row0_col0 = transactionsTable.prepareRenderer(transactionsTable.getCellRenderer(0, 0), 0, 0);
        assert(normal_row0_col0.getBackground().equals(rowColor));
        Component normal_row0_col1 = transactionsTable.prepareRenderer(transactionsTable.getCellRenderer(0, 1), 0, 1);
        assert(normal_row0_col1.getBackground().equals(rowColor));

        // Check row 1 --> highlighted
        Component highlight_row1_col0 = transactionsTable.prepareRenderer(transactionsTable.getCellRenderer(1, 0), 1, 0);
        assert(highlight_row1_col0.getBackground().equals(highlightColor));
        Component highlight_row1_col1 = transactionsTable.prepareRenderer(transactionsTable.getCellRenderer(1, 1), 1, 1);
        assert(highlight_row1_col1.getBackground().equals(highlightColor));

        // Check row 2 --> not highlighted
        Component normal_row2_col0 = transactionsTable.prepareRenderer(transactionsTable.getCellRenderer(2, 0), 2, 0);
        assert(normal_row2_col0.getBackground().equals(rowColor));
        Component normal_row2_col1 = transactionsTable.prepareRenderer(transactionsTable.getCellRenderer(2, 1), 2, 1);
        assert(normal_row2_col1.getBackground().equals(rowColor));

        // Check row 3 --> highlighted
        Component highlight_row3_col0 = transactionsTable.prepareRenderer(transactionsTable.getCellRenderer(3, 0), 3, 0);
        assert(highlight_row3_col0.getBackground().equals(highlightColor));
        Component highlight_row3_col1 = transactionsTable.prepareRenderer(transactionsTable.getCellRenderer(3, 1), 3, 1);
        assert(highlight_row3_col1.getBackground().equals(highlightColor));
    }

    @Test
    public void testFilterByCategoryAndHighlight(){
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
        boolean messageShown = false;

        // Perform the action: Add a transaction
        controller.addTransaction(100, "food");
        controller.addTransaction(50, "travel");
        controller.addTransaction(100, "bills");
        controller.addTransaction(50, "entertainment");
        controller.addTransaction(50, "other");
        controller.addTransaction(50, "travel");

        // Perform the action: filter by category = "travel"
        CategoryFilter categoryFilter = new CategoryFilter("travel");
        controller.setFilter(categoryFilter);
        controller.applyFilter();

        // Post-condition: List of transactions contains only
        //                 the added transaction
        assertEquals(6, model.getTransactions().size());

        // Check the color of each rows
        Color rowColor = view.getTransactionsTable().getBackground();
        Color highlightColor = new Color(173, 255, 168);

        JTable transactionsTable = view.getTransactionsTable();

        // Check row 0 --> not highlighted
        Component normal_row0_col0 = transactionsTable.prepareRenderer(transactionsTable.getCellRenderer(0, 0), 0, 0);
        assert(normal_row0_col0.getBackground().equals(rowColor));
        Component normal_row0_col1 = transactionsTable.prepareRenderer(transactionsTable.getCellRenderer(0, 1), 0, 1);
        assert(normal_row0_col1.getBackground().equals(rowColor));

        // Check row 1 --> highlighted
        Component highlight_row1_col0 = transactionsTable.prepareRenderer(transactionsTable.getCellRenderer(1, 0), 1, 0);
        assert(highlight_row1_col0.getBackground().equals(highlightColor));
        Component highlight_row1_col1 = transactionsTable.prepareRenderer(transactionsTable.getCellRenderer(1, 1), 1, 1);
        assert(highlight_row1_col1.getBackground().equals(highlightColor));

        // Check row 2 --> not highlighted
        Component normal_row2_col0 = transactionsTable.prepareRenderer(transactionsTable.getCellRenderer(2, 0), 2, 0);
        assert(normal_row2_col0.getBackground().equals(rowColor));
        Component normal_row2_col1 = transactionsTable.prepareRenderer(transactionsTable.getCellRenderer(2, 1), 2, 1);
        assert(normal_row2_col1.getBackground().equals(rowColor));

        // Check row 3 --> not highlighted
        Component normal_row3_col0 = transactionsTable.prepareRenderer(transactionsTable.getCellRenderer(3, 0), 3, 0);
        assert(normal_row3_col0.getBackground().equals(rowColor));
        Component normal_row3_col1 = transactionsTable.prepareRenderer(transactionsTable.getCellRenderer(3, 1), 3, 1);
        assert(normal_row3_col1.getBackground().equals(rowColor));

        // Check row 4 --> not highlighted
        Component normal_row4_col0 = transactionsTable.prepareRenderer(transactionsTable.getCellRenderer(4, 0), 4, 0);
        assert(normal_row4_col0.getBackground().equals(rowColor));
        Component normal_row4_col1 = transactionsTable.prepareRenderer(transactionsTable.getCellRenderer(4, 1), 4, 1);
        assert(normal_row4_col1.getBackground().equals(rowColor));

        // Check row 5 --> highlighted
        Component highlight_row5_col0 = transactionsTable.prepareRenderer(transactionsTable.getCellRenderer(5, 0), 5, 0);
        assert(highlight_row5_col0.getBackground().equals(highlightColor));
        Component highlight_row5_col1 = transactionsTable.prepareRenderer(transactionsTable.getCellRenderer(5, 1), 5, 1);
        assert(highlight_row5_col1.getBackground().equals(highlightColor));
    }


    @Test
    public void testUndoDisallowed() {
      // Pre-condition: List of transactions is empty
      assertTrue(model.getTransactions().size() == 0);
      // Perform the action: trying to click on the undo button, but the button is disabled to click
      assertTrue(!view.getRemoveBtn().isEnabled());
      // Post-condition: List of transactions is still empty
      assertTrue(model.getTransactions().size() == 0);
    }

    @Test
    public void testUndoAllowed() {
        // Pre-condition: List of transactions is empty
        assertTrue(model.getTransactions().size() == 0);
        assertTrue(!view.getRemoveBtn().isEnabled());

        // Perform the action: add two transactions
        double amt = 45.0;
        String category = "food";
        controller.addTransaction(amt, category);

        // Perform the action: Undo the action by selecting the transaction programmatically
        JTable table = view.getTransactionsTable();
        table.setRowSelectionInterval(0,0);
        controller.applyRemove();


        // Post-condition: List of transactions should contain zero transaction after undo once.
        assertEquals(0, model.getTransactions().size());

        // Check the transaction is removed from the table after undo, and only the total row is displayed
        int rowCnt = table.getRowCount();
        assertEquals(rowCnt, 1);

        // Check the total cost shown in table after undo one transaction
        Object totalRowAmount = table.getValueAt(0, 3);
        assertEquals(totalRowAmount, 0.0);

        // The remove button should be disabled again since there is no transactions
        assertTrue(!view.getRemoveBtn().isEnabled());

    }


}
