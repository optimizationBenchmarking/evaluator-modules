package org.optimizationBenchmarking.evaluator.evaluation.impl.all.modeling;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.optimizationBenchmarking.evaluator.attributes.PerInstanceRuns;
import org.optimizationBenchmarking.evaluator.attributes.clusters.ICluster;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;
import org.optimizationBenchmarking.evaluator.evaluation.impl.all.modeling._Section._InnerContents;
import org.optimizationBenchmarking.utils.document.impl.Renderers;
import org.optimizationBenchmarking.utils.document.impl.SectionRenderer;
import org.optimizationBenchmarking.utils.document.spec.ELabelType;
import org.optimizationBenchmarking.utils.document.spec.EMathComparison;
import org.optimizationBenchmarking.utils.document.spec.IComplexText;
import org.optimizationBenchmarking.utils.document.spec.ILabel;
import org.optimizationBenchmarking.utils.document.spec.IList;
import org.optimizationBenchmarking.utils.document.spec.IMath;
import org.optimizationBenchmarking.utils.document.spec.ISectionBody;
import org.optimizationBenchmarking.utils.document.spec.ISemanticComponent;
import org.optimizationBenchmarking.utils.math.statistics.IStatisticInfo;
import org.optimizationBenchmarking.utils.math.statistics.statisticInfo.StatisticInfoBuilder;
import org.optimizationBenchmarking.utils.ml.fitting.spec.IFittingResult;
import org.optimizationBenchmarking.utils.ml.fitting.spec.ParametricUnaryFunction;
import org.optimizationBenchmarking.utils.text.ESequenceMode;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.numbers.InTextNumberAppender;

