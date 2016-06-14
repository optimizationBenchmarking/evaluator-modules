package org.optimizationBenchmarking.evaluator.evaluation.impl.all.aggregation2D;

import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.evaluation.impl.abstr.ExperimentSetModule;
import org.optimizationBenchmarking.evaluator.evaluation.spec.EModuleType;
import org.optimizationBenchmarking.utils.config.Configuration;

/**
 * The Aggregation2D painter. This module paints the Aggregation2D for
 * different experiments into one diagram.
 */
public final class AllAggregation2D extends ExperimentSetModule {

  /** the aggregation parameter */
  public static final String PARAM_AGGREGATION = "aggregation"; //$NON-NLS-1$

  /** create the instance information tool */
  AllAggregation2D() {
    super(EModuleType.BODY);
  }

  /** {@inheritDoc} */
  @Override
  public final _AllAggregation2DJob createJob(final IExperimentSet data,
      final Configuration config, final Logger logger) {
    return new _AllAggregation2DJob(data, config, logger);
  }

  /**
   * Get the globally shared instance of the aggregation 2D module
   *
   * @return the globally shared instance of the aggregation 2D module
   */
  public static final AllAggregation2D getInstance() {
    return __Aggregation2DLoader.INSTANCE;
  }

  /** the Aggregation2D loader */
  private static final class __Aggregation2DLoader {
    /** the shared instance */
    static final AllAggregation2D INSTANCE = new AllAggregation2D();
  }
}
