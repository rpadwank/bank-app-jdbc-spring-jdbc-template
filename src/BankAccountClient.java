package com.capgemini.bankapp.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.capgemini.bankapp.exception.AccountNotFoundException;
import com.capgemini.bankapp.exception.LowBalanceException;
import com.capgemini.bankapp.model.BankAccount;
import com.capgemini.bankapp.service.BankAccountService;
import com.capgemini.bankapp.service.impl.BankAccountServiceImpl;

public class BankAccountClient {

	static final Logger logger = Logger.getLogger(BankAccountClient.class);

	public static void main(String[] args) {
		int choice;
		long accountId;
		String accountHolderName;
		String accountType;
		double accountBalance;
		ApplicationContext context=new ClassPathXmlApplicationContext("context.xml");
		BankAccountService bankService=(BankAccountService)context.getBean("bankAccountServiceImpl");
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			while (true) {
				System.out.println("1. Add New Bank Account\n2. Withdraw\n3. Deposit\n4. Fund Transfer");
				System.out.println("5. Delete Bank Account\n6. Display All BankAccount Details");
				System.out.println("7. Search Bank Account\n8. Check Balance");
				System.out.println("9. Update Bank Account details\n10. Exit\n");

				System.out.print("Please enter your choice = ");
				choice = Integer.parseInt(reader.readLine());

				switch (choice) {

				case 1:
					System.out.println("Enter account holder name:");
					accountHolderName = reader.readLine();
					System.out.println("Enter account type");
					accountType = reader.readLine();
					System.out.println("Enter account balance");
					accountBalance = Double.parseDouble(reader.readLine());
					BankAccount account = (BankAccount)context.getBean("bankAccount");
					account.setAccountHolderName(accountHolderName);
					account.setAccountType(accountType);
					account.setAccountBalance(accountBalance);
					if (bankService.addNewBankAccount(account))
						System.out.println("Bank Account successfully created.");
					else
						System.out.println("Bank Account creation failed");
					break;

				case 2:
					System.out.println("Enter account id");
					accountId = Long.parseLong(reader.readLine());

					System.out.println("Enter amount: ");
					double amount = Double.parseDouble(reader.readLine());
					try {
						accountBalance = bankService.withdraw(accountId, amount);
						System.out.println("Balance : " + accountBalance);
					} catch (LowBalanceException | AccountNotFoundException e) {
						// System.out.println(e.getMessage());
						logger.error("Exception: ", e);
					}

					break;

				case 3:
					System.out.println("Enter account id");
					accountId = Long.parseLong(reader.readLine());

					System.out.println("Enter amount: ");
					amount = Double.parseDouble(reader.readLine());

					try {
						accountBalance = bankService.deposit(accountId, amount);
						System.out.println("Balance : " + accountBalance);
					} catch (AccountNotFoundException e) {
						logger.error("Exception: ", e);
					}

					break;

				case 4:
					System.out.println("Enter account id of sender: ");
					long fromAccountId = Long.parseLong(reader.readLine());

					System.out.println("Enter account id of recepient: ");
					long toAccountId = Long.parseLong(reader.readLine());

					System.out.println("Enter amount: ");
					amount = Double.parseDouble(reader.readLine());
					try {
						accountBalance = bankService.fundTransfer(fromAccountId, toAccountId, amount);
						System.out.println("Balance : " + accountBalance);
					} catch (LowBalanceException | AccountNotFoundException e) {
						logger.error("Exception: ", e);
					}
					break;

				case 5:
					System.out.println("Enter account id: ");
					accountId = Long.parseLong(reader.readLine());
					try {
						if (bankService.deleteBankAccount(accountId))
							System.out.println("Account deletion successful");
						else
							System.out.println("Account deletion failed");
					} catch (AccountNotFoundException e) {
						logger.error("Exception: ", e);
					}
					break;

				case 6:
					List<BankAccount> accounts = new ArrayList<>();

					accounts = bankService.findAllBankAccounts();
					for (BankAccount bankAccount : accounts) {
						System.out.println(bankAccount);
					}

					break;

				case 7:
					System.out.println("Enter account id: ");
					accountId = Long.parseLong(reader.readLine());
					try {
						BankAccount bankAccount = bankService.searchAccount(accountId);
						System.out.println(bankAccount);
					} catch (AccountNotFoundException e) {
						// System.out.println(e.getMessage());
						logger.error("Exception: ", e);
					}
					break;

				case 8:
					System.out.println("Enter account id: ");
					accountId = Long.parseLong(reader.readLine());
					try {
						accountBalance = bankService.checkBalance(accountId);
						System.out.println(accountBalance);
					} catch (AccountNotFoundException e) {
						System.out.println(e.getMessage());
						logger.error("Exception: ", e);
					}

					break;

				case 9:
					System.out.println("Enter accountId: ");
					accountId = Long.parseLong(reader.readLine());
					try {
						account = bankService.searchAccount(accountId);
						System.out.println("Enter your new name");
						accountHolderName = reader.readLine();
						System.out.println("Enter new type");
						accountType = reader.readLine();
						account = new BankAccount(account.getAccountId(),accountHolderName, accountType, account.getAccountBalance());
						bankService.updateAccountDetails(account);
					} catch (AccountNotFoundException e) {
						logger.error("Exception: ", e);
					}
					catch (RuntimeException e) {
						// System.out.println(e.getMessage());
						logger.error("Exception: ", e);
					}
					break;
				case 10:
					System.out.println("Thanks for banking with us.");
					System.exit(0);

				}
			}
		}

		catch (IOException e) {
			// e.printStackTrace();
			logger.error("Exception: ", e);
		}

	}

}
