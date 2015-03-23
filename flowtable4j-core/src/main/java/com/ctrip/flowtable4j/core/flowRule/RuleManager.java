package com.ctrip.flowtable4j.core.flowRule;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import com.ctrip.flowtable4j.core.dao.cardriskdb.entity.InfoSecurity_CheckResultLog;
import com.ctrip.flowtable4j.core.flowRule.entity.FlowRuleEntity;



public class RuleManager {
	static List<FlowRuleEntity> flowRuleList;
	
	public static void SetRuleEntities(List<FlowRuleEntity> ruleList){
		flowRuleList = ruleList;
	}
	
	


	
	
	
    public static List<FlowRuleEntity> GetFlowRuleListByOrderType(String strOrderType)
    {
    	return GetFlowRuleListByOrderType(strOrderType,"ALL");
    }
	
    public static List<FlowRuleEntity> GetFlowRuleListByOrderType(String strOrderType, String prepayType)
    {
    	int orderType;
    	orderType = org.apache.commons.lang.math.NumberUtils.toInt(strOrderType, 0);
        List<FlowRuleEntity> list = new ArrayList<FlowRuleEntity>();
        
        for(FlowRuleEntity tmp:GetFlowRuleList() ){
        	if( (tmp.getOrderType()==0 || tmp.getOrderType() == orderType)
        		&& prepayType.equalsIgnoreCase(tmp.getPrepayType())){
        		list.add(tmp);
        	}
        }
        return list;
    }

    /**
     * 根据OrderType获取所有 preType = All 的流量规则
     * @param strOrderType
     * @param isWhiteCheck
     * @return
     */
    public static List<FlowRuleEntity> GetFlowRuleListByOrderType(String strOrderType, boolean isWhiteCheck)
    {
    	return GetFlowRuleListByOrderType(strOrderType,"ALL",isWhiteCheck);
    }
    
    public static List<FlowRuleEntity> GetFlowRuleListByOrderType(String strOrderType, String prepayType, boolean isWhiteCheck)
    {
    	int orderType;
    	orderType = org.apache.commons.lang.math.NumberUtils.toInt(strOrderType, 0);
        List<FlowRuleEntity> list = new ArrayList<FlowRuleEntity>();
        
        for(FlowRuleEntity tmp:GetFlowRuleList() ){
        	if( (tmp.getOrderType()==0 || tmp.getOrderType() == orderType)
        		&& prepayType.equalsIgnoreCase(tmp.getPrepayType())){
        		if(isWhiteCheck){
        			if(tmp.getRiskLevel() == 0)
        				list.add(tmp);
        		}else{
        			if(tmp.getRiskLevel() > 0)
        				list.add(tmp);
        		}
        		
        	}
        }
        return list;
    }
    
    
    
    /**
     * 获取全部流量
     * @return
     */
    public static List<FlowRuleEntity> GetFlowRuleList(){
    	return flowRuleList;
    }
    
    
    public static InfoSecurity_CheckResultLog getCheckedFlowRuleInfo(FlowRuleEntity en)
    {
        InfoSecurity_CheckResultLog log = null;

        try
        {
            if (en != null)
            {
                log = new InfoSecurity_CheckResultLog();
                log.setRiskLevel(en.getRiskLevel());
                log.setRuleID(en.getFlowRuleID());
                log.setRuleName( en.getRuleName());
                log.setRuleRemark(en.getRuleDesc());
                log.setRuleType("D");
            }
        }
        catch(Exception e)
        {
            //nRef = (int)RetCodeDefinition.CheckRiskRetCode.SaveCheckedBlackRuleErr;
        }

        return log;
    }
    
    public enum FlowStatisticType{
    	COUNT,
    	SUM
    }
    
    
	public class TimerTaskFreshDefaultTagData extends java.util.TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			try {
				if(flowRuleList == null)
					flowRuleList = new ArrayList<FlowRuleEntity>();

				//放入最新


			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}

