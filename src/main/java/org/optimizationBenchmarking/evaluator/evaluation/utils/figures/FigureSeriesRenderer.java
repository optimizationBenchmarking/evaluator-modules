package org.optimizationBenchmarking.evaluator.evaluation.utils.figures;

import java.util.Iterator;

import org.optimizationBenchmarking.utils.document.spec.ELabelType;
import org.optimizationBenchmarking.utils.document.spec.IComplexText;
import org.optimizationBenchmarking.utils.document.spec.IFigure;
import org.optimizationBenchmarking.utils.document.spec.IFigureSeries;
import org.optimizationBenchmarking.utils.document.spec.ILabel;
import org.optimizationBenchmarking.utils.document.spec.ISectionBody;
import org.optimizationBenchmarking.utils.document.spec.ISemanticComponent;
import org.optimizationBenchmarking.utils.text.ESequenceMode;
import org.optimizationBenchmarking.utils.text.ETextCase;

/**
 * A creator for series of figures as commonly used in our evaluation
 * modules.
 *
 * @param <CONF>
 *          the figure series configuration type
 * @param <FD>
 *          the data item used for the figures
 * @param <SD>
 *          the source data type
 */
public abstract class FigureSeriesRenderer<CONF extends FigureConfiguration, SD, FD> {

  /** Create the figure series creator */
  protected FigureSeriesRenderer() {
    super();
  }

  /**
   * render this figure series to a given section body.
   *
   * @param configuration
   *          the configuration to be used for the rendering
   * @param sourceData
   *          the source sourceData element
   * @param useLabel
   *          the label to be used, or {@code null} to generate one
   * @param body
   *          the section body to render to
   * @return the label of the figure series, or {@code null} if nothing was
   *         rendered
   */
  @SuppressWarnings("resource")
  public ILabel render(final CONF configuration, final SD sourceData,
      final ILabel useLabel, final ISectionBody body) {
    final Iterator<FD> iterator;
    FD cached, current;
    boolean nextIsLegend;
    ILabel mainLabel, legendLabel;
    IFigureSeries series;

    if (configuration == null) {
      throw new IllegalArgumentException(
          "Figure configuration must not be null."); //$NON-NLS-1$
    }

    iterator = this.getFigureData(sourceData);
    mainLabel = useLabel;
    if (iterator != null) {
      nextIsLegend = configuration.hasLegendFigure();
      current = cached = null;
      series = null;

      while (iterator.hasNext()) {
        current = iterator.next();
        if (current == null) {
          continue;
        }

        // OK, if we do need a legend: create a figure series, render the
        // legend, then render the figure
        if (nextIsLegend) {
          nextIsLegend = false;
          if (mainLabel == null) {
            mainLabel = body.createLabel(ELabelType.FIGURE);
          }
          series = body.figureSeries(mainLabel,
              configuration.getFigureSize(),
              this.getFigureSeriesPathComponentSuggestion(configuration,
                  sourceData));

          legendLabel = body.createLabel(ELabelType.SUBFIGURE);

          try (final IComplexText caption = series.caption()) {
            this.renderFigureSeriesCaption(sourceData, caption);
            caption.append(" (legend in "); //$NON-NLS-1$
            caption.reference(ETextCase.IN_SENTENCE, ESequenceMode.AND,
                legendLabel);
            caption.append(')');
          }

          try (final IFigure legendFigure = series.figure(legendLabel,
              this.getLegendPathComponentSuggestion(configuration,
                  sourceData, current))) {
            this.renderLegendFigure(configuration, sourceData, current,
                legendFigure);
          }
        }

        if (cached != null) {
          if (mainLabel == null) {
            mainLabel = body.createLabel(ELabelType.FIGURE);
          }
          series = body.figureSeries(mainLabel,
              configuration.getFigureSize(),
              this.getFigureSeriesPathComponentSuggestion(configuration,
                  sourceData));

          try (final IComplexText caption = series.caption()) {
            this.renderFigureSeriesCaption(sourceData, caption);
          }

          try (final IFigure itemFigure = series.figure(null,
              this.getItemFigurePathComponentSuggestion(configuration,
                  sourceData, cached))) {
            this.renderItemFigure(configuration, sourceData, cached,
                itemFigure);
          }
          cached = null;
        }

        if (series != null) {
          try (final IFigure itemFigure = series.figure(null,
              this.getItemFigurePathComponentSuggestion(configuration,
                  sourceData, current))) {
            this.renderItemFigure(configuration, sourceData, current,
                itemFigure);
          }
          continue;
        }

        cached = current;
      }

      if (cached != null) {
        if (mainLabel == null) {
          mainLabel = body.createLabel(ELabelType.FIGURE);
        }
        try (final IFigure singleFigure = body.figure(mainLabel,
            configuration.getFigureSize(),
            this.getSingleFigurePathComponentSuggestion(configuration,
                sourceData, cached))) {
          this.renderSingleFigure(configuration, sourceData, cached,
              singleFigure);
        }
        cached = null;
      }

      if (series != null) {
        series.close();
      }
    }

    return mainLabel;
  }

