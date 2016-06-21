package org.optimizationBenchmarking.evaluator.evaluation.impl.all.modeling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.attributes.PerInstanceRuns;
import org.optimizationBenchmarking.evaluator.attributes.modeling.DimensionRelationship;
import org.optimizationBenchmarking.evaluator.attributes.modeling.DimensionRelationshipModels;
import org.optimizationBenchmarking.evaluator.data.spec.IDimension;
import org.optimizationBenchmarking.evaluator.data.spec.IDimensionSet;
import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IInstance;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;
import org.optimizationBenchmarking.evaluator.evaluation.impl.abstr.ExperimentSetJob;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.comparison.Compare;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.document.impl.SemanticComponentUtils;
import org.optimizationBenchmarking.utils.document.spec.ELabelType;
import org.optimizationBenchmarking.utils.document.spec.EMathComparison;
import org.optimizationBenchmarking.utils.document.spec.ETableCellDef;
import org.optimizationBenchmarking.utils.document.spec.IComplexText;
import org.optimizationBenchmarking.utils.document.spec.IDocument;
import org.optimizationBenchmarking.utils.document.spec.ILabel;
import org.optimizationBenchmarking.utils.document.spec.IList;
import org.optimizationBenchmarking.utils.document.spec.IMath;
import org.optimizationBenchmarking.utils.document.spec.IParameterRenderer;
import org.optimizationBenchmarking.utils.document.spec.IPlainText;
import org.optimizationBenchmarking.utils.document.spec.ISection;
import org.optimizationBenchmarking.utils.document.spec.ISectionBody;
import org.optimizationBenchmarking.utils.document.spec.ISectionContainer;
import org.optimizationBenchmarking.utils.document.spec.ISemanticComponent;
import org.optimizationBenchmarking.utils.document.spec.ITable;
import org.optimizationBenchmarking.utils.document.spec.ITableRow;
import org.optimizationBenchmarking.utils.document.spec.ITableSection;
import org.optimizationBenchmarking.utils.graphics.style.spec.IStyle;
import org.optimizationBenchmarking.utils.graphics.style.spec.IStyles;
import org.optimizationBenchmarking.utils.math.statistics.IStatisticInfo;
import org.optimizationBenchmarking.utils.math.statistics.statisticInfo.StatisticInfoBuilder;
import org.optimizationBenchmarking.utils.math.statistics.statisticInfo.StatisticInfoPrinter;
import org.optimizationBenchmarking.utils.math.text.ABCParameterRenderer;
import org.optimizationBenchmarking.utils.math.text.DoubleConstantParameters;
import org.optimizationBenchmarking.utils.ml.fitting.spec.IFittingResult;
import org.optimizationBenchmarking.utils.ml.fitting.spec.ParametricUnaryFunction;
import org.optimizationBenchmarking.utils.text.ESequenceMode;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.ISequenceable;
import org.optimizationBenchmarking.utils.text.numbers.InTextNumberAppender;
import org.optimizationBenchmarking.utils.text.numbers.NumberAppender;
import org.optimizationBenchmarking.utils.text.numbers.TextNumberAppender;
import org.optimizationBenchmarking.utils.text.numbers.TruncatedNumberAppender;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/** the modeling job */
final class _ModelingJob extends ExperimentSetJob {

  /** the parameter renderer */
  static final IParameterRenderer RENDERER = ABCParameterRenderer.INSTANCE;

  /** the number appender to use */
  private static final NumberAppender APPENDER = TruncatedNumberAppender.INSTANCE;

  /** the {@code x}-axis dimension */
  final IDimension m_dimX;
  /** the {@code y}-axis dimension */
  private final IDimension m_dimY;

  /** the dimension relationship attribute */
  private final DimensionRelationship m_attribute;

  /** Overall, unsorted printing */
  private final EModelInfo m_overall;
  /** Should we list the models per algorithm? */
  private final EModelInfo m_perAlgorithm;
  /** Should we list the models per benchmark instance? */
  private final EModelInfo m_perInstance;

