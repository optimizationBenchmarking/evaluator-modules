package org.optimizationBenchmarking.evaluator.evaluation.impl.all.ecdf;

import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.evaluation.impl.abstr.ExperimentSetModule;
import org.optimizationBenchmarking.evaluator.evaluation.spec.EModuleType;
import org.optimizationBenchmarking.utils.config.Configuration;

/**
 * The ECDF painter. This module paints the ECDF for different experiments
 * into one diagram.
 */
public final class AllECDF extends ExperimentSetModule {

  /** the ecdf parameter */
  public static final String PARAM_ECDF = "ecdf"; //$NON-NLS-1$

  /** create the instance information tool */
  AllECDF() {
    super(EModuleType.BODY);
  }

  /** {@inheritDoc} */
  @Override
  public final _AllECDFJob createJob(final IExperimentSet data,
      final Configuration config, final Logger logger) {
    return new _AllECDFJob(data, config, logger);
  }

  /**
   * Get the globally shared instance of the instance information module
   *
   * @return the globally shared instance of the instance information
   *         module
   */
  public static final AllECDF getInstance() {
    return __AllECDF.INSTANCE;
  }

  /** the all experiments ECDF loader */
  private static final class __AllECDF {
    /** the shared instance */
    static final AllECDF INSTANCE = new AllECDF();
  }
}
