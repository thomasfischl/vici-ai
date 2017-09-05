package vici.ai.rule;

import lombok.Builder;
import lombok.Data;
import vici.ai.engine.DataContext;

@Builder
@Data
public class SimpleCondition implements Condition {

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

  @Override
  public String toString(DataContext data) {
    if (op == ConditionOperator.EQUALS) {
      return srcActorName + " == " + varState + "[" + data.get(srcActorName) + "]";
    } else if (op == ConditionOperator.EQUALS) {
      return srcActorName + " != " + varState + "[" + data.get(srcActorName) + "]";
    } else {
      throw new IllegalStateException("Invalid op");
    }

  }

  @Override
  public String getShortForm() {
    return srcActorName + "|" + op.ordinal() + "|" + varState;
  }

  @Override
  public boolean eval(DataContext ctx) {
    int currActorState = ctx.get(srcActorName);
    if (op == ConditionOperator.EQUALS) {
      return currActorState == varState;
    } else if (op == ConditionOperator.EQUALS) {
      return currActorState != varState;
    } else {
      throw new IllegalStateException("Invalid Operator '" + op + "'");
    }
  }

}
