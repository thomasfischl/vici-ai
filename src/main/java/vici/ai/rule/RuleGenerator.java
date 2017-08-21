package vici.ai.rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RuleGenerator {

  private static Random rand = new Random();

  private final StateRangeService rangeService;

  public RuleGenerator(StateRangeService rangeService) {
    this.rangeService = rangeService;
  }

  public Rule random(List<String> targetActors, List<String> sourceActors, int targetActorState) {

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

      String srcActorName = null;
      while (srcActorName == null) {
        srcActorName = sourceActors.get(rand.nextInt(sourceActors.size()));
        if (srcActorName.equals(targetActor)) {
          srcActorName = null;
        }
      }

      conditions.add(Condition.builder()
          .srcActorName(srcActorName)
          .op(ConditionOperator.EQUALS)
          .varState(rangeService.getRandomState(srcActorName))
          .exprOp(BoolExprOperator.AND)
          .build());
    }

    return Rule.builder()
        .conditions(conditions)
        .targetActorName(targetActor)
        .targetActorState(targetActorState)
        .build();

  }

  public Rule random(String targetActor, int targetActorState, List<String> sourceActors) {

    List<Condition> conditions = new ArrayList<>();

    for (int i = 0, max = rand.nextInt(1) + 1; i < max; i++) {

      String srcActorName = null;
      while (srcActorName == null) {
        srcActorName = sourceActors.get(rand.nextInt(sourceActors.size()));
        if (srcActorName.equals(targetActor + "_0")) {
          srcActorName = null;
        }
      }

      conditions.add(Condition.builder()
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
