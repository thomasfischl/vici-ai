package vici.ai.engine;

import java.util.List;

import vici.ai.rule.BoolExprOperator;
import vici.ai.rule.Condition;
import vici.ai.rule.ConditionOperator;
import vici.ai.rule.Rule;

public class RuleEngine {

  public boolean eval(Rule rule, DataContext ctx) {
    Boolean result = null;

    for (Condition condition : rule.getConditions()) {

      int currActorState = ctx.get(condition.getSrcActorName());

      boolean matchCondition;
      if (condition.getOp() == ConditionOperator.EQUALS) {
        matchCondition = currActorState == condition.getVarState();
      } else if (condition.getOp() == ConditionOperator.EQUALS) {
        matchCondition = currActorState != condition.getVarState();
      } else {
        throw new IllegalStateException("Invalid Operator '" + condition.getOp() + "'");
      }

      if (result == null) {
        result = matchCondition;
      } else {

        if (condition.getExprOp() == BoolExprOperator.AND) {
          result = result && matchCondition;
        } else if (condition.getExprOp() == BoolExprOperator.OR) {
          result = result || matchCondition;
        } else {
          throw new IllegalStateException("Invalid Operator '" + condition.getExprOp() + "'");
        }

      }

    }

    if (result == null) {
      throw new IllegalStateException("No result: " + rule);
    }

    return result;
  }

  public RuleEngineResult match(Rule rule, DataContext ctx) {
    boolean match = eval(rule, ctx);

    if (match) {
      return rule.getTargetActorState() == ctx.get(rule.getTargetActorName()) ? RuleEngineResult.MATCH_POSITIVE : RuleEngineResult.FALSE_POSITIVE;
    } else {
      return rule.getTargetActorState() != ctx.get(rule.getTargetActorName()) ? RuleEngineResult.MATCH_NEGATIVE : RuleEngineResult.FALSE_NEGATIVE;
    }
  }

  public RuleEngineMatchSummary matchAll(Rule rule, List<DataContext> data) {
    int countMatchP = 0;
    int countMatchN = 0;
    int countFN = 0;
    int countFP = 0;

    for (DataContext ctx : data) {
      RuleEngineResult result = match(rule, ctx);
      switch (result) {
      case FALSE_NEGATIVE:
        countFN++;
        break;
      case FALSE_POSITIVE:
        countFP++;
        break;
      case MATCH_NEGATIVE:
        countMatchN++;
        break;
      case MATCH_POSITIVE:
        countMatchP++;
        break;
      }
    }

    return new RuleEngineMatchSummary(countMatchP, countMatchN, countFN, countFP, rule.getConditions().size());
  }

}
