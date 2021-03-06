package vici.ai;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Charsets;
import com.google.common.base.Stopwatch;
import com.google.common.io.Files;

import vici.ai.dataset.DataSet;
import vici.ai.dataset.DataSetLoader;
import vici.ai.engine.DataContext;
import vici.ai.engine.RuleEngineMatchSummary;
import vici.ai.ga.GA;
import vici.ai.ga.GAConfig;
import vici.ai.ga.Individium;
import vici.ai.ga.ProblemDef;
import vici.ai.ga.RuleProblemDef;
import vici.ai.rule.Rule;
import vici.ai.rule.StateRangeService;

public class MultiRuleDriver {

  private final File dataFile;

  private final int numberOfRuns;

  private final File outputFile;

  public MultiRuleDriver(File dataFile, int numberOfRuns, File outputFile) {
    this.dataFile = dataFile;
    this.numberOfRuns = numberOfRuns;
    this.outputFile = outputFile;
  }

  public static void main(String[] args) {
    new MultiRuleDriver(new File("./Scenario6.csv"), 2, new File("./result.txt")).run();
  }

  public void run() {
    DataSetLoader loader = new DataSetLoader();
    trainMultipleTimes(loader.loadThomasDataSetWithTimeFinal(dataFile), numberOfRuns);
  }

  private void trainMultipleTimes(DataSet dataSet, int runs) {
    List<Individium<Rule>> results = new ArrayList<>();

    for (int i = 0; i < runs; i++) {
      Stopwatch s = Stopwatch.createStarted();
      System.out.println("Run: " + (i + 1));

      Individium<Rule> bestRule = train(dataSet.getData(), results);
      results.add(bestRule);

      System.out.println("Duration: " + s.stop());
      System.out.println("=======================================================================================");
    }

    displayResults(results, dataSet);
  }

  private Individium<Rule> train(List<DataContext> data, List<Individium<Rule>> results) {
    GAConfig config = GAConfig.builder()
        .elitism(1)
        .maxNumberOfGenerations(400)
        .numberOfCrossoverOperations(100)
        .numberOfMutationOperations(80)
        .populationSize(200)
        .threadCount(6)
        .build();

    List<String> names = data.get(0).getNames().stream().filter(name -> !name.startsWith("A")).collect(Collectors.toList());

    StateRangeService rangeService = new StateRangeService();

    Set<String> unwantedIndividiums = new HashSet<>();
    for (Individium<Rule> result : results) {
      unwantedIndividiums.add(result.getValue().getShortForm());
    }

    ProblemDef<Rule> problemDef = new RuleProblemDef(names, rangeService.getTargetActors(), rangeService, unwantedIndividiums);
    GA<Rule> ga = new GA<Rule>(config, problemDef);
    Individium<Rule> bestRule = ga.train(data);
    return bestRule;
  }

  private void displayResults(List<Individium<Rule>> results, DataSet dataSet) {

    Collections.sort(results, (o1, o2) -> Double.compare(o2.getFitness(), o1.getFitness()));

    StringBuilder sb = new StringBuilder();
    sb.append(
        "===================================================================================================================================================\n");
    sb.append(" Results: \n");
    sb.append(
        "===================================================================================================================================================\n");

    sb.append(
        "                                                                                                               [ TP  | TN  |  FP |  FN ] - Fitness\n");

    List<String> targetActors = results.stream().map(o -> o.getValue().getTargetActorName()).distinct().sorted().collect(Collectors.toList());

    for (String targetActor : targetActors) {
      sb.append("\n");
      sb.append("  Target Actor: " + targetActor + " (" + dataSet.getDeviceNameMapping().get(targetActor) + ")\n");
      sb.append(
          "---------------------------------------------------------------------------------------------------------------------------------------------------\n");

      for (Individium<Rule> result : results) {
        if (result.isAcceptableSolution() && result.getValue().getTargetActorName().equals(targetActor)) {
          sb.append(printIndividiumResult(result));
        }
      }
    }

    sb.append("\n");
    sb.append("  Not Acceptable Solutions\n");
    sb.append(
        "---------------------------------------------------------------------------------------------------------------------------------------------------\n");
    for (Individium<Rule> result : results) {
      if (!result.isAcceptableSolution()) {
        sb.append(printIndividiumResult(result));
      }
    }

    try {
      Files.write(sb, outputFile, Charsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String printIndividiumResult(Individium<Rule> result) {
    RuleEngineMatchSummary rawResult = (RuleEngineMatchSummary) result.getResultData();
    String output = "       " + result.getValue();

    for (int i = output.length(); i < 110; i++) {
      output += " ";
    }

    return String.format("%s [%5d|%5d|%5d|%5d] - %.3f\n", output, rawResult.getCountMatchP(), rawResult.getCountMatchN(), rawResult.getCountFP(),
        rawResult.getCountFN(), rawResult.getFitness());
  }

}
