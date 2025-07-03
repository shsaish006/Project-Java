import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner; // Still needed for Keypad class if not refactoring it out completely

/**
 * Main class for the GUI ATM machine simulation.

 */
public class AtmGui extends JFrame {

    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;

    /**
     * Constructor for the AtmGui frame.
     */
    public AtmGui() {
        setTitle("GUI ATM Machine");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setResizable(false); // Fixed size for simplicity

        // Create and add the ATM panel
        AtmPanel atmPanel = new AtmPanel();
        add(atmPanel);

        setVisible(true);
    }

    /**
     * Main method to start the GUI ATM
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(AtmGui::new);
    }
}

/**
 * Represents a bank account.
 */
class BankAccount {
    private int accountNumber;
    private int pin;
    private double availableBalance;
    private double totalBalance;

    public BankAccount(int accountNumber, int pin, double availableBalance, double totalBalance) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.availableBalance = availableBalance;
        this.totalBalance = totalBalance;
    }

    public boolean validatePIN(int userPin) {
        return userPin == pin;
    }

    public double getAvailableBalance() {
        return availableBalance;
    }

    public double getTotalBalance() {
        return totalBalance;
    }

    public void credit(double amount) {
        totalBalance += amount;
        availableBalance += amount;
    }

    public void debit(double amount) {
        availableBalance -= amount;
        totalBalance -= amount;
    }

    public int getAccountNumber() {
        return accountNumber;
    }
}

/**
 * Simulates the bank's database of accounts.
 */
class BankDatabase {
    private Map<Integer, BankAccount> accounts;

    public BankDatabase() {
        accounts = new HashMap<>();
        accounts.put(12345, new BankAccount(12345, 1111, 1000.0, 1000.0));
        accounts.put(98765, new BankAccount(98765, 2222, 500.0, 500.0));
    }

    public BankAccount getAccount(int accountNumber) {
        return accounts.get(accountNumber);
    }

    public boolean authenticateUser(int userAccountNumber, int userPin) {
        BankAccount userAccount = getAccount(userAccountNumber);
        if (userAccount != null) {
            return userAccount.validatePIN(userPin);
        }
        return false;
    }
}

/**
 * Represents the ATM's cash dispenser.
 */
class CashDispenser {
    private final static int INITIAL_CASH = 500;
    private int count;

    public CashDispenser() {
        count = INITIAL_CASH;
    }

    public void dispenseCash(double amount) {
        int billsRequired = (int) (amount / 20);
        count -= billsRequired;
    }

    public boolean isSufficientCashAvailable(double amount) {
        int billsRequired = (int) (amount / 20);
        return count >= billsRequired;
    }
}

/**
 * Represents the ATM's deposit slot.
 */
class DepositSlot {
    public boolean isEnvelopeReceived() {
        return true; // Always true for simulation
    }
}

/**
 * Utility class for loading images from URLs and caching them.
 *
 */
class ImageLoader {
    private static final Map<String, Image> IMAGE_CACHE = new HashMap<>();

    public static Image loadImage(String imageUrl) {
        if (IMAGE_CACHE.containsKey(imageUrl)) {
            return IMAGE_CACHE.get(imageUrl);
        }

        try {
            URL url = new URL(imageUrl);
            ImageIcon icon = new ImageIcon(url);
            Image image = icon.getImage();
            if (image != null) {
                IMAGE_CACHE.put(imageUrl, image);
                return image;
            }
        } catch (Exception e) {
            System.err.println("Error loading image from URL: " + imageUrl + " - " + e.getMessage());
        }
        return null;
    }
}

/**
 * The main JPanel for the GUI ATM, handling drawing and interaction.
 */
class AtmPanel extends JPanel implements ActionListener {

    // --- Image URLs (Placeholders) ---
    private static final String ATM_BACKGROUND_URL = "https://placehold.co/800x600/87CEEB/FFFFFF?text=ATM+Machine";
    private static final String ATM_SCREEN_URL = "https://placehold.co/500x250/222222/00FF00?text=ATM+Screen";
    private static final String BUTTON_GENERIC_URL = "https://placehold.co/100x50/CCCCCC/000000?text=Button";
    private static final String BUTTON_WITHDRAW_URL = "https://placehold.co/100x50/ADD8E6/000000?text=Withdraw";
    private static final String BUTTON_DEPOSIT_URL = "https://placehold.co/100x50/90EE90/000000?text=Deposit";
    private static final String BUTTON_BALANCE_URL = "https://placehold.co/100x50/FFD700/000000?text=Balance";
    private static final String BUTTON_EXIT_URL = "https://placehold.co/100x50/FF6347/000000?text=Exit";
    private static final String BUTTON_LOGIN_URL = "https://placehold.co/100x50/32CD32/FFFFFF?text=Login";
    private static final String BUTTON_BACK_URL = "https://placehold.co/100x50/808080/FFFFFF?text=Back";


