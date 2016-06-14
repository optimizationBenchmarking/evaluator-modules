package org.optimizationBenchmarking.evaluator.evaluation.impl.all.modeling;

import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.evaluation.impl.abstr.ExperimentSetModule;
import org.optimizationBenchmarking.evaluator.evaluation.spec.EModuleType;
import org.optimizationBenchmarking.utils.config.Configuration;

/**
 * This module creates models of the relationship between two dimensions
 * for a given experiment set.
 */
public final class Modeler extends ExperimentSetModule {

  /** the x axis */
  public static final String PARAM_X = "x"; //$NON-NLS-1$
  /** the y axis */
  public static final String PARAM_Y = "y"; //$NON-NLS-1$
  /** the should we list the models for each instance? */
  public static final String PARAM_LIST_PER_INSTANCE = "listPerInstance"; //$NON-NLS-1$
  /** the should we list the models for each algorithm? */
  public static final String PARAM_LIST_PER_ALGORITHM = "listPerAlgorithm"; //$NON-NLS-1$

  /** create the instance information tool */
  Modeler() {
    super(EModuleType.BODY);
  }

  /** {@inheritDoc} */
  @Override
  public final _ModelingJob createJob(final IExperimentSet data,
      final Configuration config, final Logger logger) {
    return new _ModelingJob(data, config, logger);
  }

  /**
   * Get the globally shared instance of the modeling module
   *
   * @return the globally shared instance of the modeling module
   */
  public static final Modeler getInstance() {
    return __Modeler.INSTANCE;
  }

  /** the all experiments modeler loader */
  private static final class __Modeler {
    /** the shared instance */
    static final Modeler INSTANCE = new Modeler();
  }
}
