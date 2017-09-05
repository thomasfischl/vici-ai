package vici.ai;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import vici.ai.dataset.DataSet;
import vici.ai.dataset.DataSetLoader;
import vici.ai.engine.DataContext;
import vici.ai.engine.RuleEngine;
import vici.ai.engine.RuleEngineMatchSummary;
import vici.ai.rule.BoolExprOperator;
import vici.ai.rule.ConditionOperator;
import vici.ai.rule.IntervalCondition;
import vici.ai.rule.Rule;
import vici.ai.rule.SimpleCondition;

public class RuleFitnessTester {

  private List<DataContext> data;

  private RuleEngine engine = new RuleEngine();

  public static void main(String[] args) {
    DataSetLoader loader = new DataSetLoader();
    DataSet data = loader.loadThomasDataSetWithTimeFinal(new File("./samples/FullScenario.csv"));
    new RuleFitnessTester(data.getData()).eval();
  }

  public RuleFitnessTester(List<DataContext> data) {
    this.data = data;
  }

  public void eval() {

    List<RuleResult> results = new ArrayList<>();
    scenarioFull(results, "A2_0");

    results.sort((o1, o2) -> compare(o1, o2));

    for (RuleResult o : results) {
      System.out.println(o.summary + ": " + o.rule);
    }

  }

