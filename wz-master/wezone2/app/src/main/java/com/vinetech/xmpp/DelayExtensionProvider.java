package com.vinetech.xmpp;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;


public class DelayExtensionProvider implements PacketExtensionProvider {

    @Override
    public PacketExtension parseExtension(XmlPullParser parser) throws Exception {
        boolean done = false;

        DelayExtension extension = new DelayExtension();
        int eventType = parser.getEventType();
        String tagName = null;

        while (!done) {

            if (eventType == XmlPullParser.START_TAG) {
                tagName = parser.getName();
            }else if (eventType == XmlPullParser.END_TAG) {
                if ("delay".equals(tagName)) {
                    done = true;
                }
            }else if(eventType == XmlPullParser.TEXT){
                if ("delay".equals(tagName)) {
                    extension.setDelay(parser.getText());
                    done = true;
                }
            }
            eventType = parser.next();

//            if (eventType == XmlPullParser.START_TAG) {
//                 if (tagName.equals("item_id")) {
//                    extension.setItemId(parser.nextText());
//                }
//            } else if (eventType == XmlPullParser.END_TAG) {
//                if (tagName.equals(extension.getElementName())) {
//                    done = true;
//                }
//            }
        }

        return extension;
    }
}
