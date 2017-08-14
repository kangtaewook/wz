package com.vinetech.xmpp;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by galuster3 on 2016-08-05.
 */

public class StreamHandlingPacket implements PacketExtension {
    private String name;
    private String namespace;
    Map<String, String> attributes;

    StreamHandlingPacket(String name, String namespace) {
        this.name = name;
        this.namespace = namespace;
        attributes = Collections.emptyMap();
    }

    public void addAttribute(String name, String value) {
        if (attributes == Collections.EMPTY_MAP)
            attributes = new HashMap<String, String>();
        attributes.put(name, value);
    }

    public String getAttribute(String name) {
        return attributes.get(name);
    }

    public String getNamespace() {
        return namespace;
    }

    public String getElementName() {
        return name;
    }

    public String toXML() {
        StringBuilder buf = new StringBuilder();
        buf.append("<").append(getElementName());

        // TODO Xmlns??
        if (getNamespace() != null) {
            buf.append(" xmlns=\"").append(getNamespace()).append("\"");
        }
        for (String key : attributes.keySet()) {
            buf.append(" ").append(key).append("=\"").append(StringUtils.escapeForXML(attributes.get(key))).append("\"");
        }
        buf.append("/>");
//        buf.append("></").append(getElementName()).append(">");

        return buf.toString();
    }

}
