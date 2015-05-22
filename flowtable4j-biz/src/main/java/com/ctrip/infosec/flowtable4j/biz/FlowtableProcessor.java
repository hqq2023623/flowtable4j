package com.ctrip.infosec.flowtable4j.biz;

import com.ctrip.infosec.flowtable4j.accountsecurity.PaymentViaAccount;
import com.ctrip.infosec.flowtable4j.bwlist.BWManager;
import com.ctrip.infosec.flowtable4j.core.utils.SimpleStaticThreadPool;
import com.ctrip.infosec.flowtable4j.flowlist.FlowRuleManager;
import com.ctrip.infosec.flowtable4j.model.*;
import com.ctrip.infosec.sars.monitor.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangsx on 2015/5/19.
 */
@Component
public class FlowtableProcessor {
    private static Logger logger = LoggerFactory.getLogger(FlowtableProcessor.class);

    @Autowired
    private PaymentViaAccount paymentViaAccount;
    @Autowired
    @Qualifier("cardRiskDBInsertTemplate")
    private JdbcTemplate cardRiskDBTemplate;

    private static final long FLOWTIMEOUT = 10000;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * 支付校验
     *
     * @param checkEntity
     * @return
     */
    public RiskResult handle(final CheckFact checkEntity) {

        long s = System.currentTimeMillis();
        logger.debug(Utils.JSON.toJSONString(checkEntity));
        logger.debug(String.format("ReqId %d to Json elapsed %d", checkEntity.getReqId(), System.currentTimeMillis() - s));

        final RiskResult listResult_w = new RiskResult();
        final RiskResult listResult = new RiskResult();

        boolean isWhite = false;
        /**
         * 1. 检测是否是白名单，是就直接返回，否则继续check黑名单，账户和flowrule
         */
        for (CheckType type : checkEntity.getCheckTypes()) {
            if (type == CheckType.BW) {
                if (BWManager.checkWhite(checkEntity.getBwFact(), listResult_w)) {
                    listResult.merge(listResult_w);
                    isWhite = true;
                }
                //log req
                long eps = System.currentTimeMillis() - s;
                String info = String.format("ReqId:%d, CheckWhite elapse %d ms", checkEntity.getReqId(), eps);
                CheckResultLog result = new CheckResultLog();
                result.setRuleRemark(info);
                result.setRuleName(String.valueOf(eps));
                result.setRuleType(CheckType.BW.toString());
                listResult.add(result);
                logger.debug(info);
            }
        }
        if (!isWhite) {
            parallelCheck(checkEntity, listResult);
        }
        listResult.setReqId(checkEntity.getReqId());
        //保存结果
        saveResult(listResult);
        return listResult;
    }

