package com.hexaware.loanmanagementsystem.dao;

import java.util.List;

import com.hexaware.loanmanagementsystem.entity.Loan;

public interface ILoanRepository {
	 void applyLoan(Loan loan); 
	    double calculateInterest(int loanId); 
	    double calculateInterest(double principalAmount, double interestRate, int loanTenure); 
	    String loanStatus(int loanId); 
	    double calculateEMI(int loanId); 
	    double calculateEMI(double principalAmount, double annualInterestRate, int loanTenure); 
	    void loanRepayment(int loanId, double amount); 
	    List<Loan> getAllLoans(); 
	    Loan getLoanById(int loanId);

}