  public void scenarioFull(List<RuleResult> results, String targetActorName) {
    Rule rule = Rule.builder()
        .conditions(Lists.newArrayList(
            SimpleCondition.builder()
                .srcActorName("Hour")
                .op(ConditionOperator.EQUALS)
                .varState(18)
                .exprOp(BoolExprOperator.AND)
                .build(),
            SimpleCondition.builder()
                .srcActorName("Minute")
                .op(ConditionOperator.EQUALS)
                .varState(30)
                .exprOp(BoolExprOperator.AND)
                .build(),

            IntervalCondition.builder()
                .srcActorName("DayOfWeek")
                .lowerBoundState(2)
                .upperBoundState(4)
                .exprOp(BoolExprOperator.AND)
                .build()))
        .targetActorName("A6_0")
        .targetActorState(1)
        .build();

    RuleEngineMatchSummary summary = engine.matchAll(rule, data, false);
    // results.add(new RuleResult(rule, summary));
    // System.out.println(rule + " " + summary);
    // System.out.println("======================================================");

    rule = Rule.builder()
        .conditions(Lists.newArrayList(
            SimpleCondition.builder()
                .srcActorName("DayOfWeek")
                .op(ConditionOperator.EQUALS)
                .varState(6)
                .exprOp(BoolExprOperator.AND)
                .build(),

            SimpleCondition.builder()
                .srcActorName("Hour")
                .op(ConditionOperator.EQUALS)
                .varState(15)
                .exprOp(BoolExprOperator.AND)
                .build(),

            SimpleCondition.builder()
                .srcActorName("Minute")
                .op(ConditionOperator.EQUALS)
                .varState(0)
                .exprOp(BoolExprOperator.AND)
                .build()))
        .targetActorName("A6_0")
        .targetActorState(1)
        .build();

    summary = engine.matchAll(rule, data, true);
    results.add(new RuleResult(rule, summary));
    System.out.println(rule + " " + summary);
    System.out.println("======================================================");

    rule = Rule.builder()
        .conditions(Lists.newArrayList(
            SimpleCondition.builder()
                .srcActorName("DayOfWeek")
                .op(ConditionOperator.EQUALS)
                .varState(6)
                .exprOp(BoolExprOperator.AND)
                .build()))
        .targetActorName("A6_0")
        .targetActorState(1)
        .build();

    summary = engine.matchAll(rule, data, true);
    results.add(new RuleResult(rule, summary));
    System.out.println(rule + " " + summary);
    System.out.println("======================================================");

    rule = Rule.builder()
        .conditions(Lists.newArrayList(

            SimpleCondition.builder()
                .srcActorName("Hour")
                .op(ConditionOperator.EQUALS)
                .varState(15)
                .exprOp(BoolExprOperator.AND)
                .build()

        ))
        .targetActorName("A6_0")
        .targetActorState(1)
        .build();

    summary = engine.matchAll(rule, data, true);
    results.add(new RuleResult(rule, summary));
    System.out.println(rule + " " + summary);
    System.out.println("======================================================");

    rule = Rule.builder()
        .conditions(Lists.newArrayList(

            SimpleCondition.builder()
                .srcActorName("Minute")
                .op(ConditionOperator.EQUALS)
                .varState(0)
                .exprOp(BoolExprOperator.AND)
                .build()))
        .targetActorName("A6_0")
        .targetActorState(1)
        .build();

    summary = engine.matchAll(rule, data, true);
    results.add(new RuleResult(rule, summary));
    System.out.println(rule + " " + summary);
    System.out.println("======================================================");

    
    rule = Rule.builder()
        .conditions(Lists.newArrayList(
             SimpleCondition.builder()
             .srcActorName("DayOfWeek")
             .op(ConditionOperator.EQUALS)
             .varState(6)
             .exprOp(BoolExprOperator.AND)
             .build(),


            SimpleCondition.builder()
                .srcActorName("Minute")
                .op(ConditionOperator.EQUALS)
                .varState(0)
                .exprOp(BoolExprOperator.AND)
                .build()))
        .targetActorName("A6_0")
        .targetActorState(1)
        .build();

    summary = engine.matchAll(rule, data, true);
    results.add(new RuleResult(rule, summary));
    System.out.println(rule + " " + summary);
    System.out.println("======================================================");
    
    rule = Rule.builder()
        .conditions(Lists.newArrayList(

            SimpleCondition.builder()
                .srcActorName("Hour")
                .op(ConditionOperator.EQUALS)
                .varState(15)
                .exprOp(BoolExprOperator.AND)
                .build(),

            SimpleCondition.builder()
                .srcActorName("Minute")
                .op(ConditionOperator.EQUALS)
                .varState(0)
                .exprOp(BoolExprOperator.AND)
                .build()))
        .targetActorName("A6_0")
        .targetActorState(1)
        .build();

    summary = engine.matchAll(rule, data, true);
    results.add(new RuleResult(rule, summary));
    System.out.println(rule + " " + summary);
    System.out.println("======================================================");
    
    rule = Rule.builder()
        .conditions(Lists.newArrayList(

            SimpleCondition.builder()
                .srcActorName("Hour")
                .op(ConditionOperator.EQUALS)
                .varState(16)
                .exprOp(BoolExprOperator.AND)
                .build()))
        .targetActorName("A6_0")
        .targetActorState(1)
        .build();

    summary = engine.matchAll(rule, data, true);
    results.add(new RuleResult(rule, summary));
    System.out.println(rule + " " + summary);
    System.out.println("======================================================");
    
    rule = Rule.builder()
        .conditions(Lists.newArrayList(
             SimpleCondition.builder()
             .srcActorName("DayOfWeek")
             .op(ConditionOperator.EQUALS)
             .varState(5)
             .exprOp(BoolExprOperator.AND)
             .build()

           ))
        .targetActorName("A6_0")
        .targetActorState(1)
        .build();

    summary = engine.matchAll(rule, data, true);
    results.add(new RuleResult(rule, summary));
    System.out.println(rule + " " + summary);
    System.out.println("======================================================");

  }

  private int compare(RuleResult o1, RuleResult o2) {
    return Double.compare(o2.summary.getFitness(), o1.summary.getFitness());
  }

  private static class RuleResult {
    Rule rule;
    RuleEngineMatchSummary summary;

    public RuleResult(Rule rule, RuleEngineMatchSummary summary) {
      this.rule = rule;
      this.summary = summary;
    }

  }

}
