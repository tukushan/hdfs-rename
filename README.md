# HDFS Rename With Hash Prefix
A simple utility to bulk rename files on HDFS with a prefix + 4 digit hexadecimal hash.

The tool recursively renames files in a folder on HDFS, with a prefix + 4 digit hexadecimal hash so they are in an optimised format for s3 (see [AWS S3 Request Rate and Performance Considerations](https://docs.aws.amazon.com/AmazonS3/latest/dev/request-rate-perf-considerations.html)) 

The tool takes as input:
*  the HDFS folder. Url form, such as `hdfs://localhost:9000/foo/bar` and absolute path form, such as `/foo/bar`, are both valid
*  a prefix that is applied to beginning of each file

The tool requires the path to `HADOOP_CONFIG`, i.e. the folder containing `hdfs-site.xml` and `core-site.xml`. Under Cloudera CDH, they are under `/etc/hadoop/conf.cloudera.hdfs`.

## Syntax

```
hdfs-rename <hadoop-home> <hdfs-path> <prefix> [--dry-run]
```

Please note that:
* <prefix> can be directory
* when `--dry-run` option is specified, the tools will tell which files will be renamed, without actually renaming them
* <hdfs-path> and its subdirectories are recursively processed

Here an example of tool usage:
```
hdfs-rename /etc/hadoop/conf/ /staging/foo /upload/
```
The previous command will connect to the HDFS instance of the CDH cluster, will rename eveything in `/staging/foo` eg: `events.1469447116780.20160728-120515.processing.avro` -> `/upload/8ade/staging/foo/events.1469447116780.20160728-120515.processing.avro`

## Building

To build the tool, type:

```
mvn package
```

The created package will be under `hdfs-rename-dist/target`.
