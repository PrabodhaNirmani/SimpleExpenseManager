package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;


/**
 * Created by Pc on 11/19/2016.
 */
public class PersistentAccountDAO implements AccountDAO {

    private SQLiteDatabase database;

    //constructor
    public PersistentAccountDAO(SQLiteDatabase db){
        this.database=db;
    }


    @Override
    public List<String> getAccountNumbersList() {
        //query to get account numbers in ascending order
        String query = "SELECT account_no from accounts_table order by account_no asc";

        Cursor cursor = database.rawQuery(query, null);

        ArrayList<String> result = new ArrayList<>();

        while (cursor.moveToNext())
        {
            result.add(cursor.getString(cursor.getColumnIndex("account_no")));
        }

        cursor.close();

        //returning results
        return result;

    }

    @Override
    public List<Account> getAccountsList() {
        //query to get all details in the accounts table
        String query = "SELECT * FROM accounts_table  ORDER BY account_no ASC";

        Cursor cursor = database.rawQuery(query, null);

        ArrayList<Account> result = new ArrayList<>();

        while (cursor.moveToNext())
        {

            Account account = new Account(cursor.getString(cursor.getColumnIndex("account_no")),
                    cursor.getString(cursor.getColumnIndex("bank_name")),
                    cursor.getString(cursor.getColumnIndex("account_holder_name")),
                    cursor.getDouble(cursor.getColumnIndex("balance")));
            result.add(account);
        }

        cursor.close();

        //returning results
        return result;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {

        //query to select account which has the given account number in the method parameter
        String query = "SELECT * FROM accounts_table WHERE account_no =  '" + accountNo + "'";
        Cursor cursor = database.rawQuery(query, null);

        Account account = null;

        if (cursor.moveToFirst()) {

            account = new Account(cursor.getString(cursor.getColumnIndex("account_no")),
                    cursor.getString(cursor.getColumnIndex("bank_name")),
                    cursor.getString(cursor.getColumnIndex("account_holder_name")),
                    cursor.getDouble(cursor.getColumnIndex("balance")));
        }

        else {
            throw new InvalidAccountException("You have selected an invalid account number...!");
        }

        cursor.close();
        //returning that account
        return account;
    }

    @Override
    public void addAccount(Account account) {

        ContentValues values = new ContentValues();

        //set values of records in the given account
        values.put("account_no", account.getAccountNo());
        values.put("bank_name", account.getBankName());
        values.put("account_holder_name", account.getAccountHolderName());
        values.put("balance", account.getBalance());

        //insert record to the accounts_table
        database.insert("accounts_table", null, values);

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {

        //query to remove which has the given account_no
        String query = "DELETE FROM accounts_table WHERE account_no = ?";
        SQLiteStatement statement = database.compileStatement(query);
        statement.bindString(1,accountNo);
        statement.executeUpdateDelete();

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        Account account = getAccount(accountNo);
        if (account!=null) {

            double new_amount=0;


            if (expenseType.equals(ExpenseType.EXPENSE)) {
                new_amount = account.getBalance() - amount;
            }

            else if (expenseType.equals(ExpenseType.INCOME)) {
                new_amount = account.getBalance() + amount;
            }

            //query to update the database with new values
            String strSQL = "UPDATE accounts_table SET balance = "+new_amount+" WHERE account_no = '"+ accountNo+"'";

            database.execSQL(strSQL);

        }

        else {
            throw new InvalidAccountException("No such account found...!");
        }
    }

}