    // --- Loaded Images ---
    private Image atmBackground;
    private Image atmScreenImage;
    private Image buttonGenericImage;
    private Image buttonWithdrawImage;
    private Image buttonDepositImage;
    private Image buttonBalanceImage;
    private Image buttonExitImage;
    private Image buttonLoginImage;
    private Image buttonBackImage;

    // --- ATM Components ---
    private BankDatabase bankDatabase;
    private CashDispenser cashDispenser;
    private DepositSlot depositSlot;

    // --- GUI Elements ---
    private JTextArea screenDisplay; // For displaying messages to the user
    private JPasswordField inputField;   // Changed from JTextField to JPasswordField
    private JButton loginButton;
    private JButton balanceButton;
    private JButton withdrawButton;
    private JButton depositButton;
    private JButton exitButton;
    private JButton backButton; // To go back to main menu

    // --- ATM State ---
    private BankAccount currentAccount;
    private String currentScreenState; // "LOGIN", "MAIN_MENU", "BALANCE", "WITHDRAW", "DEPOSIT"
    private String loginStep; // "ACCOUNT_NUMBER" or "PIN"
    private int tempAccountNumber; // Temporarily store account number during login

    // --- Constants for states ---
    private static final String STATE_LOGIN = "LOGIN";
    private static final String STATE_MAIN_MENU = "MAIN_MENU";
    private static final String STATE_BALANCE = "BALANCE";
    private static final String STATE_WITHDRAW = "WITHDRAW";
    private static final String STATE_DEPOSIT = "DEPOSIT";

    private static final String LOGIN_STEP_ACCOUNT_NUMBER = "ACCOUNT_NUMBER";
    private static final String LOGIN_STEP_PIN = "PIN";


    /**
     * Constructor for AtmPanel. Initializes components and loads images.
     */
    public AtmPanel() {
        setLayout(null); // Use absolute positioning for custom layout
        setBackground(Color.DARK_GRAY);

        // Initialize ATM core components
        bankDatabase = new BankDatabase();
        cashDispenser = new CashDispenser();
        depositSlot = new DepositSlot();

        // Load images
        loadImages();

        // Setup GUI components
        setupGuiComponents();

        // Set initial state
        resetLoginState(); // Start at account number input
        currentScreenState = STATE_LOGIN;
        updateScreen();
    }

    /**
     * Loads all necessary images using ImageLoader.
     */
    private void loadImages() {
        atmBackground = ImageLoader.loadImage(ATM_BACKGROUND_URL);
        atmScreenImage = ImageLoader.loadImage(ATM_SCREEN_URL);
        buttonGenericImage = ImageLoader.loadImage(BUTTON_GENERIC_URL);
        buttonWithdrawImage = ImageLoader.loadImage(BUTTON_WITHDRAW_URL);
        buttonDepositImage = ImageLoader.loadImage(BUTTON_DEPOSIT_URL);
        buttonBalanceImage = ImageLoader.loadImage(BUTTON_BALANCE_URL);
        buttonExitImage = ImageLoader.loadImage(BUTTON_EXIT_URL);
        buttonLoginImage = ImageLoader.loadImage(BUTTON_LOGIN_URL);
        buttonBackImage = ImageLoader.loadImage(BUTTON_BACK_URL);
    }

