package org.optimizationBenchmarking.evaluator.evaluation.impl.all.modeling;

import java.util.Map;

import org.optimizationBenchmarking.evaluator.attributes.PerInstanceRuns;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;
import org.optimizationBenchmarking.utils.document.impl.OptionalElements;
import org.optimizationBenchmarking.utils.document.impl.OptionalSection;
import org.optimizationBenchmarking.utils.document.spec.IComplexText;
import org.optimizationBenchmarking.utils.document.spec.ISectionBody;
import org.optimizationBenchmarking.utils.document.spec.ISemanticComponent;
import org.optimizationBenchmarking.utils.ml.fitting.spec.IFittingResult;

/**
 * This class builds the base class for printing contents sections. It does
 * not yet print anything, but allows us to optionally have sections or
 * subsections for different selected elements.
 */
abstract class _AbstractSection extends OptionalSection {

  /** the data set */
  final IExperimentSet m_data;
  /** the results */
  private final PerInstanceRuns<IFittingResult> m_results;

  /** do we have multiple contents sections? */
  private boolean m_hasMultipleContents;
  /** the cached selection */
  private ISemanticComponent m_cachedSelection;
  /** the cached selection */
  private Map.Entry<IInstanceRuns, IFittingResult>[] m_cachedResuls;

  /**
   * create
   *
   * @param data
   *          the data set
   * @param results
   *          the results
   */
  _AbstractSection(final IExperimentSet data,
      final PerInstanceRuns<IFittingResult> results) {
    super();
    if (data == null) {
      throw new IllegalArgumentException("IExperimentSet cannot be null."); //$NON-NLS-1$
    }
    if (results == null) {
      throw new IllegalArgumentException(
          "PerInstanceRuns cannot be null."); //$NON-NLS-1$
    }
    this.m_data = data;
    this.m_results = results;
  }

  /**
   * Print the body of the section. Here we will select the data to be
   * printed and write an introduction.
   *
   * @param isNewSection
   *          was a new section created ({@code true}) or an already
   *          section existing ({@code false})?
   * @param body
   *          the section body
   * @param results
   *          the results to select from
   */
  abstract void _writeSectionBody(final boolean isNewSection,
      final ISectionBody body,
      final PerInstanceRuns<IFittingResult> results);

  /** {@inheritDoc} */
  @Override
  public final void writeSectionBody(final boolean isNewSection,
      final ISectionBody body) {
    if (!(isNewSection)) {
      body.appendLineBreak();
    }
    this._writeSectionBody(isNewSection, body, this.m_results);
    if (this.m_cachedResuls != null) {
      try {
        this.__doWriteSubSection(body, this.m_cachedSelection,
            this.m_cachedResuls);
      } finally {
        this.m_cachedResuls = null;
        this.m_cachedSelection = null;
      }
    }
  }

  /**
   * Write the contents section
   *
   * @param body
   *          the section body
   * @param selection
   *          the selection, or {@code null} if nothing was selected
   * @param results
   *          the selected results
   */
  final void _writeSubSectionFor(final ISectionBody body,
      final ISemanticComponent selection,
      final Map.Entry<IInstanceRuns, IFittingResult>[] results) {
    if (!(this.m_hasMultipleContents)) {
      if (this.m_cachedSelection == null) {
        this.m_cachedSelection = selection;
        this.m_cachedResuls = results;
        return;
      }
      this.m_hasMultipleContents = true;
      try {
        this.__doWriteSubSection(body, this.m_cachedSelection,
            this.m_cachedResuls);
      } finally {
        this.m_cachedResuls = null;
        this.m_cachedSelection = null;
      }
    }
    this.__doWriteSubSection(body, selection, results);
  }

  /**
   * Write the contents section
   *
   * @param body
   *          the section body
   * @param selection
   *          the selection, or {@code null} if nothing was selected
   * @param results
   *          the selected results
   */
  private final void __doWriteSubSection(final ISectionBody body,
      final ISemanticComponent selection,
      final Map.Entry<IInstanceRuns, IFittingResult>[] results) {
    OptionalElements.optionalSection(body, this.m_hasMultipleContents,
        null, new _InnerContents(selection, results));
  }

  /**
   * Write the body of a contents section
   *
   * @param isNewSection
   *          is the section a new section or not?
   * @param body
   *          the section body
   * @param selection
   *          the selection, or {@code null} if nothing was selected
   * @param results
   *          the selected results
   */
  abstract void _writeSubSectionBody(final boolean isNewSection,
      final ISectionBody body, final ISemanticComponent selection,
      final Map.Entry<IInstanceRuns, IFittingResult>[] results);

  /**
   * write the title of a contents section
   *
   * @param title
   *          the title of the contents section
   * @param selection
   *          the selection, or {@code null} if nothing was selected
   * @param results
   *          the selected results
   */
  void _writeSubSectionTitle(final IComplexText title,
      final ISemanticComponent selection,
      final Map.Entry<IInstanceRuns, IFittingResult>[] results) {
    //
  }

  /** the inner contents */
  private final class _InnerContents extends OptionalSection {
    /** the selection */
    private final ISemanticComponent m_innerSelection;
    /** the results */
    private final Map.Entry<IInstanceRuns, IFittingResult>[] m_innerResults;

    /**
     * Write the contents section
     *
     * @param selection
     *          the selection, or {@code null} if nothing was selected
     * @param results
     *          the selected results
     */
    _InnerContents(final ISemanticComponent selection,
        final Map.Entry<IInstanceRuns, IFittingResult>[] results) {
      super();
      this.m_innerSelection = selection;
      this.m_innerResults = results;
    }

    /** {@inheritDoc} */
    @Override
    public final void writeSectionTitle(final IComplexText title) {// empty
      _AbstractSection.this._writeSubSectionTitle(title,
          this.m_innerSelection, this.m_innerResults);
    }

    /** {@inheritDoc} */
    @Override
    public final void writeSectionBody(final boolean isNewSection,
        final ISectionBody body) {// empty
      _AbstractSection.this._writeSubSectionBody(isNewSection, body,
          this.m_innerSelection, this.m_innerResults);
    }
  }
}
