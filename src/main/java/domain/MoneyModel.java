package domain;

public class MoneyModel implements Comparable<MoneyModel> {
    private double amount;
    private CurrencyType currency;
    private double conversionRateEURToRON = 5;

    public MoneyModel(double amount, CurrencyType currency) {
        this.amount = amount;
        this.currency = currency;

    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public CurrencyType getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyType currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "MoneyModel{" +
                "amount=" + amount +
                ", currency=" + currency +
                '}';
    }

    public double amountInRON() {
        if (this.currency == CurrencyType.EUR) {
            return this.amount * conversionRateEURToRON;
        }

        return this.amount;
    }

    @Override
    public int compareTo(MoneyModel o) {
        return Double.compare(this.amountInRON(), o.amountInRON());
    }
}