package test.junit.org.optimizationBenchmarking.evaluator.impl.all.ecdf;

import java.util.Random;

import org.junit.Assert;
import org.optimizationBenchmarking.evaluator.attributes.functions.DimensionTransformationParser;
import org.optimizationBenchmarking.evaluator.attributes.functions.FunctionAttribute;
import org.optimizationBenchmarking.evaluator.attributes.functions.NamedParameterTransformationParser;
import org.optimizationBenchmarking.evaluator.attributes.functions.ecdf.ECDF;
import org.optimizationBenchmarking.evaluator.data.spec.IDataPoint;
import org.optimizationBenchmarking.evaluator.data.spec.IDimension;
import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;
import org.optimizationBenchmarking.evaluator.data.spec.IRun;
import org.optimizationBenchmarking.evaluator.evaluation.impl.all.ecdf.AllECDF;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.comparison.ComparisonParser;
import org.optimizationBenchmarking.utils.comparison.EComparison;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.math.MathUtils;
import org.optimizationBenchmarking.utils.math.statistics.aggregate.QuantileAggregate;
import org.optimizationBenchmarking.utils.math.statistics.parameters.ArithmeticMean;
import org.optimizationBenchmarking.utils.math.statistics.parameters.InterQuartileRange;
import org.optimizationBenchmarking.utils.math.statistics.parameters.Maximum;
import org.optimizationBenchmarking.utils.math.statistics.parameters.Median;
import org.optimizationBenchmarking.utils.math.statistics.parameters.Minimum;
import org.optimizationBenchmarking.utils.math.statistics.parameters.StandardDeviation;
import org.optimizationBenchmarking.utils.math.statistics.parameters.StatisticalParameter;
import org.optimizationBenchmarking.utils.math.statistics.parameters.StatisticalParameterParser;
import org.optimizationBenchmarking.utils.math.statistics.parameters.Variance;
import org.optimizationBenchmarking.utils.parsers.AnyNumberParser;

import shared.junit.org.optimizationBenchmarking.evaluator.evaluation.ExperimentSetModuleTest;

/** Test the ECDF */
public class AllECDFTest extends ExperimentSetModuleTest {

  /** test the ECDF with linear x axis */
  public AllECDFTest() {
    super(AllECDF.getInstance());
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
    final EComparison[] comparisons;
    final QuantileAggregate upper, lower;
    final NamedParameterTransformationParser nptParser;
    final Double boundDouble;
    double lowerBound, upperBound, bound;
    ArrayListView<? extends IDataPoint> runData;

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

    config.get(ECDF.AGGREGATE_PARAM,
        StatisticalParameterParser.getInstance(),
        AllECDFTest._getAggregate(random));

    comparisons = EComparison.values();
    config.get(ECDF.CRITERION_PARAM, ComparisonParser.getInstance(),
        comparisons[random.nextInt(comparisons.length)]);

    upper = new QuantileAggregate(0.5d);
    lower = new QuantileAggregate(0.5d);
    for (final IExperiment experiment : data.getData()) {
      for (final IInstanceRuns instanceRuns : experiment.getData()) {
        for (final IRun run : instanceRuns.getData()) {
          runData = run.getData();
          lower.append(runData.get(0).get(dimY.getIndex()));
          upper.append(
              runData.get(runData.size() - 1).get(dimY.getIndex()));
        }
      }
    }

    lowerBound = lower.doubleValue();
    upperBound = upper.doubleValue();

    setBound: {
      if (MathUtils.isFinite(lowerBound)) {
        if (MathUtils.isFinite(upperBound)) {
          bound = ((0.5d * lowerBound) + (0.5d * upperBound));
          if (MathUtils.isFinite(bound)) {
            boundDouble = Double.valueOf(bound);
            break setBound;
          }
          bound = Math.nextUp(Math.min(upperBound, lowerBound));
          if (MathUtils.isFinite(bound)) {
            boundDouble = Double.valueOf(bound);
            break setBound;
          }
          bound = Math.nextAfter(Math.max(upperBound, lowerBound),
              Double.NEGATIVE_INFINITY);
          if (MathUtils.isFinite(bound)) {
            boundDouble = Double.valueOf(bound);
            break setBound;
          }
          boundDouble = Double
              .valueOf(random.nextBoolean() ? lowerBound : upperBound);
          break setBound;
        }
        boundDouble = Double.valueOf(lowerBound);
        break setBound;
      }

      if (MathUtils.isFinite(upperBound)) {
        boundDouble = Double.valueOf(upperBound);
        break setBound;
      }
      boundDouble = Double.valueOf(0d);
    }

    config.get(ECDF.GOAL_PARAM, AnyNumberParser.INSTANCE, boundDouble);

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
