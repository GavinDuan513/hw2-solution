# hw1- Manual Review

The homework will be based on this project named "Expense Tracker",where users will be able to add/remove daily transaction. 

## Compile

To compile the code from terminal, use the following command:
```
cd src
javac ExpenseTrackerApp.java
java ExpenseTracker
```

You should be able to view the GUI of the project upon successful compilation. 

## Java Version
This code is compiled with ```openjdk 17.0.7 2023-04-18```. Please update your JDK accordingly if you face any incompatibility issue.


# New Functionality

## Transaction Remove (Simple Undo)
User could select the row they want to remove and then click on the remove button on top to remove the transaction. This is the undo function asked by the assignment. Test on Undo Allowed and 
Undo Disallowed are designed for this functionality. 

## Transaction Undo (Complex Undo)
We implement the undo as the Undo button will be available after user add/remove transaction from the table. Everytime user click the undo button, the last add/remove action will be reverted.  