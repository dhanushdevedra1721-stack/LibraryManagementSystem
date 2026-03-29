# Kotlin Library Management System

**[Click here to run the application live in your browser!](https://replit.com/@dhanushdevendra/LibraryManagementSystem)**

A console-based Library Management application written in Kotlin. This project demonstrates object-oriented programming, data classes, and real-world library workflows like book issuing, returns, overdue fines, and a simulated day system.

## Features

- **Add Books:** Register new books with a unique code, title, author, and number of copies. Adding an existing code automatically increases its stock.
- **Remove Books:** Delete books from the catalog — blocked if copies are still out on loan.
- **Search Books:** Find books by title, author, or book code (case-insensitive).
- **View Catalog:** Display all books with availability status (copies left vs total).
- **Add Members:** Register library members with a unique member code and name.
- **Remove Members:** Remove members — blocked if they have unreturned books.
- **Issue Books:** Lend a book to a member with due-date tracking. Enforces a 3-book loan limit and prevents duplicate loans.
- **Return Books:** Process returns with automatic overdue fine calculation (Rs. 10/day after 7 days).
- **View Active Loans:** See all currently issued books with their due status.
- **Day Simulation:** Advance the in-app day counter to simulate time passing and trigger overdue detection.

## Prerequisites

To run this project locally, you need:

- [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/downloads/)
- [Kotlin Compiler](https://kotlinlang.org/docs/command-line.html)

## How to Run Locally

1. **Clone the repository:**
   ```
   git clone https://github.com/<your-username>/<your-repo-name>.git
   ```

2. **Navigate to the folder:**
   ```
   cd path/to/your/folder
   ```

3. **Compile the Kotlin file:**
   ```
   kotlinc lib.kt -include-runtime -d library.jar
   ```

4. **Run the application:**
   ```
   java -jar library.jar
   ```

## How It Works

The system runs a menu loop with 12 options. It tracks a simulated current day — use option **11 (Next Day)** to advance time and see overdue fines kick in on returns.

**Loan Rules:**
- Max **3 books** per member at a time
- Loan period: **7 days**
- Fine: **Rs. 10 per overdue day**

---

*Developed by Dhanush Devendra*
