# Virtual Financial Portfolio Engine (CLI)

An object-oriented Java console application simulating an investment portfolio with real-time buy/sell transactional mechanics and CSV data persistence.

## Features & OOP Concepts
- **Inheritance & Polymorphism:** Abstract `FinancialProduct` base class extended by `Stock` and `Crypto` derived classes.
- **Transactional Logic:** Interactive buy/sell system with balance verification and unit tracking.
- **Data Persistence:** Local storage mapping via Java I/O Streams to `portfolio_data.csv`.

## How to Run
1. Compile the Java file:
   ```bash
   javac FinancialPortfolioApp.java
Run the application:

Bash
java FinancialPortfolioApp