  /**
   * Create the modeling job
   *
   * @param data
   *          the data
   * @param logger
   *          the logger
   * @param config
   *          the configuration
   */
  _ModelingJob(final IExperimentSet data, final Configuration config,
      final Logger logger) {
    super(data, logger);

    final IDimensionSet dimensions;

    dimensions = data.getDimensions();
    this.m_dimX = dimensions.find(config.getString(Modeler.PARAM_X, null));
    if (this.m_dimX == null) {
      throw new IllegalArgumentException(((//
      "Must provide a source/x dimension via parameter '" //$NON-NLS-1$
          + Modeler.PARAM_X) + '\'') + '.');
    }
    this.m_dimY = dimensions.find(config.getString(Modeler.PARAM_Y, null));
    if (this.m_dimY == null) {
      throw new IllegalArgumentException(((//
      "Must provide a target/y dimension via parameter '" //$NON-NLS-1$
          + Modeler.PARAM_Y) + '\'') + '.');
    }

    if (Compare.equals(this.m_dimX, this.m_dimY)) {
      throw new IllegalArgumentException(((//
      "Source/x and target/y dimension cannot be the same, but both are set to '" //$NON-NLS-1$
          + this.m_dimX.getName()) + '\'') + '.');
    }

    this.m_attribute = new DimensionRelationship(this.m_dimX, this.m_dimY);

    this.m_perAlgorithm = config.get(Modeler.PARAM_PER_ALGORITHM,
        ModelInfoParser.INSTANCE, EModelInfo.NONE);
    this.m_perInstance = config.get(Modeler.PARAM_PER_INSTANCE,
        ModelInfoParser.INSTANCE, EModelInfo.NONE);
    this.m_overall = config.get(Modeler.PARAM_OVERALL,
        ModelInfoParser.INSTANCE, //
        ((this.m_perAlgorithm == EModelInfo.NONE)
            && (this.m_perInstance == EModelInfo.NONE))//
                ? EModelInfo.STATISTICS_ONLY : EModelInfo.NONE);
  }

  /** {@inheritDoc} */
  @Override
  protected final void doMain(final IExperimentSet data,
      final ISectionContainer sectionContainer, final Logger logger) {
    final IStyles styles;
    final int sections;
    final PerInstanceRuns<IFittingResult> results;

    results = new PerInstanceRuns<>(data, this.m_attribute, logger);

    try (final ISection section = sectionContainer.section(null)) {
      styles = section.getStyles();
      try (final IComplexText title = section.title()) {
        title.append("Modeling Algorithm Behavior in Terms of ");//$NON-NLS-1$
        this.m_dimY.printLongName(title, ETextCase.IN_TITLE);
        title.append(" over ");//$NON-NLS-1$
        this.m_dimX.printLongName(title, ETextCase.IN_TITLE);
        title.append(" per Benchmark Instance"); //$NON-NLS-1$
      }
      try (final ISectionBody body = section.body()) {
        this.__writeIntro(data, body, styles);

        sections = ((this.m_overall != EModelInfo.NONE) ? 1 : 0) + //
            ((this.m_perAlgorithm != EModelInfo.NONE) ? 1 : 0) + //
            ((this.m_perInstance != EModelInfo.NONE) ? 1 : 0);//

        if (this.m_overall != EModelInfo.NONE) {
          if (sections > 1) {
            try (final ISection subsection = body.section(null)) {
              try (final IComplexText subtitle = subsection.title()) {
                subtitle.append(//
                    "Overall Model Information"); //$NON-NLS-1$
              }
              try (final ISectionBody subbody = subsection.body()) {
                this.__writeOverall(data, subbody, styles, results);
              }
            }
          } else {
            body.appendLineBreak();
            this.__writeOverall(data, body, styles, results);
          }
        }

        if (this.m_perAlgorithm != EModelInfo.NONE) {
          if (sections > 1) {
            try (final ISection subsection = body.section(null)) {
              try (final IComplexText subtitle = subsection.title()) {
                subtitle.append(//
                    "Models Grouped by Algorithm Setup"); //$NON-NLS-1$
              }
              try (final ISectionBody subbody = subsection.body()) {
                this.__writePerAlgorithm(data, subbody, styles, results);
              }
            }
          } else {
            body.appendLineBreak();
            this.__writePerAlgorithm(data, body, styles, results);
          }
        }

        if (this.m_perInstance != EModelInfo.NONE) {
          if (sections > 1) {
            try (final ISection subsection = body.section(null)) {
              try (final IComplexText subtitle = subsection.title()) {
                subtitle.append(//
                    "Models Grouped by Benchmark Instance Setup"); //$NON-NLS-1$
              }
              try (final ISectionBody subbody = subsection.body()) {
                this.__writePerInstance(data, subbody, styles, results);
              }
            }
          } else {
            body.appendLineBreak();
            this.__writePerInstance(data, body, styles, results);
          }
        }

      }
    }
  }

