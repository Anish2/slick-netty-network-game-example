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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A property name registry to speedup property access. This class is thread safe.
 * 
 * @author Matthias Mann
 */
public class PropertyRegistry {

    private final HashMap<String, Integer> map;
    private final ArrayList<String> names;

    public PropertyRegistry() {
        this.map = new HashMap<String, Integer>();
        this.names = new ArrayList<String>();
    }

    public synchronized String getName(int id) {
        return names.get(id);
    }
    
    /**
     * Looks up a property name.
     * @param name the property name
     * @return the id of that property or -1 if it was not found.
     */
    public synchronized int lookup(String name) {
        Integer id = map.get(name);
        return (id != null) ? id.intValue() : -1;
    }

    /**
     * Looks up or registers a property name
     * 
     * @param name the property name
     * @return the id of that property. Will never be -1
     */
    public synchronized int lookupOrRegister(String name) {
        Integer id = map.get(name);
        if(id == null) {
            id = names.size();
            names.add(name);
            map.put(name, id);
        }
        return id.intValue();
    }
}
