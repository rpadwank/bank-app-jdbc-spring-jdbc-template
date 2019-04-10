package com.capgemini.bankapp.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.*;  
import org.apache.log4j.Logger;
import javax.sql.DataSource;
import com.capgemini.bankapp.dao.BankAccountDao;
import com.capgemini.bankapp.exception.AccountNotFoundException;
import com.capgemini.bankapp.model.BankAccount;
import com.capgemini.bankapp.util.DbUtil;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.*;

@Transactional
public class BankAccountDaoImpl implements BankAccountDao {

	Logger logger = Logger.getLogger(BankAccountDaoImpl.class);
	private JdbcTemplate jdbcTemplate;
	
	public BankAccountDaoImpl(JdbcTemplate jdbcTemplate){
		this.jdbcTemplate = jdbcTemplate;
	}
	
	@Override
	public double getBalance(long accountId) throws AccountNotFoundException {
		Double balance = -1.0;
		String query = "SELECT account_balance FROM bankaccounts WHERE account_id = ?";
		Object[] params = {accountId};
		try{
			balance = jdbcTemplate.queryForObject(query, params, Double.class);
		}
		catch(Exception e){
			throw new AccountNotFoundException("account doesn't exist");
		}
		
		return balance;
	}

	@Override
	public void updateBalance(long accountId, double newBalance) throws AccountNotFoundException {
		String query = "UPDATE bankaccounts SET account_balance = ? WHERE account_id = ?";
		Object[] params = {newBalance, accountId};
		try{
			int result = jdbcTemplate.update(query, params);
			System.out.println("No. of rows updated: " + result);
		}catch(Exception e){
			throw new AccountNotFoundException("account doesn't exist");
		}

	}

	@Override
	public boolean deleteBankAccount(long accountId) throws AccountNotFoundException {
		String query = "DELETE FROM bankaccounts WHERE account_id = ?";
		Object[] params = {accountId};
		try{
			int result = jdbcTemplate.update(query, params);
			if(result==1)
				return true;
		}catch(Exception e){
			throw new AccountNotFoundException("account doesn't exist");
		}	
		
		return false;
	}

	@Override
	public boolean addNewBankAccount(BankAccount account) {
		
		Object[] params = {account.getAccountHolderName(), account.getAccountType(), account.getAccountBalance()};
		String query = "INSERT INTO bankaccounts (customer_name, account_type, account_balance) VALUES(?, ?, ?)";
		int result = jdbcTemplate.update(query, params);
		if(result==1)
			return true;
		return false;
	}

	@Override
	public List<BankAccount> findAllBankAccounts() {
		String query = "SELECT * FROM bankaccounts";
		List<BankAccount> accounts = jdbcTemplate.query(query, (rs, rowNum)->{
				long accountId = rs.getLong(1);
				String accountHolderName = rs.getString(2);
				String accountType = rs.getString(3);
				double accountBalance = rs.getDouble(4);
				BankAccount account = new BankAccount(accountId, accountHolderName, accountType, accountBalance);
				return account;
				});

		return accounts;
	}

	@Override
	public BankAccount searchAccount(long accountId) throws AccountNotFoundException {
		String query = "SELECT * FROM bankaccounts WHERE account_id = ?";
		Object params[] = {accountId};
		BankAccount account = null;
		try{
		account = jdbcTemplate.queryForObject(query, params, (rs, rowNum)->{
				String accountHolderName = rs.getString(2);
				String accountType = rs.getString(3);
				double accountBalance = rs.getDouble(4);
				BankAccount accounts = new BankAccount(accountId, accountHolderName, accountType, accountBalance);
				return accounts;
				});
		}
		catch(Exception e){
			throw new AccountNotFoundException("account doesn't exist");
		}
		return account;
	}

	@Override
	public boolean updateAccountDetails(BankAccount account) throws AccountNotFoundException {
		String query = "UPDATE bankaccounts SET customer_name = ?, account_type = ? WHERE account_id = ?";
		Object[] params = {account.getAccountHolderName(), account.getAccountType(), account.getAccountId()};
		try{
			int result = jdbcTemplate.update(query, params);
			if(result==1)
			{
				System.out.println("No. of rows updated: " + result);
				return true;
			}
		}catch(Exception e){
			throw new AccountNotFoundException("account doesn't exist");
		}
		return false;
	}

	
}
