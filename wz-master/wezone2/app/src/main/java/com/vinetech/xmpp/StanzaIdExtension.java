package com.vinetech.xmpp;

import org.jivesoftware.smack.packet.PacketExtension;

public class StanzaIdExtension implements PacketExtension {

    private String by;
    private String id;

    public void setBy(String by){
        this.by = by;
    }
    public void setId(String id){
        this.id=id;
    }

    @Override
    public String getElementName() {
        // TODO Auto-generated method stub
        return "stanza-id";
    }

    @Override
    public String getNamespace() {
        // TODO Auto-generated method stub
        return "urn:xmpp:sid:0";
    }

    @Override
    public String toXML() {
        StringBuilder buf = new StringBuilder();

        buf.append("<stanza-id");
        buf.append(" xmlns=\"").append(getNamespace()).append("\"");

        if(by != null) {
            buf.append(" by=\"").append(by).append("\"");
        }
        if(id != null){
            buf.append(" id=\"").append(id).append("\"");
        }
//        buf.append("/>");
        buf.append("></stanza-id>");
        return buf.toString();
    }


}
