package task03progress;

import java.io.*;
import java.util.*;

// Base Abstract Class (Abstraction & Encapsulation)
abstract class FinancialProduct {
    private String symbol;
    private String name;
    private double currentPrice;

    public FinancialProduct(String symbol, String name, double currentPrice) {
        this.symbol = symbol;
        this.name = name;
        this.currentPrice = currentPrice;
    }

    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }

    public abstract String getType();
}

// Derived Class 1 (Inheritance & Polymorphism)
class Stock extends FinancialProduct {
    public Stock(String symbol, String name, double currentPrice) {
        super(symbol, name, currentPrice);
    }

    @Override
    public String getType() {
        return "Stock";
    }
}

// Derived Class 2 (Inheritance & Polymorphism)
class Crypto extends FinancialProduct {
    public Crypto(String symbol, String name, double currentPrice) {
        super(symbol, name, currentPrice);
    }

    @Override
    public String getType() {
        return "Crypto";
    }
}

// Main Portfolio Engine Class
public class FinancialPortfolioApp {
    private static final String FILE_NAME = "portfolio_data.csv";
    private static final Map<String, FinancialProduct> market = new HashMap<>();
    private static final Map<String, Integer> portfolio = new HashMap<>();
    private static double cashBalance = 10000.00; // Starting cash balance

    public static void main(String[] args) {
        initMarketData();
        loadPortfolioFromFile();

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=== VIRTUAL FINANCIAL PORTFOLIO ===");
            System.out.printf("Available Cash: $%.2f%n", cashBalance);
            System.out.println("1. View Market Products");
            System.out.println("2. View My Portfolio");
            System.out.println("3. Buy Asset");
            System.out.println("4. Sell Asset");
            System.out.println("5. Save & Exit");
            System.out.print("Select an option (1-5): ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    displayMarket();
                    break;
                case 2:
                    displayPortfolio();
                    break;
                case 3:
                    buyAsset(scanner);
                    break;
                case 4:
                    sellAsset(scanner);
                    break;
                case 5:
                    savePortfolioToFile();
                    running = false;
                    System.out.println("Data saved successfully. Exiting system...");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
        scanner.close();
    }

    private static void initMarketData() {
        market.put("AAPL", new Stock("AAPL", "Apple Inc.", 180.50));
        market.put("TSLA", new Stock("TSLA", "Tesla Inc.", 240.00));
        market.put("BTC", new Crypto("BTC", "Bitcoin", 62000.00));
        market.put("ETH", new Crypto("ETH", "Ethereum", 3400.00));
    }

    private static void displayMarket() {
        System.out.println("\n--- Available Assets ---");
        for (FinancialProduct p : market.values()) {
            System.out.printf("[%s] %s (%s) - $%.2f%n", p.getType(), p.getName(), p.getSymbol(), p.getCurrentPrice());
        }
    }

    private static void displayPortfolio() {
        System.out.println("\n--- My Portfolio Holdings ---");
        if (portfolio.isEmpty()) {
            System.out.println("No assets owned yet.");
            return;
        }

        double totalValue = cashBalance;
        for (Map.Entry<String, Integer> entry : portfolio.entrySet()) {
            String symbol = entry.getKey();
            int qty = entry.getValue();
            FinancialProduct product = market.get(symbol);
            double assetVal = qty * product.getCurrentPrice();
            totalValue += assetVal;

            System.out.printf("%s (%s): %d units | Value: $%.2f%n", product.getName(), symbol, qty, assetVal);
        }
        System.out.printf("Total Portfolio Valuation (including cash): $%.2f%n", totalValue);
    }

    private static void buyAsset(Scanner scanner) {
        System.out.print("Enter Symbol to buy (e.g. AAPL, BTC): ");
        String symbol = scanner.nextLine().toUpperCase();

        if (!market.containsKey(symbol)) {
            System.out.println("Asset not found!");
            return;
        }

        System.out.print("Enter Quantity: ");
        int qty;
        try {
            qty = Integer.parseInt(scanner.nextLine());
            if (qty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity!");
            return;
        }

        FinancialProduct product = market.get(symbol);
        double totalCost = product.getCurrentPrice() * qty;

        if (totalCost > cashBalance) {
            System.out.println("Insufficient cash balance!");
            return;
        }

        cashBalance -= totalCost;
        portfolio.put(symbol, portfolio.getOrDefault(symbol, 0) + qty);
        System.out.printf("Successfully bought %d units of %s!%n", qty, symbol);
    }

    private static void sellAsset(Scanner scanner) {
        System.out.print("Enter Symbol to sell: ");
        String symbol = scanner.nextLine().toUpperCase();

        if (!portfolio.containsKey(symbol) || portfolio.get(symbol) == 0) {
            System.out.println("You do not own this asset!");
            return;
        }

        System.out.print("Enter Quantity: ");
        int qty;
        try {
            qty = Integer.parseInt(scanner.nextLine());
            if (qty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity!");
            return;
        }

        int currentQty = portfolio.get(symbol);
        if (qty > currentQty) {
            System.out.println("You cannot sell more than you own!");
            return;
        }

        FinancialProduct product = market.get(symbol);
        double totalReturn = product.getCurrentPrice() * qty;

        cashBalance += totalReturn;
        if (qty == currentQty) {
            portfolio.remove(symbol);
        } else {
            portfolio.put(symbol, currentQty - qty);
        }

        System.out.printf("Successfully sold %d units of %s!%n", qty, symbol);
    }

    private static void savePortfolioToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            writer.println("CASH," + cashBalance);
            for (Map.Entry<String, Integer> entry : portfolio.entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue());
            }
        } catch (IOException e) {
            System.out.println("Error saving portfolio: " + e.getMessage());
        }
    }

    private static void loadPortfolioFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    if (parts[0].equals("CASH")) {
                        cashBalance = Double.parseDouble(parts[1]);
                    } else {
                        portfolio.put(parts[0], Integer.parseInt(parts[1]));
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Notice: Fresh portfolio starting or invalid file format.");
        }
    }
}