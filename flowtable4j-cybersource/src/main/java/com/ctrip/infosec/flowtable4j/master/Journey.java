package com.ctrip.infosec.flowtable4j.master;

import com.ctrip.infosec.flowtable4j.Common.BaseNode;

import java.util.List;

/**
 * Created by thyang on 2015-08-19.
 */
public class Journey extends BaseNode {
    private String ticket_number;
    private String pnr;
    private List<Leg> legs;
    private List<Passenger> passengers;

    @Override
    public String toXML()
    {
        StringBuilder sb= new StringBuilder();
        sb.append("<Journey>\n");
        createNode(sb,"pnr",pnr);
        createNode(sb,"ticket_number",ticket_number);
        sb.append("<Legs>\n");
        if(legs!=null){
            for(Leg leg:legs) {
                sb.append(leg.toXML());
            }
        }
        sb.append("</Legs>\n");
        sb.append("<Passengers>\n");
        if(passengers!=null){
            for(Passenger passenger:passengers) {
                sb.append(passenger.toXML());
            }
        }
        sb.append("</Passengers>\n");
        sb.append("</Journey>\n");
        return sb.toString();
    }

    public void setTicket_number(String ticket_number) {
        this.ticket_number = ticket_number;
    }

    public void setPnr(String pnr) {
        this.pnr = pnr;
    }

    public void setLegs(List<Leg> legs) {
        this.legs = legs;
    }

    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
    }
}
