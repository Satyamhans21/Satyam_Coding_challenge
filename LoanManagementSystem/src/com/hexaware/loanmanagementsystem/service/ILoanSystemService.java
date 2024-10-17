package com.hexaware.loanmanagementsystem.service;

import java.util.List;

import com.hexaware.loanmanagementsystem.entity.Loan;

public interface ILoanSystemService {
	   void applyLoan(Loan loan) throws Exception; // Apply for a loan
	    double calculateInterest(int loanId) ; // Calculate interest for a loan
	    String loanStatus(int loanId); // Get loan status
	    double calculateEMI(int loanId);  // Calculate EMI for a loan
	    void loanRepayment(int loanId, double amount); // Handle loan repayment
	    List<Loan> getAllLoans(); // Get all loans
	    Loan getLoanById(int loanId) ;// Get loan by ID

}
