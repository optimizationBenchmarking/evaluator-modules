package org.optimizationBenchmarking.evaluator.evaluation.impl.all.grouping;

import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.evaluation.impl.abstr.ExperimentSetModule;
import org.optimizationBenchmarking.evaluator.evaluation.spec.EModuleType;
import org.optimizationBenchmarking.utils.config.Configuration;

/** This module prints the description of a specified grouping. */
public final class Grouper extends ExperimentSetModule {

  /** create the instance information tool */
  Grouper() {
    super(EModuleType.BODY);
  }

  /** {@inheritDoc} */
  @Override
  public final _GroupingJob createJob(final IExperimentSet data,
      final Configuration config, final Logger logger) {
    return new _GroupingJob(data, config, logger);
  }

  /**
   * Get the globally shared instance of the grouping module
   *
   * @return the globally shared instance of the grouping module
   */
  public static final Grouper getInstance() {
    return __Grouper.INSTANCE;
  }

  /** the all experiments grouping loader */
  private static final class __Grouper {
    /** the shared instance */
    static final Grouper INSTANCE = new Grouper();
  }
}
