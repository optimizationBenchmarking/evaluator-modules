package org.optimizationBenchmarking.evaluator.evaluation.impl.all.modeling;

import java.util.Iterator;
import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.attributes.clusters.ICluster;
import org.optimizationBenchmarking.utils.collections.iterators.BasicIterator;
import org.optimizationBenchmarking.utils.document.impl.SectionRenderer;

/** an iterator for optional sections */
final class _ClusterIterator extends BasicIterator<SectionRenderer> {
  /** the owning modeling job */
  private final _ModelingJob m_owner;
  /** the clustering */
  private final Iterator<? extends ICluster> m_clusters;
  /** the logger */
  private final Logger m_logger;

  /**
   * create the iterator
   *
   * @param owner
   *          the owning modeling job
   * @param clusters
   *          the clusters
   * @param logger
   *          the logger
   */
  _ClusterIterator(final _ModelingJob owner,
      final Iterator<? extends ICluster> clusters, final Logger logger) {
    super();
    this.m_owner = owner;
    this.m_clusters = clusters;
    this.m_logger = logger;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean hasNext() {
    return this.m_clusters.hasNext();
  }

  /** {@inheritDoc} */
  @Override
  public final SectionRenderer next() {
    final ICluster cluster;
    cluster = this.m_clusters.next();
    return new _ForExperimentSet(this.m_owner, cluster, this.m_logger,
        (this.m_owner._getPathComponentSuggestion() + '/'
            + cluster.getPathComponentSuggestion()));
  }
}