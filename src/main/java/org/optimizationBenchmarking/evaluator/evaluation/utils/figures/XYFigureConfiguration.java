package org.optimizationBenchmarking.evaluator.evaluation.utils.figures;

import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.evaluation.impl.all.function.FunctionJob;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.document.spec.EFigureSize;
import org.optimizationBenchmarking.utils.parsers.AnyNumberParser;

/** A base class for configuring a figure series. */
public class XYFigureConfiguration extends FigureConfiguration {
  /** Should the figures include axis titles? */
  public static final String PARAM_PRINT_AXIS_TITLES = "showAxisTitles"; //$NON-NLS-1$

  /** the minimum value for the x-axis */
  public static final String PARAM_MIN_X = "minX";//$NON-NLS-1$
  /** the maximum value for the x-axis */
  public static final String PARAM_MAX_X = "maxX";//$NON-NLS-1$
  /** the minimum value for the y-axis */
  public static final String PARAM_MIN_Y = "minY";//$NON-NLS-1$
  /** the maximum value for the y-axis */
  public static final String PARAM_MAX_Y = "maxY";//$NON-NLS-1$

  /** should we print axis titles? */
  private final boolean m_showAxisTitles;
  /**
   * the minimum value for the x-axis, or {@code null} if undefined
   *
   * @see #PARAM_MIN_X
   */
  private final Number m_minX;
  /**
   * the maximum value for the x-axis, or {@code null} if undefined
   *
   * @see #PARAM_MAX_X
   */
  private final Number m_maxX;
  /**
   * the minimum value for the y-axis, or {@code null} if undefined
   *
   * @see #PARAM_MIN_Y
   */
  private final Number m_minY;
  /**
   * the maximum value for the y-axis, or {@code null} if undefined
   *
   * @see #PARAM_MAX_Y
   */
  private final Number m_maxY;

  /**
   * create the figure series configuration
   *
   * @param configuration
   *          the configuration of the figure series
   * @param data
   *          the experiment set
   */
  public XYFigureConfiguration(final Configuration configuration,
      final IExperimentSet data) {
    super(configuration, data);

    this.m_showAxisTitles = configuration.getBoolean(
        FunctionJob.PARAM_PRINT_AXIS_TITLES, this.getDefaultShowAxisTitles(
            data, this.getFigureSize(), this.hasLegendFigure()));

    this.m_minX = configuration.get(FunctionJob.PARAM_MIN_X,
        AnyNumberParser.INSTANCE, null);
    this.m_maxX = configuration.get(FunctionJob.PARAM_MAX_X,
        AnyNumberParser.INSTANCE, null);
    this.m_minY = configuration.get(FunctionJob.PARAM_MIN_Y,
        AnyNumberParser.INSTANCE, null);
    this.m_maxY = configuration.get(FunctionJob.PARAM_MAX_Y,
        AnyNumberParser.INSTANCE, null);
  }

  /**
   * Get the minimum value for the x-axis provided via the configuration.
   *
   * @return the configured minimum value for the {@code x} axis, or
   *         {@code null} if none is defined
   */
  public final Number getXAxisConfiguredMin() {
    return this.m_minX;
  }

  /**
   * Get the maximum value for the x-axis provided via the configuration.
   *
   * @return the configured maximum value for the {@code x} axis, or
   *         {@code null} if none is defined
   */
  public final Number getXAxisConfiguredMax() {
    return this.m_maxX;
  }

  /**
   * Get the minimum value for the y-axis provided via the configuration.
   *
   * @return the configured minimum value for the {@code y} axis, or
   *         {@code null} if none is defined
   */
  public final Number getYAxisConfiguredMin() {
    return this.m_minY;
  }

  /**
   * Get the maximum value for the y-axis provided via the configuration.
   *
   * @return the configured maximum value for the {@code y} axis, or
   *         {@code null} if none is defined
   */
  public final Number getYAxisConfiguredMax() {
    return this.m_maxY;
  }

  /**
   * Will there be axis titles?
   *
   * @return {@code true} if the each figure contains axis titles,
   *         {@code false} if no axis titles are shown (except for in a
   *         potential {@link #hasLegendFigure() legend figure}
   */
  public final boolean hasAxisTitles() {
    return this.m_showAxisTitles;
  }

  /**
   * Get the default value for deciding whether axis titles should be
   * included in the figure.
   *
   * @param data
   *          the experiment set
   * @param figureSize
   *          the figure size
   * @param hasLegendFigure
   *          is there a separate legend figure?
   * @return {@code true} if axis titles should be displayed in each
   *         figure, {@code false} if axis titles are not printed (except
   *         for in a potential legend figure)
   */
  protected boolean getDefaultShowAxisTitles(final IExperimentSet data,
      final EFigureSize figureSize, final boolean hasLegendFigure) {
    int n;

    if (hasLegendFigure) {
      return false;
    }

    n = figureSize.getNX();
    if (!(figureSize.spansAllColumns())) {
      n <<= 1;
    }

    return (n <= 4);
  }

  /** {@inheritDoc} */
  @Override
  public String getPathComponentSuggestion() {
    String path;

    path = super.getPathComponentSuggestion();

    if (this.m_minX != null) {
      if ((path != null) && (path.length() > 0)) {
        path = (path + "_x1=");//$NON-NLS-1$
      } else {
        path = "x1=";//$NON-NLS-1$
      }
      path += this.m_minX;
    }
    if (this.m_maxX != null) {
      if ((path != null) && (path.length() > 0)) {
        path = (path + "_x2=");//$NON-NLS-1$
      } else {
        path = "x2=";//$NON-NLS-1$
      }
      path += this.m_maxX;
    }
    if (this.m_minY != null) {
      if ((path != null) && (path.length() > 0)) {
        path = (path + "_y1=");//$NON-NLS-1$
      } else {
        path = "y1=";//$NON-NLS-1$
      }
      path += this.m_minY;
    }
    if (this.m_maxY != null) {
      if ((path != null) && (path.length() > 0)) {
        path = (path + "_y2=");//$NON-NLS-1$
      } else {
        path = "y2=";//$NON-NLS-1$
      }
      path += this.m_maxY;
    }

    return (((path != null) && (path.length() > 0)) ? path : "");//$NON-NLS-1$
  }
}
