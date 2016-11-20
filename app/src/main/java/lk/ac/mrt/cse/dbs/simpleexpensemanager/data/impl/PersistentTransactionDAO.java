package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;


/**
 * Created by Pc on 11/19/2016.
 */
public class PersistentTransactionDAO implements TransactionDAO {
    private SQLiteDatabase database;


    //constructor
    public PersistentTransactionDAO(SQLiteDatabase db){
        this.database=db;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {


        //Save transaction details to the transactions_table
        ContentValues values = new ContentValues();
        values.put("account_no", accountNo);
        values.put("date", convertDateToString(date));
        values.put("amount", amount);
        values.put("expense_type", expenseType.toString());

        database.insert("transactions_table",null,values);
    }

    //method to convert date in the standard format to a string
    private String convertDateToString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String dateString = dateFormat.format(date);
        return dateString;
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        return getPaginatedTransactionLogs(0);
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {


        //Query to get details of all the transactions in transactions_table
        String query = "SELECT account_no, date, expense_type, amount FROM transactions_table ORDER BY transaction_id  DESC";

        Cursor cursor = database.rawQuery(query, null);

        ArrayList<Transaction> transactions = new ArrayList<>();

        //Add the transaction details to a list
        while (cursor.moveToNext())
        {
            try {

                ExpenseType expenseType = null;
                if (cursor.getString(cursor.getColumnIndex("expense_type")).equals(ExpenseType.INCOME.toString())) {
                    expenseType = ExpenseType.INCOME;
                }
                else{
                    expenseType = ExpenseType.EXPENSE;
                }

                String dateString = cursor.getString(cursor.getColumnIndex("date"));
                Date date = convertStringToDate(dateString);

                Transaction tans = new Transaction(
                        date,
                        cursor.getString(cursor.getColumnIndex("account_no")),
                        expenseType,
                        cursor.getDouble(cursor.getColumnIndex("amount")));

                transactions.add(tans);

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        //Return the list of transactions
        return transactions;
    }

    //method to convert string date into the standard format
    private Date convertStringToDate(String dateString) throws ParseException{
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date strDate = dateFormat.parse(dateString);
        return strDate;

    }
}
