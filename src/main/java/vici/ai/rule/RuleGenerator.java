package vici.ai.rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import vici.ai.dataset.ConditionSet;

public class RuleGenerator {

  private static Random rand = new Random();

  private final StateRangeService rangeService;

  private final ConditionSet exceptableConditions;

  public RuleGenerator(StateRangeService rangeService, ConditionSet conditions) {
    this.rangeService = rangeService;
    this.exceptableConditions = conditions;
  }

  public Rule randomRule(List<String> targetActors, List<String> sourceActors, int targetActorState) {

    String targetActor;
    if (targetActors.size() == 1) {
      targetActor = targetActors.get(0);
    } else {
      targetActor = targetActors.get(rand.nextInt(targetActors.size() - 1));
    }

    List<Condition> conditions = new ArrayList<>();

    int max = 1;
    if (rand.nextDouble() > 0.8) {
      max = rand.nextInt(3) + 1;
    }

    for (int i = 0; i < max; i++) {
      Condition c = generateRandomCondition(conditions);
      if (c != null) {
        conditions.add(c);
      }
    }

    return Rule.builder()
        .conditions(conditions)
        .targetActorName(targetActor)
        .targetActorState(targetActorState)
        .build();

  }

  public String randomSourceActor(List<String> sourceActors, String targetActor) {
    String srcActorName = null;
    while (srcActorName == null) {
      srcActorName = sourceActors.get(rand.nextInt(sourceActors.size()));
      if (srcActorName.equals(targetActor)) {
        srcActorName = null;
      }
    }
    return srcActorName;
  }

  public Condition generateRandomCondition(List<Condition> conditions) {
    return exceptableConditions.randomCondition(conditions.stream().map(o -> o.getSrcActorName()).collect(Collectors.toList()));
  }

  // public Condition generateRandomCondition(List<String> sourceActors, String targetActor) {
  // if (rand.nextDouble() > 0.9) {
  //
  // return randomTimeOffsetCondition(targetActor);
  // }
  //
  // String srcActorName = randomSourceActor(sourceActors, targetActor);
  //
  // if (rangeService.getStateCount(srcActorName) > 2 && rand.nextDouble() > 0.6) {
  // return randomIntervalCondition(srcActorName);
  // } else {
  // return randomSimpleCondition(srcActorName);
  // }
  // }

  private Condition randomTimeOffsetCondition(String targetActor) {

    String srcActor = rangeService.getRandomSourceActorForOffsetCondition();
    while (targetActor.startsWith(srcActor)) {
      srcActor = rangeService.getRandomSourceActorForOffsetCondition();
    }

    int startOffset = rand.nextInt(3);
    int offsetLength = rand.nextInt(6 - startOffset);

    return TimeOffsetCondition.builder()
        .srcActorName(srcActor)
        .startOffset(startOffset)
        .offsetLength(offsetLength)
        .exprOp(BoolExprOperator.AND)
        .build();
  }

  private Condition randomIntervalCondition(String srcActorName) {
    int lowerBoundState = rangeService.getRandomState(srcActorName);
    int upperBoundState = rangeService.getRandomState(srcActorName);

    if (rand.nextBoolean()) {
      upperBoundState = lowerBoundState + 4;
    }

    if (lowerBoundState > upperBoundState) {
      int temp = lowerBoundState;
      lowerBoundState = upperBoundState;
      upperBoundState = temp;
    }

    return IntervalCondition.builder()
        .srcActorName(srcActorName)
        .lowerBoundState(lowerBoundState)
        .upperBoundState(upperBoundState)
        .exprOp(BoolExprOperator.AND)
        .build();
  }

  private Condition randomSimpleCondition(String srcActorName) {
    return SimpleCondition.builder()
        .srcActorName(srcActorName)
        .op(ConditionOperator.EQUALS)
        .varState(rangeService.getRandomState(srcActorName))
        .exprOp(BoolExprOperator.AND)
        .build();
  }

  public Rule randomRule(String targetActor, int targetActorState, List<String> sourceActors) {

    List<Condition> conditions = new ArrayList<>();

    for (int i = 0, max = rand.nextInt(1) + 1; i < max; i++) {

      String srcActorName = null;
      while (srcActorName == null) {
        srcActorName = sourceActors.get(rand.nextInt(sourceActors.size()));
        if (srcActorName.equals(targetActor + "_0")) {
          srcActorName = null;
        }
      }

      conditions.add(SimpleCondition.builder()
          .srcActorName(srcActorName)
          .op(ConditionOperator.EQUALS)
          .varState(rangeService.getRandomState(srcActorName))
          .exprOp(BoolExprOperator.AND)
          .build());
    }

    return Rule.builder()
        .conditions(conditions)
        .targetActorName(targetActor + "_0")
        .targetActorState(targetActorState)
        .build();

  }

}
