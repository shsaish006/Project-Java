# Project-Java
# ATM GUI Application

![ATM GUI Screenshot](https://placehold.co/800x600/87CEEB/FFFFFF?text=ATM+Machine)

A Java Swing-based ATM simulation with graphical user interface that demonstrates core banking operations.

## Features

- **User Authentication**
  - Account number and PIN validation
  - Secure password input (masked with asterisks)

- **Account Operations**
  - Balance inquiry (available and total balance)
  - Cash withdrawal with denomination validation
  - Deposit processing

- **User Interface**
  - Interactive buttons for all operations
  - Clear transaction messages
  - Visual feedback for all actions

## Technical Components

### Core Classes

1. **AtmGui** - Main application window
2. **AtmPanel** - Primary GUI panel handling all interactions
3. **BankAccount** - Models account data and operations
4. **BankDatabase** - Manages account storage and authentication
5. **CashDispenser** - Handles cash availability and dispensing
6. **DepositSlot** - Simulates deposit envelope processing
7. **ImageLoader** - Utility for loading and caching images

## How to Run

1. Ensure you have Java JDK 8+ installed
2. Clone the repository
3. Compile and run the main class:
   ```
   javac AtmGui.java
   java AtmGui
   ```

## Sample Accounts

For testing purposes, two accounts are pre-configured:

| Account Number | PIN  | Balance |
|----------------|------|---------|
| 12345          | 1111 | $1000   |
| 98765          | 2222 | $500    |

## Implementation Notes

- Uses Swing for GUI components
- Follows MVC-like architecture
- Includes basic input validation
- Designed for 800x600 resolution

## Future Enhancements

- [ ] Add transaction history
- [ ] Implement proper database connection
- [ ] Add sound effects
- [ ] Support for multiple currencies
- [ ] Responsive layout for different screen sizes

## License

MIT License - Free for educational and personal use
