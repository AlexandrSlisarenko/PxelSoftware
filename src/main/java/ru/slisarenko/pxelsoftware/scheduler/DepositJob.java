package ru.slisarenko.pxelsoftware.scheduler;

import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import ru.slisarenko.pxelsoftware.service.DepositService;

@Component
@RequiredArgsConstructor
public class DepositJob implements Job {

    private final DepositService depositService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

    }
}
