package ru.slisarenko.pxelsoftware.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.slisarenko.pxelsoftware.service.DepositService;


@Slf4j
@Component
@RequiredArgsConstructor
public class DepositJob implements Job {

    @Value("${max-deposit-percent}")
    private  Integer maxDepositPercent;

    private final DepositService depositService;


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("Deposit job started");
        depositService.clearStateDeposits();
        depositService.updateStateDeposits(maxDepositPercent);
        log.info("Deposit " + depositService.getAccountsDeposit().size() + " deposits");
        depositService.getAccountsDeposit().forEach(deposit -> log.info("Balance " + deposit.getBalance().toString()));
        depositService.addPercent();
        log.info("Deposit update");
        depositService.getAccountsDeposit().forEach(deposit -> log.info("Balance " + deposit.getBalance().toString()));
    }
}
