package com.hexaware.loanmanagementsystem.main;

import java.util.List;
import java.util.Scanner;

import com.hexaware.loanmanagementsystem.dao.ILoanRepository;
import com.hexaware.loanmanagementsystem.dao.LoanRepositoryImpl;
import com.hexaware.loanmanagementsystem.entity.Customer;
import com.hexaware.loanmanagementsystem.entity.Loan;

import com.hexaware.loanmanagementsystem.service.LoanSystemServiceImpl;

public class MainModule {

    private static LoanRepositoryImpl loanRepository = new LoanRepositoryImpl();
    private static LoanSystemServiceImpl loanService = new LoanSystemServiceImpl();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean exit = false;

        System.out.println(" _____Loan Management System_____");

        while (!exit) {
            Menu();
            int choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1:
                    applyLoan();
                    break;
                case 2:
                    calculateInterest();
                    break;
                case 3:
                    checkLoanStatus();
                    break;
                case 4:
                    calculateEMI();
                    break;
                case 5:
                    repayLoan();
                    break;
                case 6:
                    getAllLoans();
                    break;
                case 7:
                    getLoanById();
                    break;
                case 8:
                    exit = true;
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }

        scanner.close();
    }

    private static void Menu() {
        System.out.println("\nMenu:");
        System.out.println("1. Apply for a Loan");
        System.out.println("2. Calculate Loan Interest");
        System.out.println("3. Check Loan Status");
        System.out.println("4. Calculate EMI");
        System.out.println("5. Repay Loan");
        System.out.println("6. View All Loans");
        System.out.println("7. View Loan by ID");
        System.out.println("8. Exit");
        System.out.print("Choose an option: ");
    }

    private static void applyLoan() {
        System.out.println("=== Apply for a Loan ===");
        System.out.print("Enter Customer ID: ");
        int customerId = scanner.nextInt();
        scanner.nextLine(); 

        
        Customer customer = loanRepository.getCustomerById(customerId);

        if (customer != null) {
            System.out.print("Enter Loan ID: ");
            int loanId = scanner.nextInt();
            System.out.print("Enter Principal Amount: ");
            double principalAmount = scanner.nextDouble();
            System.out.print("Enter Interest Rate: ");
            double interestRate = scanner.nextDouble();
            System.out.print("Enter Loan Term (in months): ");
            int loanTerm = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            System.out.print("Enter Loan Type (HomeLoan/CarLoan): ");
            String loanType = scanner.nextLine();

            Loan loan = new Loan( loanId, customer,  principalAmount, interestRate, loanTerm,
        			 loanType, "pending");
            loanRepository.applyLoan(loan); 
        } else {
            System.out.println("Customer not found!");
        }
    }
    private static void calculateInterest() {
        System.out.println("=== Calculate Loan Interest ===");
        System.out.print("Enter Loan ID: ");
        int loanId = scanner.nextInt();

        double interest = loanService.calculateInterest(loanId);
        if (interest > 0) {
            System.out.println("Calculated Interest: " + interest);
        } else {
            System.out.println("Loan not found!");
        }
    }

    private static void checkLoanStatus() {
        System.out.println("=== Check Loan Status ===");
        System.out.print("Enter Loan ID: ");
        int loanId = scanner.nextInt();

        String status = loanService.loanStatus(loanId);
        System.out.println("Loan Status: " + status);
    }

    private static void calculateEMI() {
        System.out.println("=== Calculate EMI ===");
        System.out.print("Enter Loan ID: ");
        int loanId = scanner.nextInt();

        double emi = loanService.calculateEMI(loanId);
        if (emi > 0) {
            System.out.println("Calculated EMI: " + emi);
        } else {
            System.out.println("Loan not found!");
        }
    }

    private static void repayLoan() {
        System.out.println("=== Repay Loan ===");
        System.out.print("Enter Loan ID: ");
        int loanId = scanner.nextInt();
        System.out.print("Enter Repayment Amount: ");
        double amount = scanner.nextDouble();

        loanService.loanRepayment(loanId, amount);
    }

    private static void getAllLoans() {
        System.out.println("=== View All Loans ===");
        List<Loan> loans = loanService.getAllLoans();
        if (!loans.isEmpty()) {
            for (Loan loan : loans) {
                System.out.println(loan);
            }
        } else {
            System.out.println("No loans available.");
        }
    }

    private static void getLoanById() {
        System.out.println("=== View Loan by ID ===");
        System.out.print("Enter Loan ID: ");
        int loanId = scanner.nextInt();

        Loan loan = loanService.getLoanById(loanId);
        if (loan != null) {
            System.out.println(loan);
        } else {
            System.out.println("Loan not found!");
        }
    }
}