package com.vinetech.xmpp;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;


public class CustomExtensionProvider implements PacketExtensionProvider {

    @Override
    public PacketExtension parseExtension(XmlPullParser parser) throws Exception {
        boolean done = false;

        CustomExtension extension = new CustomExtension();
        while (!done) {
            int eventType = parser.next();

            String tagName = parser.getName();

            if (eventType == XmlPullParser.START_TAG) {
                if (tagName.equals("user_uuid")) {
                    extension.setUserUuid(parser.nextText());
                } else if (tagName.equals("user_name")) {
                    extension.setUserName(parser.nextText());
                }else if (tagName.equals("user_img_url")) {
                    extension.setUserImageUrl(parser.nextText());
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (tagName.equals(extension.getElementName())) {
                    done = true;
                }
            }
        }

        return extension;
    }
}
