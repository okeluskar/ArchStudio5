/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.archstudio.xadl3.structure_3_0.validation;

import org.archstudio.xadl3.structure_3_0.Interface;

import org.archstudio.xadl3.xadlcore_3_0.Extension;

import org.eclipse.emf.common.util.EList;

/**
 * A sample validator interface for {@link org.archstudio.xadl3.structure_3_0.Link}.
 * This doesn't really do anything, and it's not a real EMF artifact.
 * It was generated by the org.eclipse.emf.examples.generator.validator plug-in to illustrate how EMF's code generator can be extended.
 * This can be disabled with -vmargs -Dorg.eclipse.emf.examples.generator.validator=false.
 */
public interface LinkValidator
{
  boolean validate();

  boolean validatePoint1(Interface value);
  boolean validatePoint2(Interface value);
  boolean validateExt(EList<Extension> value);
  boolean validateId(String value);
  boolean validateName(String value);
}
