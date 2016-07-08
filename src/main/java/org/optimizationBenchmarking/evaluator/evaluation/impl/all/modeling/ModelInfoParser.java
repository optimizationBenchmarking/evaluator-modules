package org.optimizationBenchmarking.evaluator.evaluation.impl.all.modeling;

import org.optimizationBenchmarking.utils.parsers.InstanceParser;
import org.optimizationBenchmarking.utils.text.TextUtils;

/** a parser for model information records. */
public final class ModelInfoParser extends InstanceParser<EModelInfo> {

  /** the serial version uid */
  private static final long serialVersionUID = 1L;
  /** print no information at all : {@link EModelInfo#NONE} */
  private static final String NONE = "none"; //$NON-NLS-1$
  /** print all information: {@link EModelInfo#ALL} */
  private static final String ALL = "all"; //$NON-NLS-1$
  /**
   * print only the plain model information: {@link EModelInfo#MODELS_ONLY}
   */
  private static final String MODELS_ONLY = "models only"; //$NON-NLS-1$
  /**
   * print only statistics information: {@link EModelInfo#STATISTICS_ONLY}
   */
  private static final String STATISTICS_ONLY = "statistics only"; //$NON-NLS-1$

  /**
   * plots only charts: {@link EModelInfo#CHARTS_ONLY}
   */
  private static final String CHARTS_ONLY = "charts only"; //$NON-NLS-1$
  /**
   * print statistics information and plot charts:
   * {@link EModelInfo#STATISTICS_AND_CHARTS}
   */
  private static final String STATISTICS_AND_CHARTS = "statistics and charts"; //$NON-NLS-1$

  /** the parser constant */
  public static final ModelInfoParser INSTANCE = new ModelInfoParser();

  /** create */
  ModelInfoParser() {
    super(EModelInfo.class, null);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("incomplete-switch")
  @Override
  public final EModelInfo parseString(final String string)
      throws IllegalArgumentException {
    String prepared;

    prepared = TextUtils.prepare(string);
    if (prepared != null) {
      switch (TextUtils.toLowerCase(prepared)) {
        case ALL: {
          return EModelInfo.ALL;
        }
        case CHARTS_ONLY: {
          return EModelInfo.CHARTS_ONLY;
        }
        case MODELS_ONLY: {
          return EModelInfo.MODELS_ONLY;
        }
        case NONE: {
          return EModelInfo.NONE;
        }
        case STATISTICS_AND_CHARTS: {
          return EModelInfo.STATISTICS_AND_CHARTS;
        }
        case STATISTICS_ONLY: {
          return EModelInfo.STATISTICS_ONLY;
        }
      }
    }
    return super.parseString(prepared);
  }
}
