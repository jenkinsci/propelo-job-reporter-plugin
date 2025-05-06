package io.jenkins.plugins.propelo.commons.service;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JobRunGitChangesParserServiceTest {

    private void compareLists(List<String> actual, List<String> expected) {
        Set<String> a = new HashSet<>(actual);
        Set<String> e = new HashSet<>(expected);
        assertEquals(a, e);
    }

    @Test
    void testParse() throws URISyntaxException {
        JobRunGitChangesParserService jobRunGitChangesParserService = new JobRunGitChangesParserService();
        Map<String, List<String>> expected = new HashMap<>();
        expected.put("changelog_25.xml", Arrays.asList("4a4353c6d9d471826328b298fe3042c28bbe318d", "cc6dfc65066fd69fb065b62b734cc7c960a46e75", "cf64153e237ee3a34ce91989accfdafb2323e7a6", "3a2eeee9bf5ba2938e7aa9c861eb0611bb4056eb", "600b571966defafb1d124db2b4d85cae22a20f10", "52e295e34a16c119c403a0130fa0093fc31f5371", "81b426e034d67ead6db7689e694b450baf14a114", "2d44ef083cf86190ef0597d705edbf02e9635bc0", "a7c7ecbdc554fa6cf083d604582743b152180208", "e667282899c3e3913f707277699de633f738e957", "7856900458ea5d5cde55d8bc5a8266470d6a2517", "e57c62527ea8e28cadc23c38e9cfb69820b9e8d5"));
        expected.put("changelog_1232.xml", List.of());
        expected.put("changelog_1233.xml", List.of("4b23e50bfc36f313a8994666140e108711187310"));
        File testDir = new File(this.getClass().getClassLoader().getResource("config_git_change_logs").toURI());
        for (File currentFile : testDir.listFiles()) {
            if (!currentFile.isFile()) {
                continue;
            }
            List<String> commitIds = jobRunGitChangesParserService.parseGitChangeCommitIds(currentFile);
            compareLists(commitIds, expected.get(currentFile.getName()));
        }
    }

}