    /**
     * Sets up the GUI components (text areas, fields, buttons).
     */
    private void setupGuiComponents() {
        // Screen Display (JTextArea within a JScrollPane for potential scrolling)
        screenDisplay = new JTextArea();
        screenDisplay.setBounds(150, 100, 500, 250); // Position and size for the "screen" area
        screenDisplay.setEditable(false);
        screenDisplay.setBackground(new Color(34, 34, 34)); // Dark background for screen
        screenDisplay.setForeground(new Color(0, 255, 0)); // Green text for screen
        screenDisplay.setFont(new Font("Monospaced", Font.BOLD, 16));
        screenDisplay.setLineWrap(true);
        screenDisplay.setWrapStyleWord(true);
        add(screenDisplay);

        // Input Field - Now JPasswordField
        inputField = new JPasswordField(); // Changed to JPasswordField
        inputField.setBounds(150, 360, 500, 30); // Below the screen
        inputField.setFont(new Font("Arial", Font.PLAIN, 18));
        inputField.setHorizontalAlignment(JTextField.CENTER);
        inputField.addActionListener(this); // Allow pressing Enter to submit
        add(inputField);

        // Buttons
        // Login Button
        loginButton = new JButton("Login");
        loginButton.setBounds(350, 400, 100, 50); // Centered below input
        loginButton.addActionListener(this);
        loginButton.setIcon(new ImageIcon(buttonLoginImage)); // Set image as icon
        loginButton.setHorizontalTextPosition(SwingConstants.CENTER); // Center text over image
        loginButton.setVerticalTextPosition(SwingConstants.CENTER);
        add(loginButton);

        // Transaction Buttons (initially hidden)
        balanceButton = new JButton("Balance");
        balanceButton.setBounds(50, 450, 150, 50); // Bottom left
        balanceButton.addActionListener(this);
        balanceButton.setIcon(new ImageIcon(buttonBalanceImage));
        balanceButton.setHorizontalTextPosition(SwingConstants.CENTER);
        balanceButton.setVerticalTextPosition(SwingConstants.CENTER);
        add(balanceButton);

        withdrawButton = new JButton("Withdraw");
        withdrawButton.setBounds(220, 450, 150, 50); // Bottom center-left
        withdrawButton.addActionListener(this);
        withdrawButton.setIcon(new ImageIcon(buttonWithdrawImage));
        withdrawButton.setHorizontalTextPosition(SwingConstants.CENTER);
        withdrawButton.setVerticalTextPosition(SwingConstants.CENTER);
        add(withdrawButton);

        depositButton = new JButton("Deposit");
        depositButton.setBounds(390, 450, 150, 50); // Bottom center-right
        depositButton.addActionListener(this);
        depositButton.setIcon(new ImageIcon(buttonDepositImage));
        depositButton.setHorizontalTextPosition(SwingConstants.CENTER);
        depositButton.setVerticalTextPosition(SwingConstants.CENTER);
        add(depositButton);

        exitButton = new JButton("Exit");
        exitButton.setBounds(560, 450, 150, 50); // Bottom right
        exitButton.addActionListener(this);
        exitButton.setIcon(new ImageIcon(buttonExitImage));
        exitButton.setHorizontalTextPosition(SwingConstants.CENTER);
        exitButton.setVerticalTextPosition(SwingConstants.CENTER);
        add(exitButton);

        // Back button (for transaction screens)
        backButton = new JButton("Back to Main Menu");
        backButton.setBounds(300, 520, 200, 40); // Below other buttons
        backButton.addActionListener(this);
        backButton.setIcon(new ImageIcon(buttonBackImage));
        backButton.setHorizontalTextPosition(SwingConstants.CENTER);
        backButton.setVerticalTextPosition(SwingConstants.CENTER);
        backButton.setVisible(false); // Initially hidden
        add(backButton);
    }

