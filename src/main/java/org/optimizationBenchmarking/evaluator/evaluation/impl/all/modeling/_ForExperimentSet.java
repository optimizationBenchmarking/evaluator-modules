package org.optimizationBenchmarking.evaluator.evaluation.impl.all.modeling;

import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.attributes.PerInstanceRuns;
import org.optimizationBenchmarking.evaluator.attributes.clusters.ICluster;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.utils.document.impl.SemanticComponentUtils;
import org.optimizationBenchmarking.utils.document.impl.optional.OptionalElements;
import org.optimizationBenchmarking.utils.document.impl.optional.OptionalSection;
import org.optimizationBenchmarking.utils.document.spec.IComplexText;
import org.optimizationBenchmarking.utils.document.spec.ISectionBody;
import org.optimizationBenchmarking.utils.ml.fitting.spec.IFittingResult;
import org.optimizationBenchmarking.utils.text.ETextCase;

/** the optional section per experiment set */
final class _ForExperimentSet extends OptionalSection {

  /** the owning job */
  private final _ModelingJob m_job;

  /** the data */
  private final IExperimentSet m_data;

  /** the logger */
  private final Logger m_logger;

  /**
   * create the experiment set job
   *
   * @param job
   *          the job
   * @param data
   *          the data
   * @param logger
   *          the logger
   */
  _ForExperimentSet(final _ModelingJob job, final IExperimentSet data,
      final Logger logger) {
    super();
    if (job == null) {
      throw new IllegalArgumentException("_ModelingJob cannot be null."); //$NON-NLS-1$
    }
    if (data == null) {
      throw new IllegalArgumentException("IExperimentSet cannot be null."); //$NON-NLS-1$
    }
    this.m_job = job;
    this.m_data = data;
    this.m_logger = logger;
  }

  /** {@inheritDoc} */
  @Override
  public final void writeSectionTitle(final IComplexText title) {
    if (this.m_data instanceof ICluster) {
      title.append("Models for Cluster "); //$NON-NLS-1$
      ((ICluster) (this.m_data)).printShortName(title, ETextCase.IN_TITLE);
    }
  }

  /**
   * Print the cluster information.
   *
   * @param cluster
   *          the cluster
   * @param text
   *          the text
   */
  private final void __printClusterInfo(final ICluster cluster,
      final IComplexText text) {
    text.append(
        "The models here have been fitted to the elements in cluster "); //$NON-NLS-1$
    SemanticComponentUtils.printLongAndShortNameIfDifferent(cluster, text,
        ETextCase.IN_SENTENCE);
    text.append('.');
    text.append(' ');
    cluster.printDescription(text, ETextCase.AT_SENTENCE_START);
  }

  /** {@inheritDoc} */
  @Override
  public void writeSectionBody(final boolean isNewSection,
      final ISectionBody body) {
    final int sections;
    final PerInstanceRuns<IFittingResult> results;

    if (!(isNewSection)) {
      body.append(' ');
    }
    if (this.m_data instanceof ICluster) {
      this.__printClusterInfo(((ICluster) (this.m_data)), body);
    }

    results = new PerInstanceRuns<>(this.m_data, this.m_job.m_attribute,
        this.m_logger);

    sections = ((this.m_job.m_overall != EModelInfo.NONE) ? 1 : 0) + //
        ((this.m_job.m_perAlgorithm != EModelInfo.NONE) ? 1 : 0) + //
        ((this.m_job.m_perInstance != EModelInfo.NONE) ? 1 : 0);//

    if (this.m_job.m_overall != EModelInfo.NONE) {
      OptionalElements.optionalSection(body, (sections > 1), null, //
          new _ForAll(this.m_data, results, this.m_job.m_overall,
              this.m_job));
    }

    if (this.m_job.m_perAlgorithm != EModelInfo.NONE) {
      OptionalElements.optionalSection(body, (sections > 1), null, //
          new _ForExperiments(this.m_data, results,
              this.m_job.m_perAlgorithm, this.m_job));
    }

    if (this.m_job.m_perInstance != EModelInfo.NONE) {
      OptionalElements.optionalSection(body, (sections > 1), null, //
          new _ForInstances(this.m_data, results, this.m_job.m_perInstance,
              this.m_job));
    }
  }
}