    private void parallelCheck(final CheckFact checkEntity, RiskResult listResult) {
        final RiskResult listResult_b = new RiskResult();
        final RiskResult listFlow = new RiskResult();
        final Map<String, Integer> mapAccount = new HashMap<String, Integer>();
        List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
        CheckType[] checkTypes = checkEntity.getCheckTypes();
        for (CheckType type : checkTypes) {
            if (CheckType.BW == type) {
                tasks.add(new Callable() {
                    @Override
                    public Object call() throws Exception {
                        long now = System.currentTimeMillis();
                        BWManager.checkBlack(checkEntity.getBwFact(), listResult_b);
                        long eps = System.currentTimeMillis() - now;
                        String info = String.format("ReqId:%d,CheckBlack elapse %d ms", checkEntity.getReqId(), eps);
                        CheckResultLog result = new CheckResultLog();
                        result.setRuleRemark(info);
                        result.setRuleName(String.valueOf(eps));
                        result.setRuleType(CheckType.BW.toString());
                        listResult_b.add(result);
                        logger.debug(info);
                        return null;
                    }
                });
            } else if (CheckType.ACCOUNT == type) {
                tasks.add(new Callable() {
                    @Override
                    public Object call() throws Exception {
                        long now = System.currentTimeMillis();
                        AccountFact item = checkEntity.getAccountFact();
                        if (item != null && item.getCheckItems() != null && item.getCheckItems().size() > 0) {
                            paymentViaAccount.checkBWGRule(item, mapAccount);
                        }
                        long eps = System.currentTimeMillis() - now;
                        String info = String.format("ReqId:%d,CheckBWGRule elapse %d ms", checkEntity.getReqId(), eps);
                        CheckResultLog result = new CheckResultLog();
                        result.setRuleRemark(info);
                        result.setRuleName(String.valueOf(eps));
                        result.setRuleType(CheckType.ACCOUNT.toString());
                        listResult_b.add(result);
                        logger.debug(info);
                        return null;
                    }
                });
            } else if (CheckType.FLOWRULE == type) {
                tasks.add(new Callable() {
                    @Override
                    public Object call() {
                        long now = System.currentTimeMillis();
                        FlowFact flowFact = checkEntity.getFlowFact();
                        FlowRuleManager.check(flowFact, listFlow);
                        long eps = System.currentTimeMillis() - now;
                        String info = String.format("ReqId:%d,CheckFlowRule elapse %d ms", checkEntity.getReqId(), eps);
                        CheckResultLog result = new CheckResultLog();
                        result.setRuleRemark(info);
                        result.setRuleName(String.valueOf(eps));
                        result.setRuleType(CheckType.FLOWRULE.toString());
                        listResult_b.add(result);
                        logger.debug(info);
                        return null;
                    }
                });
            }
        }
        try {
            for (Future future : SimpleStaticThreadPool.getInstance().invokeAll(tasks, FLOWTIMEOUT, TimeUnit.MILLISECONDS)) {
                if (future.isCancelled()) {
                    listResult.setStatus("TIMEOUT");
                    logger.warn("rule execute timeout [" + FLOWTIMEOUT + "ms]");
                }
            }
        } catch (InterruptedException e) {
            logger.error("InterruptedException", e);
        }
        for (Iterator<String> it = mapAccount.keySet().iterator(); it.hasNext(); ) {
            String sceneType = it.next();
            if (mapAccount.get(sceneType) > 0) {
                CheckResultLog riskResult = new CheckResultLog();
                riskResult.setRuleType(CheckType.ACCOUNT.toString());
                riskResult.setRuleName(sceneType);
                riskResult.setRiskLevel(mapAccount.get(sceneType));
                listResult.add(riskResult);
            }
        }
        listResult.merge(listResult_b);
        listResult.merge(listFlow);
    }

    private void saveResult(RiskResult result) {
        final long reqId = result.getReqId();
        List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
        for (final CheckResultLog item : result.getResults()) {
            SimpleStaticThreadPool.getInstance().submit(new Runnable() {
                @Override
                public void run() {
                    cardRiskDBTemplate.execute(
                            new CallableStatementCreator() {
                                public CallableStatement createCallableStatement(Connection con) throws SQLException {
                                    String storedProc = "{call spA_InfoSecurity_CheckResult4j_i ( ?,?,?,?,?,?,?,?)}";// 调用的sql
                                    CallableStatement cs = con.prepareCall(storedProc);
                                    cs.setLong(2, reqId);
                                    cs.setString(3, item.getRuleType());
                                    cs.setInt(4, item.getRuleID());
                                    cs.setString(5, Objects.toString(item.getRuleName(), ""));
                                    cs.setInt(6, item.getRiskLevel());
                                    cs.setString(7, Objects.toString(item.getRuleRemark(), ""));
                                    cs.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
                                    cs.registerOutParameter(1, Types.BIGINT);// 注册输出参数的类型
                                    return cs;
                                }
                            }, new CallableStatementCallback() {
                                public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                                    cs.execute();
                                    return null;
                                }
                            });
                }
            });
        }
    }
}