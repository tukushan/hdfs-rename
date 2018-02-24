package com.tenaris.bigdata.hdfs.renamer;

import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class Renamer {

    private Configuration config;

    public Renamer(String hadoopHome) {
        config = new Configuration();
        config.addResource(new Path(hadoopHome, "hdfs-site.xml"));
        config.addResource(new Path(hadoopHome, "core-site.xml"));
        config.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        config.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
    }

    public void rename(String path, String prefix, boolean verbose, boolean dryRun) throws IOException {

        // Check input variables
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("The ptah is empty");
        }

        if (prefix == null || prefix.trim().isEmpty()) {
            throw new IllegalArgumentException("The prefix is empty");
        }

        FileSystem hadoopFs = FileSystem.get(config);
        Path rootPath = new Path(path);

        FileStatus[] fileStatus = hadoopFs.listStatus(rootPath);

        rename(hadoopFs, fileStatus, prefix, verbose, dryRun);
    }

    private void rename(FileSystem hadoopFs, FileStatus[] fileStatus, String prefix, boolean verbose, boolean dryRun) throws IOException {
        for (int i = 0; i < fileStatus.length; i++) {
            if (fileStatus[i].isDirectory()) {
                FileStatus[] children = hadoopFs.listStatus(fileStatus[i].getPath());
                rename(hadoopFs, children, prefix, verbose, dryRun);

            } else if (fileStatus[i].isFile()) {
                String currentPath = Path.getPathWithoutSchemeAndAuthority(fileStatus[i].getPath()).toString();
                String fileName = fileStatus[i].getPath().getName();
                // includes trailing /
                String dir = currentPath.substring(0, currentPath.lastIndexOf("/") + 1);
                String newDir = prefix + DigestUtils.md2Hex(dir).subSequence(0, 4).toString() + dir;
                Path newPath = new Path(newDir, fileName);

                if (verbose && dryRun) {
                    System.out.println("Would be renaming " + currentPath + " to " + newPath);
                } else if (verbose && !dryRun) {
                    System.out.println("Renaming " + currentPath + " to " + newPath);
                }

                if (!dryRun) {
                    hadoopFs.mkdirs(newPath);
                    boolean renameSuccess = hadoopFs.rename(fileStatus[i].getPath(), newPath);
                    if (!renameSuccess) {
                        System.err.println("Unable to create " + newPath + ", does the file exist already?");
                    }
                }
            }
        }

    }

    // Groups are simply counted finding '$'
    private int countTemplateUsedGroups(String template) {
        return countOccurrences(template, '$');
    }

    // Groups are simply counted finding '('
    private int countRegExGroups(String searchRegexStr) {
        return countOccurrences(searchRegexStr, '(');
    }

    private int countOccurrences(String haystack, char needle) {

        int counter = 0;

        for (int i = 0; i < haystack.length(); i++) {
            if (haystack.charAt(i) == needle) {
                counter++;
            }
        }

        return counter;
    }

}