  /**
   * Get the models used for the fitting.
   *
   * @return the models
   */
  private final ArrayListView<ParametricUnaryFunction> __models() {
    return DimensionRelationshipModels.getModels(this.m_dimX, this.m_dimY);
  }

  /** {@inheritDoc} */
  @Override
  protected final void doInitialize(final IExperimentSet data,
      final IDocument document, final Logger logger) {
    final IStyles styles;

    styles = document.getStyles();
    for (final ParametricUnaryFunction func : this.__models()) {
      _ModelingJob.__modelColor(styles, func);
    }
  }

  /**
   * get the color of a given model
   *
   * @param styles
   *          the style set
   * @param func
   *          the function
   * @return the color
   */
  private static final IStyle __modelColor(final IStyles styles,
      final ParametricUnaryFunction func) {
    return styles.getColor(func.getClass().getCanonicalName(), true);
  }

  /**
   * Write the introduction text regarding the modeling.
   *
   * @param data
   *          the data
   * @param body
   *          the body
   * @param styles
   *          the provided styles
   */
  private final void __writeIntro(final IExperimentSet data,
      final ISectionBody body, final IStyles styles) {
    final ArrayListView<ParametricUnaryFunction> models;

    body.append(
        "In order to better understand the behavior of the investigated algorithms, we try to fit models, i.e., mathematical functions, to their behavior.");//$NON-NLS-1$
    body.append(
        " The input (x-axis, domain) of the functions is the measured dimension ");//$NON-NLS-1$
    SemanticComponentUtils.printLongAndShortNameIfDifferent(this.m_dimX,
        body, ETextCase.IN_SENTENCE);
    body.append(" and the output (y-axis, codomain) is ");//$NON-NLS-1$
    SemanticComponentUtils.printLongAndShortNameIfDifferent(this.m_dimY,
        body, ETextCase.IN_SENTENCE);
    body.append(". In other words, we want to find a function ");//$NON-NLS-1$
    try (final IMath rootMath = body.inlineMath()) {
      try (final IMath equals = rootMath.compare(EMathComparison.EQUAL)) {
        this.m_dimY.mathRender(equals, _ModelingJob.RENDERER);
        try (final IMath func = equals.nAryFunction("f", 1, 1)) {//$NON-NLS-1$
          try (final IMath braces = func.inBraces()) {
            this.m_dimX.mathRender(braces, _ModelingJob.RENDERER);
          }
        }
      }
    }
    body.append(
        " which describes how the algorithms progress in terms of ");//$NON-NLS-1$
    this.m_dimY.printShortName(body, ETextCase.IN_SENTENCE);
    body.append(" over ");//$NON-NLS-1$
    this.m_dimX.printShortName(body, ETextCase.IN_SENTENCE);
    body.append('.');

    body.appendLineBreak();

    body.append("Obviously, we cannot know the nature of ");//$NON-NLS-1$
    try (final IMath rootMath = body.inlineMath()) {
      try (final IMath func = rootMath.nAryFunction("f", 1, 1)) {//$NON-NLS-1$
        try (final IMath braces = func.inBraces()) {
          this.m_dimX.mathRender(braces, _ModelingJob.RENDERER);
        }
      }
    }
    body.append(
        " in advance, which complicates things. However, many optimization algorithms have behaviors which fit to similar ");//$NON-NLS-1$
    try (final IPlainText quotes = body.inQuotes()) {
      quotes.append("categories.");//$NON-NLS-1$
    }
    models = this.__models();
    body.append(" We therefore simply use a selection of ");//$NON-NLS-1$
    TextNumberAppender.INSTANCE.appendTo(models.size(),
        ETextCase.IN_SENTENCE, body);
    body.append(" models, namely ");//$NON-NLS-1$
    ESequenceMode.AND.appendSequence(ETextCase.IN_SENTENCE,
        this.__getSequenceableModels(styles), true, body);
    body.append(
        ". We fit each model to the data and take the best-fitting model. Of course, this process has to be done for each of the ");//$NON-NLS-1$
    TextNumberAppender.INSTANCE.appendTo(data.getData().size(),
        ETextCase.IN_SENTENCE, body);
    body.append(" algorithm setups on each of the ");//$NON-NLS-1$
    TextNumberAppender.INSTANCE.appendTo(
        data.getInstances().getData().size(), //
        ETextCase.IN_SENTENCE, body);
    body.append(" benchmark instances.");//$NON-NLS-1$

    body.appendLineBreak();
    body.append(//
        "Since all models are fitted on measured data, there are two sources of errors that may bias the results: First, the measurements may be imprecise. Second, we cannot guarantee that the fitting algorithms will find the globally best fit for a model or even the best model. Thus, all ");//$NON-NLS-1$
    _ModelingJob.APPENDER.printDescription(body, ETextCase.IN_SENTENCE);
    body.append('.');
  }

