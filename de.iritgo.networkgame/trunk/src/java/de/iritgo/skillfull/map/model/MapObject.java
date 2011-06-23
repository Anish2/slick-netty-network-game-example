/*
 * Copyright (c) 2008-2010, Matthias Mann
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Matthias Mann nor the names of its contributors may
 *       be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.iritgo.skillfull.map.model;

import de.matthiasmann.twl.utils.XMLParser;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 *
 * @author Matthias Mann
 */
public class MapObject {

    protected final ObjectGroup objectGroup;
    protected final String name;
    protected final String type;
    protected final int x;
    protected final int y;
    protected final int width;
    protected final int height;
    protected final Properties properties;

    public MapObject(XMLParser xmlp, ObjectGroup objectGroup) throws XmlPullParserException, IOException {
        this.objectGroup = objectGroup;
        this.properties = new Properties(objectGroup.getTiledMap().getPropertyRegistry());

        xmlp.require(XmlPullParser.START_TAG, null, "object");
        name = xmlp.getAttributeNotNull("name");
        type = xmlp.getAttributeNotNull("type");
        x = xmlp.parseIntFromAttribute("x");
        y = xmlp.parseIntFromAttribute("y");
        width = xmlp.parseIntFromAttribute("width");
        height = xmlp.parseIntFromAttribute("height");

        xmlp.nextTag();
        while(!xmlp.isEndTag()) {
            xmlp.require(XmlPullParser.START_TAG, null, null);
            String tag = xmlp.getName();
            if("properties".equals(tag)) {
                properties.parseFromXML(xmlp);
            } else {
                throw xmlp.unexpected();
            }
            xmlp.require(XmlPullParser.END_TAG, null, tag);
            xmlp.nextTag();
        }
    }

    public TiledMap getTiledMap() {
        return objectGroup.getTiledMap();
    }

    public ObjectGroup getObjectGroup() {
        return objectGroup;
    }

    public PropertyRegistry getPropertyRegistry() {
        return objectGroup.getPropertyRegistry();
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Properties getProperties() {
        return properties;
    }
    
}
