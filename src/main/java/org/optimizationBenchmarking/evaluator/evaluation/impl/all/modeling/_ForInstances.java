package org.optimizationBenchmarking.evaluator.evaluation.impl.all.modeling;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.optimizationBenchmarking.evaluator.attributes.PerInstanceRuns;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IInstance;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;
import org.optimizationBenchmarking.utils.collections.iterators.TransformingIterator;
import org.optimizationBenchmarking.utils.document.spec.IComplexText;
import org.optimizationBenchmarking.utils.document.spec.IMath;
import org.optimizationBenchmarking.utils.document.spec.ISectionBody;
import org.optimizationBenchmarking.utils.document.spec.ISemanticComponent;
import org.optimizationBenchmarking.utils.ml.fitting.spec.IFittingResult;
import org.optimizationBenchmarking.utils.text.ETextCase;

/** the section for instances */
final class _ForInstances extends _Section {

  /**
   * create
   *
   * @param data
   *          the data set
   * @param results
   *          the results
   * @param info
   *          the model info
   * @param job
   *          the owning modeling job
   * @param pathComponent
   *          the path component
   */
  _ForInstances(final IExperimentSet data,
      final PerInstanceRuns<IFittingResult> results, final EModelInfo info,
      final _ModelingJob job, final String pathComponent) {
    super(data, results, info, job, (pathComponent + "/instance"));//$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  final ETextCase _printSelection(final IComplexText text,
      final ISemanticComponent selection, final ETextCase textCase) {
    final ETextCase next;
    next = textCase.appendWords("benchmark instance", text);//$NON-NLS-1$
    text.append(' ');
    return selection.printShortName(text, next);
  }

  /** {@inheritDoc} */
  @Override
  final ETextCase _printSelectionFull(final IComplexText text,
      final ISemanticComponent selection, final ETextCase textCase) {
    final ETextCase next;
    next = this._printSelection(text, selection, textCase);
    text.append(' ');
    return next.appendWords("for any algorithm setup", text);//$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  final void _printResultsIntro(final IComplexText text,
      final ISemanticComponent selection) {
    text.append(
        "We now list the fitted models for each algorithm setup which was applied to benchmark instance ");//$NON-NLS-1$
    selection.printShortName(text, ETextCase.IN_SENTENCE);
    text.append('.');
  }

  /** {@inheritDoc} */
  @Override
  final void _printResultsPrototypeFunction(final IMath math) {
    try (final IMath func = math.nAryFunction(this.m_job.m_dimY.getName(),
        2, 2)) {
      try (final IComplexText text = func.text()) {
        text.append("algorithm setup"); //$NON-NLS-1$
      }
      this.m_job.m_dimX.mathRender(func, _ModelingJob.RENDERER);
    }
  }

  /** {@inheritDoc} */
  @Override
  final void _printResultsKey(final IInstanceRuns runs,
      final IComplexText text) {
    runs.getOwner().printShortName(text, ETextCase.IN_SENTENCE);
  }

  /** {@inheritDoc} */
  @Override
  final void _writeSubSectionBody(final boolean isNewSection,
      final ISectionBody body, final ISemanticComponent selection,
      final Entry<IInstanceRuns, IFittingResult>[] results) {

    if (!(isNewSection)) {
      body.append(
          " Only a single benchmark instance was modeled, namely "); //$NON-NLS-1$
      selection.printShortName(body, ETextCase.IN_SENTENCE);
      body.append('.');
      body.append(' ');
    }

    super._writeSubSectionBody(isNewSection, body, selection, results);
  }

  /** {@inheritDoc} */
  @Override
  final void _writeSubSectionTitle(final IComplexText title,
      final ISemanticComponent selection,
      final Map.Entry<IInstanceRuns, IFittingResult>[] results) {
    this._printSelection(title, selection, ETextCase.AT_TITLE_START);
  }

  /** {@inheritDoc} */
  @Override
  protected final void doRenderSectionTitle(final IComplexText title) {
    title.append("Models Grouped by Benchmark Instance"); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public final Iterator<_InnerContents> iterator() {
    return new _Transform(this.m_data.getInstances().getData().iterator());
  }

  /** the transformation from instance to sections */
  private final class _Transform
      extends TransformingIterator<IInstance, _InnerContents> {
    /**
     * create
     *
     * @param inner
     *          the inner iterator
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    _Transform(final Iterator inner) {
      super(inner);
    }

    /** {@inheritDoc} */
    @Override
    protected final _InnerContents transform(final IInstance element) {
      final Map.Entry<IInstanceRuns, IFittingResult>[] list;

      list = _ForInstances.this.m_results.getAllForInstance(element);
      if ((list != null) && (list.length > 0)) {
        return new _InnerContents(_ForInstances.this, element, list);
      }
      return null;
    }
  }
}
