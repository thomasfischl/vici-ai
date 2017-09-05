package vici.ai.rule;

import lombok.Builder;
import lombok.Data;
import vici.ai.engine.DataContext;

@Builder
@Data
public class TimeOffsetCondition implements Condition {

  private final String srcActorName;

  private final int startOffset;

  private final int offsetLength;

  private final BoolExprOperator exprOp;

  @Override
  public String toString() {
    return "offset(" + srcActorName + "," + startOffset + "," + offsetLength + ")";
  }

  @Override
  public String toString(DataContext data) {
    return "offset(" + srcActorName + "," + startOffset + "," + offsetLength + ")" + "[" + data.get(srcActorName) + "]";
  }

  @Override
  public String getShortForm() {
    return "o|" + srcActorName + "|" + startOffset + "|" + offsetLength;
  }

  @Override
  public boolean eval(DataContext ctx) {
    int offset;

    for (int i = 0; i < offsetLength; i++) {
      offset = Math.min(0, startOffset + i);
      int currActorState = ctx.get(srcActorName + "_" + offset);

      if (currActorState == 1) {
        return true;
      }
    }
    return false;
  }

}
