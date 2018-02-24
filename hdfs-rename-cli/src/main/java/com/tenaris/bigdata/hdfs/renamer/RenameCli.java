package com.tenaris.bigdata.hdfs.renamer;

public class RenameCli {

	public static void main(String[] args) {
		
		if(args.length < 3) {
			System.err.println("Syntax: hdfs-rename <hadoop-home> <hdfs-path> <prefix> [--dry-run]");
			System.exit(1);
		}
		
		String hadoopHome = args[0];
		String path = args[1];
		String prefix = args[2];
		Boolean dryRun = false;
		
		if(args.length == 4) {
			dryRun = args[3].trim().equals("--dry-run");
		}
		
		boolean verbose = true;
	
		if(dryRun) {
			System.out.println("** RUN DRY Mode ON: no actual modification will be applied **");
		}
		
		Renamer renamer = new Renamer(hadoopHome);
	
		try {
			renamer.rename(path, prefix, verbose, dryRun);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
		
		System.exit(0);
	}

}
