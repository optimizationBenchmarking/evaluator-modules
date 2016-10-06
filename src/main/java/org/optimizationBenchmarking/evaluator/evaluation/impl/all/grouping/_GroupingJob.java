package org.optimizationBenchmarking.evaluator.evaluation.impl.all.grouping;

import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.attributes.OnlySharedInstances;
import org.optimizationBenchmarking.evaluator.attributes.clusters.ClustererLoader;
import org.optimizationBenchmarking.evaluator.attributes.clusters.IClustering;
import org.optimizationBenchmarking.evaluator.data.spec.Attribute;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.evaluation.impl.abstr.ExperimentSetJob;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.document.spec.IComplexText;
import org.optimizationBenchmarking.utils.document.spec.ISection;
import org.optimizationBenchmarking.utils.document.spec.ISectionBody;
import org.optimizationBenchmarking.utils.document.spec.ISectionContainer;
import org.optimizationBenchmarking.utils.text.ETextCase;

/** the grouping job */
final class _GroupingJob extends ExperimentSetJob {

  /** the grouper */
  private final Attribute<? super IExperimentSet, ? extends IClustering> m_grouper;

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
  _GroupingJob(final IExperimentSet data, final Configuration config,
      final Logger logger) {
    super(data, logger);
    this.m_grouper = ClustererLoader.configureClustering(data, config);
    if (this.m_grouper == null) {
      throw new IllegalArgumentException(
          "Grouper/clusterer cannot be null."); //$NON-NLS-1$
    }
  }

  /** {@inheritDoc} */
  @Override
  protected final void doMain(final IExperimentSet data,
      final ISectionContainer sectionContainer, final Logger logger) {
    final IClustering result;

    result = this.m_grouper.get(//
        OnlySharedInstances.INSTANCE.get(data, logger), logger);

    try (final ISection section = sectionContainer.section(null)) {
      try (final IComplexText title = section.title()) {
        result.printLongName(title, ETextCase.AT_TITLE_START);
      }
      try (final ISectionBody body = section.body()) {
        result.printLongDescription(body);
      }
    }
  }
}