  /**
   * Render the caption of the figure series
   *
   * @param sourceData
   *          the sourceData type
   * @param caption
   *          the caption destination
   */
  protected void renderFigureSeriesCaption(final SD sourceData,
      final IComplexText caption) {
    //
  }

  /**
   * Render the legend figure
   *
   * @param configuration
   *          the configuration
   * @param sourceData
   *          the source data
   * @param figureData
   *          the figure data
   * @param figure
   *          the destination figure
   */
  protected void renderLegendFigure(final CONF configuration,
      final SD sourceData, final FD figureData, final IFigure figure) {
    //
  }

  /**
   * Render an item figure
   *
   * @param configuration
   *          the configuration
   * @param sourceData
   *          the source data
   * @param figureData
   *          the figure data
   * @param figure
   *          the destination figure
   */
  protected void renderItemFigure(final CONF configuration,
      final SD sourceData, final FD figureData, final IFigure figure) {
    //
  }

  /**
   * Render an item figure
   *
   * @param configuration
   *          the configuration
   * @param sourceData
   *          the source data
   * @param figureData
   *          the figure data
   * @param figure
   *          the destination figure
   */
  protected void renderSingleFigure(final CONF configuration,
      final SD sourceData, final FD figureData, final IFigure figure) {
    this.renderItemFigure(configuration, sourceData, figureData, figure);
  }

  /**
   * Get the figure path component suggestion for a legend.
   *
   * @param configuration
   *          the configuration
   * @param sourceData
   *          the sourceData
   * @param figureData
   *          the figure data
   * @return the suggestion
   */
  protected String getLegendPathComponentSuggestion(
      final CONF configuration, final SD sourceData, final FD figureData) {
    return "legend"; //$NON-NLS-1$
  }

  /**
   * Get the path component suggestion for a figure.
   *
   * @param configuration
   *          the configuration
   * @param sourceData
   *          the sourceData
   * @param figureData
   *          the figure data
   * @return the suggestion
   */
  protected String getItemFigurePathComponentSuggestion(
      final CONF configuration, final SD sourceData, final FD figureData) {
    if (figureData instanceof ISemanticComponent) {
      return ((ISemanticComponent) figureData)
          .getPathComponentSuggestion();
    }
    return ""; //$NON-NLS-1$
  }

  /**
   * Get the path component suggestion for a single figure.
   *
   * @param configuration
   *          the configuration
   * @param sourceData
   *          the sourceData
   * @param figureData
   *          the figure data
   * @return the suggestion
   */
  protected String getSingleFigurePathComponentSuggestion(
      final CONF configuration, final SD sourceData, final FD figureData) {
    return FigureSeriesRenderer.__merge(
        this.getFigureSeriesPathComponentSuggestion(configuration,
            sourceData),
        this.getItemFigurePathComponentSuggestion(configuration,
            sourceData, figureData));
  }

  /**
   * Get the figure series path component suggestion.
   *
   * @param configuration
   *          the configuration
   * @param sourceData
   *          the sourceData
   * @return the suggestion
   */
  protected String getFigureSeriesPathComponentSuggestion(
      final CONF configuration, final SD sourceData) {
    final String path1, path2;

    if (sourceData instanceof ISemanticComponent) {
      path1 = ((ISemanticComponent) sourceData)
          .getPathComponentSuggestion();
    } else {
      path1 = null;
    }

    path2 = configuration.getPathComponentSuggestion();

    return FigureSeriesRenderer.__merge(path1, path2);
  }

  /**
   * merge two paths
   *
   * @param path1
   *          the first path
   * @param path2
   *          the second path
   * @return their merged result
   */
  private static final String __merge(final String path1,
      final String path2) {

    if ((path1 != null) && (path1.length() > 0)) {
      if ((path2 != null) && (path2.length() > 0)) {
        return path1 + '_' + path2;
      }
      return path1;
    }

    if ((path2 == null) || (path2.length() <= 0)) {
      return ""; //$NON-NLS-1$
    }

    return path2;
  }

  /**
   * Get the iterator for the figure sourceData
   *
   * @param sourceData
   *          the source sourceData
   * @return the figure sourceData iterator
   */
  protected abstract Iterator<FD> getFigureData(final SD sourceData);
}
