package ATM;

import java.io.Serializable;

public class Account implements Serializable {

    private String cardNumber;
    private String password;
    private String name;
    private double balance;

    public Account(String cardNumber,String password,String name, double balance){
        this.cardNumber=cardNumber;
        this.password=password;
        this.name=name;
        this.balance=balance;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getPassword() { return password; }

    public String getName() { return name; }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount){
        this.balance+=amount;
    }

    public boolean withdraw(double amount){
        if (amount<=balance){
            balance-=amount;
            return true;
        }
        return false;
    }
    public void changePass(String pass){
        this.password=pass;
    }
}
