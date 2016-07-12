package org.optimizationBenchmarking.evaluator.evaluation.utils.figures;

import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.evaluation.impl.all.function.FunctionJob;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.document.impl.FigureSizeParser;
import org.optimizationBenchmarking.utils.document.spec.EFigureSize;

/** A base class for configuring a figure series. */
public class FigureConfiguration {
  /**
   * Should there be a sub-figure which only serves as legend, hence
   * allowing us to omit legends in the other figures?
   */
  public static final String PARAM_MAKE_LEGEND_FIGURE = "makeLegendFigure"; //$NON-NLS-1$

  /** the figure size */
  private final EFigureSize m_figureSize;

  /** should we make a legend figure? */
  private final boolean m_makeLegendFigure;

  /**
   * create the figure series configuration
   *
   * @param configuration
   *          the configuration of the figure series
   * @param data
   *          the experiment set
   */
  public FigureConfiguration(final Configuration configuration,
      final IExperimentSet data) {
    super();
    final EFigureSize defaultFigureSize;

    defaultFigureSize = this.getDefaultFigureSize(data);
    if (defaultFigureSize == null) {
      throw new IllegalArgumentException(//
          "Default figure size cannot be null."); //$NON-NLS-1$
    }

    this.m_figureSize = configuration.get(
        FigureSizeParser.PARAM_FIGURE_SIZE, FigureSizeParser.INSTANCE,
        defaultFigureSize);

    this.m_makeLegendFigure = configuration.getBoolean(
        FunctionJob.PARAM_MAKE_LEGEND_FIGURE,
        this.getDefaultMakeLegendFigure(data, this.m_figureSize));
  }

  /**
   * Get the figure size of this function job.
   *
   * @return the figure size of this function job
   */
  public final EFigureSize getFigureSize() {
    return this.m_figureSize;
  }

  /**
   * Will this job include dedicated legend figures?
   *
   * @return {@code true} if the each figure also contains a dedicated
   *         legend figure, {@code false} if no dedicated legend is painted
   *         (i.e., each figure contains a legend).
   */
  public final boolean hasLegendFigure() {
    return this.m_makeLegendFigure;
  }

  /**
   * Get the default figure size
   *
   * @param data
   *          the experiment set
   * @return the default figure size
   */
  protected EFigureSize getDefaultFigureSize(final IExperimentSet data) {
    final int size;

    size = data.getData().size();
    if (size > 6) {
      if (size > 12) {
        return EFigureSize.PAGE_WIDE;
      }
      return EFigureSize.PAGE_2_PER_ROW;
    }

    return EFigureSize.PAGE_3_PER_ROW;
  }

  /**
   * Get the default value for deciding whether a legend figure should be
   * made.
   *
   * @param data
   *          the experiment set
   * @param figureSize
   *          the figure size
   * @return {@code true} if a legend figure should be made, {@code false}
   *         otherwise
   */
  protected boolean getDefaultMakeLegendFigure(final IExperimentSet data,
      final EFigureSize figureSize) {
    final int size;
    int n;

    n = figureSize.getNX();
    if (!(figureSize.spansAllColumns())) {
      n <<= 1;
    }

    if (n >= 5) {
      return true;
    }

    size = data.getData().size();
    if (size >= 16) {
      return true;
    }

    if ((size >= 8) && (n >= 2)) {
      return true;
    }

    return false;
  }

  /**
   * Get the path component suggestion
   *
   * @return the path component suggestion
   */
  public String getPathComponentSuggestion() {
    return ""; //$NON-NLS-1$
  }
}
