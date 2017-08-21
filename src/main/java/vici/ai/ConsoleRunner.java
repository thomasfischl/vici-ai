package vici.ai;

import java.io.File;
import java.io.IOException;

public class ConsoleRunner {

  public static void main(String[] args) throws IOException {

    if (args.length != 3) {
      System.out.println("Usage: vici <datafile> <numberOfRuns> <outputfile>");
      System.exit(-1);
    }

    File dataFile = new File(args[0]);
    if (!dataFile.isFile()) {
      System.out.println("Can't read file '" + dataFile.getAbsolutePath() + "'.");
    }

    File outputFile = new File(args[2]);
    outputFile.createNewFile();
    
    int numberOfRuns = Integer.parseInt(args[1]);
    MultiRuleDriver driver = new MultiRuleDriver(dataFile, numberOfRuns, outputFile);
    driver.run();
  }

}
