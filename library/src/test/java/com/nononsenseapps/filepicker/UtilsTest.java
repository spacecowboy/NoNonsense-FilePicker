package com.nononsenseapps.filepicker;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UtilsTest {

    @Test
    public void appendPathSimple() throws Exception {
        assertEquals("A/B", Utils.appendPath("A", "B"));
        assertEquals("A/B", Utils.appendPath("A", "B/"));
        assertEquals("/A/B", Utils.appendPath("/A", "B"));
        assertEquals("/A/B/C/D", Utils.appendPath("/A/B", "C/D"));
        assertEquals("A/B/C/D", Utils.appendPath("A/B", "C/D/"));
    }

    @Test
    public void appendPathDot() throws Exception {
        assertEquals("A/./B", Utils.appendPath("A", "./B"));
        assertEquals("A/./B", Utils.appendPath("A", "./B/"));
        assertEquals("/A/./B", Utils.appendPath("/A", "./B"));
        assertEquals("/A/B/./C/D", Utils.appendPath("/A/B", "./C/D"));
        assertEquals("A/B/./C/D", Utils.appendPath("A/B", "./C/D/"));
    }

    @Test
    public void appendPathDotDot() throws Exception {
        assertEquals("A/../B", Utils.appendPath("A", "../B"));
        assertEquals("A/../B", Utils.appendPath("A", "../B/"));
        assertEquals("/A/../B", Utils.appendPath("/A", "../B"));
        assertEquals("/A/B/../C/D", Utils.appendPath("/A/B", "../C/D"));
        assertEquals("A/B/C/../D", Utils.appendPath("A/B", "C/../D/"));
    }

    @Test
    public void appendPathRoot() throws Exception {
        assertEquals("A/B", Utils.appendPath("A", "/B"));
        assertEquals("/A/B", Utils.appendPath("/A", "/B"));
        assertEquals("/A/B/C/D", Utils.appendPath("/A/B", "/C/D"));
        assertEquals("/A/B/C/D", Utils.appendPath("/A/B", "/C/D/"));
    }

    @Test
    public void appendSlashesSlashesSlashes() throws Exception {
        assertEquals("A/B", Utils.appendPath("A//", "///B"));
        assertEquals("/", Utils.appendPath("////", "/////"));
    }
}
