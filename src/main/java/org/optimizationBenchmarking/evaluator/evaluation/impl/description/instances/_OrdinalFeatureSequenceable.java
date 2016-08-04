package org.optimizationBenchmarking.evaluator.evaluation.impl.description.instances;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.data.spec.IFeature;
import org.optimizationBenchmarking.evaluator.data.spec.IFeatureValue;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.document.spec.IPlainText;
import org.optimizationBenchmarking.utils.error.RethrowMode;
import org.optimizationBenchmarking.utils.text.ESequenceMode;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.ISequenceable;
import org.optimizationBenchmarking.utils.text.numbers.InTextNumberAppender;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/** A sequenceable for ordinal features */
final class _OrdinalFeatureSequenceable implements ISequenceable {

  /** the feature */
  final IFeature m_feature;
  /** the logger */
  private final Logger m_logger;

  /**
   * create the numerical feature sequenceable
   *
   * @param feature
   *          the feature
   * @param logger
   *          the logger
   */
  _OrdinalFeatureSequenceable(final IFeature feature,
      final Logger logger) {
    super();
    this.m_feature = feature;
    this.m_logger = logger;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("resource")
  @Override
  public final ETextCase toSequence(final boolean isFirstInSequence,
      final boolean isLastInSequence, final ETextCase textCase,
      final ITextOutput textOut) {
    ETextCase next;
    ArrayListView<? extends IFeatureValue> values;
    ITextOutput use;
    int size, i;
    ArrayList<_SingleFeatureValueSequenceable> seq;

    next = this.m_feature.printShortName(textOut, textCase);

    textOut.append(' ');

    if (textOut instanceof IPlainText) {
      use = ((IPlainText) textOut).inBraces();
    } else {
      use = textOut;
      use.append('(');
    }
    try {
      values = this.m_feature.getData();
      size = values.size();

      printValues: {
        if (size <= 1) {
          next = next.appendWord("only", use);//$NON-NLS-1$
          use.append(' ');
          next = InTextNumberAppender.INSTANCE.appendTo(size, next, use);
          use.append(' ');
          next = next.appendWord("value", use); //$NON-NLS-1$
          use.append(':');
          use.append(' ');
          use.append(values.get(0).getValue());
          break printValues;
        }
        next = InTextNumberAppender.INSTANCE.appendTo(size, next, use);
        use.append(' ');
        next = next.appendWord("values", use); //$NON-NLS-1$
        use.append(':');
        use.append(' ');

        seq = new ArrayList<>(size);
        for (i = 0; i < size; i++) {
          seq.add(new _SingleFeatureValueSequenceable(values.get(i),
              this.m_logger));
        }

        next = ESequenceMode.AND.appendSequence(next, seq, true, use);
      }
    } finally {
      if (use != textOut) {
        try {
          ((Closeable) use).close();
        } catch (final IOException ioe) {
          RethrowMode.AS_RUNTIME_EXCEPTION.rethrow(null, false, ioe);
        }
      } else {
        textOut.append(')');
      }
    }
    return next;
  }
}
