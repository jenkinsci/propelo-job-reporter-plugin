package io.jenkins.plugins.propelo.commons.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class JobRunPerforceChangesParserServiceTest {

    private static final ObjectMapper xmlMapper = new XmlMapper();
    private static final JobRunPerforceChangesParserService parserService = new JobRunPerforceChangesParserService();

    @Test
    public void deserializeXml() throws URISyntaxException, IOException {
        File testDir = new File(this.getClass().getClassLoader().getResource("perforce_changes_dir/1").toURI());
        File[] childrenFiles = testDir.listFiles();
        if (childrenFiles == null)
            Assert.fail("Children files in perforce directory is not expected to be null");
        else {
            for(File currentFile : childrenFiles){
                if(!currentFile.isFile()){
                    continue;
                }
                List<String> commitIds = parserService.parsePerforceChangeCommitIds(xmlMapper, currentFile);
                Assert.assertEquals(commitIds.size(), 1);
                Assert.assertTrue(commitIds.contains("21"));
            }
        }
    }
}
