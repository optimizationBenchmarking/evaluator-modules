package org.optimizationBenchmarking.evaluator.evaluation.impl.all.modeling;

import java.util.Iterator;

import org.optimizationBenchmarking.evaluator.attributes.PerInstanceRuns;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;
import org.optimizationBenchmarking.utils.collections.iterators.InstanceIterator;
import org.optimizationBenchmarking.utils.document.spec.IComplexText;
import org.optimizationBenchmarking.utils.document.spec.IMath;
import org.optimizationBenchmarking.utils.document.spec.ISemanticComponent;
import org.optimizationBenchmarking.utils.ml.fitting.spec.IFittingResult;
import org.optimizationBenchmarking.utils.text.ETextCase;

/** the section for all */
final class _ForAll extends _Section {

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
  _ForAll(final IExperimentSet data,
      final PerInstanceRuns<IFittingResult> results, final EModelInfo info,
      final _ModelingJob job, final String pathComponent) {
    super(data, results, info, job, (pathComponent + "_all")); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  final ETextCase _printSelection(final IComplexText text,
      final ISemanticComponent selection, final ETextCase textCase) {
    return textCase.appendWords("any runs set", //$NON-NLS-1$
        text);
  }

  /** {@inheritDoc} */
  @Override
  final ETextCase _printSelectionFull(final IComplexText text,
      final ISemanticComponent selection, final ETextCase textCase) {
    return this._printSelection(text, selection, textCase);
  }

  /** {@inheritDoc} */
  @Override
  final void _printResultsIntro(final IComplexText text,
      final ISemanticComponent selection) {
    text.append("We now list all the fitted models: ");//$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  final void _printResultsPrototypeFunction(final IMath math) {
    try (final IMath func = math.nAryFunction(this.m_job.m_dimY.getName(),
        3, 3)) {
      try (final IComplexText text = func.text()) {
        text.append("benchmark instance"); //$NON-NLS-1$
      }
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
    runs.getInstance().printShortName(text, ETextCase.IN_SENTENCE);
    text.append(" and "); //$NON-NLS-1$
    runs.getOwner().printShortName(text, ETextCase.IN_SENTENCE);
  }

  /** {@inheritDoc} */
  @Override
  protected final void doRenderSectionTitle(final IComplexText title) {
    title.append("All Models without Grouping"); //$NON-NLS-1$
  }

  /** {@inheritDoc} */
  @Override
  public final Iterator<_InnerContents> iterator() {
    return new InstanceIterator<>(
        new _InnerContents(_ForAll.this, null, this.m_results.getAll()));
  }
}
