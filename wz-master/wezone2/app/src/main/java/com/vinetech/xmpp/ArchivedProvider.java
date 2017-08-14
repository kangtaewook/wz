package com.vinetech.xmpp;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;


public class ArchivedProvider implements PacketExtensionProvider {

    @Override
    public PacketExtension parseExtension(XmlPullParser parser) throws Exception {

        ArchivedExtension extension = new ArchivedExtension();
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
