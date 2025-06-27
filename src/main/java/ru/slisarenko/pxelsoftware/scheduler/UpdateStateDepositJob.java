package ru.slisarenko.pxelsoftware.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import ru.slisarenko.pxelsoftware.service.DepositService;

@Slf4j
@Configuration
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class UpdateStateDepositJob implements Job {

    @Autowired
    private DepositService depositService;

    public UpdateStateDepositJob() {
        this.depositService = null;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Update deposit job started");
        depositService.clearStateDeposits();
        depositService.updateStateDeposits();
    }
}
