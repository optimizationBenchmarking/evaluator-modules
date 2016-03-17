package org.optimizationBenchmarking.evaluator.evaluation.impl;

import java.io.IOException;
import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.evaluation.definition.data.ModuleDescription;
import org.optimizationBenchmarking.evaluator.evaluation.definition.data.ModuleDescriptions;
import org.optimizationBenchmarking.evaluator.evaluation.definition.data.ModuleDescriptionsBuilder;
import org.optimizationBenchmarking.evaluator.evaluation.definition.io.ModuleDescriptionXMLInput;
import org.optimizationBenchmarking.evaluator.evaluation.spec.IEvaluationModule;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.error.RethrowMode;
import org.optimizationBenchmarking.utils.io.structured.spec.IXMLInputJobBuilder;
import org.optimizationBenchmarking.utils.reflection.ReflectionUtils;

/** A shared list of evaluation module descriptions */
public final class EvaluationModuleDescriptions
    extends ModuleDescriptions {

  /** the serial version uid */
  private static final long serialVersionUID = 1L;

  /** the synchronizer */
  private static final Object SYNCH = new Object();

  /** the globally shared instance of the module descriptions */
  private static ModuleDescriptions DESCRIPTIONS = null;

  /**
   * create the module descriptions
   *
   * @param description
   *          the array of descriptions
   */
  EvaluationModuleDescriptions(final ModuleDescription[] description) {
    super(description);
  }

  /** {@inheritDoc} */
  @Override
  protected final Class<? extends IEvaluationModule> parseModuleClass(
      final String module) {
    try {
      return ReflectionUtils.findClass(module, IEvaluationModule.class);
    } catch (@SuppressWarnings("unused") final Throwable error) {
      return EvaluationModuleParser.getInstance().parseString(module)
          .getClass();
    }
  }

  /**
   * Get the default module descriptions
   *
   * @return the descriptions
   */
  public static final ModuleDescriptions getDescriptions() {
    return EvaluationModuleDescriptions.getDescriptions(null);
  }

  /**
   * Get the default module descriptions
   *
   * @param logger
   *          a logger to use
   * @return the descriptions
   */
  public static final ModuleDescriptions getDescriptions(
      final Logger logger) {
    final IXMLInputJobBuilder<ModuleDescriptionsBuilder> job;
    final String argh;

    synchronized (EvaluationModuleDescriptions.SYNCH) {
      if (EvaluationModuleDescriptions.DESCRIPTIONS == null) {
        try (
            final __ModuleDescriptionsBuilder builder = new __ModuleDescriptionsBuilder()) {

          try {
            job = ModuleDescriptionXMLInput.getInstance().use();
            if (logger != null) {
              job.setLogger(logger);
            }
            job.addResource(EvaluationModuleDescriptions.class,
                "modules.xml").configure(Configuration.getRoot()) //$NON-NLS-1$
                .setDestination(builder).create().call();
          } catch (final IOException ioe) {
            argh = "Could not load the module list.";//$NON-NLS-1$
            ErrorUtils.logError(logger, argh, ioe, false,
                RethrowMode.AS_ILLEGAL_STATE_EXCEPTION);
          }
          EvaluationModuleDescriptions.DESCRIPTIONS = builder.getResult();
        }
      }
      return EvaluationModuleDescriptions.DESCRIPTIONS;
    }
  }

  /**
   * get the object replacing a read instance.
   *
   * @return the object replacing a read instance.
   */
  private final Object readResolve() {
    return EvaluationModuleDescriptions.getDescriptions(null);
  }

  /**
   * get the object replacing an instance to write
   *
   * @return the object replacing an instance to write
   */
  private final Object writeReplace() {
    return EvaluationModuleDescriptions.getDescriptions(null);
  }

  /** the module descriptions builder */
  private static final class __ModuleDescriptionsBuilder
      extends ModuleDescriptionsBuilder {
    /** create */
    __ModuleDescriptionsBuilder() {
      super();
    }

    /** {@inheritDoc} */
    @Override
    protected ModuleDescriptions make(final ModuleDescription[] array) {
      return new EvaluationModuleDescriptions(array);
    }
  }
}
