package edu.uw.danco;

import edu.uw.ext.framework.account.*;
import edu.uw.ext.framework.order.Order;

import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 * Created with IntelliJ IDEA.
 * User: dcostinett
 * Date: 4/13/13
 * Time: 6:13 PM
 */
public final class AccountImpl implements Account {
    /** Log important events */
    private static final Logger LOGGER = Logger.getLogger(AccountImpl.class.getName());

    /** the account name */
    private String name;

    /** account balance */
    private int balance;

    /** full name of account holder */
    private String fullName;

    /** address of account holder */
    private Address address;

    /** phone number for account holder */
    private String phone;

    /** email address for account holder */
    private String email;

    /** cc for account holder */
    private CreditCard creditCard;

    /** password hash for account */
    private byte[] passwordHash;

    /** unserializable account manager */
    private transient AccountManager accountManager;

    /** Preference value holder for min account name length */
    private static final int minAccountNameLength;

    /** Preference value holder for min account balance */
    private static final int minAccountBalance;

    static {
        final Preferences prefs = Preferences.userNodeForPackage(Account.class);
        // init prefs
        minAccountNameLength = prefs.getInt("minAccountLength", 8);
        minAccountBalance = prefs.getInt("minAccountBalance", 0);
    }

    /**
     * Default constructor for bean support
     */
    public AccountImpl() {
    }


    @Override
    /**
     * Gets the name
     */
    public String getName() {
        return name;
    }


    @Override
    /**
     * Sets the name
     */
    public void setName(String name) throws AccountException {
        if (name.length() < minAccountNameLength) {
            throw new AccountException("Account name too short: " + name);
        }
        this.name = name;
    }


    @Override
    /**
     * Gets the password hash
     */
    public byte[] getPasswordHash() {
        return passwordHash;
    }


    @Override
    /**
     * sets the password hash
     */
    public void setPasswordHash(byte[] passwordHash) {
        // make defensive copy of the array as a best practice
        this.passwordHash = passwordHash;
    }


    @Override
    /**
     * Gets the balance
     */
    public int getBalance() {
        return balance;
    }


    @Override
    /**
     * Sets the balance
     */
    public void setBalance(int balance) {
        if (balance < minAccountBalance) {
            //log this maybe?
        }
        this.balance = balance;
    }


    @Override
    /**
     * Gets the full name
     */
    public String getFullName() {
        return fullName;
    }


    @Override
    /**
     * Sets the full name
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    @Override
    /**
     * Gets the address
     */
    public Address getAddress() {
        return address;
    }


    @Override
    /**
     * Sets the address
     */
    public void setAddress(Address address) {
        this.address = address;
    }


    @Override
    /**
     * Gets the phone
     */
    public String getPhone() {
        return phone;
    }


    @Override
    /**
     * Sets the phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }


    @Override
    /**
     * Gets the email
     */
    public String getEmail() {
        return email;
    }


    @Override
    /**
     * Sets the email
     */
    public void setEmail(String email) {
        this.email = email;
    }


    @Override
    /**
     * Gets the credit card
     */
    public CreditCard getCreditCard() {
        return creditCard;
    }


    @Override
    /**
     * Sets the credit card
     */
    public void setCreditCard(CreditCard card) {
        this.creditCard = card;
    }


    @Override
    /**
     * Sets the account manager responsible for persisting/managing this account.
     * This may be invoked exactly once on any given account, any subsequent
     * invocations should be ignored. The account manager member should not be
     * serialized with implementing class object.
     */
    public void registerAccountManager(AccountManager m) {
        if (accountManager == null) {
            accountManager = m;
        } else {
            LOGGER.severe("Should only set account manager one time");
        }
    }


    @Override
    /**
     * Incorporates the effect of an order in the balance.
     */
    public void reflectOrder(Order order, int executionPrice) {
        // valueOfOrder determines if the value of the order placed is positive or negative
        balance += order.valueOfOrder(executionPrice);
    }
}