  /**
   * Write the overall results
   *
   * @param data
   *          the experiment set
   * @param body
   *          the body
   * @param styles
   *          the styles
   * @param results
   *          the results
   */
  private final void __writeOverall(final IExperimentSet data,
      final ISectionBody body, final IStyles styles,
      final PerInstanceRuns<IFittingResult> results) {

    this.__writeContents(body, styles, results.getAll(), null,
        this.m_overall);
  }

  /**
   * Write the results per algorithm
   *
   * @param data
   *          the experiment set
   * @param body
   *          the body
   * @param styles
   *          the styles
   * @param results
   *          the results
   */
  private final void __writePerAlgorithm(final IExperimentSet data,
      final ISectionBody body, final IStyles styles,
      final PerInstanceRuns<IFittingResult> results) {
    Map.Entry<IInstanceRuns, IFittingResult>[] list;

    for (final IExperiment experiment : data.getData()) {
      list = results.getAllForExperiment(experiment);
      if ((list != null) && (list.length > 0)) {
        try (final ISection subsection = body.section(null)) {
          try (final IComplexText subtitle = subsection.title()) {
            experiment.printShortName(subtitle, ETextCase.AT_TITLE_START);
          }
          try (final ISectionBody subbody = subsection.body()) {
            this.__writeContents(subbody, styles, list, experiment,
                this.m_perAlgorithm);
          }
        }
      }
    }
  }

  /**
   * Write the results per algorithm
   *
   * @param data
   *          the experiment set
   * @param body
   *          the body
   * @param styles
   *          the styles
   * @param results
   *          the results
   */
  private final void __writePerInstance(final IExperimentSet data,
      final ISectionBody body, final IStyles styles,
      final PerInstanceRuns<IFittingResult> results) {
    Map.Entry<IInstanceRuns, IFittingResult>[] list;

    for (final IInstance instance : data.getInstances().getData()) {
      list = results.getAllForInstance(instance);
      if ((list != null) && (list.length > 0)) {
        try (final ISection subsection = body.section(null)) {
          try (final IComplexText subtitle = subsection.title()) {
            instance.printShortName(subtitle, ETextCase.AT_TITLE_START);
          }
          try (final ISectionBody subbody = subsection.body()) {
            this.__writeContents(subbody, styles, list, instance,
                this.m_perInstance);
          }
        }
      }
    }
  }

