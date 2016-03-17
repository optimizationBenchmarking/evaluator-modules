package test.junit.org.optimizationBenchmarking.evaluator.evaluation.impl.all.ecdf;

import org.optimizationBenchmarking.evaluator.data.spec.IDimension;

/** Test the ECDF */
public class AllECDFTestSqrtXTest extends AllECDFTest {

  /** create ecdf test with sqrt X axis */
  public AllECDFTestSqrtXTest() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  protected String getXDimensionString(final IDimension xDimension) {
    return ("sqrt(|" + xDimension.getName() + "|+1)"); //$NON-NLS-1$//$NON-NLS-2$
  }
}
