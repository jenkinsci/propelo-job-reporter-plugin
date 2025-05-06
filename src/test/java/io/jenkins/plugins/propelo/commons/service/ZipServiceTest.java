package io.jenkins.plugins.propelo.commons.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ZipServiceTest {

    private Map<String, String> directoryDetails(File dir) throws IOException {
        Map<String, String> details = new HashMap<>();
        Queue<File> q = new LinkedList<>();
        q.offer(dir);
        while (!q.isEmpty()) {
            File c = q.poll();
            File[] children = c.listFiles();
            if (children == null) {
                continue;
            }
            for (File child : children) {
                if (child.isDirectory()) {
                    q.offer(child);
                }
                if (child.isFile()) {
                    details.put(child.getAbsolutePath().substring(dir.getAbsolutePath().length() + 1), DigestUtils.sha256Hex(Files.readAllBytes(child.toPath())));
                }
            }
        }
        return details;
    }

    private void compareDirectoryDetails(Map<String, String> e, Map<String, String> a) {
        assertEquals(e.size(), a.size());
        for (String key : e.keySet()) {
            assertEquals(e.get(key), a.get(key));
        }
    }

    private void compareDirectories(File expected, File actual) throws IOException {
        Map<String, String> e = directoryDetails(expected);
        Map<String, String> a = directoryDetails(actual);
        compareDirectoryDetails(e, a);
    }

    @Test
    void testZipUnzip() throws IOException, URISyntaxException {
        File zipFile = null;
        File unZipDir = null;
        try {
            File sourceDirectory = new File(this.getClass().getClassLoader().getResource("configs").toURI());
            zipFile = File.createTempFile("testZip", ".zip");
            unZipDir = Files.createTempDirectory("testUnzip").toFile();
            ZipService zipService = new ZipService();
            zipService.zipDirectory(sourceDirectory, zipFile);
            zipService.unZip(zipFile, unZipDir);
            compareDirectories(sourceDirectory, unZipDir);
        } finally {
            if (zipFile != null) {
                zipFile.delete();
            }
            if (unZipDir != null) {
                FileUtils.deleteDirectory(unZipDir);
            }
        }
    }

}