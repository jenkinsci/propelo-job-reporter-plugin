package io.jenkins.plugins.propelo.commons.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jenkins.plugins.propelo.commons.models.perforce.PerforceChangelog;
import io.jenkins.plugins.propelo.commons.models.perforce.PerforceChangelogChangeNumber;
import io.jenkins.plugins.propelo.commons.models.perforce.PerforceChangelogEntry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobRunPerforceChangesParserService {

    private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    public List<String> parsePerforceChangeCommitIds(ObjectMapper xmlMapper, File jobRunPerforceChangesCommitsFile) throws IOException {
        if (!jobRunPerforceChangesCommitsFile.exists()) {
            return Collections.emptyList();
        }
        String xmlChangelog = getInputStreamAsString(new FileInputStream(jobRunPerforceChangesCommitsFile));
        List<String> commitIds = new ArrayList<>();
        try {
            PerforceChangelog changelog = xmlMapper.readValue(xmlChangelog, PerforceChangelog.class);
            if (CollectionUtils.isEmpty(changelog.getEntries()))
                return commitIds;
            for (PerforceChangelogEntry entry : changelog.getEntries()) {
                if (CollectionUtils.isEmpty(entry.getChangeNumbers()))
                    continue;
                for (PerforceChangelogChangeNumber changeNumber : entry.getChangeNumbers())
                    if (StringUtils.isNotEmpty(changeNumber.getChangeInfo()))
                        commitIds.add(changeNumber.getChangeInfo());
            }
        } catch (IOException e) {
            LOGGER.log(Level.FINEST, "perforce changelog parsing skipped", e);
        }
        return commitIds;
    }

    private String getInputStreamAsString(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        /*
        https://stackoverflow.com/questions/884007/correct-way-to-close-nested-streams-and-writers-in-java
        Good:
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
          // do something with ois
        }
        Better:
        try (FileInputStream fis = new FileInputStream(f); ObjectInputStream ois = new ObjectInputStream(fis)) {
          // do something with ois
        }
        Reason: The try-with-resources is not aware of the inner FileInputStream, so if the ObjectInputStream constructor throws an exception,
        the FileInputStream is never closed (until the garbage collector gets to it).
         */
        try ( InputStreamReader isr = new InputStreamReader(inputStream);
              BufferedReader br = new BufferedReader(isr) ) {
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            LOGGER.log(Level.FINEST, "Failed to read input", e);
        } finally {
            inputStream.close();
        }
        return stringBuilder.toString();
    }
}
