package org.chaplib;

import java.util.HashSet;
import java.util.Set;

public class AllowIANACharacterSetsPolicy {

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
