package services;

import domain.AccountModel;
import domain.CurrencyType;
import domain.MoneyModel;
import domain.SavingsAccountModel;
import domain.TransactionModel;
import repository.AccountsRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionManagerService {
    private double conversionRateEURToRON = 5;
    private double conversionRateRONToEUR = 0.2;

    public TransactionModel transfer(String fromAccountId, String toAccountId, MoneyModel value) {
        AccountModel fromAccount = AccountsRepository.INSTANCE.get(fromAccountId);
        AccountModel toAccount = AccountsRepository.INSTANCE.get(toAccountId);

        if (fromAccount == null || toAccount == null) {
            throw new RuntimeException("Specified account does not exist");
        }

        if (fromAccount instanceof SavingsAccountModel) {
            throw new RuntimeException("You cannot transfer from a savings account");
        }

        if (!toAccount.getBalance().getCurrency().equals(value.getCurrency())) {
            this.convertCurrency(toAccount.getBalance().getCurrency(), value);
        }

        if (fromAccount.getBalance().compareTo(value) < 0) {
            throw new RuntimeException("The transaction would lead to a negative balance in account " + fromAccountId);
        }

        TransactionModel transaction = new TransactionModel(
                UUID.randomUUID(),
                fromAccountId,
                toAccountId,
                value,
                LocalDate.now()
        );

        fromAccount.getBalance().setAmount(fromAccount.getBalance().getAmount() - value.getAmount());
        fromAccount.getTransactions().add(transaction);

        toAccount.getBalance().setAmount(toAccount.getBalance().getAmount() + value.getAmount());
        toAccount.getTransactions().add(transaction);

        return transaction;
    }

    private void convertCurrency(CurrencyType toConvert, MoneyModel value) {
        value.setCurrency(toConvert);
        if (toConvert == CurrencyType.EUR) {
            value.setAmount(value.getAmount() * conversionRateRONToEUR);
        }
        else {
            value.setAmount(value.getAmount() * conversionRateEURToRON);
        }
    }

    public TransactionModel withdraw(String accountId, MoneyModel value) {
        AccountModel account = AccountsRepository.INSTANCE.get(accountId);

        if (!account.getBalance().getCurrency().equals(value.getCurrency())) {
            this.convertCurrency(account.getBalance().getCurrency(), value);
        }

        if (account.getBalance().compareTo(value) < 0) {
            throw new RuntimeException("The transaction would lead to a negative balance in account " + account);
        }

        TransactionModel transaction = new TransactionModel(UUID.randomUUID(), accountId, accountId, value, LocalDate.now());
        account.getBalance().setAmount(account.getBalance().getAmount() - value.getAmount());
        account.getTransactions().add(transaction);

        return transaction;
    }

    public MoneyModel checkFunds(String accountId) {
        if (!AccountsRepository.INSTANCE.exist(accountId)) {
            throw new RuntimeException("Specified account does not exist");
        }
        return AccountsRepository.INSTANCE.get(accountId).getBalance();
    }

    public List<TransactionModel> retrieveTransactions(String accountId) {
        if (!AccountsRepository.INSTANCE.exist(accountId)) {
            throw new RuntimeException("Specified account does not exist");
        }
        return new ArrayList<>(AccountsRepository.INSTANCE.get(accountId).getTransactions());
    }
}

