package io.jenkins.plugins.propelo.commons.service;

import hudson.model.Run;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.jenkins.plugins.propelo.commons.utils.JobUtils.writeLogData;

public class JobLogsService {
    private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private static final String separator = System.getProperty("line.separator");
    private static final String JOB_BUILDS = "builds";

    public UUID getLogFailedString(Run run, File jobRunCompleteDataDirectory) {
        File failedJobLogFile = getPathToLogFile(run.getParent().getRootDir().toString(), run.getNumber()).toFile();
        StringBuilder logsFromFile = new StringBuilder();
        String lineFromFile;
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
        try ( FileReader fr = new FileReader(failedJobLogFile);
              BufferedReader bufferedReader = new BufferedReader(fr)) {
            while ((lineFromFile = bufferedReader.readLine()) != null) {
                logsFromFile.append(lineFromFile);
                logsFromFile.append(separator);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading job run logs!", e);
        }
        return writeLogData(jobRunCompleteDataDirectory, logsFromFile.toString());
    }

    @NotNull
    private Path getPathToLogFile(String jobAbsolutePath, long buildNumber) {
        return Paths.get(jobAbsolutePath, JOB_BUILDS, String.valueOf(buildNumber), "log");
    }
}
