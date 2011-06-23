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
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 *
 * @author Matthias Mann
 */
public class Layer {

    protected final TiledMap tiledMap;
    protected final String name;
    protected final int width;
    protected final int height;
    protected final Properties properties;
    protected final int[][] data;

    public Layer(XMLParser xmlp, TiledMap tiledMap) throws XmlPullParserException, IOException {
        this.tiledMap = tiledMap;
        this.properties = new Properties(tiledMap.getPropertyRegistry());
        
        xmlp.require(XmlPullParser.START_TAG, null, "layer");
        name = xmlp.getAttributeNotNull("name");
        width = xmlp.parseIntFromAttribute("width");
        height = xmlp.parseIntFromAttribute("height");
        data = new int[height][width];

        xmlp.nextTag();
        while(!xmlp.isEndTag()) {
            xmlp.require(XmlPullParser.START_TAG, null, null);
            String tag = xmlp.getName();
            if("properties".equals(tag)) {
                properties.parseFromXML(xmlp);
            } else if("data".equals(tag)) {
                parseData(xmlp);
            } else {
                throw xmlp.unexpected();
            }
            xmlp.require(XmlPullParser.END_TAG, null, tag);
            xmlp.nextTag();
        }
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public String getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTileAt(int x, int y) {
        return data[y][x];
    }

    public String getTileProperty(int x, int y, int propertyID) {
        int tileID = getTileAt(x, y);
        return tiledMap.getTileProperty(tileID, propertyID);
    }

    private void parseData(XMLParser xmlp) throws XmlPullParserException, IOException {
        String encoding = xmlp.getAttributeNotNull("encoding");
        String compression = xmlp.getAttributeNotNull("compression");

        if(!"base64".equals(encoding)) {
            throw xmlp.error("Unsupported encoding: " + encoding);
        }
        if(!"gzip".equals(compression)) {
            throw xmlp.error("Unsupported compression: " + compression);
        }

        GZIPInputStream gzipis = new GZIPInputStream(new Base64InputStream(xmlp));
        try {
            BufferedInputStream bis = new BufferedInputStream(gzipis);

            for(int y=0 ; y<height ; y++) {
                int[] row = data[y];
                for(int x=0 ; x<width ; x++) {
                    int tileID = bis.read() |
                            (bis.read() << 8) |
                            (bis.read() << 16) |
                            (bis.read() << 24);
                    row[x] = tileID;
                }
            }
        } finally {
            gzipis.close();
        }

        xmlp.skipText();
    }

    private static class Base64InputStream extends InputStream {
        private final static byte[] decodeTable;
        static {
            decodeTable = new byte[128];
            Arrays.fill(decodeTable, (byte)-1);
            for (int i='A' ; i<='Z' ; i++) {
                decodeTable[i] = (byte)(i - 'A');
            }
            for (int i='a' ; i<='z' ; i++) {
                decodeTable[i] = (byte)(i - 'a' + 26);
            }
            for (int i='0' ; i<='9' ; i++) {
                decodeTable[i] = (byte)(i - '0' + 52);
            }
            decodeTable['+'] = 62;
            decodeTable['/'] = 63;
            decodeTable['='] = 0;
        }

        private final XMLParser xmlp;
        private final int[] startAndEnd;
        private char[] chars;
        private int savedData;
        private int savedBits;
        private boolean eof;

        public Base64InputStream(XMLParser xmlp) {
            this.xmlp = xmlp;
            this.startAndEnd = new int[2];
        }

        @Override
        public int read() throws IOException {
            byte[] tmp = new byte[1];
            if(read(tmp) != 1) {
                return -1;
            }
            return (int)tmp[0] & 0xFF;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if(len <= 0) {
                return 0;
            }
            if(eof) {
                return -1;
            }

            int bits = savedBits;
            int data = savedData;
            int startOff = off;
            int endOff = off + len;

            while(!eof && off < endOff) {
                int strIdx = startAndEnd[0];
                int strEnd = startAndEnd[1];

                if(strIdx == strEnd) {
                    try {
                        chars = xmlp.nextText(startAndEnd);
                        startAndEnd[1] += startAndEnd[0];
                    } catch (XmlPullParserException ex) {
                        eof = true;
                        throw (IOException)(new IOException("Can't read base64 data").initCause(ex));
                    }
                    if(chars == null) {
                        eof = true;
                    }
                    continue;
                }

                while(off < endOff && strIdx < strEnd) {
                    char ch = chars[strIdx++];
                    if(ch > ' ') {
                        int code = -1;
                        if(ch < 128) {
                            code = decodeTable[ch];
                        }
                        if(code < 0) {
                            eof = true;
                            throw new IOException("Unexpected character in base64 data: " + (int)ch);
                        }
                        data = (data << 6) | code;
                        if(bits >= 2) {
                            bits -= 2;
                            b[off++] = (byte)(data >> bits);
                        } else {
                            bits += 6;
                        }
                        if(ch == '=') {
                            eof = true;
                            break;
                        }
                    }
                }

                startAndEnd[0] = strIdx;
            }
            
            savedBits = bits;
            savedData = data;
            
            return off - startOff;
        }
    }
}
