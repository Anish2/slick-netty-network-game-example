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
import java.util.HashMap;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 *
 * @author Matthias Mann
 */
public class Properties {

    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    
    private final PropertyRegistry registry;

    private String[] values;

    public Properties(PropertyRegistry registry) {
        this.registry = registry;
        this.values = EMPTY_STRING_ARRAY;
    }

    public String get(int id) {
        if(id < 0) {
            throw new IllegalArgumentException("id");
        }
        if(id >= values.length) {
            return null;
        } else {
            return values[id];
        }
    }

    public String get(String name) {
        int id = registry.lookup(name);
        if(id >= 0) {
            return get(id);
        } else {
            return null;
        }
    }

    public String get(int id, String defaultValue) {
        String value = get(id);
        return (value != null) ? value : defaultValue;
    }

    public String get(String name, String defaultValue) {
        String value = get(name);
        return (value != null) ? value : defaultValue;
    }

    public Map<String, String> toMap() {
        HashMap<String, String> result = new HashMap<String, String>();
        for(int i=0 ; i<values.length ; i++) {
            String value = values[i];
            if(value != null) {
                result.put(registry.getName(i), value);
            }
        }
        return result;
    }

    void parseFromXML(XMLParser xmlp) throws XmlPullParserException, IOException {
        xmlp.require(XmlPullParser.START_TAG, null, "properties");
        xmlp.nextTag();
        while(!xmlp.isEndTag()) {
            xmlp.require(XmlPullParser.START_TAG, null, "property");
            String name = xmlp.getAttributeNotNull("name");
            String value = xmlp.getAttributeNotNull("value");
            xmlp.nextTag();
            xmlp.require(XmlPullParser.END_TAG, null, "property");
            xmlp.nextTag();
            put(name, value);
        }
        xmlp.require(XmlPullParser.END_TAG, null, "properties");
    }

    private void put(String name, String value) {
        int id = registry.lookupOrRegister(name);
        if(id >= values.length) {
            grow(id);
        }
        values[id] = value;
    }

    private void grow(int id) {
        int size = Math.max(8, values.length);
        while(size <= id) {
            size <<= 1;
        }
        String[] tmp = new String[size];
        System.arraycopy(values, 0, tmp, 0, values.length);
        values = tmp;
    }
}
