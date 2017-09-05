package vici.ai.rule;

import vici.ai.engine.DataContext;

public interface Condition {

  BoolExprOperator getExprOp();

  String getSrcActorName();

  String getShortForm();

  boolean eval(DataContext ctx);

  String toString(DataContext data);

}
