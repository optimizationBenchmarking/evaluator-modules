package org.optimizationBenchmarking.evaluator.evaluation.impl.all.modeling;

import org.optimizationBenchmarking.utils.document.spec.IComplexText;
import org.optimizationBenchmarking.utils.document.spec.IMath;
import org.optimizationBenchmarking.utils.document.spec.ISemanticMathComponent;
import org.optimizationBenchmarking.utils.graphics.style.spec.IColorStyle;
import org.optimizationBenchmarking.utils.math.text.DoubleConstantParameters;
import org.optimizationBenchmarking.utils.ml.fitting.spec.ParametricUnaryFunction;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.ISequenceable;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/** A model holder */
final class _Model implements ISequenceable {

  /** the function */
  final ParametricUnaryFunction m_function;

  /** the style */
  final IColorStyle m_style;

  /** the math renderable */
  final ISemanticMathComponent m_x;

  /**
   * Create the model holder.
   *
   * @param function
   *          the function
   * @param style
   *          the style
   * @param x
   *          the math renderable for {@code x}-coordinates
   */
  _Model(final ParametricUnaryFunction function, final IColorStyle style,
      final ISemanticMathComponent x) {
    super();
    if (function == null) {
      throw new IllegalArgumentException(
          "Function of the model cannot be null."); //$NON-NLS-1$
    }
    if (style == null) {
      throw new IllegalArgumentException("The style of the model."); //$NON-NLS-1$
    }
    if (x == null) {
      throw new IllegalArgumentException(
          "The x element must not be null."); //$NON-NLS-1$
    }
    this.m_function = function;
    this.m_style = style;
    this.m_x = x;
  }

  /**
   * render this function in a plain way.
   *
   * @param complexText
   *          the destination text
   */
  final void _renderPlain(final IComplexText complexText) {
    try (final IMath math = complexText.inlineMath()) {
      this.m_function.mathRender(math, _ModelingJob.RENDERER, this.m_x);
    }
  }

  /**
   * render this function.
   *
   * @param complexText
   *          the destination text
   */
  final void _renderStyled(final IComplexText complexText) {
    try (IComplexText text = complexText.style(this.m_style)) {
      this._renderPlain(text);
    }
  }

  /**
   * Render this model with parameters without using the style
   *
   * @param complexText
   *          the complex text destination
   * @param parameters
   *          the parameters
   */
  final void _renderWithParametersPlain(final IComplexText complexText,
      final double[] parameters) {
    try (final IMath math = complexText.inlineMath()) {
      this.m_function.mathRender(math,
          new DoubleConstantParameters(_ModelingJob.APPENDER, parameters),
          this.m_x);
    }
  }

  /**
   * Render this model with parameters with styles
   *
   * @param complexText
   *          the complex text destination
   * @param parameters
   *          the parameters
   */
  final void _renderWithParametersStyles(final IComplexText complexText,
      final double[] parameters) {
    try (IComplexText text = complexText.style(this.m_style)) {
      this._renderWithParametersPlain(text, parameters);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final ETextCase toSequence(final boolean isFirstInSequence,
      final boolean isLastInSequence, final ETextCase textCase,
      final ITextOutput textOut) {
    if (textOut instanceof IComplexText) {
      this._renderStyled((IComplexText) textOut);
      return textCase.nextCase();
    }
    throw new IllegalStateException("Can only render to IComplexText."); //$NON-NLS-1$
  }
}
