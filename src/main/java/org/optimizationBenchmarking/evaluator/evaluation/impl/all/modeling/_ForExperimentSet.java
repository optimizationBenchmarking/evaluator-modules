package org.optimizationBenchmarking.evaluator.evaluation.impl.all.modeling;

import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.attributes.PerInstanceRuns;
import org.optimizationBenchmarking.evaluator.attributes.clusters.ICluster;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.utils.collections.iterators.BasicIterator;
import org.optimizationBenchmarking.utils.document.impl.Renderers;
import org.optimizationBenchmarking.utils.document.impl.SectionRenderer;
import org.optimizationBenchmarking.utils.document.impl.SemanticComponentUtils;
import org.optimizationBenchmarking.utils.document.spec.IComplexText;
import org.optimizationBenchmarking.utils.document.spec.ISectionBody;
import org.optimizationBenchmarking.utils.ml.fitting.spec.IFittingResult;
import org.optimizationBenchmarking.utils.text.ETextCase;

/** the optional section per experiment set */
final class _ForExperimentSet extends SectionRenderer {

  /** the owning job */
  final _ModelingJob m_job;

  /** the data */
  final IExperimentSet m_data;

  /** the logger */
  final Logger m_logger;

  /** the basic path component */
  final String m_basePathComponent;

  /**
   * create the experiment set job
   *
   * @param job
   *          the job
   * @param data
   *          the data
   * @param logger
   *          the logger
   * @param basePathComponent
   *          the basic path component
   */
  _ForExperimentSet(final _ModelingJob job, final IExperimentSet data,
      final Logger logger, final String basePathComponent) {
    super();
    if (job == null) {
      throw new IllegalArgumentException("_ModelingJob cannot be null."); //$NON-NLS-1$
    }
    if (data == null) {
      throw new IllegalArgumentException("IExperimentSet cannot be null."); //$NON-NLS-1$
    }
    if (basePathComponent == null) {
      throw new IllegalArgumentException(
          "Basic path component cannot be null."); //$NON-NLS-1$
    }
    this.m_job = job;
    this.m_data = data;
    this.m_logger = logger;
    this.m_basePathComponent = basePathComponent;
  }

  /** {@inheritDoc} */
  @Override
  protected final void doRenderSectionTitle(final IComplexText title) {
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
  protected final void doRenderSectionBody(final boolean isNewSection,
      final ISectionBody body) {
    if (!(isNewSection)) {
      body.append(' ');
    }
    if (this.m_data instanceof ICluster) {
      this.__printClusterInfo(((ICluster) (this.m_data)), body);
    }

    Renderers.renderSections(body, null,
        new __PartIterator(new PerInstanceRuns<>(this.m_data,
            this.m_job.m_attribute, this.m_logger)));
  }

  /** an iterator for optional sections */
  private final class __PartIterator
      extends BasicIterator<SectionRenderer> {

    /** the results */
    private PerInstanceRuns<IFittingResult> m_results;
    /** the index */
    private int m_index;

    /**
     * create the iterator
     *
     * @param results
     *          the results
     */
    __PartIterator(final PerInstanceRuns<IFittingResult> results) {
      super();
      this.m_results = results;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean hasNext() {
      return (this.m_index < 3);
    }

    /** {@inheritDoc} */
    @Override
    public final SectionRenderer next() {
      PerInstanceRuns<IFittingResult> results;
      switch (this.m_index++) {
        case 0: {
          if (_ForExperimentSet.this.m_job.m_overall == EModelInfo.NONE) {
            return null;
          }
          return new _ForAll(_ForExperimentSet.this.m_data, this.m_results,
              _ForExperimentSet.this.m_job.m_overall,
              _ForExperimentSet.this.m_job,
              _ForExperimentSet.this.m_basePathComponent);
        }
        case 1: {
          if (_ForExperimentSet.this.m_job.m_perAlgorithm == EModelInfo.NONE) {
            return null;
          }
          return new _ForExperiments(_ForExperimentSet.this.m_data,
              this.m_results, _ForExperimentSet.this.m_job.m_perAlgorithm,
              _ForExperimentSet.this.m_job,
              _ForExperimentSet.this.m_basePathComponent);
        }
        case 2: {
          results = this.m_results;
          this.m_results = null;
          if (_ForExperimentSet.this.m_job.m_perInstance == EModelInfo.NONE) {
            return null;
          }
          return new _ForInstances(_ForExperimentSet.this.m_data, results,
              _ForExperimentSet.this.m_job.m_perInstance,
              _ForExperimentSet.this.m_job,
              _ForExperimentSet.this.m_basePathComponent);
        }
        default: {
          return super.next();
        }
      }
    }
  }
}
