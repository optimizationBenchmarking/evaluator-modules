package org.optimizationBenchmarking.evaluator.evaluation.impl.all.modeling;

import java.awt.Color;
import java.util.concurrent.Future;

import org.optimizationBenchmarking.evaluator.attributes.functions.DimensionTransformation;
import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.data.spec.IInstance;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;
import org.optimizationBenchmarking.evaluator.data.spec.IRun;
import org.optimizationBenchmarking.utils.chart.spec.ELegendMode;
import org.optimizationBenchmarking.utils.chart.spec.ELineType;
import org.optimizationBenchmarking.utils.chart.spec.IAxis;
import org.optimizationBenchmarking.utils.chart.spec.ILine2D;
import org.optimizationBenchmarking.utils.chart.spec.ILineChart2D;
import org.optimizationBenchmarking.utils.document.impl.FigureRenderer;
import org.optimizationBenchmarking.utils.document.spec.EFigureSize;
import org.optimizationBenchmarking.utils.document.spec.IComplexText;
import org.optimizationBenchmarking.utils.document.spec.IFigure;
import org.optimizationBenchmarking.utils.document.spec.ILabel;
import org.optimizationBenchmarking.utils.document.spec.ILabelBuilder;
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

/** the internal figure renderer class */
final class _FigureRenderer extends FigureRenderer {

  /** the owning section */
  private final _Section m_owner;
  /** the selection */
  private final ISemanticComponent m_selection;
  /** the runs */
  private final IInstanceRuns m_runs;
  /** the result */
  private final IFittingResult m_result;
  /** the label to use */
  private final ILabel m_useLabel;

  /**
   * create
   *
   * @param owner
   *          the owning section
   * @param selection
   *          the selection
   * @param runs
   *          the runs
   * @param result
   *          the result
   * @param useLabel
   *          the label to use
   */
  _FigureRenderer(final _Section owner, final ISemanticComponent selection,
      final IInstanceRuns runs, final IFittingResult result,
      final ILabel useLabel) {
    super();
    this.m_owner = owner;
    this.m_selection = selection;
    this.m_runs = runs;
    this.m_result = result;
    this.m_useLabel = useLabel;
  }

  /** {@inheritDoc} */
  @Override
  protected final ILabel doCreateFigureLabel(final boolean isPartOfSeries,
      final ILabelBuilder builder) {
    return (isPartOfSeries ? null : this.m_useLabel);
  }

  /** {@inheritDoc} */
  @Override
  protected final String doGetFigurePathComponentSuggestion(
      final boolean isPartOfSeries) {
    final String local;
    final IInstance instance;
    final IExperiment experiment;

    instance = this.m_runs.getInstance();
    experiment = this.m_runs.getOwner();

    if (instance == this.m_selection) {
      local = experiment.getPathComponentSuggestion();
    } else {
      if (experiment == this.m_selection) {
        local = instance.getPathComponentSuggestion();
      } else {
        local = experiment.getPathComponentSuggestion() + '_'
            + instance.getPathComponentSuggestion();
      }
    }

    if (isPartOfSeries) {
      return local;
    }

    return ((_FigureSeriesRenderer._basePathComponentSelection(
        this.m_owner.m_pathComponent, this.m_selection,
        this.m_owner.m_data) + '/') + local);
  }

  /** {@inheritDoc} */
  @Override
  public final EFigureSize getFigureSize() {
    return this.m_owner.m_job.m_figureSize;
  }

  /** {@inheritDoc} */
  @Override
  protected final void doRenderFigure(final boolean isPartOfSeries,
      final IFigure figure) {
    final DimensionTransformation dimX, dimY;
    final int colX, colY;
    final _Model model;
    Future<AbstractMatrix[]> backgroundLinesGetter;
    Future<DoubleMatrix1D> modelLinesGetter;

    FiniteMaximumAggregate maxX;
    FiniteMinimumAggregate minX;
    IAggregate aggX;
    MemoryTextOutput memTO;
    UnaryFunction xTransformation, yTransformation, function;

    dimX = this.m_owner.m_job.m_transformationX;
    dimY = this.m_owner.m_job.m_transformationY;

    xTransformation = dimX.use(this.m_runs);
    yTransformation = dimY.use(this.m_runs);

    colX = dimX.getDimension().getIndex();
    colY = dimY.getDimension().getIndex();

    // launch the transformation of the original run data in the background
    backgroundLinesGetter = Execute
        .parallel(new MultiMatrixColumnTransformationJob(//
            this.m_runs.getData(), new int[] { colX, colY },
            new UnaryFunction[] { xTransformation, yTransformation }));

    // compute the range over which we need to calculate the model function
    minX = new FiniteMinimumAggregate();
    maxX = new FiniteMaximumAggregate();
    aggX = CompoundAggregate.combine(minX, maxX);

    for (final IRun run : this.m_runs.getData()) {
      run.aggregateColumn(colX, aggX);
    }
    aggX = null;

    function = this.m_result.getFittedFunction()
        .toUnaryFunction(this.m_result.getFittedParametersRef());

    // now let us sample the model
    modelLinesGetter = Execute.parallel(new FunctionSamplingJob(
        UnaryFunctionBuilder.getInstance().compound(yTransformation,
            function),
        minX.doubleValue(), maxX.doubleValue(), xTransformation));
    model = this.m_owner.m_job.m_models
        .get(this.m_result.getFittedFunction());
    try (final IComplexText caption = figure.caption()) {
      this.m_owner._renderFigureCaption(isPartOfSeries, this.m_selection,
          this.m_runs, this.m_result, model, caption);
    }
    try (final ILineChart2D chart = figure.lineChart2D()) {
      chart.setLegendMode(ELegendMode.HIDE_COMPLETE_LEGEND);

      try (final IAxis axis = chart.xAxis()) {
        axis.setMinimum(new FiniteMinimumAggregate());
        axis.setMaximum(new FiniteMaximumAggregate());

        memTO = new MemoryTextOutput();
        this.m_owner.m_job.m_transformationX.mathRender(memTO,
            DefaultParameterRenderer.INSTANCE);
        axis.setTitle(memTO.toString());
        memTO.clear();
      }

      try (final IAxis axis = chart.yAxis()) {
        axis.setMinimum(new FiniteMinimumAggregate());
        axis.setMaximum(new FiniteMaximumAggregate());

        this.m_owner.m_job.m_transformationY.mathRender(memTO,
            DefaultParameterRenderer.INSTANCE);
        axis.setTitle(memTO.toString());
        memTO = null;
      }

      for (final AbstractMatrix matrix : backgroundLinesGetter.get()) {
        try (final ILine2D line = chart.line()) {
          line.setType(ELineType.SMOOTH);
          line.setColor(Color.GRAY);
          line.setStroke(this.m_owner.m_job.m_thinLine.getStroke());
          line.setData(matrix);
        }
      }
      backgroundLinesGetter = null;

      try (final ILine2D line = chart.line()) {
        line.setType(ELineType.SMOOTH);
        line.setColor(model.m_style.getColor());
        line.setStroke(this.m_owner.m_job.m_normalLine.getStroke());
        line.setData(modelLinesGetter.get());
        modelLinesGetter = null;
      }

    } catch (final RuntimeException runtime) {
      throw runtime;
    } catch (final Throwable error) {
      throw new IllegalStateException("Cannot plot model functions.", //$NON-NLS-1$
          error);
    }
  }
}