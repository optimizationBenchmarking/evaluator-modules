package org.optimizationBenchmarking.evaluator.evaluation.impl.all.ecdf;

import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.attributes.functions.ecdf.ECDF;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.evaluation.impl.all.function.FunctionJob;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.math.statistics.aggregate.ScalarAggregate;

/** A job of the ECDF module. */
final class _AllECDFJob extends FunctionJob {

  /**
   * Create the ECDF job
   *
   * @param data
   *          the data
   * @param logger
   *          the logger
   * @param config
   *          the configuration
   */
  _AllECDFJob(final IExperimentSet data, final Configuration config,
      final Logger logger) {
    super(data, ECDF.create(data, config), config, logger);
  }

  /** {@inheritDoc} */
  @Override
  protected final ScalarAggregate getYAxisMinimumAggregate() {
    return null;
  }

  /** {@inheritDoc} */
  @Override
  protected final double getYAxisMinimumValue() {
    return 0d;
  }

  /** {@inheritDoc} */
  @Override
  protected final ScalarAggregate getYAxisMaximumAggregate() {
    if (this.getRankingStrategy() == null) {
      return null;
    }
    return super.getYAxisMaximumAggregate();
  }

  /** {@inheritDoc} */
  @Override
  protected final double getYAxisMaximumValue() {
    if (this.getRankingStrategy() == null) {
      return 1d;
    }
    return super.getYAxisMaximumValue();
  }
}