  /**
   * Render the contents
   *
   * @param results
   *          the results
   * @param body
   *          the body
   * @param styles
   *          the provided styles
   * @param component
   *          the semantic component
   * @param info
   *          the information to present
   */
  private final void __writeContents(final ISectionBody body,
      final IStyles styles,
      final Map.Entry<IInstanceRuns, IFittingResult>[] results,
      final ISemanticComponent component, final EModelInfo info) {

    if (info.m_printStatistics) {
      this.__writeStatistics(body, styles, results, component);
    }
    if (info.m_printModels) {
      this.__writeResults(body, styles, results, component);
    }
  }

  /**
   * Render a list of results
   *
   * @param results
   *          the results
   * @param body
   *          the body
   * @param styles
   *          the provided styles
   * @param component
   *          the semantic component
   */
  private final void __writeResults(final ISectionBody body,
      final IStyles styles,
      final Map.Entry<IInstanceRuns, IFittingResult>[] results,
      final ISemanticComponent component) {
    final int nparams;
    final boolean groupedByInstance, groupedByAlgorithm;
    IFittingResult result;
    ParametricUnaryFunction function;
    IInstanceRuns runs;

    if (component != null) {
      if (component instanceof IInstance) {
        body.append(
            "We now present the fitted models for each algorithm setup which was applied to benchmark instance ");//$NON-NLS-1$
        component.printShortName(body, ETextCase.IN_SENTENCE);
        body.append('.');
        nparams = 2;
        groupedByInstance = true;
        groupedByAlgorithm = false;
      } else {
        body.append(
            "We now present the fitted models for each benchmark instance to which algorithm setup ");//$NON-NLS-1$
        component.printShortName(body, ETextCase.IN_SENTENCE);
        body.append("was applied.");//$NON-NLS-1$
        nparams = 2;
        groupedByAlgorithm = true;
        groupedByInstance = false;
      }
    } else {
      body.append("We now present all the fitted models: ");//$NON-NLS-1$
      nparams = 3;
      groupedByAlgorithm = groupedByInstance = false;
    }

    try (final IMath math = body.inlineMath()) {
      try (final IMath approx = math
          .compare(EMathComparison.APPROXIMATELY)) {
        try (final IMath func = approx.nAryFunction(this.m_dimY.getName(),
            nparams, nparams)) {

          if (!groupedByInstance) {
            try (final IComplexText text = func.text()) {
              text.append("benchmark instance"); //$NON-NLS-1$
            }
          }
          if (!groupedByAlgorithm) {
            try (final IComplexText text = func.text()) {
              text.append("algorithm setup"); //$NON-NLS-1$
            }
          }
          this.m_dimX.mathRender(func, _ModelingJob.RENDERER);
        }
        try (final IComplexText text = approx.text()) {
          text.append('\u2026');
        }
      }
    }

    try (final IList list = body.itemization()) {
      for (final Map.Entry<IInstanceRuns, IFittingResult> entry : results) {
        try (final IComplexText item = list.item()) {
          runs = entry.getKey();
          if (!groupedByInstance) {
            runs.getInstance().printShortName(item, ETextCase.IN_SENTENCE);
            if (!groupedByAlgorithm) {
              item.append(" and "); //$NON-NLS-1$
            }
          }
          if (!groupedByAlgorithm) {
            runs.getOwner().printShortName(item, ETextCase.IN_SENTENCE);
          }
          item.append(':');
          item.append(' ');
          result = entry.getValue();
          function = result.getFittedFunction();
          try (final IComplexText functionText = item
              .style(_ModelingJob.__modelColor(styles, function))) {
            try (final IMath math = functionText.inlineMath()) {
              function.mathRender(math,
                  new DoubleConstantParameters(_ModelingJob.APPENDER,
                      result.getFittedParametersRef()),
                  this.m_dimX);
            }
          }
        }
      }
    }
  }

