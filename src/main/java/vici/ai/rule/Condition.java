package vici.ai.rule;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Condition {

  private final String srcActorName;

  private final ConditionOperator op;

  private final int varState;
  
  private final BoolExprOperator exprOp;

  @Override
  public String toString() {
    if (op == ConditionOperator.EQUALS) {
      return srcActorName + " == " + varState;
    } else if (op == ConditionOperator.EQUALS) {
      return srcActorName + " != " + varState;
    } else {
      throw new IllegalStateException("Invalid op");
    }
  }

}
