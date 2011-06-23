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
import java.net.URL;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 *
 * @author Matthias Mann
 */
public class TileSet {

    protected final TiledMap tiledMap;
    protected final URL baseURL;
    protected final String name;
    protected final int firstGid;
    protected final int tileWidth;
    protected final int tileHeight;
    protected final int margin;
    protected final int spacing;
    protected final ArrayList<Image> images;

    public TileSet(XMLParser xmlp, URL baseURL, TiledMap tiledMap, int firstGid) throws XmlPullParserException, IOException {
        this.tiledMap = tiledMap;
        this.baseURL = baseURL;
        this.images = new ArrayList<Image>();
        
        xmlp.require(XmlPullParser.START_TAG, null, "tileset");
        name = xmlp.getAttributeNotNull("name");
        this.firstGid = firstGid;
        tileWidth = xmlp.parseIntFromAttribute("tilewidth");
        tileHeight = xmlp.parseIntFromAttribute("tileheight");
        margin = xmlp.parseIntFromAttribute("margin", 0);
        spacing = xmlp.parseIntFromAttribute("spacing", 0);
        xmlp.nextTag();
        while(!xmlp.isEndTag()) {
            xmlp.require(XmlPullParser.START_TAG, null, null);
            String tag = xmlp.getName();
            if("image".equals(tag)) {
                parseImage(xmlp);
            } else if("tile".equals(tag)) {
                int id = xmlp.parseIntFromAttribute("id");
                if(id < 0) {
                    throw xmlp.error("Invalid tile ID: " + id);
                }
                xmlp.nextTag();
                Properties properties = new Properties(tiledMap.getPropertyRegistry());
                properties.parseFromXML(xmlp);
                xmlp.nextTag();
                tiledMap.setTileProperty(firstGid + id, properties);
            } else {
                throw xmlp.unexpected();
            }
            xmlp.require(XmlPullParser.END_TAG, null, tag);
            xmlp.nextTag();
        }
        xmlp.require(XmlPullParser.END_TAG, null, "tileset");
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public URL getBaseURL() {
        return baseURL;
    }
    
    public String getName() {
        return name;
    }

    public int getFirstGid() {
        return firstGid;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public int getMargin() {
        return margin;
    }

    public int getSpacing() {
        return spacing;
    }

    public int getNumImages() {
        return images.size();
    }

    public Image getImage(int idx) {
        return images.get(idx);
    }

    private void parseImage(XMLParser xmlp) throws IOException, XmlPullParserException {
        String source = xmlp.getAttributeNotNull("source");
        String trans = xmlp.getAttributeValue(null, "trans");
        xmlp.nextTag();
        images.add(new Image(source, trans));
    }

    public static class Image {
        private final String source;
        private final String trans;

        public Image(String source, String trans) {
            this.source = source;
            this.trans = trans;
        }

        public String getSource() {
            return source;
        }

        public String getTrans() {
            return trans;
        }
    }
}
