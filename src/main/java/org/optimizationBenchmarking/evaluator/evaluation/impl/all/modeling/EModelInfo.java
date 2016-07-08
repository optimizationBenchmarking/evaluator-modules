package org.optimizationBenchmarking.evaluator.evaluation.impl.all.modeling;

/** The type of model information to be printed. */
public enum EModelInfo {

  /** print no model information at all */
  NONE(false, false, false),
  /** print only the model themselves */
  MODELS_ONLY(false, true, false),
  /** print only the model charts */
  CHARTS_ONLY(false, false, true), //
  /** print only the statistics */
  STATISTICS_ONLY(true, false, false),
  /** print the statistics as table and plot the models as charts */
  STATISTICS_AND_CHARTS(true, false, true),
  /** print all available information */
  ALL(true, true, true);

  /** should we print summarizing statistics about the models? */
  final boolean m_printStatistics;
  /** should we print the single models? */
  final boolean m_printModels;
  /** should we plot the models as graphics? */
  final boolean m_plotModels;

  /**
   * Create the model info record
   *
   * @param printStatistics
   *          printStatistics
   * @param printModels
   *          should we print the single models?
   * @param plotModels
   *          should we plot the models as graphics?
   */
  EModelInfo(final boolean printStatistics, final boolean printModels,
      final boolean plotModels) {
    this.m_printStatistics = printStatistics;
    this.m_printModels = printModels;
    this.m_plotModels = plotModels;
  }
}
