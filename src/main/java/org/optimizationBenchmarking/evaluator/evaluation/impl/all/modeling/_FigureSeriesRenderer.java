package org.optimizationBenchmarking.evaluator.evaluation.impl.all.modeling;

import java.awt.Color;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import org.optimizationBenchmarking.evaluator.attributes.functions.DimensionTransformation;
import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.data.spec.IInstance;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;
import org.optimizationBenchmarking.evaluator.data.spec.IRun;
import org.optimizationBenchmarking.evaluator.evaluation.utils.figures.FigureSeriesRenderer;
import org.optimizationBenchmarking.evaluator.evaluation.utils.figures.XYFigureConfiguration;
import org.optimizationBenchmarking.utils.chart.spec.ELegendMode;
import org.optimizationBenchmarking.utils.chart.spec.ELineType;
import org.optimizationBenchmarking.utils.chart.spec.IAxis;
import org.optimizationBenchmarking.utils.chart.spec.ILine2D;
import org.optimizationBenchmarking.utils.chart.spec.ILineChart2D;
import org.optimizationBenchmarking.utils.collections.iterators.ArrayIterator;
import org.optimizationBenchmarking.utils.document.spec.IComplexText;
import org.optimizationBenchmarking.utils.document.spec.IFigure;
import org.optimizationBenchmarking.utils.document.spec.ISemanticComponent;
import org.optimizationBenchmarking.utils.math.functions.UnaryFunction;
import org.optimizationBenchmarking.utils.math.functions.compound.UnaryFunctionBuilder;
import org.optimizationBenchmarking.utils.math.matrix.AbstractMatrix;
import org.optimizationBenchmarking.utils.math.matrix.impl.DoubleMatrix1D;
import org.optimizationBenchmarking.utils.math.matrix.processing.FunctionSamplingJob;
import org.optimizationBenchmarking.utils.math.matrix.processing.MultiMatrixColumnTransformationJob;
import org.optimizationBenchmarking.utils.math.statistics.aggregate.CompoundAggregate;
import org.optimizationBenchmarking.utils.math.statistics.aggregate.FiniteMaximumAggregate;
import org.optimizationBenchmarking.utils.math.statistics.aggregate.FiniteMinimumAggregate;
import org.optimizationBenchmarking.utils.math.statistics.aggregate.IAggregate;
import org.optimizationBenchmarking.utils.math.text.DefaultParameterRenderer;
import org.optimizationBenchmarking.utils.ml.fitting.spec.IFittingResult;
import org.optimizationBenchmarking.utils.parallel.Execute;
import org.optimizationBenchmarking.utils.text.textOutput.MemoryTextOutput;

