package ru.slisarenko.pxelsoftware.scheduler;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import ru.slisarenko.pxelsoftware.config.AppConfigProperties;
import ru.slisarenko.pxelsoftware.service.DepositService;


@Slf4j
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
@Configuration
public class UpdateDepositJob implements Job {

    @Autowired
    private final DepositService depositService;

    public UpdateDepositJob() {
        this.depositService = null;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("Update deposit job started");
        depositService.clearStateDeposits();
        depositService.updateStateDeposits();
        log.info("Deposit " + depositService.getAccountsDeposit().size() + " deposits");
        depositService.getAccountsDeposit().forEach(deposit -> log.info("Balance " + deposit.getBalance().toString()));
        depositService.addPercent();
        log.info("Deposit update");
        depositService.getAccountsDeposit().forEach(deposit -> log.info("Balance " + deposit.getBalance().toString()));
    }
}
