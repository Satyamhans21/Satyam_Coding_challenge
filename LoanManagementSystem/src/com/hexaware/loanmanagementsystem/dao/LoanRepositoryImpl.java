package com.hexaware.loanmanagementsystem.dao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.*;

import com.hexaware.loanmanagementsystem.entity.Customer;
import com.hexaware.loanmanagementsystem.entity.Loan;

import com.hexaware.loanmanagementsystem.util.DBConnection;


public class LoanRepositoryImpl implements ILoanRepository {

    private Connection conn;

    public LoanRepositoryImpl() {
        conn = DBConnection.getConnection();
    }
    
    
    public void applyLoan(Loan loan) {
        
        String loanStatus = "Pending";
        loan.setLoanStatus(loanStatus);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to apply for the loan? (Yes/No)");
        String confirmation = scanner.next();

        if (confirmation.equalsIgnoreCase("Yes")) {
            
            String sql = "INSERT INTO Loans (loan_id, customer_id, principal_amount, interest_rate, loan_term, loan_type, loan_status) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (
                 PreparedStatement statement = conn.prepareStatement(sql)) {
                 
                statement.setInt(1, loan.getLoanId()); 
                statement.setInt(2, loan.getCustomer().getCustomerId()); 
                statement.setDouble(3, loan.getPrincipalAmount());
                statement.setDouble(4, loan.getInterestRate());
                statement.setInt(5, loan.getLoanTerm());
                statement.setString(6, loan.getLoanType());
                statement.setString(7, loanStatus); 

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Loan application submitted successfully!");
                } else {
                    System.out.println("Failed to apply for loan.");
                }
            } catch (SQLException e) {
                System.out.println("Error applying for loan: " + e.getMessage());
            }
        } else {
            System.out.println("Loan application cancelled.");
        }

        scanner.close(); 
    }

    @Override
    public double calculateInterest(int loanId) {
        double interest = 0.0;
        try {
            String query = "SELECT principalAmount, interestRate, loanTerm FROM loans WHERE loanId = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, loanId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double principalAmount = rs.getDouble("principalAmount");
                double interestRate = rs.getDouble("interestRate");
                int loanTerm = rs.getInt("loanTerm");

                interest = (principalAmount * interestRate * loanTerm) / 12;
            } else {
                System.out.println("Loan ID not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return interest;
    }

    @Override
    public double calculateInterest(double principalAmount, double interestRate, int loanTenure) {
        return (principalAmount * interestRate * loanTenure) / 12;
    }

    @Override
    public String loanStatus(int loanId) {
        String status = "Rejected";
        try {
            
            String customerQuery = "SELECT customerId FROM loans WHERE loanId = ?";
            PreparedStatement customerPs = conn.prepareStatement(customerQuery);
            customerPs.setInt(1, loanId);
            ResultSet customerRs = customerPs.executeQuery();

            if (customerRs.next()) {
                int customerId = customerRs.getInt("customerId");
                
                
                String query = "SELECT creditScore FROM customers WHERE customerId = ?";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setInt(1, customerId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int creditScore = rs.getInt("creditScore");
                    if (creditScore > 650) {
                        status = "Approved";
                    }
                    
                    String updateQuery = "UPDATE loans SET loanStatus = ? WHERE loanId = ?";
                    PreparedStatement psUpdate = conn.prepareStatement(updateQuery);
                    psUpdate.setString(1, status);
                    psUpdate.setInt(2, loanId);
                    psUpdate.executeUpdate();
                }
            } else {
                System.out.println("Loan ID not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return status;
    }


    @Override
    public double calculateEMI(int loanId) {
        double emi = 0.0;
        try {
            String query = "SELECT principalAmount, interestRate, loanTerm FROM loans WHERE loanId = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, loanId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double principalAmount = rs.getDouble("principalAmount");
                double interestRate = rs.getDouble("interestRate");
                int loanTerm = rs.getInt("loanTerm");

                double monthlyRate = interestRate / 12 / 100;
                emi = (principalAmount * monthlyRate * Math.pow(1 + monthlyRate, loanTerm)) /
                      (Math.pow(1 + monthlyRate, loanTerm) - 1);
            } else {
                System.out.println("Loan ID not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return emi;
    }

    @Override
    public double calculateEMI(double principalAmount, double annualInterestRate, int loanTenure) {
        double monthlyRate = annualInterestRate / 12 / 100;
        return (principalAmount * monthlyRate * Math.pow(1 + monthlyRate, loanTenure)) /
               (Math.pow(1 + monthlyRate, loanTenure) - 1);
    }

    @Override
    public void loanRepayment(int loanId, double amount) {
        try {
            double emi = calculateEMI(loanId);
            if (amount < emi) {
                System.out.println("Payment rejected: Amount is less than the EMI");
            } else {
                int numOfEmis = (int) (amount / emi);
                String updateQuery = "UPDATE loans SET loanTerm = loanTerm - ? WHERE loanId = ?";
                PreparedStatement ps = conn.prepareStatement(updateQuery);
                ps.setInt(1, numOfEmis);
                ps.setInt(2, loanId);
                ps.executeUpdate();
                System.out.println("Loan repayment successful! Remaining EMIs: " + numOfEmis);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Loan> getAllLoans() {
        List<Loan> loans = new ArrayList<>();
        try {
            String query = "SELECT * FROM loans";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int customerId = rs.getInt("customerId");
                Customer customer = getCustomerById(customerId);
                Loan loan = new Loan(
                    rs.getInt("loanId"), 
                    customer,
                    rs.getDouble("principalAmount"),
                    rs.getDouble("interestRate"),
                    rs.getInt("loanTerm"),
                    rs.getString("loanType"),
                    rs.getString("loanStatus")
                );
                loans.add(loan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }
    
    public void getAllLoan() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT c.name, l.loanId, l.customerId, l.principalAmount, l.interestRate, l.loanTerm, l.loanType, l.loanStatus FROM loan l LEFT JOIN customer c ON l.customerId = c.customerId;");
            
            while (rs.next()) {
                System.out.println("customer_name: " + rs.getString(1) + " loanId: " + rs.getInt(2) + " customerId: " + rs.getInt(3) + " principal_amount: " + rs.getInt(4) + " interest_rate: " + rs.getInt(5) + " loan_term: " + rs.getInt(6) + " loan_type: " + rs.getString(7) + " loan_status: " + rs.getString(8));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Cannot get the customer table details");
        }
    }

    public Loan getLoanById(int loanId) {
        Loan loan = null;
        try {
            String query = "SELECT * FROM loans WHERE loanId = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, loanId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                loan = new Loan(
                    rs.getInt("loanId"),
                    getCustomerById(rs.getInt("customerId")),
                    rs.getDouble("principalAmount"),
                    rs.getDouble("interestRate"),
                    rs.getInt("loanTerm"),
                    rs.getString("loanType"),
                    rs.getString("loanStatus")
                );
            } else {
                System.out.println("Loan not found with ID: " + loanId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loan;
    }

    public Customer getCustomerById(int customerId) {
        Customer customer = null;
        try {
            String query = "SELECT * FROM customers WHERE customerId = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                customer = new Customer(
                    rs.getInt("customerId"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phoneNumber"),
                    rs.getString("address"),
                    rs.getInt("creditScore")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customer;
    }

		}



