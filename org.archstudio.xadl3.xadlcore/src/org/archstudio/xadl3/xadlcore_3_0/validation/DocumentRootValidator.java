/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.archstudio.xadl3.xadlcore_3_0.validation;

import org.archstudio.xadl3.xadlcore_3_0.XADLType;

import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * A sample validator interface for {@link org.archstudio.xadl3.xadlcore_3_0.DocumentRoot}.
 * This doesn't really do anything, and it's not a real EMF artifact.
 * It was generated by the org.eclipse.emf.examples.generator.validator plug-in to illustrate how EMF's code generator can be extended.
 * This can be disabled with -vmargs -Dorg.eclipse.emf.examples.generator.validator=false.
 */
public interface DocumentRootValidator
{
  boolean validate();

  boolean validateMixed(FeatureMap value);
  boolean validateXMLNSPrefixMap(EMap<String, String> value);
  boolean validateXSISchemaLocation(EMap<String, String> value);
  boolean validateTopLevelElement(EObject value);
  boolean validateXADL(XADLType value);
}