    /**
     * Paints the background image and the ATM screen image.
     * @param g The Graphics object.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw ATM background image
        if (atmBackground != null) {
            g2d.drawImage(atmBackground, 0, 0, AtmGui.WINDOW_WIDTH, AtmGui.WINDOW_HEIGHT, this);
        } else {
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect(0, 0, AtmGui.WINDOW_WIDTH, AtmGui.WINDOW_HEIGHT);
        }

        // Draw ATM screen image (behind the JTextArea)
        if (atmScreenImage != null) {
            g2d.drawImage(atmScreenImage, 145, 95, 510, 260, this); // Slightly larger to act as frame
        }
    }

    /**
     * Handles button clicks and input field 'Enter' presses.
     * @param e The ActionEvent generated by a button click or JTextField 'Enter'.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // If the action comes from the input field (Enter key press) AND we are in login state
        if (e.getSource() == inputField && currentScreenState.equals(STATE_LOGIN)) {
            handleLogin();
        } else if (e.getSource() == loginButton && currentScreenState.equals(STATE_LOGIN)) {
            handleLogin(); // Also handle login button click
        }
        else if (e.getSource() == balanceButton) {
            showBalance();
        } else if (e.getSource() == withdrawButton) {
            showWithdrawalOptions();
        } else if (e.getSource() == depositButton) {
            showDepositPrompt();
        } else if (e.getSource() == exitButton) {
            handleExit();
        } else if (e.getSource() == backButton) {
            showMainMenu();
        }
        // Handle specific withdrawal amounts if on withdrawal screen
        else if (currentScreenState.equals(STATE_WITHDRAW) && e.getSource() instanceof JButton) {
            JButton clickedButton = (JButton) e.getSource();
            try {
                // Check if it's a numeric amount button
                if (clickedButton.getText().matches("\\$\\d+")) {
                    double amount = Double.parseDouble(clickedButton.getText().replace("$", ""));
                    performWithdrawal(amount);
                } else if (clickedButton.getText().equals("Custom Amount")) {
                    // Allow user to type custom amount
                    screenDisplay.setText("Enter custom withdrawal amount (multiples of 20):");
                    inputField.setVisible(true);
                    inputField.setText("");
                    inputField.requestFocusInWindow();
                    // Custom amount will be processed when user types in inputField and presses Enter
                }
            } catch (NumberFormatException ex) {
                // Not a withdrawal amount button, ignore or handle
                screenDisplay.setText("Invalid amount format.");
            }
        }
        // Handle custom withdrawal amount entered in inputField
        else if (e.getSource() == inputField && currentScreenState.equals(STATE_WITHDRAW)) {
            try {
                double amount = Double.parseDouble(inputField.getText().trim());
                performWithdrawal(amount);
            } catch (NumberFormatException ex) {
                screenDisplay.setText("Invalid amount. Please enter a number.");
            }
        }
        // Handle specific deposit confirmation if on deposit screen
        else if (currentScreenState.equals(STATE_DEPOSIT) && e.getSource() instanceof JButton) {
            JButton clickedButton = (JButton) e.getSource();
            if (clickedButton.getText().equals("Confirm Deposit")) {
                performDeposit();
            }
        }
        // Handle deposit amount entered in inputField
        else if (e.getSource() == inputField && currentScreenState.equals(STATE_DEPOSIT)) {
            performDeposit(); // Process deposit amount from input field
        }
        updateScreen(); // Update screen after any action
    }

    /**
     * Resets the login state to prompt for account number.
     */
    private void resetLoginState() {
        loginStep = LOGIN_STEP_ACCOUNT_NUMBER;
        tempAccountNumber = 0;
        currentAccount = null;
        inputField.setText("");
        inputField.setEchoChar((char)0); // Ensure text is visible for account number
    }

