package test.junit.org.optimizationBenchmarking.evaluator.evaluation.impl;

import org.junit.Assert;
import org.junit.Test;
import org.optimizationBenchmarking.evaluator.attributes.functions.ecdf.ECDF;
import org.optimizationBenchmarking.evaluator.evaluation.impl.EvaluationModuleParser;
import org.optimizationBenchmarking.evaluator.evaluation.impl.all.aggregation2D.AllAggregation2D;
import org.optimizationBenchmarking.evaluator.evaluation.impl.description.instances.InstanceInformation;
import org.optimizationBenchmarking.evaluator.evaluation.impl.single.experimentInfo.ExperimentInformation;

import shared.junit.InstanceTest;

/** The test of the evaluation module parser */
public class EvaluationModuleParserTest
    extends InstanceTest<EvaluationModuleParser> {

  /** create the experiment information test */
  public EvaluationModuleParserTest() {
    super(null, EvaluationModuleParser.getInstance(), true, false);
  }

  /** test for a particular module */
  @Test(timeout = 3600000)
  public void testAllAggregation2D() {
    final EvaluationModuleParser parser;

    parser = this.getInstance();
    Assert.assertNotNull(parser);

    Assert.assertNotNull(
        parser.parseString("aggregation2D.AllAggregation2D")); //$NON-NLS-1$
    Assert.assertNotNull(
        parser.parseString(AllAggregation2D.class.getCanonicalName()));
  }

  /** test for a particular module */
  @Test(timeout = 3600000)
  public void testAllECDF() {
    final EvaluationModuleParser parser;

    parser = this.getInstance();
    Assert.assertNotNull(parser);

    Assert.assertNotNull(parser.parseString("ecdf.AllECDF")); //$NON-NLS-1$
    Assert
        .assertNotNull(parser.parseString(ECDF.class.getCanonicalName()));
  }

  /** test for a particular module */
  @Test(timeout = 3600000)
  public void testInstanceInformation() {
    final EvaluationModuleParser parser;

    parser = this.getInstance();
    Assert.assertNotNull(parser);

    Assert.assertNotNull(
        parser.parseString("instances.InstanceInformation")); //$NON-NLS-1$
    Assert.assertNotNull(
        parser.parseString(InstanceInformation.class.getCanonicalName()));
  }

  /** test for a particular module */
  @Test(timeout = 3600000)
  public void testExperimentExperimentInformation() {
    final EvaluationModuleParser parser;

    parser = this.getInstance();
    Assert.assertNotNull(parser);

    Assert.assertNotNull(
        parser.parseString("experimentInfo.ExperimentInformation")); //$NON-NLS-1$
    Assert.assertNotNull(parser
        .parseString(ExperimentInformation.class.getCanonicalName()));
  }
}
