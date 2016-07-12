package org.optimizationBenchmarking.evaluator.evaluation.impl.all.modeling;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.optimizationBenchmarking.evaluator.attributes.PerInstanceRuns;
import org.optimizationBenchmarking.evaluator.attributes.clusters.ICluster;
import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IInstance;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;
import org.optimizationBenchmarking.evaluator.evaluation.utils.SectionRenderer;
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
  /** the path component */
  final String m_pathComponent;

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
  _Section(final IExperimentSet data,
      final PerInstanceRuns<IFittingResult> results, final EModelInfo info,
      final _ModelingJob job, final String pathComponent) {
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
    this.m_pathComponent = pathComponent;
  }

  /** {@inheritDoc} */
  @Override
  protected void renderSectionBody(final boolean isNewSection,
      final ISectionBody body) {
    SectionRenderer.renderSections(body, null, this);
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
    boolean has;

    has = false;
    if (this.m_info.m_printStatistics) {
      this.__writeStatistics(body, selection, results);
      has = true;
    }

    if (this.m_info.m_plotModels) {
      if (has) {
        body.appendLineBreak();
      }
      has = true;
      this.__writeFigures(body, selection, results);
    }

    if (this.m_info.m_printModels) {
      if (has) {
        body.appendLineBreak();
      }
      has = true;
      this.__writeResults(body, selection, results);
    }
  }

  /**
   * write the figures
   *
   * @param body
   *          the section body
   * @param selection
   *          the selection, or {@code null} if nothing was selected
   * @param results
   *          the selected results
   */
  private final void __writeFigures(final ISectionBody body,
      final ISemanticComponent selection,
      final Entry<IInstanceRuns, IFittingResult>[] results) {
    final ILabel figureLabel;

    figureLabel = body.createLabel(ELabelType.FIGURE);
    body.append("In ");//$NON-NLS-1$
    body.reference(ETextCase.IN_SENTENCE, ESequenceMode.AND, figureLabel);
    body.append(
        " we plot the fitted models over the actual measured data. Each one of the "); //$NON-NLS-1$
    this.m_job.m_strokeForRuns.appendDescription(ETextCase.IN_SENTENCE,
        body, false);
    body.append(" stands for an independent run, while the colored "); //$NON-NLS-1$
    this.m_job.m_strokeForModels.appendDescription(ETextCase.IN_SENTENCE,
        body, false);
    body.append(" represents the fitted model."); //$NON-NLS-1$

    new _FigureSeriesRenderer(this, selection)
        .render(this.m_job.m_figureConfig, results, figureLabel, body);
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
      record._table(body, this.m_job.m_dimY, selection);
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

  /**
   * render a figure series caption
   *
   * @param selection
   *          the selection
   * @param results
   *          the results
   * @param caption
   *          the caption text
   */
  void _renderFigureSeriesCaption(final ISemanticComponent selection,
      final Entry<IInstanceRuns, IFittingResult>[] results,
      final IComplexText caption) {
    caption.append("All models ("); //$NON-NLS-1$
    this.m_job.m_strokeForModels.appendDescription(ETextCase.IN_SENTENCE,
        caption, false);
    caption.append(" lines)");//$NON-NLS-1$
    if (selection != null) {
      caption.append(" for ");//$NON-NLS-1$
      this._printSelectionFull(caption, selection, ETextCase.IN_SENTENCE);
    }
    this.__printRestOfFigureDescription(caption);
  }

  /**
   * print the rest of the figure description.
   *
   * @param caption
   *          the caption
   */
  private final void __printRestOfFigureDescription(
      final IComplexText caption) {
    caption.append(" rendered on top of the actually measured runs ("); //$NON-NLS-1$
    this.m_job.m_strokeForRuns.appendDescription(ETextCase.IN_SENTENCE,
        caption, false);
    caption.append(" lines) in terms of "); //$NON-NLS-1$
    this.m_job.m_transformationY.printDescription(caption,
        ETextCase.IN_SENTENCE);
    caption.append(" over ");//$NON-NLS-1$
    this.m_job.m_transformationX.printDescription(caption,
        ETextCase.IN_SENTENCE);
    if (this.m_data instanceof ICluster) {
      caption.append(" for group ");//$NON-NLS-1$
      ((ICluster) (this.m_data)).printShortName(caption,
          ETextCase.IN_SENTENCE);
    }
    caption.append('.');
  }

  /**
   * render a figure series caption
   *
   * @param isPartOfSeries
   *          is this an element of a series or not?
   * @param selection
   *          the selection
   * @param runs
   *          the runs
   * @param result
   *          the fitting result
   * @param caption
   *          the caption text
   */
  void _renderFigureCaption(final boolean isPartOfSeries,
      final ISemanticComponent selection, final IInstanceRuns runs,
      final IFittingResult result, final IComplexText caption) {
    final _Model model;
    IExperiment experiment;
    IInstance instance;
    boolean has;

    model = this.m_job.m_models.get(result.getFittedFunction());

    if (!isPartOfSeries) {
      caption.append("Fitted model for "); //$NON-NLS-1$
    }
    experiment = runs.getOwner();
    if (experiment == selection) {
      has = false;
    } else {
      caption.append("setup ");//$NON-NLS-1$
      experiment.printShortName(caption, ETextCase.IN_SENTENCE);
      has = true;
    }
    instance = runs.getInstance();
    if (instance != selection) {
      if (has) {
        caption.append(" on ");//$NON-NLS-1$
      }
      caption.append("instance "); //$NON-NLS-1$
      instance.printShortName(caption, ETextCase.IN_SENTENCE);
    }
    caption.append(' ');
    caption.append(':');
    model._renderWithParametersStyles(caption,
        result.getFittedParametersRef());

    if (!isPartOfSeries) {
      caption.append(' ');
      caption.append('(');
      this.m_job.m_strokeForModels.appendDescription(ETextCase.IN_SENTENCE,
          caption, false);
      caption.append(" line)"); //$NON-NLS-1$
      this.__printRestOfFigureDescription(caption);
    }
  }

}