  /**
   * Render a list of results
   *
   * @param results
   *          the results
   * @param body
   *          the body
   * @param styles
   *          the provided styles
   * @param component
   *          the component to render
   */
  private final void __writeStatistics(final ISectionBody body,
      final IStyles styles,
      final Map.Entry<IInstanceRuns, IFittingResult>[] results,
      final ISemanticComponent component) {
    final __InfoRecord[] records;
    final ILabel[] labels;
    int index;

    records = this.__getStatisticsTables(body, styles, results);
    index = records.length;

    body.append(
        "We first present statistics summarizing the parameters of the ");//$NON-NLS-1$
    InTextNumberAppender.INSTANCE.appendTo(index, ETextCase.IN_SENTENCE,
        body);
    body.append(" models that fit to ");//$NON-NLS-1$
    if (component != null) {
      body.append("the run sets in ");//$NON-NLS-1$
      if (component instanceof IExperiment) {
        body.append("algorithm setup ");//$NON-NLS-1$
        component.printShortName(body, ETextCase.IN_SENTENCE);
        body.append(" on any benchmark instance");//$NON-NLS-1$
      } else {
        if (component instanceof IExperiment) {
          body.append("benchmark instance ");//$NON-NLS-1$
          component.printShortName(body, ETextCase.IN_SENTENCE);
          body.append(" on for any algorithm setup");//$NON-NLS-1$
        }
      }
    } else {
      body.append("any runs set");//$NON-NLS-1$
    }
    body.append(" in "); //$NON-NLS-1$

    labels = new ILabel[index];
    for (; (--index) >= 0;) {
      labels[index] = records[index].m_label;
    }

    body.reference(ETextCase.IN_SENTENCE, ESequenceMode.AND, labels);
    body.append('.');
    for (final __InfoRecord record : records) {
      this.__writeStatisticsTable(body, styles, record);
    }
  }

  /**
   * Render a lists of statistics
   *
   * @param results
   *          the results
   * @param body
   *          the body
   * @param styles
   *          the provided styles
   * @return the information records
   */
  private final __InfoRecord[] __getStatisticsTables(
      final ISectionBody body, final IStyles styles,
      final Map.Entry<IInstanceRuns, IFittingResult>[] results) {
    final HashMap<ParametricUnaryFunction, StatisticInfoBuilder[]> builderMap;
    final __InfoRecord[] records;
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
    records = new __InfoRecord[builderMap.size()];
    outer: for (final ParametricUnaryFunction functionx : this
        .__models()) {
      builders = builderMap.remove(functionx);
      if (builders == null) {
        continue outer;
      }
      infos = new IStatisticInfo[builders.length];
      for (index = infos.length; (--index) >= 0;) {
        infos[index] = builders[index].build();
        builders[index] = null;
      }
      builders = null;

      records[recordIndex++] = new __InfoRecord(functionx, infos,
          body.createLabel(ELabelType.TABLE));
    }

    return records;
  }

