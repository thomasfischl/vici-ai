package vici.ai.rule;

import lombok.Builder;
import lombok.Data;
import vici.ai.engine.DataContext;

@Builder
@Data
public class IntervalCondition implements Condition {

  private final String srcActorName;

  private final int lowerBoundState;

  private final int upperBoundState;

  private final BoolExprOperator exprOp;

  @Override
  public String toString() {
    return lowerBoundState + " <= " + srcActorName + " <= " + upperBoundState;
  }

  @Override
  public String toString(DataContext data) {
    return lowerBoundState + " <= " + srcActorName + "[" + data.get(srcActorName) + "]" + " <= " + upperBoundState;
  }

  @Override
  public String getShortForm() {
    return srcActorName + "|" + lowerBoundState + "|" + upperBoundState;
  }

  @Override
  public boolean eval(DataContext ctx) {
    int currActorState = ctx.get(srcActorName);
    return lowerBoundState <= currActorState && currActorState <= upperBoundState;
  }

}
