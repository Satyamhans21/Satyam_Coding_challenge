package com.hexaware.loanmanagementsystem.service;

import java.util.List;

import com.hexaware.loanmanagementsystem.dao.ILoanRepository;
import com.hexaware.loanmanagementsystem.dao.LoanRepositoryImpl;
import com.hexaware.loanmanagementsystem.entity.Loan;
import com.hexaware.loanmanagementsystem.exceptions.InvalidLoanException;

public class LoanSystemServiceImpl implements ILoanSystemService{
	 private ILoanRepository loanRepository;

	    public LoanSystemServiceImpl() {
	        this.loanRepository = new LoanRepositoryImpl(); 
	    }

	    @Override
	    public void applyLoan(Loan loan) throws Exception {
	        loanRepository.applyLoan(loan);
	    }

	    @Override
	    public double calculateInterest(int loanId){
	        return loanRepository.calculateInterest(loanId);
	    }

	    @Override
	    public String loanStatus(int loanId)  {
	        return loanRepository.loanStatus(loanId);
	    }

	    @Override
	    public double calculateEMI(int loanId) {
	        return loanRepository.calculateEMI(loanId);
	    }

	    @Override
	    public void loanRepayment(int loanId, double amount){
	        loanRepository.loanRepayment(loanId, amount);
	    }

	    @Override
	    public List<Loan> getAllLoans() {
	        return loanRepository.getAllLoans();
	    }

	    @Override
	    public Loan getLoanById(int loanId) {
	        return loanRepository.getLoanById(loanId);
	    }

}
