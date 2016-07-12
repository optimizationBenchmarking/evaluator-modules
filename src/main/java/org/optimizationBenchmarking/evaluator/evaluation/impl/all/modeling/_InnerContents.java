package org.optimizationBenchmarking.evaluator.evaluation.impl.all.modeling;

import java.util.Map;

import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;
import org.optimizationBenchmarking.evaluator.evaluation.utils.SectionRenderer;
import org.optimizationBenchmarking.utils.document.spec.IComplexText;
import org.optimizationBenchmarking.utils.document.spec.ISectionBody;
import org.optimizationBenchmarking.utils.document.spec.ISemanticComponent;
import org.optimizationBenchmarking.utils.ml.fitting.spec.IFittingResult;

/** the inner contents delegating back to the section class */
final class _InnerContents extends SectionRenderer {
  /** the owner */
  private final _Section m_owner;
  /** the selection */
  private final ISemanticComponent m_innerSelection;
  /** the results */
  private final Map.Entry<IInstanceRuns, IFittingResult>[] m_innerResults;

  /**
   * Write the contents section
   *
   * @param owner
   *          the owning section
   * @param selection
   *          the selection, or {@code null} if nothing was selected
   * @param results
   *          the selected results
   */
  _InnerContents(final _Section owner, final ISemanticComponent selection,
      final Map.Entry<IInstanceRuns, IFittingResult>[] results) {
    super();
    this.m_owner = owner;
    this.m_innerSelection = selection;
    this.m_innerResults = results;
  }

  /** {@inheritDoc} */
  @Override
  protected final void renderSectionTitle(final IComplexText title) {// empty
    this.m_owner._writeSubSectionTitle(title, this.m_innerSelection,
        this.m_innerResults);
  }

  /** {@inheritDoc} */
  @Override
  protected final void renderSectionBody(final boolean isNewSection,
      final ISectionBody body) {// empty
    this.m_owner._writeSubSectionBody(isNewSection, body,
        this.m_innerSelection, this.m_innerResults);
  }
}