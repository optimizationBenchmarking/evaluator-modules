package org.optimizationBenchmarking.evaluator.evaluation.impl.all.modeling;

import java.util.LinkedHashMap;
import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.attributes.PerInstanceRuns;
import org.optimizationBenchmarking.evaluator.attributes.modeling.DimensionRelationship;
import org.optimizationBenchmarking.evaluator.attributes.modeling.DimensionRelationshipModels;
import org.optimizationBenchmarking.evaluator.data.spec.IDimension;
import org.optimizationBenchmarking.evaluator.data.spec.IDimensionSet;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.evaluation.impl.abstr.ExperimentSetJob;
import org.optimizationBenchmarking.utils.comparison.Compare;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.document.impl.SemanticComponentUtils;
import org.optimizationBenchmarking.utils.document.impl.optional.OptionalElements;
import org.optimizationBenchmarking.utils.document.spec.EMathComparison;
import org.optimizationBenchmarking.utils.document.spec.IComplexText;
import org.optimizationBenchmarking.utils.document.spec.IDocument;
import org.optimizationBenchmarking.utils.document.spec.IMath;
import org.optimizationBenchmarking.utils.document.spec.IParameterRenderer;
import org.optimizationBenchmarking.utils.document.spec.IPlainText;
import org.optimizationBenchmarking.utils.document.spec.ISection;
import org.optimizationBenchmarking.utils.document.spec.ISectionBody;
import org.optimizationBenchmarking.utils.document.spec.ISectionContainer;
import org.optimizationBenchmarking.utils.graphics.style.spec.IStyles;
import org.optimizationBenchmarking.utils.math.text.ABCParameterRenderer;
import org.optimizationBenchmarking.utils.ml.fitting.spec.IFittingResult;
import org.optimizationBenchmarking.utils.ml.fitting.spec.ParametricUnaryFunction;
import org.optimizationBenchmarking.utils.text.ESequenceMode;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.numbers.NumberAppender;
import org.optimizationBenchmarking.utils.text.numbers.TextNumberAppender;
import org.optimizationBenchmarking.utils.text.numbers.TruncatedNumberAppender;

/** the modeling job */
final class _ModelingJob extends ExperimentSetJob {

  /** the parameter renderer */
  static final IParameterRenderer RENDERER = ABCParameterRenderer.INSTANCE;

  /** the number appender to use */
  static final NumberAppender APPENDER = TruncatedNumberAppender.INSTANCE;

  /** the {@code x}-axis dimension */
  final IDimension m_dimX;
  /** the {@code y}-axis dimension */
  final IDimension m_dimY;

  /** the dimension relationship attribute */
  private final DimensionRelationship m_attribute;

  /** the model set */
  final LinkedHashMap<ParametricUnaryFunction, _Model> m_models;

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

    this.m_models = new LinkedHashMap<>();

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
  protected final void doInitialize(final IExperimentSet data,
      final IDocument document, final Logger logger) {
    final IStyles styles;

    styles = document.getStyles();
    for (final ParametricUnaryFunction function : //
    DimensionRelationshipModels.getModels(this.m_dimX, this.m_dimY)) {
      this.m_models.put(function,
          new _Model(
              function, styles
                  .getColor(function.getClass().getCanonicalName(), true),
              this.m_dimX));
    }
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
          OptionalElements.optionalSection(body, (sections > 1), null, //
              new _ForAll(data, results, this.m_overall, this));
        }

        if (this.m_perAlgorithm != EModelInfo.NONE) {
          OptionalElements.optionalSection(body, (sections > 1), null, //
              new _ForExperiments(data, results, this.m_perAlgorithm,
                  this));
        }

        if (this.m_perInstance != EModelInfo.NONE) {
          OptionalElements.optionalSection(body, (sections > 1), null, //
              new _ForInstances(data, results, this.m_perInstance, this));
        }
      }
    }
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
    body.append(" We therefore simply use a selection of ");//$NON-NLS-1$
    TextNumberAppender.INSTANCE.appendTo(this.m_models.size(),
        ETextCase.IN_SENTENCE, body);
    body.append(" models, namely ");//$NON-NLS-1$
    ESequenceMode.AND.appendSequence(ETextCase.IN_SENTENCE,
        this.m_models.values(), true, body);
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
}