/** the figure series renderer */
final class _FigureSeriesRenderer extends
    FigureSeriesRenderer<XYFigureConfiguration, Entry<IInstanceRuns, IFittingResult>[], Entry<IInstanceRuns, IFittingResult>> {

  /** the owner */
  private final _Section m_owner;
  /** the selection */
  private final ISemanticComponent m_selection;

  /**
   * create the selection
   *
   * @param owner
   *          the owning section
   * @param selection
   *          the selection
   */
  _FigureSeriesRenderer(final _Section owner,
      final ISemanticComponent selection) {
    super();
    this.m_owner = owner;
    this.m_selection = selection;
  }

  /** {@inheritDoc} */
  @Override
  protected final Iterator<Entry<IInstanceRuns, IFittingResult>> getFigureData(
      final Entry<IInstanceRuns, IFittingResult>[] sourceData) {
    return new ArrayIterator<>(sourceData);
  }

  /** {@inheritDoc} */
  @Override
  protected final void renderFigureSeriesCaption(
      final Entry<IInstanceRuns, IFittingResult>[] sourceData,
      final IComplexText caption) {
    this.m_owner._renderFigureSeriesCaption(this.m_selection, sourceData,
        caption);
  }

  /** {@inheritDoc} */
  @Override
  protected final String getFigureSeriesPathComponentSuggestion(
      final XYFigureConfiguration configuration,
      final Entry<IInstanceRuns, IFittingResult>[] sourceData) {
    final String addendum;
    String path;

    path = this.m_owner.m_pathComponent;

    addendum = configuration.getPathComponentSuggestion();
    if ((addendum != null) && (addendum.length() > 0)) {
      path = (path + '_' + addendum);
    }

    if (this.m_selection != null) {
      return (path + '/' + this.m_selection.getPathComponentSuggestion());
    }

    return path;
  }

  /** {@inheritDoc} */
  @Override
  protected final String getItemFigurePathComponentSuggestion(
      final XYFigureConfiguration configuration,
      final Entry<IInstanceRuns, IFittingResult>[] sourceData,
      final Entry<IInstanceRuns, IFittingResult> figureData) {
    final IInstanceRuns runs;
    final IInstance instance;
    final IExperiment experiment;

    runs = figureData.getKey();
    instance = runs.getInstance();
    experiment = runs.getOwner();

    if (instance == this.m_selection) {
      return experiment.getPathComponentSuggestion();
    }
    if (experiment == this.m_selection) {
      return instance.getPathComponentSuggestion();
    }
    return experiment.getPathComponentSuggestion() + '_'
        + instance.getPathComponentSuggestion();
  }

  /** {@inheritDoc} */
  @Override
  protected final void renderLegendFigure(
      final XYFigureConfiguration configuration,
      final Entry<IInstanceRuns, IFittingResult>[] sourceData,
      final Entry<IInstanceRuns, IFittingResult> figureData,
      final IFigure figure) {
    try (final IComplexText caption = figure.caption()) {
      caption.append("legend"); //$NON-NLS-1$
    }
    this.__renderFigure(figureData.getKey(), figureData.getValue(),
        configuration, figure, ELegendMode.CHART_IS_LEGEND);
  }

  /** {@inheritDoc} */
  @Override
  protected final void renderItemFigure(
      final XYFigureConfiguration configuration,
      final Entry<IInstanceRuns, IFittingResult>[] sourceData,
      final Entry<IInstanceRuns, IFittingResult> figureData,
      final IFigure figure) {
    final IInstanceRuns runs;
    final IFittingResult result;

    runs = figureData.getKey();
    result = figureData.getValue();
    try (final IComplexText caption = figure.caption()) {
      this.m_owner._renderFigureCaption(true, this.m_selection, runs,
          result, caption);
    }
    this.__renderFigure(runs, result, configuration, figure,
        ELegendMode.HIDE_COMPLETE_LEGEND);
  }

  /** {@inheritDoc} */
  @Override
  protected final void renderSingleFigure(
      final XYFigureConfiguration configuration,
      final Entry<IInstanceRuns, IFittingResult>[] sourceData,
      final Entry<IInstanceRuns, IFittingResult> figureData,
      final IFigure figure) {
    final IInstanceRuns runs;
    final IFittingResult result;

    runs = figureData.getKey();
    result = figureData.getValue();
    try (final IComplexText caption = figure.caption()) {
      this.m_owner._renderFigureCaption(false, this.m_selection, runs,
          result, caption);
    }
    this.__renderFigure(runs, result, configuration, figure,
        ELegendMode.SHOW_COMPLETE_LEGEND);
  }

  /**
   * render the figure
   *
   * @param runs
   *          the runs
   * @param result
   *          the results
   * @param configuration
   *          the configuration
   * @param figure
   *          the destination figure
   * @param legendMode
   *          the legend mode
   */
  private final void __renderFigure(final IInstanceRuns runs,
      final IFittingResult result,
      final XYFigureConfiguration configuration, final IFigure figure,
      final ELegendMode legendMode) {
    this.__renderFigure(runs, result,
        configuration.getXAxisConfiguredMin(),
        configuration.getXAxisConfiguredMax(),
        configuration.getYAxisConfiguredMin(),
        configuration.getYAxisConfiguredMax(),
        ((legendMode == ELegendMode.CHART_IS_LEGEND) ? true
            : configuration.hasAxisTitles()),
        legendMode, figure);
  }

  /**
   * render the figure
   *
   * @param runs
   *          the runs
   * @param result
   *          the result
   * @param configuredMinX
   *          the configured minimum x
   * @param configuredMaxX
   *          the configured maximum x
   * @param configuredMinY
   *          the configured minimum y
   * @param configuredMaxY
   *          the configured maximum x
   * @param showAxisTitles
   *          should we print axis titles?
   * @param legendMode
   *          the legend mode
   * @param figure
   *          the destination figure
   */
  private final void __renderFigure(final IInstanceRuns runs,
      final IFittingResult result, final Number configuredMinX,
      final Number configuredMaxX, final Number configuredMinY,
      final Number configuredMaxY, final boolean showAxisTitles,
      final ELegendMode legendMode,

      final IFigure figure) {
    final DimensionTransformation dimX, dimY;
    final int colX, colY;
    final _Model model;
    Future<AbstractMatrix[]> backgroundLinesGetter;
    Future<DoubleMatrix1D> modelLinesGetter;
    AbstractMatrix[] matrices;
    boolean plotLineName;
    int index;
    FiniteMaximumAggregate maxX;
    FiniteMinimumAggregate minX;
    IAggregate aggX;
    MemoryTextOutput memTO;
    UnaryFunction xTransformation, yTransformation, function;

    dimX = this.m_owner.m_job.m_transformationX;
    dimY = this.m_owner.m_job.m_transformationY;

    xTransformation = dimX.use(runs);
    yTransformation = dimY.use(runs);

    colX = dimX.getDimension().getIndex();
    colY = dimY.getDimension().getIndex();

    // launch the transformation of the original run data in the background
    backgroundLinesGetter = Execute
        .parallel(new MultiMatrixColumnTransformationJob(//
            runs.getData(), new int[] { colX, colY },
            new UnaryFunction[] { xTransformation, yTransformation }));

    // compute the range over which we need to calculate the model function
    minX = new FiniteMinimumAggregate();
    maxX = new FiniteMaximumAggregate();
    aggX = CompoundAggregate.combine(minX, maxX);

    for (final IRun run : runs.getData()) {
      run.aggregateColumn(colX, aggX);
    }
    aggX = null;

    function = result.getFittedFunction()
        .toUnaryFunction(result.getFittedParametersRef());

    // now let us sample the model
    modelLinesGetter = Execute.parallel(new FunctionSamplingJob(
        UnaryFunctionBuilder.getInstance().compound(yTransformation,
            function),
        minX.doubleValue(), maxX.doubleValue(), xTransformation));
    model = this.m_owner.m_job.m_models.get(result.getFittedFunction());

    try (final ILineChart2D chart = figure.lineChart2D()) {
      chart.setLegendMode(legendMode);

      try (final IAxis axis = chart.xAxis()) {
        if (configuredMinX == null) {
          axis.setMinimum(new FiniteMinimumAggregate());
        } else {
          axis.setMinimum(configuredMinX);
        }
        if (configuredMaxX == null) {
          axis.setMaximum(new FiniteMaximumAggregate());
        } else {
          axis.setMinimum(configuredMaxX);
        }

        if (showAxisTitles
            || (legendMode == ELegendMode.CHART_IS_LEGEND)) {
          memTO = new MemoryTextOutput();
          this.m_owner.m_job.m_transformationX.mathRender(memTO,
              DefaultParameterRenderer.INSTANCE);
          axis.setTitle(memTO.toString());
          memTO = null;
        }
      }

      try (final IAxis axis = chart.yAxis()) {

        if (configuredMinY == null) {
          axis.setMinimum(new FiniteMinimumAggregate());
        } else {
          axis.setMinimum(configuredMinY);
        }
        if (configuredMaxY == null) {
          axis.setMaximum(new FiniteMaximumAggregate());
        } else {
          axis.setMinimum(configuredMaxY);
        }

        if (showAxisTitles
            || (legendMode == ELegendMode.CHART_IS_LEGEND)) {
          memTO = new MemoryTextOutput();
          this.m_owner.m_job.m_transformationY.mathRender(memTO,
              DefaultParameterRenderer.INSTANCE);
          axis.setTitle(memTO.toString());
          memTO = null;
        }
      }

      plotLineName = legendMode.isLegendShown();
      matrices = backgroundLinesGetter.get();
      backgroundLinesGetter = null;
      index = 0;
      for (final AbstractMatrix matrix : matrices) {
        try (final ILine2D line = chart.line()) {
          line.setData(matrix);
          matrices[index++] = null;
          line.setType(ELineType.STAIRS_KEEP_LEFT);
          line.setColor(_FigureSeriesRenderer.__makeRunColor(index,
              matrices.length));
          line.setStroke(this.m_owner.m_job.m_strokeForRuns.getStroke());
          if (plotLineName) {
            line.setTitle("measured run"); //$NON-NLS-1$
            plotLineName = false;
          }
        }
      }
      matrices = null;

      try (final ILine2D line = chart.line()) {
        line.setType(ELineType.SMOOTH);
        line.setColor(model.m_style.getColor());
        line.setStroke(this.m_owner.m_job.m_strokeForModels.getStroke());
        line.setData(modelLinesGetter.get());
        if (legendMode.isLegendShown()) {
          line.setTitle("fitted model");//$NON-NLS-1$
        }
        modelLinesGetter = null;
      }

    } catch (final RuntimeException runtime) {
      throw runtime;
    } catch (final Throwable error) {
      throw new IllegalStateException("Cannot plot model functions.", //$NON-NLS-1$
          error);
    }
  }

  /**
   * make a run color
   * 
   * @param index
   *          the run index
   * @param total
   *          the total number of runs
   * @return the run color
   */
  private static final Color __makeRunColor(final int index,
      final int total) {
    float value;
    int totalColors, useIndex;

    if (index <= 0) {
      return Color.GRAY;
    }

    useIndex = (index - 1);
    totalColors = (total - 1);

    if ((useIndex % 1) == 0) {
      useIndex = ((totalColors - (useIndex << 1)) % totalColors);
    } else {
      useIndex = ((useIndex << 1) % totalColors);
    }

    value = ((float) (((useIndex / ((double) totalColors)) * 77d) + 30d));
    return new Color(value, value, value);
  }
}