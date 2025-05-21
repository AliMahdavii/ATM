package ATM;

public class ExchangeRate {
    private String date;
    private double rate;

    public ExchangeRate(double rate, String date) {
        this.rate = rate;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public double getRate() {
        return rate;
    }
}
