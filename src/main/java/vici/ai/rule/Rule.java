package vici.ai.rule;

import java.util.Collections;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Rule {

  private List<Condition> conditions;

  private String targetActorName;

  private int targetActorState;

  public Rule(List<Condition> conditions, String targetActorName, int targetActorState) {
    this.conditions = conditions;
    this.targetActorName = targetActorName;
    this.targetActorState = targetActorState;

    Collections.sort(conditions, (o1, o2) -> o1.getSrcActorName().compareTo(o2.getSrcActorName()));
  }

  public List<Condition> getConditions() {
    Collections.sort(conditions, (o1, o2) -> o1.getSrcActorName().compareTo(o2.getSrcActorName()));
    return conditions;
  }

  public String getShortForm() {
    String conditionStr = "";

    List<Condition> conditions = getConditions();
    for (int i = 0; i < conditions.size(); i++) {
      if (i != 0) {
        conditionStr += conditions.get(i).getExprOp();
      }
      conditionStr += conditions.get(i).getSrcActorName() + "|" + conditions.get(i).getOp().ordinal() + "|" + conditions.get(i).getVarState();
    }

    return conditionStr + ">" + targetActorName + "=" + targetActorState;
  }

  @Override
  public String toString() {

    String conditionStr = "";

    for (int i = 0; i < conditions.size(); i++) {
      if (i == 0) {
        conditionStr += conditions.get(i) + " ";
      } else {
        conditionStr += conditions.get(i).getExprOp() + " " + conditions.get(i) + " ";
      }
    }

    return "IF (" + conditionStr + ") THEN " + targetActorName + " == " + targetActorState;
  }

}
