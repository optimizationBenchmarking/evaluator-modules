package org.optimizationBenchmarking.evaluator.evaluation.impl.all.function;

import org.optimizationBenchmarking.utils.collections.visitors.IVisitor;
import org.optimizationBenchmarking.utils.math.BasicNumber;
import org.optimizationBenchmarking.utils.math.matrix.impl.MatrixFunctionBuilder;
import org.optimizationBenchmarking.utils.math.matrix.processing.iterator2D.MatrixIteration2DState;

/** the internal class for performing ranking transformations */
final class _RankingTransformer
    implements IVisitor<MatrixIteration2DState> {

  /** the owning function job */
  private final FunctionJob m_owner;
  /** the builders */
  private final MatrixFunctionBuilder[] m_builders;
  /** the ranks */
  private final double[] m_ranks;

  /**
   * create
   *
   * @param owner
   *          the owning function job
   * @param builders
   *          the builders
   */
  _RankingTransformer(final FunctionJob owner,
      final MatrixFunctionBuilder[] builders) {
    super();

    this.m_owner = owner;
    this.m_builders = builders;
    this.m_ranks = new double[builders.length];
  }

  @Override
  public boolean visit(final MatrixIteration2DState object) {
    int index;
    MatrixFunctionBuilder builder;
    BasicNumber x;

    this.m_owner.m_ranking.rankRow(object.getY(), 0, this.m_ranks);
    x = object.getX();

    for (index = object.getSourceMatrixCount(); (--index) >= 0;) {
      builder = this.m_builders[object.getSourceMatrixIndex(index)];
      if (x.isInteger()) {
        builder.addPoint(x.longValue(), this.m_ranks[index]);
      } else {
        builder.addPoint(x.doubleValue(), this.m_ranks[index]);
      }
    }

    return true;
  }

}
