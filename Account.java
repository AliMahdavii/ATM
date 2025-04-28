public class Account {

    private String cardNumber;
    private String password;
    private double balance;

    public Account(String cardNumber,String password,double balance){
        this.cardNumber=cardNumber;
        this.password=password;
        this.balance=balance;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getPassword() {
        return password;
    }

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
}
