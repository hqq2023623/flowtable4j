package com.ctrip.flowtable4j.core.flowRule;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/flowtable4j-test.xml"})
public class RuleManagerTest {
	
    @Resource(name = "cardRiskDBTemplate")
    JdbcTemplate cardRiskDBTemplate;
    @Resource(name = "riskCtrlPreProcDBTemplate")
    JdbcTemplate riskCtrlPreProcDBTemplate;
    
    
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@Before
	public void setUp() throws Exception {
        System.out.println("CardRiskDB");
        List<Map<String,Object>> results = cardRiskDBTemplate.queryForList("select top 100* from dbo.InfoSecurity_FlowRule");
        System.out.println("results: " + results.size());
	}

	@Test
	public void testSetRuleEntities() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFlowRuleListByOrderTypeString() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFlowRuleListByOrderTypeStringString() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFlowRuleListByOrderTypeStringBoolean() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFlowRuleListByOrderTypeStringStringBoolean() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFlowRuleList() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCheckedFlowRuleInfo() {
		fail("Not yet implemented");
	}

}
