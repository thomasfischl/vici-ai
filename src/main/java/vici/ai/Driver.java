package vici.ai;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import vici.ai.dataset.DataSetLoader;
import vici.ai.engine.DataContext;
import vici.ai.engine.RuleEngine;
import vici.ai.engine.RuleEngineMatchSummary;
import vici.ai.ga.GA;
import vici.ai.ga.GAConfig;
import vici.ai.ga.Individium;
import vici.ai.ga.ProblemDef;
import vici.ai.ga.RuleProblemDef;
import vici.ai.rule.Condition;
import vici.ai.rule.ConditionOperator;
import vici.ai.rule.Rule;
import vici.ai.rule.RuleGenerator;
import vici.ai.rule.StateRangeService;

public class Driver {

  public static void main(String[] args) {

    DataSetLoader loader = new DataSetLoader();

    // List<DataContenxt> data = loader.loadThomasDacctaSetWithTimeFinal(new File("./Scenario4-half.txt"));
    // List<DataContenxt> data = loader.loadThomasDataSetWithTimeFinal(new File("./Scenario3.txt"));
    // List<DataContenxt> data = loader.loadThomasDataSetWithTimeFinal(new File("./sample900.txt"));
    // List<DataContenxt> data = loader.loadThomasDataSetWithTime(new File("./sample003.txt"));
    // List<DataContenxt> data = loader.loadWithOffset(new File("./sample002.txt"), 10);
    // DataContenxt.print(data);
    // System.out.println(data.size());

    // RuleFitnessTester tester = new RuleFitnessTester(data);
    // tester.eval();

    // test(data);
    // train(data);
    // trainGa(loader.loadThomasDataSetWithTimeFinal(new File("./sample900.txt")), Lists.newArrayList("A7_0", "A13_0"));
    // trainGa(loader.loadThomasDataSetWithTimeFinal(new File("./Scenario3.txt")), Lists.newArrayList("A0_0"));
    // trainGa(loader.loadThomasDataSetWithTimeFinal(new File("./Scenario3.txt")), Lists.newArrayList("A3_0"));
    // trainGa(loader.loadThomasDataSetWithTimeFinal(new File("./Scenario4.txt")), Lists.newArrayList("A18_0"));
    // trainGa(loader.loadThomasDataSetWithTimeFinal(new File("./Scenario4-half.txt")), Lists.newArrayList("A18_0"));
    trainGa(loader.loadThomasDataSetWithTimeFinal(new File("./Scenario4-half.txt")).getData(), null);
    // trainGa(loader.loadThomasDataSetWithTimeFinal(new File("./Scenario4.txt")), Lists.newArrayList( "A6_0"));
    // trainGa(loader.loadThomasDataSetWithTimeFinal(new File("./Scenario4-half.txt")), Lists.newArrayList("A4_0"));

    // trainGa(loader.loadThomasDataSetWithTimeFinal(new File("./Scenario5.txt")), null);
    // trainGa(loader.loadThomasDataSetWithTimeFinal(new File("./Scenario5.txt")), Lists.newArrayList("A14_0"));

  }

  private static void trainGa(List<DataContext> data, ArrayList<String> targetActors) {

    List<Individium<Rule>> results = new ArrayList<>();

    GAConfig config = GAConfig.builder()
        .elitism(1)
        .maxNumberOfGenerations(500)
        .numberOfCrossoverOperations(120)
        .numberOfMutationOperations(100)
        .populationSize(300)
        .build();

    // GAConfig config = GAConfig.builder()
    // .elitism(1)
    // .maxNumberOfGenerations(500)
    // .numberOfCrossoverOperations(80)
    // .numberOfMutationOperations(40)
    // .populationSize(150)
    // .build();

    List<String> names = data.get(0).getNames().stream().filter(name -> !name.startsWith("A")).collect(Collectors.toList());

    StateRangeService rangeService = new StateRangeService();
    // rangeService.removeHourState(18);
    // rangeService.removeHourState(16);
    // rangeService.removeHourState(11);
    // rangeService.removeMinuteState(15);
    // rangeService.removeDayOfWeekState(6);

    if (targetActors == null) {
      targetActors = rangeService.getTargetActors();
      targetActors.remove("A4_0");
    }

    Set<String> unwantedIndividiums = new HashSet<>();

    ProblemDef<Rule> problemDef = new RuleProblemDef(names, targetActors, rangeService, unwantedIndividiums);
    GA<Rule> ga = new GA<Rule>(config, problemDef);
    Individium<Rule> bestRule = ga.train(data);

    results.add(bestRule);

    for (Individium<Rule> rule : results) {
      System.out.println("Best found Rule: " + rule.getValue() + " [" + rule.getResultDetails() + "]");
    }
  }

  public static void train(List<DataContext> data) {
    RuleEngine engine = new RuleEngine();
    RuleGenerator generator = new RuleGenerator(new StateRangeService());

    int iterations = 0;

    // Rule rule = null;
    RuleEngineMatchSummary summary = new RuleEngineMatchSummary(0, 0, 0, 1000000, 0);

    while (summary.getCountMatchP() < data.size()) {

      Rule r0 = generator.random("A1", 1, data.get(0).getNames());
      RuleEngineMatchSummary tmpSummary = engine.matchAll(r0, data);

      iterations++;

      if (summary.getFitness() < tmpSummary.getFitness()) {
        // rule = r0;
        summary = tmpSummary;
        System.out.println(iterations + "  Result: " + tmpSummary + "        " + r0);
      }

      if (tmpSummary.getFitness() > 50) {
        System.out.println(iterations + "  Result: " + tmpSummary + "        " + r0);
      }

      if (iterations % 1000 == 0) {
        System.out.println(iterations + "  Result: " + summary);
      }

    }

    System.out.println("Iterations: " + iterations);
  }

  public static void test(List<DataContext> data) {
    RuleEngine engine = new RuleEngine();

    Rule r0;

    r0 = Rule.builder()
        .conditions(Lists.newArrayList(
            Condition.builder()
                .srcActorName("Hour")
                .op(ConditionOperator.EQUALS)
                .varState(20)
                .build()))
        .targetActorName("A18_0")
        .targetActorState(1)
        .build();

    System.out.println("Result: " + engine.matchAll(r0, data) + "        " + r0);

    r0 = Rule.builder()
        .conditions(Lists.newArrayList(
            Condition.builder()
                .srcActorName("Minute")
                .op(ConditionOperator.EQUALS)
                .varState(15)
                .build()))
        .targetActorName("A18_0")
        .targetActorState(1)
        .build();

    System.out.println("Result: " + engine.matchAll(r0, data) + "        " + r0);

  }

}
