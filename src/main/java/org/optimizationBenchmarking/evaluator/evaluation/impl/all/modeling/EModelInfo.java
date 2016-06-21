package org.optimizationBenchmarking.evaluator.evaluation.impl.all.modeling;

/** The type of model information to be printed. */
public enum EModelInfo {

  /** print no model information at all */
  NONE(false, false),
  /** print only the model themselves */
  MODELS_ONLY(false, true),
  /** print only the statistics */
  STATISTICS_ONLY(true, false),
  /** print all available information */
  ALL(true, true);

  /** should we print summarizing statistics about the models? */
  final boolean m_printStatistics;
  /** should we print the single models? */
  final boolean m_printModels;

  /**
   * Create the model info record
   *
   * @param printStatistics
   *          printStatistics
   * @param printModels
   *          should we print the single models?
   */
  EModelInfo(final boolean printStatistics, final boolean printModels) {
    this.m_printStatistics = printStatistics;
    this.m_printModels = printModels;
  }
}