    /**
     * Updates the visibility of GUI components and the display text based on the current state.
     */
    private void updateScreen() {
        // Hide all transaction buttons and back button by default
        balanceButton.setVisible(false);
        withdrawButton.setVisible(false);
        depositButton.setVisible(false);
        exitButton.setVisible(false);
        backButton.setVisible(false);
        loginButton.setVisible(false);
        inputField.setVisible(false);
        inputField.setText(""); // Clear input field

        // Remove old dynamic buttons (e.g., withdrawal amounts)
        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof JButton && comp != loginButton && comp != balanceButton &&
                    comp != withdrawButton && comp != depositButton && comp != exitButton && comp != backButton) {
                remove(comp);
            }
        }
        revalidate(); // Re-layout the container
        repaint(); // Repaint to reflect changes

        // Set visibility and text based on state
        switch (currentScreenState) {
            case STATE_LOGIN:
                loginButton.setVisible(true);
                inputField.setVisible(true);
                inputField.requestFocusInWindow(); // Give focus to input field
                if (loginStep.equals(LOGIN_STEP_ACCOUNT_NUMBER)) {
                    screenDisplay.setText("Welcome!\nPlease enter your account number:");
                    inputField.setToolTipText("Enter Account Number");
                    inputField.setEchoChar((char)0); // Show characters
                } else { // LOGIN_STEP_PIN
                    screenDisplay.setText("Account: " + tempAccountNumber + "\nPlease enter your PIN:");
                    inputField.setToolTipText("Enter PIN");
                    inputField.setEchoChar('*'); // Hide characters for PIN
                }
                break;
            case STATE_MAIN_MENU:
                screenDisplay.setText("Authentication successful!\n\nATM Main Menu:\n1 - View my balance\n2 - Withdraw cash\n3 - Deposit funds\n4 - Exit");
                balanceButton.setVisible(true);
                withdrawButton.setVisible(true);
                depositButton.setVisible(true);
                exitButton.setVisible(true);
                break;
            case STATE_BALANCE:
                screenDisplay.setText("Balance Information:\n- Available balance: $" + String.format("%,.2f", currentAccount.getAvailableBalance()) +
                        "\n- Total balance:     $" + String.format("%,.2f", currentAccount.getTotalBalance()));
                backButton.setVisible(true);
                break;
            case STATE_WITHDRAW:
                screenDisplay.setText("Withdrawal Menu:\nChoose a withdrawal amount (multiples of 20):");
                inputField.setVisible(true); // For custom amount
                setupWithdrawalButtons();
                backButton.setVisible(true);
                break;
            case STATE_DEPOSIT:
                screenDisplay.setText("Please enter the deposit amount (e.g., 100.00 for $100.00, or 0 to cancel):");
                inputField.setVisible(true);
                setupDepositButtons(); // Setup confirm/cancel buttons
                backButton.setVisible(true);
                break;
            default:
                screenDisplay.setText("An unexpected error occurred. Please restart.");
                break;
        }
    }

    /**
     * Handles the login process.
     */
    private void handleLogin() {
        try {
            String input = new String(inputField.getPassword()).trim(); // Get password as String
            if (input.isEmpty()) {
                screenDisplay.setText("Input cannot be empty. Please enter a value.");
                return;
            }

            if (loginStep.equals(LOGIN_STEP_ACCOUNT_NUMBER)) {
                tempAccountNumber = Integer.parseInt(input);
                loginStep = LOGIN_STEP_PIN; // Move to PIN step
                inputField.setText(""); // Clear for PIN
                inputField.setEchoChar('*'); // Hide characters for PIN
                screenDisplay.setText("Account: " + tempAccountNumber + "\nPlease enter your PIN:");
            } else if (loginStep.equals(LOGIN_STEP_PIN)) {
                int pin = Integer.parseInt(input);
                if (bankDatabase.authenticateUser(tempAccountNumber, pin)) {
                    currentAccount = bankDatabase.getAccount(tempAccountNumber); // Get real account
                    currentScreenState = STATE_MAIN_MENU;
                    screenDisplay.setText("Authentication successful!");
                    inputField.setText(""); // Clear field
                } else {
                    screenDisplay.setText("Invalid account number or PIN. Please try again.");
                    resetLoginState(); // Reset to account number input
                }
            }
        } catch (NumberFormatException ex) {
            screenDisplay.setText("Invalid input. Please enter numbers only.");
            inputField.setText("");
            resetLoginState(); // Reset on invalid input
        }
    }

    /**
     * Shows the balance inquiry screen.
     */
    private void showBalance() {
        if (currentAccount != null) {
            currentScreenState = STATE_BALANCE;
        } else {
            screenDisplay.setText("Error: Not logged in. Please log in.");
            currentScreenState = STATE_LOGIN;
            resetLoginState();
        }
    }

    /**
     * Sets up buttons for fixed withdrawal amounts.
     */
    private void setupWithdrawalButtons() {
        int[] amounts = {20, 40, 60, 100, 200};
        int xOffset = 50;
        int yOffset = 400;
        int buttonWidth = 100;
        int buttonHeight = 50;
        int spacing = 10;

        for (int i = 0; i < amounts.length; i++) {
            JButton amountButton = new JButton("$" + amounts[i]);
            amountButton.setBounds(xOffset + (i * (buttonWidth + spacing)), yOffset, buttonWidth, buttonHeight);
            amountButton.addActionListener(this);
            amountButton.setIcon(new ImageIcon(buttonGenericImage));
            amountButton.setHorizontalTextPosition(SwingConstants.CENTER);
            amountButton.setVerticalTextPosition(SwingConstants.CENTER);
            add(amountButton);
        }
        // Custom amount button
        JButton customAmountButton = new JButton("Custom Amount");
        customAmountButton.setBounds(xOffset + (amounts.length * (buttonWidth + spacing)), yOffset, 150, buttonHeight);
        customAmountButton.addActionListener(this);
        customAmountButton.setIcon(new ImageIcon(buttonGenericImage));
        customAmountButton.setHorizontalTextPosition(SwingConstants.CENTER);
        customAmountButton.setVerticalTextPosition(SwingConstants.CENTER);
        add(customAmountButton);
    }

    /**
     * Shows the withdrawal options screen.
     */
    private void showWithdrawalOptions() {
        currentScreenState = STATE_WITHDRAW;
    }

    /**
     * Performs the withdrawal transaction.
     * @param amount The amount to withdraw.
     */
    private void performWithdrawal(double amount) {
        if (currentAccount == null) {
            screenDisplay.setText("Error: Not logged in. Please log in.");
            currentScreenState = STATE_LOGIN;
            resetLoginState();
            return;
        }

        if (amount % 20 != 0 || amount <= 0) {
            screenDisplay.setText("Withdrawal amounts must be positive multiples of $20.");
            return;
        }

        if (currentAccount.getAvailableBalance() >= amount) {
            if (cashDispenser.isSufficientCashAvailable(amount)) {
                currentAccount.debit(amount);
                cashDispenser.dispenseCash(amount);
                screenDisplay.setText("Your cash of $" + String.format("%,.2f", amount) + " has been dispensed.\nPlease take your cash now.");
            } else {
                screenDisplay.setText("Insufficient cash available in the ATM. Please choose a smaller amount.");
            }
        } else {
            screenDisplay.setText("Insufficient funds in your account. Please choose a smaller amount.");
        }
        // After transaction, go back to main menu after a short delay or user action
        Timer timer = new Timer(3000, e -> showMainMenu());
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Sets up buttons for deposit confirmation/cancellation.
     */
    private void setupDepositButtons() {
        JButton confirmButton = new JButton("Confirm Deposit");
        confirmButton.setBounds(250, 400, 150, 50);
        confirmButton.addActionListener(this);
        confirmButton.setIcon(new ImageIcon(buttonDepositImage));
        confirmButton.setHorizontalTextPosition(SwingConstants.CENTER);
        confirmButton.setVerticalTextPosition(SwingConstants.CENTER);
        add(confirmButton);

        JButton cancelButton = new JButton("Cancel Deposit");
        cancelButton.setBounds(410, 400, 150, 50);
        cancelButton.addActionListener(e -> showMainMenu());
        cancelButton.setIcon(new ImageIcon(buttonExitImage));
        cancelButton.setHorizontalTextPosition(SwingConstants.CENTER);
        cancelButton.setVerticalTextPosition(SwingConstants.CENTER);
        add(cancelButton);
    }

    /**
     * Shows the deposit prompt screen.
     */
    private void showDepositPrompt() {
        currentScreenState = STATE_DEPOSIT;
    }

    /**
     * Performs the deposit transaction.
     */
    private void performDeposit() {
        if (currentAccount == null) {
            screenDisplay.setText("Error: Not logged in. Please log in.");
            currentScreenState = STATE_LOGIN;
            resetLoginState();
            return;
        }

        try {
            double amount = Double.parseDouble(inputField.getText());
            if (amount <= 0) {
                screenDisplay.setText("Deposit amount must be positive.");
                return;
            }
            // For simplicity, we assume cash is always inserted correctly
            if (depositSlot.isEnvelopeReceived()) {
                currentAccount.credit(amount);
                screenDisplay.setText("Your deposit of $" + String.format("%,.2f", amount) + " has been credited to your account.");
            } else {
                // This branch is currently unreachable due to isEnvelopeReceived always returning true
                screenDisplay.setText("You did not insert an envelope, so your transaction has been canceled.");
            }
        } catch (NumberFormatException ex) {
            screenDisplay.setText("Invalid amount. Please enter a number.");
        }
        // After transaction, go back to main menu after a short delay or user action
        Timer timer = new Timer(3000, e -> showMainMenu());
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Handles exiting the ATM.
     */
    private void handleExit() {
        currentAccount = null; // Log out
        currentScreenState = STATE_LOGIN;
        resetLoginState(); // Reset login state for next user
        screenDisplay.setText("Thank you for using the ATM. Goodbye!");
        // Optionally, dispose the frame: SwingUtilities.getWindowAncestor(this).dispose();
    }

    /**
     * Returns to the main menu.
     */
    private void showMainMenu() {
        currentScreenState = STATE_MAIN_MENU;
        updateScreen();
    }
}
