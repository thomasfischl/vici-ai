package vici.ai.dataset;

import com.google.common.collect.Lists;

import vici.ai.rule.BoolExprOperator;
import vici.ai.rule.ConditionOperator;
import vici.ai.rule.IntervalCondition;
import vici.ai.rule.SimpleCondition;
import vici.ai.rule.StateRangeService;

public class ExceptableConditionExtractor {

  // private RuleEngine engine = new RuleEngine();

  private StateRangeService rangeService = new StateRangeService();

  public ConditionSet getConditions(DataSet set, String targetActor) {
    ConditionSet result = new ConditionSet();

    // add simple conditions
    for (String srcActor : rangeService.getSourceActorForOffsetCondition()) {
      for (int i = 0; i < 6; i++) {
        SimpleCondition condition = SimpleCondition.builder()
            .srcActorName(srcActor + "_" + i)
            .op(ConditionOperator.EQUALS)
            .varState(1)
            .exprOp(BoolExprOperator.AND)
            .build();

        // evalCondition(set, targetActor, result, condition);
        result.getActorConditions().add(condition);
        result.addCondition(condition);
      }
    }

    for (String srcActor : Lists.newArrayList("Hour", "Minute", "DayOfWeek")) {
      for (int state = rangeService.getLowestValue(srcActor), max = rangeService.getHighestValue(srcActor); state <= max; state++) {
        SimpleCondition condition = SimpleCondition.builder()
            .srcActorName(srcActor)
            .op(ConditionOperator.EQUALS)
            .varState(state)
            .exprOp(BoolExprOperator.AND)
            .build();

        // evalCondition(set, targetActor, result, condition);
        result.getTimeConditions().add(condition);
        result.addCondition(condition);
      }
    }

    // add interval conditions

    for (String srcActor : Lists.newArrayList("Hour", "Minute", "DayOfWeek")) {
      int max = rangeService.getHighestValue(srcActor);
      for (int i = rangeService.getLowestValue(srcActor); i <= max; i++) {
        for (int j = i + 1; j <= max; j++) {
          IntervalCondition condition = IntervalCondition.builder()
              .srcActorName(srcActor)
              .lowerBoundState(i)
              .upperBoundState(j)
              .exprOp(BoolExprOperator.AND)
              .build();

          // evalCondition(set, targetActor, result, condition);
          result.getTimeIntervalConditions().add(condition);
          result.addCondition(condition);
        }
      }
    }

    // int max = rangeService.getSourceActorForOffsetCondition().size() * 6 + 7 + 24 + 60;
    // System.out.println("Found ExceptableConditions: " + result.size());

    return result;
  }

  // private void evalCondition(DataSet set, String targetActor, ConditionSet result, Condition condition) {
  // Rule rule = Rule.builder()
  // .conditions(Lists.newArrayList(condition))
  // .targetActorName(targetActor)
  // .targetActorState(1)
  // .build();
  //
  // RuleEngineMatchSummary summary = engine.matchAll(rule, set.getData(), false);
  // if (summary.getCountMatchP() > 2) {
  // // System.out.println(rule + " => " + summary);
  // result.add(condition);
  // } else {
  // System.out.println("removed condition: " + rule + " => " + summary);
  //
  // }
  // result.add(condition);
  // }

}
