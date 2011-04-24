/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.archstudio.xadl3.domain_3_0.validation;

import org.archstudio.xadl3.domain_3_0.Domain;

/**
 * A sample validator interface for {@link org.archstudio.xadl3.domain_3_0.DomainExtension}.
 * This doesn't really do anything, and it's not a real EMF artifact.
 * It was generated by the org.eclipse.emf.examples.generator.validator plug-in to illustrate how EMF's code generator can be extended.
 * This can be disabled with -vmargs -Dorg.eclipse.emf.examples.generator.validator=false.
 */
public interface DomainExtensionValidator
{
  boolean validate();

  boolean validateDomain(Domain value);
}
