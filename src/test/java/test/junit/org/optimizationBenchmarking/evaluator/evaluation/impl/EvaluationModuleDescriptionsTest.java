package test.junit.org.optimizationBenchmarking.evaluator.evaluation.impl;

import org.junit.Assert;
import org.junit.Test;
import org.optimizationBenchmarking.evaluator.evaluation.definition.data.ModuleDescription;
import org.optimizationBenchmarking.evaluator.evaluation.definition.data.ModuleDescriptions;
import org.optimizationBenchmarking.evaluator.evaluation.impl.EvaluationModuleDescriptions;

import shared.junit.org.optimizationBenchmarking.utils.collections.ListTest;

/** the evaluation module descriptions test */
public class EvaluationModuleDescriptionsTest
    extends ListTest<ModuleDescription, ModuleDescriptions> {

  /** create */
  public EvaluationModuleDescriptionsTest() {
    super(null, EvaluationModuleDescriptions.getDescriptions(), true,
        false);
  }

  /** ensure that the module descriptions list is not empty */
  @Test(timeout = 3600000)
  public void testNotEmpty() {
    Assert.assertFalse(this.getInstance().isEmpty());
    Assert.assertTrue(this.getInstance().size() > 0);
  }
}
