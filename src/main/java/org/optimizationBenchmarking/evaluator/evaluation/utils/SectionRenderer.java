package org.optimizationBenchmarking.evaluator.evaluation.utils;

import java.util.Collection;
import java.util.Iterator;

import org.optimizationBenchmarking.utils.document.spec.IComplexText;
import org.optimizationBenchmarking.utils.document.spec.ILabel;
import org.optimizationBenchmarking.utils.document.spec.ILabelBuilder;
import org.optimizationBenchmarking.utils.document.spec.ISection;
import org.optimizationBenchmarking.utils.document.spec.ISectionBody;

/** An abstract base class for section renderers. */
public abstract class SectionRenderer {

  /** create the optional section */
  protected SectionRenderer() {
    super();
  }

  /**
   * Create the section label
   *
   * @param builder
   *          the label builder
   * @return the label, or {@code null} if none is needed
   */
  public ILabel createSectionLabel(final ILabelBuilder builder) {
    return null;
  }

  /**
   * private final final void __renderSectionTitle(final IComplexText
   * title) {// empty this.m_hasTitle = true;
   * this.doRenderSectionTitle(title); } /** Do the actual work of
   * rendering the section body.
   *
   * @param isNewSection
   *          was the section rendered as a new section ({@code true}) or
   *          as part of an existing section ({@code false})?
   * @param body
   *          the body to render to
   */
  protected void renderSectionBody(final boolean isNewSection,
      final ISectionBody body) {// empty
  }

  /**
   * Do the actual work of rendering the section title
   *
   * @param title
   *          the title to render to
   */
  protected void renderSectionTitle(final IComplexText title) {// empty
  }

  /**
   * Optionally create a section or directly include its body.
   *
   * @param body
   *          an existing body
   * @param createSection
   *          should a new section be created ({@code true}) or the
   *          existing {@code body} be reused ({@code false})?
   * @param labelCollection
   *          the destination collection to receive the created labels, if
   *          any, or {@code null} to store the labels
   * @param renderer
   *          the section renderer
   */
  public static final void renderSection(final ISectionBody body,
      final boolean createSection,
      final Collection<ILabel> labelCollection,
      final SectionRenderer renderer) {
    final ILabel label;
    if (createSection) {
      label = renderer.createSectionLabel(body);
      if ((label != null) && (labelCollection != null)) {
        labelCollection.add(label);
      }
      try (final ISection section = body.section(label)) {
        try (final IComplexText title = section.title()) {
          renderer.renderSectionTitle(title);
        }
        try (final ISectionBody text = section.body()) {
          renderer.renderSectionBody(true, text);
        }
      }
    } else {
      renderer.renderSectionBody(false, body);
    }
  }

  /**
   * Optionally create a set of sections. {@code writers} is an iterator
   * returning the sections to be written. If it returns only a single
   * section, no sub-section is created in {@code body} and the body of the
   * section is directly printed. If it returns multiple sections, a
   * section is created for each of them. It can also sometimes return
   * {@code null}, which is always ignored.
   *
   * @param body
   *          an existing body
   * @param labelCollection
   *          the destination collection to receive the created labels, if
   *          any, or {@code null} to store the labels
   * @param writers
   *          the optional section writers
   */
  public static final void renderSections(final ISectionBody body,
      final Collection<ILabel> labelCollection,
      final Iterator<? extends SectionRenderer> writers) {
    boolean putSections;
    SectionRenderer cached, current;

    if (writers != null) {
      cached = null;
      putSections = false;
      while (writers.hasNext()) {
        current = writers.next();
        if (current != null) {
          if (!putSections) {
            if (cached == null) {
              cached = current;
              continue;
            }
            putSections = true;
            SectionRenderer.renderSection(body, true, labelCollection,
                cached);
            cached = null;
          }

          SectionRenderer.renderSection(body, true, labelCollection,
              current);
        }
      }
      if (cached != null) {
        SectionRenderer.renderSection(body, false, labelCollection,
            cached);
      }
    }
  }

  /**
   * Optionally create a set of sections. {@code writers} is an iterator
   * returning the sections to be written. If it returns only a single
   * section, no sub-section is created in {@code body} and the body of the
   * section is directly printed. If it returns multiple sections, a
   * section is created for each of them. It can also sometimes return
   * {@code null}, which is always ignored.
   *
   * @param body
   *          an existing body
   * @param labelCollection
   *          the destination collection to receive the created labels, if
   *          any, or {@code null} to store the labels
   * @param writers
   *          the optional section writers
   */
  public static final void renderSections(final ISectionBody body,
      final Collection<ILabel> labelCollection,
      final Iterable<? extends SectionRenderer> writers) {
    if (writers != null) {
      SectionRenderer.renderSections(body, labelCollection,
          writers.iterator());
    }
  }
}
