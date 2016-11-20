package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;



import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;

/**
 * Created by Pc on 11/19/2016.
 */
public class PersistentExpenseManager extends ExpenseManager {

    private Context context=null;
    public PersistentExpenseManager( Context context) {
        this.context = context;
        setup();
    }



    public void setup()  {
        //opening the database connection to my database 140205V_database
        //if the database not exist, new database will be create
        SQLiteDatabase mydatabase = context.openOrCreateDatabase("140205V_database", context.MODE_PRIVATE, null);

        //query for create accounts_table if that table does not exist
        String accountquery ="CREATE TABLE IF NOT EXISTS accounts_table " +
                "(account_no VARCHAR(20) NOT NULL PRIMARY KEY," +
                "bank_name VARCHAR(100) NULL," +
                "account_holder_name VARCHAR(100) NULL," +
                "balance DECIMAL(10,2) NULL )";

        //query for create transactions_table if that table does not exist
        String transactionquery ="CREATE TABLE IF NOT EXISTS transactions_table" +
                "(transaction_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "account_no VARCHAR(100) NOT NULL," +
                "date DATE NULL," +
                "amount DECIMAL(10,2) NULL," +
                "expense_type VARCHAR(100) NULL," +
                " FOREIGN KEY(account_no) REFERENCES accounts_table(account_no))";

        mydatabase.execSQL(accountquery);
        mydatabase.execSQL(transactionquery);

        //Setup AccountDAO object
        AccountDAO persistentAccountDAO = new PersistentAccountDAO(mydatabase);
        setAccountsDAO(persistentAccountDAO);

        //Setup TransactionDAO object
        TransactionDAO persistentTransactionDAO = new PersistentTransactionDAO(mydatabase);
        setTransactionsDAO(persistentTransactionDAO);

    }
}
