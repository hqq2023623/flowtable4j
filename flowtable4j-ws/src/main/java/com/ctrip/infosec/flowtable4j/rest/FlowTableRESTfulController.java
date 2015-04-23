/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ctrip.infosec.flowtable4j.rest;

import com.ctrip.infosec.flowtable4j.model.CheckFact;
import com.ctrip.infosec.flowtable4j.model.CheckResultLog;
import com.ctrip.infosec.flowtable4j.model.RiskResult;
import com.ctrip.infosec.sars.monitor.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 配置读取
 *
 * @author zhengby
 */
@Controller
public class FlowTableRESTfulController {

    @Autowired
    Processor processor;
    private static Logger logger = LoggerFactory.getLogger(FlowTableRESTfulController.class);
    @RequestMapping(value = "/checkRisk")
    public @ResponseBody
    RiskResult checkRisk(@RequestBody String requestInfo) {
        CheckFact checkEntity = Utils.JSON.parseObject(requestInfo,CheckFact.class);
        checkEntity.processOrderTypes();
        return processor.handle(checkEntity);
    }
}
