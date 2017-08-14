package com.vinetech.xmpp;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;


public class StanzaIdProvider implements PacketExtensionProvider {

    @Override
    public PacketExtension parseExtension(XmlPullParser parser) throws Exception {

        StanzaIdExtension extension = new StanzaIdExtension();
        int attributeCount = parser.getAttributeCount();
        for (int i = 0 ; i < attributeCount ; i++) {

            String name = parser.getAttributeName(i);
            String value = parser.getAttributeValue(i);
            if("id".equals(name)){
                extension.setId(value);
            }else if("by".equals(name)){
                extension.setBy(value);
            }
        }

        return extension;
    }
}
