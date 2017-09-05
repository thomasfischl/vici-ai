package vici.ai.ga;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.Lists;

import vici.ai.dataset.ConditionSet;
import vici.ai.engine.DataContext;
import vici.ai.engine.RuleEngine;
import vici.ai.engine.RuleEngineMatchSummary;
import vici.ai.rule.Condition;
import vici.ai.rule.Rule;
import vici.ai.rule.RuleGenerator;
import vici.ai.rule.StateRangeService;

public class RuleProblemDef implements ProblemDef<Rule> {

  private final List<String> srcActorNames;

  private final RuleEngine engine = new RuleEngine();

  private final Random rand = new Random();

  private final RuleMutator mutator;

  private final RuleGenerator generator;

  private final List<String> targetActors;

  private final StateRangeService rangeService;

  private final Set<String> unwantedIndividiums;

  public RuleProblemDef(List<String> srcActorNames, List<String> targetActors, StateRangeService rangeService, Set<String> unwantedIndividiums,
      ConditionSet conditions) {
    this.srcActorNames = srcActorNames;
    this.targetActors = targetActors;
    this.rangeService = rangeService;
    this.unwantedIndividiums = unwantedIndividiums;

    mutator = new RuleMutator(srcActorNames, rangeService, conditions);
    generator = new RuleGenerator(rangeService, conditions);
  }

  @Override
  public Individium<Rule> createRandomIndividium() {
    return new Individium<Rule>(generator.randomRule(targetActors, srcActorNames, 1));
  }

  @Override
  public void calculateFitness(Individium<Rule> individium, List<DataContext> data) {
    RuleEngineMatchSummary result = engine.matchAll(individium.getValue(), data, false);
    individium.setFitness(result.getFitness());
    individium.setResultDetails(result.toString());
    individium.setResultData(result);
  }

  @Override
  public Individium<Rule> crossover(Individium<Rule> i1, Individium<Rule> i2) {

    Rule newRule = Rule.builder()
        .conditions(Lists.newArrayList())
        .targetActorName(i1.getValue().getTargetActorName())
        .targetActorState(i1.getValue().getTargetActorState())
        .build();

    List<Condition> conditions = new ArrayList<>(i1.getValue().getConditions());
    conditions.addAll(i2.getValue().getConditions());

    for (int i = 0; i < Math.min(rand.nextInt(5) + 1, conditions.size()); i++) {

      Condition c = conditions.remove(rand.nextInt(conditions.size()));

      if (!containsConditionsSrcActor(newRule.getConditions(), c)) {
        newRule.getConditions().add(c);
      }

    }

    return new Individium<Rule>(newRule);
  }

  public boolean containsConditionsSrcActor(List<Condition> list, Condition c) {
    for (Condition con : list) {
      if (con.getSrcActorName().equals(c.getSrcActorName())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Individium<Rule> mutate(Individium<Rule> i1) {
    return mutator.mutate(i1);
  }

  @Override
  public void showPopulationStatistics(Individium<Rule>[] population) {
    Set<String> availableGensInPopulation = new HashSet<>();

    double minFitness = Double.MAX_VALUE;
    double maxFitness = Double.MIN_VALUE;
    double sumFitness = 0;

    Map<String, AtomicInteger> duplicates = new HashMap<>();

    for (Individium<Rule> individium : population) {
      Rule value = individium.getValue();
      for (Condition c : value.getConditions()) {
        availableGensInPopulation.add(c.getShortForm());
      }

      minFitness = Math.min(minFitness, individium.getFitness());
      maxFitness = Math.max(maxFitness, individium.getFitness());

      sumFitness += individium.getFitness();

      if (duplicates.containsKey(value.getShortForm())) {
        duplicates.get(value.getShortForm()).incrementAndGet();
      } else {
        duplicates.put(value.getShortForm(), new AtomicInteger(1));
      }
    }

    int numberOfDuplicates = 0;
    for (Entry<String, AtomicInteger> entry : duplicates.entrySet()) {
      if (entry.getValue().get() > 1) {
        numberOfDuplicates++;
      }
    }

    double diversity = (double) availableGensInPopulation.size() / rangeService.getNumberOfPossibleGens();
    System.out.format("Diversity: %.3f (%d/%d) - Min/Avg/Max [%.2f|%.2f|%.2f] - Duplicates %d\n", diversity, availableGensInPopulation.size(),
        rangeService.getNumberOfPossibleGens(),
        minFitness, sumFitness / population.length, maxFitness, numberOfDuplicates);

    // System.out.println(availableGensInPopulation);
  }

  @Override
  public boolean isExceptableSolution(Individium<Rule> individium) {
    RuleEngineMatchSummary resultData = (RuleEngineMatchSummary) individium.getResultData();

    // if (resultData.getCountMatchP() > 50 && resultData.getCountFP() <= 50) {
    // return true;
    // }
    if (resultData.getCountMatchP() > 5 && resultData.getCountFP() <= 1) {
      return true;
    }

    return false;
  }

  @Override
  public void removeUnwantedIndividiums(Individium<Rule>[] population) {

    boolean unwantedFound = true;

    while (unwantedFound) {
      unwantedFound = false;

      Set<String> duplicates = new HashSet<>();

      for (int i = 0; i < population.length; i++) {

        Individium<Rule> individium = population[i];
        Rule r = individium.getValue();

        String shortForm = individium.getValue().getShortForm();
        if (unwantedIndividiums.contains(shortForm)) {
          unwantedFound = true;
          population[i] = createRandomIndividium();
        } else if (duplicates.contains(shortForm)) {
          unwantedFound = true;
          population[i] = createRandomIndividium();
        } else if (r.getConditions()
            .stream()
            .map(o -> o.getSrcActorName())
            .filter(o -> o.equals(r.getTargetActorName()) || r.getTargetActorName().startsWith(o + "_"))
            .findFirst()
            .isPresent()) {

          unwantedFound = true;
          population[i] = createRandomIndividium();
        }

        duplicates.add(shortForm);
      }

    }

  }

}