/** the section class for printing contents */
abstract class _Section extends SectionRenderer
    implements Iterable<_InnerContents> {
  /** the data set */
  final IExperimentSet m_data;
  /** the results */
  final PerInstanceRuns<IFittingResult> m_results;
  /** the modeling job */
  final _ModelingJob m_job;
  /** the model info */
  final EModelInfo m_info;

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
   */
  _Section(final IExperimentSet data,
      final PerInstanceRuns<IFittingResult> results, final EModelInfo info,
      final _ModelingJob job) {
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
    if (info == null) {
      throw new IllegalArgumentException(
          "EModelInfo setting cannot be null."); //$NON-NLS-1$
    }
    if (job == null) {
      throw new IllegalArgumentException("Modeling jobcannot be null."); //$NON-NLS-1$
    }
    this.m_info = info;
    this.m_job = job;
  }

  /** {@inheritDoc} */
  @Override
  protected void doRenderSectionBody(final boolean isNewSection,
      final ISectionBody body) {
    Renderers.renderSections(body, null, this);
  }

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
  void _writeSubSectionBody(final boolean isNewSection,
      final ISectionBody body, final ISemanticComponent selection,
      final Entry<IInstanceRuns, IFittingResult>[] results) {
    if (this.m_info.m_printStatistics) {
      this.__writeStatistics(body, selection, results);
      if (this.m_info.m_printModels) {
        body.append(' ');
      }
    }
    if (this.m_info.m_printModels) {
      this.__writeResults(body, selection, results);
    }
  }

  /**
   * Render a lists of statistics
   *
   * @param results
   *          the results
   * @param body
   *          the body
   * @return the information records
   */
  private final _InfoRecord[] __getStatisticsTables(
      final ISectionBody body,
      final Map.Entry<IInstanceRuns, IFittingResult>[] results) {
    final HashMap<ParametricUnaryFunction, StatisticInfoBuilder[]> builderMap;
    final _InfoRecord[] records;
    IFittingResult result;
    StatisticInfoBuilder[] builders;
    IStatisticInfo[] infos;
    ParametricUnaryFunction function;
    double[] fitting;
    int index, recordIndex;

    builderMap = new HashMap<>();
    for (final Map.Entry<IInstanceRuns, IFittingResult> resultRec : results) {
      result = resultRec.getValue();
      function = result.getFittedFunction();
      builders = builderMap.get(function);
      if (builders == null) {
        index = function.getParameterCount();
        builders = new StatisticInfoBuilder[index];
        builderMap.put(function, builders);
        for (; (--index) >= 0;) {
          builders[index] = new StatisticInfoBuilder();
        }
      }
      fitting = result.getFittedParametersRef();
      for (index = builders.length; (--index) >= 0;) {
        builders[index].append(fitting[index]);
      }
    }

    recordIndex = 0;
    records = new _InfoRecord[builderMap.size()];
    outer: for (final _Model model : this.m_job.m_models.values()) {
      builders = builderMap.remove(model.m_function);
      if (builders == null) {
        continue outer;
      }
      infos = new IStatisticInfo[builders.length];
      for (index = infos.length; (--index) >= 0;) {
        infos[index] = builders[index].build();
        builders[index] = null;
      }
      builders = null;

      records[recordIndex++] = new _InfoRecord(model, infos,
          body.createLabel(ELabelType.TABLE),
          ((this.m_data instanceof ICluster) ? ((ICluster) (this.m_data))
              : null));
    }

    return records;
  }

  /**
   * print the selection
   *
   * @param text
   *          the destination text
   * @param selection
   *          the selection
   * @param textCase
   *          the text case to use
   * @return the next text case
   */
  abstract ETextCase _printSelection(final IComplexText text,
      final ISemanticComponent selection, final ETextCase textCase);

  /**
   * print the selection
   *
   * @param text
   *          the destination text
   * @param selection
   *          the selection
   * @param textCase
   *          the text case to use
   * @return the next text case
   */
  abstract ETextCase _printSelectionFull(final IComplexText text,
      final ISemanticComponent selection, final ETextCase textCase);

  /**
   * Render a list of results
   *
   * @param results
   *          the results
   * @param body
   *          the body
   * @param selection
   *          the component to render
   */
  private final void __writeStatistics(final ISectionBody body,
      final ISemanticComponent selection,
      final Map.Entry<IInstanceRuns, IFittingResult>[] results) {
    final _InfoRecord[] records;
    final ILabel[] labels;
    int index;

    records = this.__getStatisticsTables(body, results);
    index = records.length;

    body.append(
        "We first present statistics summarizing the parameters of the ");//$NON-NLS-1$
    InTextNumberAppender.INSTANCE.appendTo(index, ETextCase.IN_SENTENCE,
        body);
    body.append(" models that fit to ");//$NON-NLS-1$
    this._printSelectionFull(body, selection, ETextCase.IN_SENTENCE);
    body.append(" in "); //$NON-NLS-1$

    labels = new ILabel[index];
    for (; (--index) >= 0;) {
      labels[index] = records[index].m_label;
    }

    body.reference(ETextCase.IN_SENTENCE, ESequenceMode.AND, labels);
    body.append('.');
    for (final _InfoRecord record : records) {
      record._table(body, this.m_job.m_dimY);
    }
  }

  /**
   * print the selection
   *
   * @param text
   *          the destination text
   * @param selection
   *          the selection
   */
  abstract void _printResultsIntro(final IComplexText text,
      final ISemanticComponent selection);

  /**
   * print the prototype function
   *
   * @param math
   *          the math context
   */
  abstract void _printResultsPrototypeFunction(final IMath math);

  /**
   * print the key identifying a model
   *
   * @param runs
   *          the instance runs
   * @param text
   *          the destination text
   */
  abstract void _printResultsKey(final IInstanceRuns runs,
      final IComplexText text);

  /**
   * Render a list of results
   *
   * @param results
   *          the results
   * @param body
   *          the body
   * @param selection
   *          the component to render
   */
  private final void __writeResults(final ISectionBody body,
      final ISemanticComponent selection,
      final Map.Entry<IInstanceRuns, IFittingResult>[] results) {
    IFittingResult result;
    ParametricUnaryFunction function;

    this._printResultsIntro(body, selection);

    try (final IMath math = body.inlineMath()) {
      try (final IMath approx = math
          .compare(EMathComparison.APPROXIMATELY)) {
        this._printResultsPrototypeFunction(approx);
        try (final IComplexText text = approx.text()) {
          text.append('\u2026');
        }
      }
    }

    try (final IList list = body.itemization()) {
      for (final Map.Entry<IInstanceRuns, IFittingResult> entry : results) {
        try (final IComplexText item = list.item()) {
          this._printResultsKey(entry.getKey(), item);
          item.append(':');
          item.append(' ');
          result = entry.getValue();
          function = result.getFittedFunction();

          this.m_job.m_models.get(function)//
              ._renderWithParametersStyles(item,
                  result.getFittedParametersRef());
        }
      }
    }
  }

  /** the inner contents */
  final class _InnerContents extends SectionRenderer {
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
    protected final void doRenderSectionTitle(final IComplexText title) {// empty
      _Section.this._writeSubSectionTitle(title, this.m_innerSelection,
          this.m_innerResults);
    }

    /** {@inheritDoc} */
    @Override
    protected final void doRenderSectionBody(final boolean isNewSection,
        final ISectionBody body) {// empty
      _Section.this._writeSubSectionBody(isNewSection, body,
          this.m_innerSelection, this.m_innerResults);
    }
  }
}
