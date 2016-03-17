package test.junit.org.optimizationBenchmarking.evaluator.evaluation.impl.all.aggregation2D;

import org.optimizationBenchmarking.evaluator.data.spec.IDimension;

/** Test aggreation over 2 dimensions */
public class AllAggregation2DLogarithmicXTest
    extends AllAggregation2DLinearTest {

  /** aggregate things overlogarithmic x */
  public AllAggregation2DLogarithmicXTest() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected String getXDimensionString(final IDimension xDimension) {
    return ("ln(|" + xDimension.getName() + "|+1)"); //$NON-NLS-1$//$NON-NLS-2$
  }
}
