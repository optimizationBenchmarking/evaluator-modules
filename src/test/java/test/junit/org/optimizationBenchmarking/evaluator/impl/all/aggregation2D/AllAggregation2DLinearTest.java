package test.junit.org.optimizationBenchmarking.evaluator.impl.all.aggregation2D;

import java.util.Random;

import org.junit.Assert;
import org.optimizationBenchmarking.evaluator.attributes.functions.DimensionTransformationParser;
import org.optimizationBenchmarking.evaluator.attributes.functions.FunctionAttribute;
import org.optimizationBenchmarking.evaluator.attributes.functions.NamedParameterTransformationParser;
import org.optimizationBenchmarking.evaluator.attributes.functions.aggregation2D.Aggregation2D;
import org.optimizationBenchmarking.evaluator.data.spec.IDimension;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.evaluation.impl.all.aggregation2D.AllAggregation2D;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.math.statistics.parameters.ArithmeticMean;
import org.optimizationBenchmarking.utils.math.statistics.parameters.InterQuartileRange;
import org.optimizationBenchmarking.utils.math.statistics.parameters.Maximum;
import org.optimizationBenchmarking.utils.math.statistics.parameters.Median;
import org.optimizationBenchmarking.utils.math.statistics.parameters.Minimum;
import org.optimizationBenchmarking.utils.math.statistics.parameters.StandardDeviation;
import org.optimizationBenchmarking.utils.math.statistics.parameters.StatisticalParameter;
import org.optimizationBenchmarking.utils.math.statistics.parameters.StatisticalParameterParser;
import org.optimizationBenchmarking.utils.math.statistics.parameters.Variance;

import shared.junit.org.optimizationBenchmarking.evaluator.evaluation.ExperimentSetModuleTest;

/** Test aggreation over 2 dimensions */
public class AllAggregation2DLinearTest extends ExperimentSetModuleTest {

  /** aggregate things linearly */
  public AllAggregation2DLinearTest() {
    super(AllAggregation2D.getInstance());
  }

  /**
   * Get the string of the x-dimension
   *
   * @param xDimension
   *          the x-dimension
   * @return the string
   */
  protected String getXDimensionString(final IDimension xDimension) {
    return xDimension.getName();
  }

  /**
   * Get the string of the y-dimension
   *
   * @param yDimension
   *          the y-dimension
   * @return the string
   */
  protected String getYInputDimensionString(final IDimension yDimension) {
    return yDimension.getName();
  }

  /**
   * get the output dimension string
   *
   * @return the transformation string
   */
  protected String getYOutputDimensionString() {
    return NamedParameterTransformationParser.DEFAULT_PARAMETER_NAME;
  }

  /** {@inheritDoc} */
  @Override
  protected Configuration getConfiguration(final IExperimentSet data) {
    final Configuration config;
    final Random random;
    final IDimension dimX, dimY;
    final ArrayListView<? extends IDimension> dimList;
    final int dimCount;
    final DimensionTransformationParser dimParser;
    final NamedParameterTransformationParser nptParser;

    config = super.getConfiguration(data);

    random = new Random();
    dimList = data.getDimensions().getData();
    Assert.assertNotNull(dimList);
    dimCount = dimList.size();
    Assert.assertTrue(dimCount > 0);
    dimX = dimList.get(random.nextInt(dimCount));
    dimY = dimList.get(random.nextInt(dimCount));

    dimParser = new DimensionTransformationParser(data);
    config.get(FunctionAttribute.X_AXIS_PARAM, dimParser,
        dimParser.parseString(this.getXDimensionString(dimX)));
    config.get(FunctionAttribute.Y_INPUT_AXIS_PARAM, dimParser,
        dimParser.parseString(this.getYInputDimensionString(dimY)));
    nptParser = new NamedParameterTransformationParser(data);
    config.get(FunctionAttribute.Y_AXIS_OUTPUT_PARAM, nptParser,
        nptParser.parseString(this.getYOutputDimensionString()));

    config.get(Aggregation2D.PRIMARY_AGGREGATE_PARAM,
        StatisticalParameterParser.getInstance(),
        AllAggregation2DLinearTest._getAggregate(random));
    config.get(Aggregation2D.SECONDARY_AGGREGATE_PARAM,
        StatisticalParameterParser.getInstance(),
        AllAggregation2DLinearTest._getAggregate(random));

    return config;
  }

  /**
   * get a random aggregate
   *
   * @param random
   *          the random number generator
   * @return the name of the random aggregate
   */
  static final StatisticalParameter _getAggregate(final Random random) {
    switch (random.nextInt(7)) {
      case 0: {
        return StandardDeviation.INSTANCE;
      }
      case 1: {
        return Median.INSTANCE;
      }
      case 2: {
        return InterQuartileRange.INSTANCE;
      }
      case 3: {
        return Maximum.INSTANCE;
      }
      case 4: {
        return Minimum.INSTANCE;
      }
      case 5: {
        return Variance.INSTANCE;
      }
      default: {
        return ArithmeticMean.INSTANCE;
      }
    }
  }
}
