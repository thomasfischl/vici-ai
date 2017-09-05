package vici.ai.ga;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import vici.ai.dataset.ConditionSet;
import vici.ai.rule.Condition;
import vici.ai.rule.IntervalCondition;
import vici.ai.rule.Rule;
import vici.ai.rule.RuleGenerator;
import vici.ai.rule.SimpleCondition;
import vici.ai.rule.StateRangeService;
import vici.ai.rule.TimeOffsetCondition;

public class RuleMutator {

  private final List<String> srcActorNames;

  private final Random rand = new Random();

  private final StateRangeService rangeService;

  private final RuleGenerator generator;

  public RuleMutator(List<String> srcActorNames, StateRangeService rangeService, ConditionSet conditions) {
    this.srcActorNames = srcActorNames;
    this.rangeService = rangeService;

    generator = new RuleGenerator(rangeService, conditions);
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
    case 3:
      removeOneRandomCondition(newRule);
      break;
    case 4:
      addRandomCondition(newRule);
      break;
    case 5:
    case 6:
    case 7:
      removeOneRandomCondition(newRule);
      addRandomCondition(newRule);
      // randomizeSrcActor(newRule);
      break;
    case 8:
    case 9:
      randomizeMultiState(newRule);
      break;
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
    Condition base = newRule.getConditions().remove(pos);

    if (base instanceof SimpleCondition) {
      randomizeSimpleConditionState(newRule, pos, (SimpleCondition) base);
    } else if (base instanceof IntervalCondition) {
      randomizeIntervalConditionState(newRule, pos, (IntervalCondition) base);
    } else if (base instanceof TimeOffsetCondition) {
      randomizeTimeOffsetCondition(newRule, pos, (TimeOffsetCondition) base);
    } else {
      throw new IllegalStateException("Implement me");
    }
  }

  private void randomizeTimeOffsetCondition(Rule newRule, int pos, TimeOffsetCondition c) {

    int startOffset = rand.nextInt(3);
    int offsetLength = rand.nextInt(6 - startOffset);

    newRule.getConditions().add(pos,
        TimeOffsetCondition.builder()
            .exprOp(c.getExprOp())
            .srcActorName(c.getSrcActorName())
            .startOffset(startOffset)
            .offsetLength(offsetLength)
            .build());
  }

  public void randomizeIntervalConditionState(Rule newRule, int pos, IntervalCondition c) {
    int lowerBoundState = c.getLowerBoundState();
    int upperBoundState = c.getUpperBoundState();

    switch (rand.nextInt(8)) {
    case 0:
      lowerBoundState += rand.nextInt(2) + 1;
      break;
    case 1:
      lowerBoundState -= rand.nextInt(2) + 1;
      break;
    case 2:
      upperBoundState += rand.nextInt(2) + 1;
      break;
    case 3:
      upperBoundState -= rand.nextInt(2) + 1;
      break;
    case 4:
    case 5:
      int val = rand.nextInt(2) + 1;
      lowerBoundState += val;
      upperBoundState += val;
      break;
    case 6:
    case 7:
    default:
      val = rand.nextInt(2) + 1;
      lowerBoundState -= val;
      upperBoundState -= val;
      break;
    }

    // System.out
    // .println("randomizeIntervalConditionState " + c.getLowerBoundState() + "-" + c.getUpperBoundState() + " => " + lowerBoundState + "-" + upperBoundState);

    newRule.getConditions().add(pos,
        IntervalCondition.builder()
            .exprOp(c.getExprOp())
            .srcActorName(c.getSrcActorName())
            .lowerBoundState(Math.max(lowerBoundState, 0))
            .upperBoundState(Math.min(upperBoundState, rangeService.getHighestValue(c.getSrcActorName())))
            .build());
  }

  private void randomizeSimpleConditionState(Rule newRule, int pos, SimpleCondition c) {
    newRule.getConditions().add(pos,
        SimpleCondition.builder()
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

    Condition base = newRule.getConditions().remove(pos);

    if (base instanceof SimpleCondition) {
      SimpleCondition c = (SimpleCondition) base;

      String srcActorName = srcActorNames.get(rand.nextInt(srcActorNames.size()));

      while (newRule.getTargetActorName().equals(srcActorName) || c.getSrcActorName().equals(srcActorName)) {
        srcActorName = srcActorNames.get(rand.nextInt(srcActorNames.size()));
      }

      newRule.getConditions().add(pos,
          SimpleCondition.builder()
              .exprOp(c.getExprOp())
              .op(c.getOp())
              .srcActorName(srcActorName)
              .varState(rangeService.getRandomState(srcActorName))
              .build());
    } else {
      // TODO implement me!!!
      newRule.getConditions().add(pos, base);
    }
  }

  private void removeOneRandomCondition(Rule newRule) {
    if (newRule.getConditions().size() <= 1) {
      return;
    }

    newRule.getConditions().remove(rand.nextInt(newRule.getConditions().size() - 1));
  }

  private void addRandomCondition(Rule newRule) {
    Condition c = generator.generateRandomCondition(newRule.getConditions());
    
    if(c==null){
      return;
    }
    
    int i = 0;
    while (isActorAlreadyExists(newRule, c)) {
      c = generator.generateRandomCondition(newRule.getConditions());

      if (i > 10) {
        return;
      }

      i++;
    }

    newRule.getConditions().add(c);
  }

  private boolean isActorAlreadyExists(Rule newRule, Condition c) {
    return newRule.getConditions().stream().filter(o -> o.getSrcActorName().equals(c.getSrcActorName())).findFirst().isPresent();
  }

}