  /**
   * Render a lists of statistics
   *
   * @param body
   *          the body
   * @param styles
   *          the provided styles
   * @param record
   *          the record to print
   */
  private final void __writeStatisticsTable(final ISectionBody body,
      final IStyles styles, final __InfoRecord record) {
    ETableCellDef[] defs;
    int columnIndex, rowIndex;

    defs = new ETableCellDef[record.m_information.length + 1];
    Arrays.fill(defs, ETableCellDef.RIGHT);
    defs[0] = ETableCellDef.LEFT;

    try (final ITable table = body.table(record.m_label, false, defs)) {
      try (final IComplexText caption = table.caption()) {
        caption.append("Statistical information about the ");//$NON-NLS-1$
        InTextNumberAppender.INSTANCE.appendTo(record.m_information.length,
            ETextCase.IN_SENTENCE, caption);
        caption.append(" parameters of each of the ");//$NON-NLS-1$
        InTextNumberAppender.INSTANCE.appendTo(
            record.m_information[0].getSampleSize(), ETextCase.IN_SENTENCE,
            caption);
        caption.append(" cases where model ");//$NON-NLS-1$
        try (final IComplexText text = caption
            .style(_ModelingJob.__modelColor(styles, record.m_function))) {
          try (final IMath math = text.inlineMath()) {
            record.m_function.mathRender(math, _ModelingJob.RENDERER);
          }
        }
        caption.append(" fit to describe how ");//$NON-NLS-1$
        this.m_dimY.printShortName(body, ETextCase.IN_SENTENCE);
        caption.append(" progresses over ");//$NON-NLS-1$
        this.m_dimX.printShortName(body, ETextCase.IN_SENTENCE);
        caption.append('.');
      }

      try (final ITableSection header = table.header()) {
        try (final ITableRow row = header.row()) {
          try (final IComplexText cell = row.cell()) {
            cell.append("stat");//$NON-NLS-1$
          }
          for (columnIndex = 0; columnIndex < record.m_information.length; columnIndex++) {
            try (final IComplexText cell = row.cell()) {
              try (final IMath math = cell.inlineMath()) {
                _ModelingJob.RENDERER.renderParameter(columnIndex, math);
              }
            }
          }
        }
      }

      try (final ITableSection tbody = table.body()) {
        for (rowIndex = StatisticInfoPrinter.TABLE_FIRST_ROW; rowIndex <= StatisticInfoPrinter.TABLE_LAST_ROW; rowIndex++) {
          try (final ITableRow row = tbody.row()) {
            try (final IComplexText cell = row.cell()) {
              StatisticInfoPrinter.tableRowHead(rowIndex, cell);
            }
            for (columnIndex = 0; columnIndex < record.m_information.length; columnIndex++) {
              try (final IComplexText cell = row.cell()) {
                StatisticInfoPrinter.tableRowValue(rowIndex,
                    record.m_information[columnIndex],
                    _ModelingJob.APPENDER, cell);
              }
            }
          }
        }
      }

      try (final ITableSection footer = table.footer()) {
        //
      }
    }
  }

  /**
   * Get the models for rendering
   *
   * @param styles
   *          the styles
   * @return the list of models
   */
  private final Collection<ISequenceable> __getSequenceableModels(
      final IStyles styles) {
    final ArrayList<ISequenceable> result;
    final ArrayListView<ParametricUnaryFunction> models;

    models = this.__models();
    result = new ArrayList<>(models.size());
    for (final ParametricUnaryFunction func : models) {
      result.add(new __ModelSequenceable(func, //
          _ModelingJob.__modelColor(styles, func)));
    }
    return result;
  }

  /** model sequenceable */
  private final class __ModelSequenceable implements ISequenceable {

    /** the function */
    private final ParametricUnaryFunction m_function;
    /** the style to use */
    private final IStyle m_style;

    /**
     * create the sequenceable
     *
     * @param func
     *          the function
     * @param style
     *          the style to use
     */
    __ModelSequenceable(final ParametricUnaryFunction func,
        final IStyle style) {
      super();
      this.m_function = func;
      this.m_style = style;
    }

    /** {@inheritDoc} */
    @Override
    public final void toSequence(final boolean isFirstInSequence,
        final boolean isLastInSequence, final ETextCase textCase,
        final ITextOutput textOut) {
      try (IComplexText text = (((IComplexText) textOut)
          .style(this.m_style))) {
        try (final IMath math = text.inlineMath()) {
          this.m_function.mathRender(math, _ModelingJob.RENDERER,
              _ModelingJob.this.m_dimX);
        }
      }
    }
  }

  /** the information record */
  private static final class __InfoRecord {
    /** the function */
    final ParametricUnaryFunction m_function;
    /** the information records */
    final IStatisticInfo[] m_information;
    /** the label */
    final ILabel m_label;

    /**
     * create the information record
     *
     * @param function
     *          the function
     * @param information
     *          the information records
     * @param label
     *          the label
     */
    __InfoRecord(final ParametricUnaryFunction function,
        final IStatisticInfo[] information, final ILabel label) {
      super();
      this.m_function = function;
      this.m_information = information;
      this.m_label = label;
    }
  }
}
