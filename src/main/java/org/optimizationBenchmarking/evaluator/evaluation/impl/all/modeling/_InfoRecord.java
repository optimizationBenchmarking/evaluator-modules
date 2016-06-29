package org.optimizationBenchmarking.evaluator.evaluation.impl.all.modeling;

import java.util.Arrays;

import org.optimizationBenchmarking.evaluator.attributes.clusters.ICluster;
import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.data.spec.IInstance;
import org.optimizationBenchmarking.utils.document.spec.ETableCellDef;
import org.optimizationBenchmarking.utils.document.spec.IComplexText;
import org.optimizationBenchmarking.utils.document.spec.ILabel;
import org.optimizationBenchmarking.utils.document.spec.IMath;
import org.optimizationBenchmarking.utils.document.spec.ISectionBody;
import org.optimizationBenchmarking.utils.document.spec.ISemanticComponent;
import org.optimizationBenchmarking.utils.document.spec.ISemanticMathComponent;
import org.optimizationBenchmarking.utils.document.spec.ITable;
import org.optimizationBenchmarking.utils.document.spec.ITableRow;
import org.optimizationBenchmarking.utils.document.spec.ITableSection;
import org.optimizationBenchmarking.utils.math.statistics.IStatisticInfo;
import org.optimizationBenchmarking.utils.math.statistics.statisticInfo.StatisticInfoPrinter;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.numbers.InTextNumberAppender;

/** the information record */
final class _InfoRecord {
  /** the model function */
  final _Model m_model;
  /** the information records */
  final IStatisticInfo[] m_information;
  /** the label */
  final ILabel m_label;
  /** the cluster */
  final ICluster m_cluster;

  /**
   * create the information record
   *
   * @param model
   *          the model function
   * @param information
   *          the information records
   * @param label
   *          the label
   * @param cluster
   *          the cluster
   */
  _InfoRecord(final _Model model, final IStatisticInfo[] information,
      final ILabel label, final ICluster cluster) {
    super();
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null."); //$NON-NLS-1$
    }
    if (information == null) {
      throw new IllegalArgumentException(
          "Statistic info set cannot be null.");//$NON-NLS-1$
    }
    if (information.length != model.m_function.getParameterCount()) {
      throw new IllegalArgumentException(
          "Length of information record must match parameter count of model.");//$NON-NLS-1$
    }
    if (label == null) {
      throw new IllegalArgumentException("Label must not be null.");//$NON-NLS-1$
    }
    this.m_model = model;
    this.m_information = information;
    this.m_label = label;
    this.m_cluster = cluster;
  }

  /**
   * Start a table environment for this record
   *
   * @param body
   *          the section body
   * @param y
   *          the semantic method component for the {@code y}-element
   * @param selection
   *          the selection component
   */
  final void _table(final ISectionBody body,
      final ISemanticMathComponent y, final ISemanticComponent selection) {
    final long count;
    ETableCellDef[] defs;
    int columnIndex, rowIndex;

    defs = new ETableCellDef[this.m_information.length + 1];
    Arrays.fill(defs, ETableCellDef.RIGHT);
    defs[0] = ETableCellDef.LEFT;
    try (final ITable table = body.table(this.m_label, false, defs)) {
      try (final IComplexText caption = table.caption()) {
        caption.append("Statistical information about the ");//$NON-NLS-1$
        InTextNumberAppender.INSTANCE.appendTo(this.m_information.length,
            ETextCase.IN_SENTENCE, caption);
        caption.append(" parameters ");//$NON-NLS-1$
        count = this.m_information[0].getSampleSize();
        if (count > 1L) {
          caption.append("of each of the ");//$NON-NLS-1$
          InTextNumberAppender.INSTANCE.appendTo(count,
              ETextCase.IN_SENTENCE, caption);
          caption.append(" cases where model ");//$NON-NLS-1$
        } else {
          caption.append("of the single case where model ");//$NON-NLS-1$
        }
        this.m_model._renderStyled(caption);
        caption.append(" fit to describe how ");//$NON-NLS-1$
        y.printShortName(caption, ETextCase.IN_SENTENCE);
        caption.append(" progresses over ");//$NON-NLS-1$
        this.m_model.m_x.printShortName(caption, ETextCase.IN_SENTENCE);
        if (this.m_cluster != null) {
          caption.append(" in cluster ");//$NON-NLS-1$
          this.m_cluster.printShortName(caption, ETextCase.IN_SENTENCE);
        }
        if (selection != null) {
          if (selection instanceof IInstance) {
            caption.append(" on benchmark instance ");//$NON-NLS-1$
          } else {
            if (selection instanceof IExperiment) {
              caption.append(" for algorithm setup ");//$NON-NLS-1$
            } else {
              caption.append(' ');
            }
          }
          selection.printShortName(caption, ETextCase.IN_SENTENCE);
        }
        caption.append('.');
        caption.append(' ');
        caption.append('(');
        StatisticInfoPrinter.tableRowDescriptions(caption);
        caption.append(')');
      }

      try (final ITableSection header = table.header()) {
        try (final ITableRow row = header.row()) {
          try (final IComplexText cell = row.cell()) {
            cell.append("Statistic");//$NON-NLS-1$
          }
          for (columnIndex = 0; columnIndex < this.m_information.length; columnIndex++) {
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
            for (columnIndex = 0; columnIndex < this.m_information.length; columnIndex++) {
              try (final IComplexText cell = row.cell()) {
                StatisticInfoPrinter.tableRowValue(rowIndex,
                    this.m_information[columnIndex], _ModelingJob.APPENDER,
                    cell);
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
}