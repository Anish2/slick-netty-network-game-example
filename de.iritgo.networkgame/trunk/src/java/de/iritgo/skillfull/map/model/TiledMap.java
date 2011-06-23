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
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * A tiled map parser / data model.
 * This model and it's parts are thread safe.
 *
 * @author Matthias Mann
 */
public class TiledMap {

    protected final PropertyRegistry propertyRegistry;
    protected final URL tiledMapURL;
    protected final String version;
    protected final int width;
    protected final int height;
    protected final int tileWidth;
    protected final int tileHeight;
    protected final Properties properties;
    protected final IntMap<Properties> tilesProperties;
    protected final ArrayList<TileSet> tileSets;
    protected final ArrayList<Layer> layers;
    protected final ArrayList<ObjectGroup> objectGroups;

    public TiledMap(PropertyRegistry propertyRegistry, URL url) throws IOException {
        this.propertyRegistry = propertyRegistry;
        this.tiledMapURL = url;
        this.properties = new Properties(propertyRegistry);
        this.tilesProperties = new IntMap<Properties>();
        this.tileSets = new ArrayList<TileSet>();
        this.layers = new ArrayList<Layer>();
        this.objectGroups = new ArrayList<ObjectGroup>();

        try {
            XMLParser xmlp = new XMLParser(url);
            xmlp.nextTag();
            xmlp.require(XmlPullParser.START_TAG, null, "map");
            version = xmlp.getAttributeValue(null, "version");
            String orientation = xmlp.getAttributeNotNull("orientation");
            if(!"orthogonal".equals(orientation)) {
                throw new IOException("Unsupported orientation");
            }
            width = xmlp.parseIntFromAttribute("width");
            height = xmlp.parseIntFromAttribute("height");
            tileWidth = xmlp.parseIntFromAttribute("tilewidth");
            tileHeight = xmlp.parseIntFromAttribute("tileheight");
            xmlp.nextTag();
            while(!xmlp.isEndTag()) {
                xmlp.require(XmlPullParser.START_TAG, null, null);
                String tag = xmlp.getName();
                if("properties".equals(tag)) {
                    properties.parseFromXML(xmlp);
                } else if("tileset".equals(tag)) {
                    parseTileSet(xmlp);
                } else if("layer".equals(tag)) {
                    layers.add(new Layer(xmlp, this));
                } else if("objectgroup".equals(tag)) {
                    objectGroups.add(new ObjectGroup(xmlp, this));
                } else {
                    throw xmlp.unexpected();
                }
                xmlp.require(XmlPullParser.END_TAG, null, tag);
                xmlp.nextTag();
            }
            xmlp.require(XmlPullParser.END_TAG, null, "map");
        } catch (XmlPullParserException ex) {
            throw (IOException)(new IOException("can't parse tiled map").initCause(ex));
        }
    }

    public TiledMap(URL url) throws IOException {
        this(new PropertyRegistry(), url);
    }

    public PropertyRegistry getPropertyRegistry() {
        return propertyRegistry;
    }

    public URL getTiledMapURL() {
        return tiledMapURL;
    }

    public String getVersion() {
        return version;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public Properties getProperties() {
        return properties;
    }

    public int getNumTileSets() {
        return tileSets.size();
    }

    public TileSet getTileSet(int idx) {
        return tileSets.get(idx);
    }

    public int getNumLayers() {
        return layers.size();
    }

    public Layer getLayer(int idx) {
        return layers.get(idx);
    }

    public Layer getLayerByName(String name) {
        for(int i=0 ; i<layers.size() ; i++) {
            Layer layer = layers.get(i);
            if(layer.getName().equals(name)) {
                return layer;
            }
        }
        return null;
    }

    public String getTileProperty(int tileID, int propertyID) {
        Properties tileProperties = tilesProperties.get(tileID);
        if(tileProperties != null) {
            String property = tileProperties.get(propertyID);
            if(property != null) {
                return property;
            }
        }
        return null;
    }

    public Iterable<TilePropertyEntry> getTilesWithProperty(final int propertyID) {
        if(propertyID < 0) {
            throw new IllegalArgumentException("propertyID");
        }
        return new Iterable<TilePropertyEntry>() {
            public Iterator<TilePropertyEntry> iterator() {
                return new Iterator<TilePropertyEntry>() {
                    final Iterator<IntMap.Entry<Properties>> propertyIterator = tilesProperties.iterator();
                    TilePropertyEntry entry;

                    public boolean hasNext() {
                        if(entry != null) {
                            return true;
                        }
                        while(propertyIterator.hasNext()) {
                            IntMap.Entry<Properties> e = propertyIterator.next();
                            String value = e.value.get(propertyID);
                            if(value != null) {
                                entry = new TilePropertyEntry(e.key, value);
                                return true;
                            }
                        }
                        return false;
                    }

                    public TilePropertyEntry next() {
                        if(!hasNext()) {
                            throw new NoSuchElementException();
                        }
                        TilePropertyEntry e = entry;
                        entry = null;
                        return e;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException("Not supported");
                    }
                };
            }
        };
    }

    public int getNumObjectGroups() {
        return objectGroups.size();
    }

    public ObjectGroup getObjectGroup(int idx) {
        return objectGroups.get(idx);
    }

    public ObjectGroup getObjectGroup(String name) {
        for(int i=0,n=objectGroups.size() ; i<n ; i++) {
            ObjectGroup og = objectGroups.get(i);
            if(og.getName().equals(name)) {
                return og;
            }
        }
        return null;
    }

    private void parseTileSet(XMLParser xmlp) throws XmlPullParserException, IOException {
        TileSet tileSet;
        URL tileSetURL;
        String source = xmlp.getAttributeValue(null, "source");
        int firstGid = xmlp.parseIntFromAttribute("firstgid");
        if(source != null) {
            tileSetURL = new URL(tiledMapURL, source);
            try {
                XMLParser xmlpTS = new XMLParser(tileSetURL);
                try {
                    xmlpTS.nextTag();
                    tileSet = new TileSet(xmlpTS, tileSetURL, this, firstGid);
                } finally {
                    xmlpTS.close();
                }
            } catch (Exception ex) {
                throw xmlp.error("Can't parse tile set '"+source+"'", ex);
            }
            xmlp.nextTag();
        } else {
            tileSetURL = tiledMapURL;
            tileSet = new TileSet(xmlp, tileSetURL, this, firstGid);
        }
        tileSets.add(tileSet);
    }

    void setTileProperty(int tileID, Properties properties) {
        tilesProperties.put(tileID, properties);
    }

    public static class TilePropertyEntry {
        final int tileID;
        final String propertyValue;

        public TilePropertyEntry(int tileID, String propertyValue) {
            this.tileID = tileID;
            this.propertyValue = propertyValue;
        }

        public String getPropertyValue() {
            return propertyValue;
        }

        public int getTileID() {
            return tileID;
        }
    }
}
