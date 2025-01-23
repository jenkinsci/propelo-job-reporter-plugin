package io.jenkins.plugins.propelo.commons.utils;

import com.google.common.io.Files;
import io.jenkins.plugins.propelo.commons.utils.DateUtils;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DateUtilsTest {
    @Test
    public void testGetDateFormattedDirName() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Date d = sdf.parse("01/01/2020");
        Assert.assertEquals("data-2020-01-01", DateUtils.getDateFormattedDirName(d));

        d = sdf.parse("31/01/2020");
        Assert.assertEquals("data-2020-01-31", DateUtils.getDateFormattedDirName(d));

        d = sdf.parse("01/10/2020");
        Assert.assertEquals("data-2020-10-01", DateUtils.getDateFormattedDirName(d));

        d = sdf.parse("31/10/2020");
        Assert.assertEquals("data-2020-10-31", DateUtils.getDateFormattedDirName(d));
    }
    @Ignore // Encountering permission issues for creating directory
    @Test
    public void testDirNames() throws ParseException {
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

        File tempDir = Files.createTempDir();
        try {
            for(String current : dirNames){
                File newDir = new File(tempDir, current);
                newDir.mkdir();
            }
            List<String> actual = new ArrayList<>();
            File[] children = tempDir.listFiles();
            for(File child : children){
                actual.add(child.getName());
            }
            Collections.sort(actual);
            Assert.assertEquals(8, actual.size());
            Assert.assertEquals("data-2020-01-01", actual.get(0));
            Assert.assertEquals("data-2020-01-31", actual.get(1));
            Assert.assertEquals("data-2020-02-01", actual.get(2));
            Assert.assertEquals("data-2020-02-22", actual.get(3));
            Assert.assertEquals("data-2020-10-01", actual.get(4));
            Assert.assertEquals("data-2020-10-31", actual.get(5));
            Assert.assertEquals("data-2020-12-01", actual.get(6));
            Assert.assertEquals("data-2020-12-31", actual.get(7));

            for(File child : children){
                child.delete();
            }
            tempDir.delete();
        } finally {


        }
    }
}