package vici.ai;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import vici.ai.engine.DataContext;
import vici.ai.engine.RuleEngine;
import vici.ai.engine.RuleEngineMatchSummary;
import vici.ai.rule.BoolExprOperator;
import vici.ai.rule.Condition;
import vici.ai.rule.ConditionOperator;
import vici.ai.rule.Rule;

public class RuleFitnessTester {

  private List<DataContext> data;

  private RuleEngine engine = new RuleEngine();

  public RuleFitnessTester(List<DataContext> data) {
    this.data = data;
  }

  public void eval() {

    List<RuleResult> results = new ArrayList<>();

//    scenario4_1(results, "A19_0");
     scenario4_2(results, "A4_0");

    results.sort((o1, o2) -> compare(o1, o2));

    for (RuleResult o : results) {
      System.out.println(o.summary + ": " + o.rule);
    }

  }

  public void scenario4_1(List<RuleResult> results, String targetActorName) {
    for (int i = 0; i < 24; i++) {
      Rule rule = Rule.builder()
          .conditions(Lists.newArrayList(
              Condition.builder()
                  .srcActorName("Hour")
                  .op(ConditionOperator.EQUALS)
                  .varState(i)
                  .build()))
          .targetActorName(targetActorName)
          .targetActorState(1)
          .build();

      RuleEngineMatchSummary summary = engine.matchAll(rule, data);
      results.add(new RuleResult(rule, summary));
    }

    for (int i = 0; i < 60; i++) {
      Rule rule = Rule.builder()
          .conditions(Lists.newArrayList(
              Condition.builder()
                  .srcActorName("Minute")
                  .op(ConditionOperator.EQUALS)
                  .varState(i)
                  .build()))
          .targetActorName(targetActorName)
          .targetActorState(1)
          .build();

      RuleEngineMatchSummary summary = engine.matchAll(rule, data);
      results.add(new RuleResult(rule, summary));
    }

    for (int i = 0; i < 24; i++) {
      for (int j = 0; j < 60; j++) {
        Rule rule = Rule.builder()
            .conditions(Lists.newArrayList(
                Condition.builder()
                    .srcActorName("Hour")
                    .op(ConditionOperator.EQUALS)
                    .varState(i)
                    .exprOp(BoolExprOperator.AND)
                    .build(),
                Condition.builder()
                    .srcActorName("Minute")
                    .op(ConditionOperator.EQUALS)
                    .varState(j)
                    .exprOp(BoolExprOperator.AND)
                    .build()))
            .targetActorName(targetActorName)
            .targetActorState(1)
            .build();

        RuleEngineMatchSummary summary = engine.matchAll(rule, data);
        results.add(new RuleResult(rule, summary));
      }
    }
  }

  public void scenario4_2(List<RuleResult> results, String targetActorName) {
    for (int i = 0; i < 24; i++) {
      Rule rule = Rule.builder()
          .conditions(Lists.newArrayList(
              Condition.builder()
                  .srcActorName("Hour")
                  .op(ConditionOperator.EQUALS)
                  .varState(i)
                  .build()))
          .targetActorName(targetActorName)
          .targetActorState(1)
          .build();
      RuleEngineMatchSummary summary = engine.matchAll(rule, data);
      results.add(new RuleResult(rule, summary));
    }

    for (int i = 0; i < 60; i++) {
      Rule rule = Rule.builder()
          .conditions(Lists.newArrayList(
              Condition.builder()
                  .srcActorName("Minute")
                  .op(ConditionOperator.EQUALS)
                  .varState(i)
                  .build()))
          .targetActorName(targetActorName)
          .targetActorState(1)
          .build();

      RuleEngineMatchSummary summary = engine.matchAll(rule, data);
      results.add(new RuleResult(rule, summary));
    }

    for (int i = 0; i < 24; i++) {
      for (int j = 0; j < 60; j++) {
        Rule rule = Rule.builder()
            .conditions(Lists.newArrayList(
                Condition.builder()
                    .srcActorName("Hour")
                    .op(ConditionOperator.EQUALS)
                    .varState(i)
                    .exprOp(BoolExprOperator.AND)
                    .build(),
                Condition.builder()
                    .srcActorName("Minute")
                    .op(ConditionOperator.EQUALS)
                    .varState(j)
                    .exprOp(BoolExprOperator.AND)
                    .build()))
            .targetActorName(targetActorName)
            .targetActorState(1)
            .build();

        RuleEngineMatchSummary summary = engine.matchAll(rule, data);
        results.add(new RuleResult(rule, summary));
      }
    }
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
