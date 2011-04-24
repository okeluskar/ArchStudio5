/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.archstudio.xadl3.implementation_3_0.impl;

import org.archstudio.xadl3.implementation_3_0.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class Implementation_3_0FactoryImpl extends EFactoryImpl implements Implementation_3_0Factory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static Implementation_3_0Factory init()
  {
    try
    {
      Implementation_3_0Factory theImplementation_3_0Factory = (Implementation_3_0Factory)EPackage.Registry.INSTANCE.getEFactory("http://www.archstudio.org/xadl3/schemas/implementation-3.0.xsd"); 
      if (theImplementation_3_0Factory != null)
      {
        return theImplementation_3_0Factory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new Implementation_3_0FactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Implementation_3_0FactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
      case Implementation_3_0Package.IMPLEMENTATION_EXTENSION: return createImplementationExtension();
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ImplementationExtension createImplementationExtension()
  {
    ImplementationExtensionImpl implementationExtension = new ImplementationExtensionImpl();
    return implementationExtension;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Implementation_3_0Package getImplementation_3_0Package()
  {
    return (Implementation_3_0Package)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static Implementation_3_0Package getPackage()
  {
    return Implementation_3_0Package.eINSTANCE;
  }

} //Implementation_3_0FactoryImpl
