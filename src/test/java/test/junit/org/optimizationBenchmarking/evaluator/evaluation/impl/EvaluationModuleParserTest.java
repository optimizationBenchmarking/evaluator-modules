package test.junit.org.optimizationBenchmarking.evaluator.evaluation.impl;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.optimizationBenchmarking.evaluator.evaluation.impl.EvaluationModuleParser;
import org.optimizationBenchmarking.evaluator.evaluation.impl.all.aggregation2D.AllAggregation2D;
import org.optimizationBenchmarking.evaluator.evaluation.impl.all.ecdf.AllECDF;
import org.optimizationBenchmarking.evaluator.evaluation.impl.description.instances.InstanceInformation;
import org.optimizationBenchmarking.evaluator.evaluation.impl.single.experimentInfo.ExperimentInformation;
import org.optimizationBenchmarking.evaluator.evaluation.spec.IEvaluationModule;

import shared.junit.InstanceTest;

/** The test of the evaluation module parser */
public class EvaluationModuleParserTest
    extends InstanceTest<EvaluationModuleParser> {

  /** create the experiment information test */
  public EvaluationModuleParserTest() {
    super(null, EvaluationModuleParser.getInstance(), true, false);
  }

  /**
   * test for a particular module
   *
   * @param clazz
   *          the expected base class
   * @param names
   *          the module names
   */
  @Ignore
  private final void __testParse(
      final Class<? extends IEvaluationModule> clazz,
      final String... names) {
    final EvaluationModuleParser parser;
    final IEvaluationModule module;

    Assert.assertNotNull(clazz);
    Assert.assertNotNull(names);
    Assert.assertTrue(names.length > 0);

    parser = this.getInstance();
    Assert.assertNotNull(parser);

    module = parser.parseString(clazz.getCanonicalName());
    Assert.assertNotNull(module);
    Assert.assertTrue(clazz.isInstance(module));
    Assert.assertNotNull(module.getType());

    for (final String name : names) {
      Assert.assertEquals(module, parser.parseString(name));
    }
  }

  /** test for a particular module */
  @Test(timeout = 3600000)
  public void testAllAggregation2D() {
    this.__testParse(AllAggregation2D.class, //
        "aggregation2D.AllAggregation2D"); //$NON-NLS-1$
  }

  /** test for a particular module */
  @Test(timeout = 3600000)
  public void testAllECDF() {
    this.__testParse(AllECDF.class, //
        "ecdf.AllECDF"); //$NON-NLS-1$
  }

  /** test for a particular module */
  @Test(timeout = 3600000)
  public void testInstanceInformation() {
    this.__testParse(InstanceInformation.class, //
        "instances.InstanceInformation"); //$NON-NLS-1$
  }

  /** test for a particular module */
  @Test(timeout = 3600000)
  public void testExperimentExperimentInformation() {
    this.__testParse(ExperimentInformation.class, //
        "experimentInfo.ExperimentInformation"); //$NON-NLS-1$
  }
}
