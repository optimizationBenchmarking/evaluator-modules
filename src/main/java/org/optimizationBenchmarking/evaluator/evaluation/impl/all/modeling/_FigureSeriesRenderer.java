package org.optimizationBenchmarking.evaluator.evaluation.impl.all.modeling;

import java.util.Map.Entry;

import org.optimizationBenchmarking.evaluator.attributes.clusters.ICluster;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;
import org.optimizationBenchmarking.utils.collections.iterators.ArrayIterator;
import org.optimizationBenchmarking.utils.document.impl.FigureSeriesRenderer;
import org.optimizationBenchmarking.utils.document.spec.EFigureSize;
import org.optimizationBenchmarking.utils.document.spec.IComplexText;
import org.optimizationBenchmarking.utils.document.spec.ILabel;
import org.optimizationBenchmarking.utils.document.spec.ILabelBuilder;
import org.optimizationBenchmarking.utils.document.spec.ISemanticComponent;
import org.optimizationBenchmarking.utils.ml.fitting.spec.IFittingResult;

/** the figure series renderer */
final class _FigureSeriesRenderer extends FigureSeriesRenderer {

  /** the owner */
  private final _Section m_owner;
  /** the selection */
  private final ISemanticComponent m_selection;
  /** the results */
  private final Entry<IInstanceRuns, IFittingResult>[] m_theResults;
  /** the internal iterator */
  private final ArrayIterator<Entry<IInstanceRuns, IFittingResult>> m_iterator;

  /** the label to use */
  final ILabel m_useLabel;

  /**
   * create the selection
   *
   * @param owner
   *          the owning section
   * @param selection
   *          the selection
   * @param results
   *          the results
   * @param useLabel
   *          the label to use
   */
  _FigureSeriesRenderer(final _Section owner,
      final ISemanticComponent selection,
      final Entry<IInstanceRuns, IFittingResult>[] results,
      final ILabel useLabel) {
    super();
    this.m_owner = owner;
    this.m_selection = selection;
    this.m_theResults = results;
    this.m_iterator = new ArrayIterator<>(results);
    this.m_useLabel = useLabel;
  }

  /**
   * create a base path component selection
   *
   * @param base
   *          the base
   * @param selection
   *          the selection
   * @param data
   *          the data
   * @return the component
   */
  static final String _basePathComponentSelection(final String base,
      final ISemanticComponent selection, final IExperimentSet data) {
    ICluster cluster;
    String baseStr;

    baseStr = base;
    if (data instanceof ICluster) {
      cluster = ((ICluster) data);
      baseStr = (baseStr + '/'
          + cluster.getOwner().getPathComponentSuggestion() + '/'
          + cluster.getPathComponentSuggestion());
    }

    if (selection == null) {
      return baseStr;
    }
    return (baseStr + '/' + selection.getPathComponentSuggestion());
  }

  /** {@inheritDoc} */
  @Override
  public final ILabel createFigureSeriesLabel(
      final ILabelBuilder builder) {
    return this.m_useLabel;
  }

  /** {@inheritDoc} */
  @Override
  public final String getFigureSeriesPathComponentSuggestion() {
    return _FigureSeriesRenderer._basePathComponentSelection(
        this.m_owner.m_pathComponent, this.m_selection,
        this.m_owner.m_data);
  }

  /** {@inheritDoc} */
  @Override
  public final EFigureSize getFigureSize() {
    return this.m_owner.m_job.m_figureSize;
  }

  /** {@inheritDoc} */
  @Override
  public final void renderFigureSeriesCaption(final IComplexText caption) {
    this.m_owner._renderFigureSeriesCaption(this.m_selection,
        this.m_theResults, caption);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean hasNext() {
    return this.m_iterator.hasNext();
  }

  /** {@inheritDoc} */
  @Override
  public final _FigureRenderer next() {
    final Entry<IInstanceRuns, IFittingResult> result;
    result = this.m_iterator.next();
    return new _FigureRenderer(this.m_owner, this.m_selection,
        result.getKey(), result.getValue(), this.m_useLabel);
  }
}