package io.jenkins.plugins.propelo.commons.utils;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateUtilsTest {

    @Test
    void testGetDateFormattedDirName() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Date d = sdf.parse("01/01/2020");
        assertEquals("data-2020-01-01", DateUtils.getDateFormattedDirName(d));

        d = sdf.parse("31/01/2020");
        assertEquals("data-2020-01-31", DateUtils.getDateFormattedDirName(d));

        d = sdf.parse("01/10/2020");
        assertEquals("data-2020-10-01", DateUtils.getDateFormattedDirName(d));

        d = sdf.parse("31/10/2020");
        assertEquals("data-2020-10-31", DateUtils.getDateFormattedDirName(d));
    }

    @Disabled("Encountering permission issues for creating directory")
    @Test
    void testDirNames() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        List<String> dirNames = new ArrayList<>();
        dirNames.add(DateUtils.getDateFormattedDirName(sdf.parse("31/12/2020")));
        dirNames.add(DateUtils.getDateFormattedDirName(sdf.parse("01/12/2020")));
        dirNames.add(DateUtils.getDateFormattedDirName(sdf.parse("31/10/2020")));
        dirNames.add(DateUtils.getDateFormattedDirName(sdf.parse("01/10/2020")));
        dirNames.add(DateUtils.getDateFormattedDirName(sdf.parse("22/02/2020")));
        dirNames.add(DateUtils.getDateFormattedDirName(sdf.parse("01/02/2020")));
        dirNames.add(DateUtils.getDateFormattedDirName(sdf.parse("31/01/2020")));
        dirNames.add(DateUtils.getDateFormattedDirName(sdf.parse("01/01/2020")));

        File tempDir = Files.createTempDirectory("junit-").toFile();
        for (String current : dirNames) {
            File newDir = new File(tempDir, current);
            newDir.mkdir();
        }
        List<String> actual = new ArrayList<>();
        File[] children = tempDir.listFiles();
        for (File child : children) {
            actual.add(child.getName());
        }
        Collections.sort(actual);
        assertEquals(8, actual.size());
        assertEquals("data-2020-01-01", actual.get(0));
        assertEquals("data-2020-01-31", actual.get(1));
        assertEquals("data-2020-02-01", actual.get(2));
        assertEquals("data-2020-02-22", actual.get(3));
        assertEquals("data-2020-10-01", actual.get(4));
        assertEquals("data-2020-10-31", actual.get(5));
        assertEquals("data-2020-12-01", actual.get(6));
        assertEquals("data-2020-12-31", actual.get(7));

        for (File child : children) {
            child.delete();
        }
        tempDir.delete();
    }
}