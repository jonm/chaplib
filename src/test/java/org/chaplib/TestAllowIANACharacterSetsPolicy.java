package org.chaplib;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class TestAllowIANACharacterSetsPolicy {

    private AllowIANACharacterSetsPolicy impl;
    private Random r;

    @Before
    public void setUp() {
        impl = new AllowIANACharacterSetsPolicy();
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
