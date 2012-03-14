/* 
 * AllowOnlyIANACharacterSetsPolicy.java
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
import java.util.Set;

public class AllowOnlyIANACharacterSetsPolicy {

    private final static Set<String> IANA_CHAR_SETS;
    static {
        IANA_CHAR_SETS = new HashSet<String>();
        for(String s : IANACharacterSets.CHARACTER_SETS)
            IANA_CHAR_SETS.add(s.toLowerCase());
    }
    
    public void validateCharacterSet(String charSet) {
        if (charSet == null || "".equals(charSet)) return;
        if (!IANA_CHAR_SETS.contains(charSet.toLowerCase()))
            throw new InvalidCharacterSetException("not an IANA character set: " + charSet);
    }

}
