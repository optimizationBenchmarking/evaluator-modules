package org.optimizationBenchmarking.evaluator.evaluation.impl.all.modeling;

import java.util.LinkedHashMap;
import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.attributes.clusters.ClustererLoader;
import org.optimizationBenchmarking.evaluator.attributes.clusters.IClustering;
import org.optimizationBenchmarking.evaluator.attributes.functions.DimensionTransformation;
import org.optimizationBenchmarking.evaluator.attributes.functions.DimensionTransformationParser;
import org.optimizationBenchmarking.evaluator.attributes.modeling.DimensionRelationship;
import org.optimizationBenchmarking.evaluator.attributes.modeling.DimensionRelationshipModels;
import org.optimizationBenchmarking.evaluator.data.spec.Attribute;
import org.optimizationBenchmarking.evaluator.data.spec.IDimension;
import org.optimizationBenchmarking.evaluator.data.spec.IDimensionSet;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.evaluation.impl.abstr.ExperimentSetJob;
import org.optimizationBenchmarking.utils.comparison.Compare;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.document.impl.FigureSizeParser;
import org.optimizationBenchmarking.utils.document.impl.Renderers;
import org.optimizationBenchmarking.utils.document.impl.SemanticComponentUtils;
import org.optimizationBenchmarking.utils.document.spec.EFigureSize;
import org.optimizationBenchmarking.utils.document.spec.EMathComparison;
import org.optimizationBenchmarking.utils.document.spec.IComplexText;
import org.optimizationBenchmarking.utils.document.spec.IDocument;
import org.optimizationBenchmarking.utils.document.spec.IMath;
import org.optimizationBenchmarking.utils.document.spec.IParameterRenderer;
import org.optimizationBenchmarking.utils.document.spec.IPlainText;
import org.optimizationBenchmarking.utils.document.spec.ISection;
import org.optimizationBenchmarking.utils.document.spec.ISectionBody;
import org.optimizationBenchmarking.utils.document.spec.ISectionContainer;
import org.optimizationBenchmarking.utils.graphics.style.spec.IStrokeStyle;
import org.optimizationBenchmarking.utils.graphics.style.spec.IStyles;
import org.optimizationBenchmarking.utils.math.text.ABCParameterRenderer;
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

  /** the basic path component */
  private static final String BASE_PATH_COMPONENT = "model"; //$NON-NLS-1$

  /** the transformation of the {@code x} dimension */
  final DimensionTransformation m_transformationX;
  /** the transformation of the {@code y} dimension */
  final DimensionTransformation m_transformationY;

  /** the {@code x}-axis dimension */
  final IDimension m_dimX;
  /** the {@code y}-axis dimension */
  final IDimension m_dimY;

  /** the dimension relationship attribute */
  final DimensionRelationship m_attribute;

  /** the model set */
  final LinkedHashMap<ParametricUnaryFunction, _Model> m_models;

  /** Overall, unsorted printing */
  final EModelInfo m_overall;
  /** Should we list the models per algorithm? */
  final EModelInfo m_perAlgorithm;
  /** Should we list the models per benchmark instance? */
  final EModelInfo m_perInstance;
  /** the clusterer, or {@code null} if none was requested */
  private final Attribute<? super IExperimentSet, ? extends IClustering> m_clusterer;

  /** the figure size */
  final EFigureSize m_figureSize;

  /** the thin line */
  IStrokeStyle m_thinLine;
  /** the normal line */
  IStrokeStyle m_normalLine;

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
    final DimensionTransformationParser parser;

    this.m_perAlgorithm = config.get(Modeler.PARAM_PER_ALGORITHM,
        ModelInfoParser.INSTANCE, EModelInfo.NONE);
    this.m_perInstance = config.get(Modeler.PARAM_PER_INSTANCE,
        ModelInfoParser.INSTANCE, EModelInfo.NONE);
    this.m_overall = config.get(Modeler.PARAM_OVERALL,
        ModelInfoParser.INSTANCE, //
        ((this.m_perAlgorithm == EModelInfo.NONE)
            && (this.m_perInstance == EModelInfo.NONE))//
                ? EModelInfo.STATISTICS_ONLY : EModelInfo.NONE);

    if (this.m_perAlgorithm.m_plotModels || //
        this.m_perInstance.m_plotModels || //
        this.m_overall.m_plotModels) {
      parser = new DimensionTransformationParser(data);
      this.m_transformationX = config.get(Modeler.PARAM_X, parser, null);
      this.m_dimX = ((this.m_transformationX != null)
          ? this.m_transformationX.getDimension() : null);
      this.m_transformationY = config.get(Modeler.PARAM_Y, parser, null);
      this.m_dimY = ((this.m_transformationY != null)
          ? this.m_transformationY.getDimension() : null);
    } else {
      dimensions = data.getDimensions();
      this.m_dimX = dimensions
          .find(config.getString(Modeler.PARAM_X, null));
      this.m_dimY = dimensions
          .find(config.getString(Modeler.PARAM_Y, null));
      this.m_transformationX = null;
      this.m_transformationY = null;
    }

    if (this.m_dimX == null) {
      throw new IllegalArgumentException(((//
      "Must provide a source/x dimension via parameter '" //$NON-NLS-1$
          + Modeler.PARAM_X) + '\'') + '.');
    }
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

    this.m_clusterer = ClustererLoader.configureClustering(data, config);

    this.m_figureSize = config.get(Modeler.PARAM_FIGURE_SIZE,
        FigureSizeParser.INSTANCE, EFigureSize.PAGE_3_PER_ROW);

    this.m_models = new LinkedHashMap<>();

  }

  /**
   * get the path component suggestion
   *
   * @return the path component suggestion
   */
  final String _getPathComponentSuggestion() {
    return _ModelingJob.BASE_PATH_COMPONENT + '_'
        + this.m_transformationX.getPathComponentSuggestion() + '_'
        + this.m_transformationY.getPathComponentSuggestion();
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

    this.m_thinLine = styles.getThinStroke();
    this.m_normalLine = styles.getThickStroke();
  }

  /** {@inheritDoc} */
  @Override
  protected final void doMain(final IExperimentSet data,
      final ISectionContainer sectionContainer, final Logger logger) {
    final IStyles styles;
    IClustering clustering;

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
        if (this.m_clusterer != null) {
          clustering = this.m_clusterer.get(data, logger);
          if (clustering.getData().size() <= 1) {
            clustering = null;
          }
        } else {
          clustering = null;
        }

        this.__writeIntro(data, clustering, body, styles);

        if (clustering != null) {
          Renderers.renderSections(body, null, new _ClusterIterator(this,
              clustering.getData().iterator(), logger));
        } else {
          Renderers.renderSection(body, false, null, new _ForExperimentSet(
              this, data, logger, this._getPathComponentSuggestion()));
        }
      }
    }

  }

  /**
   * Write the introduction text regarding the modeling.
   *
   * @param data
   *          the data
   * @param clustering
   *          the clustering
   * @param body
   *          the body
   * @param styles
   *          the provided styles
   */
  private final void __writeIntro(final IExperimentSet data,
      final IClustering clustering, final ISectionBody body,
      final IStyles styles) {

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

    if (clustering != null) {
      body.appendLineBreak();
      clustering.printDescription(body, ETextCase.AT_SENTENCE_START);
    }

    body.appendLineBreak();

    body.append("Obviously, we cannot know the nature of the models ");//$NON-NLS-1$
    try (final IMath rootMath = body.inlineMath()) {
      try (final IMath func = rootMath.nAryFunction("f", 1, 1)) {//$NON-NLS-1$
        try (final IMath braces = func.inBraces()) {
          this.m_dimX.mathRender(braces, _ModelingJob.RENDERER);
        }
      }
    }
    body.append(
        " that we will fit in advance, which complicates things. However, many optimization algorithms have behaviors which fit to similar ");//$NON-NLS-1$
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
