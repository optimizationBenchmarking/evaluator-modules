package org.optimizationBenchmarking.evaluator.evaluation.impl.all.function;

import org.optimizationBenchmarking.utils.chart.spec.ILineChart2D;
import org.optimizationBenchmarking.utils.document.spec.IFigure;
import org.optimizationBenchmarking.utils.graphics.style.spec.IStyles;

/** The utility class for drawing charts by a function job */
final class _DrawChart implements Runnable {

  /** the owning function job */
  private final FunctionJob m_owner;
  /** the figure */
  private final IFigure m_figure;
  /** the chart */
  private final ILineChart2D m_chart;
  /** the data */
  private final ExperimentSetFunctions m_data;
  /** the line titles */
  private final boolean m_showLineTitles;
  /** the axis */
  private final boolean m_showAxisTitles_;
  /** the styles */
  private final IStyles m_styles;

  /**
   * draw the line chart
   *
   * @param owner
   *          the owning function job
   * @param figure
   *          the figure
   * @param chart
   *          the chart
   * @param data
   *          the data to paint
   * @param showAxisTitles
   *          should we show the axis titles?
   * @param showLineTitles
   *          should line titles be shown?
   * @param styles
   *          the style set
   */
  _DrawChart(final FunctionJob owner, final IFigure figure,
      final ILineChart2D chart, final ExperimentSetFunctions data,
      final boolean showLineTitles, final boolean showAxisTitles,
      final IStyles styles) {
    super();
    this.m_owner = owner;
    this.m_figure = figure;
    this.m_chart = chart;
    this.m_data = data;
    this.m_showLineTitles = showLineTitles;
    this.m_showAxisTitles_ = showAxisTitles;
    this.m_styles = styles;
  }

  /** {@inheritDoc} */
  @Override
  public final void run() {
    try (final IFigure figure = this.m_figure) {
      try (final ILineChart2D chart = this.m_chart) {
        this.m_owner._drawChart(chart, this.m_data, this.m_showLineTitles,
            this.m_showAxisTitles_, this.m_styles);
      }
    }
  }
}