package com.vinetech.xmpp;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;


public class FromExtensionProvider implements PacketExtensionProvider {

    @Override
    public PacketExtension parseExtension(XmlPullParser parser) throws Exception {
        boolean done = false;

        FromExtension extension = new FromExtension();
        int eventType = parser.getEventType();
        String tagName = null;

        while (!done) {

            if (eventType == XmlPullParser.START_TAG) {
                tagName = parser.getName();
            }else if (eventType == XmlPullParser.END_TAG) {
                if ("from".equals(tagName)) {
                    done = true;
                }
            }else if(eventType == XmlPullParser.TEXT){
                if ("from".equals(tagName)) {
                    extension.setFrom(parser.getText());
                    done = true;
                }
            }
            eventType = parser.next();
        }

        return extension;
    }
}
