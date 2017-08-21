package vici.ai.dataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Value;
import vici.ai.engine.DataContext;

@Value
public class DataSet {

  private final List<DataContext> data = new ArrayList<>();

  private final Map<String, String> deviceNameMapping = new HashMap<>();

}
