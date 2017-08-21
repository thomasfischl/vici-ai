package vici.ai.ga;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import vici.ai.rule.BoolExprOperator;
import vici.ai.rule.Condition;
import vici.ai.rule.ConditionOperator;
import vici.ai.rule.Rule;
import vici.ai.rule.StateRangeService;

public class RuleMutator {

  private final List<String> srcActorNames;

  private final Random rand = new Random();

  private final StateRangeService rangeService;

  public RuleMutator(List<String> srcActorNames, StateRangeService rangeService) {
    this.srcActorNames = srcActorNames;
    this.rangeService = rangeService;
  }

  public Individium<Rule> mutate(Individium<Rule> i1) {

    Rule newRule = Rule.builder()
        .conditions(Lists.newArrayList(i1.getValue().getConditions()))
        .targetActorName(i1.getValue().getTargetActorName())
        .targetActorState(i1.getValue().getTargetActorState())
        .build();

    switch (rand.nextInt(10)) {
    case 1:
    case 2:
      removeOneRandomCondition(newRule);
      break;
    case 3:
    case 4:
      addRandomCondition(newRule);
      break;
    case 5:
    case 6:
    case 7:
      randomizeSrcActor(newRule);
      break;
    case 8:
      randomizeMultiState(newRule);
    case 9:
    case 10:
    default:
      randomizeState(newRule);
    }

    return new Individium<Rule>(newRule);
  }

  private void randomizeState(Rule newRule) {
    int pos = 0;
    if (newRule.getConditions().size() > 1) {
      pos = rand.nextInt(newRule.getConditions().size() - 1);
    }

    randomizeState(newRule, pos);
  }

  private void randomizeMultiState(Rule newRule) {
    for (int i = 0, max = rand.nextInt(3) + 1; i < max; i++) {
      int pos = 0;
      if (newRule.getConditions().size() > 1) {
        pos = rand.nextInt(newRule.getConditions().size() - 1);
      }

      randomizeState(newRule, pos);
    }
  }

  private void randomizeState(Rule newRule, int pos) {
    Condition c = newRule.getConditions().remove(pos);

    newRule.getConditions().add(pos,
        Condition.builder()
            .exprOp(c.getExprOp())
            .op(c.getOp())
            .srcActorName(c.getSrcActorName())
            .varState(rangeService.getRandomState(c.getSrcActorName()))
            .build());
  }

  private void randomizeSrcActor(Rule newRule) {
    int pos = 0;
    if (newRule.getConditions().size() > 1) {
      pos = rand.nextInt(newRule.getConditions().size() - 1);
    }

    Condition c = newRule.getConditions().remove(pos);

    String srcActorName = srcActorNames.get(rand.nextInt(srcActorNames.size()));

    while (newRule.getTargetActorName().equals(srcActorName) || c.getSrcActorName().equals(srcActorName)) {
      srcActorName = srcActorNames.get(rand.nextInt(srcActorNames.size()));
    }

    newRule.getConditions().add(pos,
        Condition.builder()
            .exprOp(c.getExprOp())
            .op(c.getOp())
            .srcActorName(srcActorName)
            .varState(rangeService.getRandomState(srcActorName))
            .build());
  }

  private void removeOneRandomCondition(Rule newRule) {
    if (newRule.getConditions().size() <= 1) {
      return;
    }

    newRule.getConditions().remove(rand.nextInt(newRule.getConditions().size() - 1));
  }

  private void addRandomCondition(Rule newRule) {

    String srcActorName = srcActorNames.get(rand.nextInt(srcActorNames.size()));
    while (srcActorName.equals(newRule.getTargetActorName())) {
      srcActorName = srcActorNames.get(rand.nextInt(srcActorNames.size()));
    }

    newRule.getConditions().add(Condition.builder()
        .srcActorName(srcActorName)
        .op(ConditionOperator.EQUALS)
        .varState(rangeService.getRandomState(srcActorName))
        .exprOp(BoolExprOperator.AND)
        .build());

  }

  // private void cloneConditions(Rule newRule) {
  // List<Condition> newConditions = new ArrayList<>();
  //
  // for (Condition c : newRule.getConditions()) {
  // newConditions.add(Condition.builder()
  // .exprOp(c.getExprOp())
  // .op(c.getOp())
  // .srcActorName(c.getSrcActorName())
  // .varState(c.getVarState())
  // .build());
  // }
  //
  // newRule.setConditions(newConditions);
  // }

}
