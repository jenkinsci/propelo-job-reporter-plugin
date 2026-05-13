package io.jenkins.plugins.propelo.commons.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipService {
    private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    public void zipDirectory(File sourceDirectory, File outputZipFile) throws IOException {
        LOGGER.finest("zipDirectory sourceDirectory = " + sourceDirectory.getAbsolutePath() + " outputZipFile = " + outputZipFile.getAbsolutePath());
        final Queue<File> queue = new LinkedList<>();
        queue.offer(sourceDirectory);

        try (FileOutputStream fos = new FileOutputStream(outputZipFile);
             ZipOutputStream zos = new ZipOutputStream(fos) ) {

            while (!queue.isEmpty()) {
                File currentDir = queue.poll();
                if (currentDir == null) {
                    continue;
                }
                File[] children = currentDir.listFiles();
                if (children == null) {
                    continue;
                }
                for (File currentChild : children) {
                    if (currentChild.equals(outputZipFile)) {
                        continue;
                    }
                    if (currentChild.isDirectory()) {
                        queue.offer(currentChild);
                    } else {
                        //Supressing this log, too much noise
                        //LOGGER.finest("File Added : " + currentChild.toString());
                        ZipEntry ze = new ZipEntry(currentChild.getPath().substring(sourceDirectory.getAbsolutePath().length() +1));
                        zos.putNextEntry(ze);
                        try (FileInputStream in = new FileInputStream(currentChild)){
                            int len;
                            byte[] buffer = new byte[1024];
                            while ((len = in .read(buffer)) > 0) {
                                zos.write(buffer, 0, len);
                            }
                        }
                    }
                }
            }

            zos.closeEntry();
        }
    }

    /*
    Unzip is not used in the plugin. Fixing this proactively.
     */
    public void unZip(File sourceZipFile, File unzipDestinationDir) throws IOException {
        byte[] buffer = new byte[1024];

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
        try (FileInputStream fis = new FileInputStream(sourceZipFile);
             ZipInputStream zis = new ZipInputStream(fis) ) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(unzipDestinationDir, zipEntry);
                try (FileOutputStream fos = new FileOutputStream(newFile)){
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        }
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
        if(!destFile.getParentFile().exists()){
            if(!destFile.getParentFile().mkdirs()){
                throw new IOException("Could not create dir: " + destFile.getParentFile().getAbsolutePath());
            }
        }
        if(!destFile.exists()){
            if(!destFile.createNewFile()){
                throw new IOException("Could not create file: " + destFile.getAbsolutePath());
            }
        }
        return destFile;
    }
}
