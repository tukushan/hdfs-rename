#!/bin/sh
LIB_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"/../lib
echo $LIB_DIR
java -cp "$LIB_DIR/*" com.tenaris.bigdata.hdfs.renamer.RenameCli "$@"
