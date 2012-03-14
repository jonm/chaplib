/* 
 * TestAllowOnlyIANACharacterSetsPolicy.java
 * 
 * Copyright (C) 2012 Jonathan Moore
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.chaplib;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class TestAllowOnlyIANACharacterSetsPolicy {

    private AllowOnlyIANACharacterSetsPolicy impl;
    private Random r;

    @Before
    public void setUp() {
        impl = new AllowOnlyIANACharacterSetsPolicy();
        r = new Random();
    }
    
    private String selectRandomIANACharSet() {
        int numSets = IANACharacterSets.CHARACTER_SETS.length;
        return IANACharacterSets.CHARACTER_SETS[r.nextInt(numSets)];
    }
    
    @Test
    public void permitsDefaultCharacterSet() {
        impl.validateCharacterSet(null);
    }
    
    @Test
    public void permitsIANACharacterSet() {
        String charSet = selectRandomIANACharSet();
        impl.validateCharacterSet(charSet);
    }
    
    private String generateUnregisteredCharacterSet() {
        StringBuilder buf = new StringBuilder();
        String alpha = "abcdefghijklmnopqrstuvwxyz";
        Set<String> charSets = new HashSet<String>();
        for(String s : IANACharacterSets.CHARACTER_SETS) {
            charSets.add(s.toLowerCase());
        }
        do {
            buf.append(alpha.charAt(r.nextInt(alpha.length())));
        } while (charSets.contains(buf.toString()));
        String charSet = buf.toString();
        return charSet;
    }
    
    @Test(expected=InvalidCharacterSetException.class)
    public void doesNotPermitUnregisteredCharacterSet() {
        String charSet = generateUnregisteredCharacterSet();
        impl.validateCharacterSet(charSet);
    }

}
