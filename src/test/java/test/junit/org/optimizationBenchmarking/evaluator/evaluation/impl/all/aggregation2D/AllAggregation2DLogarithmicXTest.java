package test.junit.org.optimizationBenchmarking.evaluator.evaluation.impl.all.aggregation2D;

import org.optimizationBenchmarking.evaluator.data.spec.IDimension;

import shared.junit.org.optimizationBenchmarking.evaluator.evaluation.EvaluationModuleTest;

/** Test aggreation over 2 dimensions */
public class AllAggregation2DLogarithmicXTest extends
AllAggregation2DLinearTest {

  /** aggregate things overlogarithmic x */
  public AllAggregation2DLogarithmicXTest() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected String getXDimensionString(final IDimension xDimension) {
    return EvaluationModuleTest.getLogarithmicScaling(xDimension);
  }
}
