package com.ctrip.infosec.flowtable4j.jobws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by zhangsx on 2015/3/13.
 * 规则更新实现类
 */
@Component
public class StaticCacheJob {

    @Autowired
    @Qualifier("simpleProcessor4BW")
    private Processor processorBW;

    @Autowired
    @Qualifier("simpleProcessor4Flow")
    private Processor processorFlow;

    @Autowired
    @Qualifier("simpleProcessor4PayAdapt")
    private Processor simpleProcessor4PayAdapt;

    private static final Logger logger = LoggerFactory.getLogger(StaticCacheJob.class);

    @Scheduled(fixedDelay = 300000)
    public void executeBW() {
        logger.info("start execute update blackWhite rule...");
        try {
            processorBW.execute();
        } catch (Throwable ex) {
            logger.error("processorBW error.",ex);
        }
        logger.info("end execute update blackWhite rule...");
    }

    @Scheduled(fixedDelay = 300000)
    public void executeFlow() {
        logger.info("start execute update flow rule...");
        try {
            processorFlow.execute();
        } catch (Throwable ex) {
            logger.error("processorFlow error",ex);
        }
        logger.info("end execute update flow rule...");
    }

    @Scheduled(fixedDelay = 300000)
    public void executePayadapt(){
        logger.info("start execute update blackWhite rule...");

        try {
            simpleProcessor4PayAdapt.execute();
        }catch (Throwable ex){
            logger.error("processorPayadapt error",ex);
        }

        logger.info("end execute update blackWhite rule...");
    }
}
