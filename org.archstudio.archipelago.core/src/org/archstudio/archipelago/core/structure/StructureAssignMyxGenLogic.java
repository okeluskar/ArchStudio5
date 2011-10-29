package org.archstudio.archipelago.core.structure;

import java.util.List;

import org.archstudio.bna.IBNAView;
import org.archstudio.bna.ICoordinate;
import org.archstudio.bna.IThing;
import org.archstudio.bna.logics.AbstractThingLogic;
import org.archstudio.bna.utils.IBNAMenuListener;
import org.archstudio.myxgen.MyxGenBrick;
import org.archstudio.myxgen.MyxGenInterface;
import org.archstudio.myxgen.eclipse.extension.MyxGenExtensions;
import org.archstudio.sysutils.SystemUtils;
import org.archstudio.sysutils.UIDGenerator;
import org.archstudio.xadl.XadlUtils;
import org.archstudio.xadl3.domain_3_0.Domain;
import org.archstudio.xadl3.domain_3_0.DomainExtension;
import org.archstudio.xadl3.domain_3_0.DomainType;
import org.archstudio.xadl3.domain_3_0.Domain_3_0Factory;
import org.archstudio.xadl3.domain_3_0.Domain_3_0Package;
import org.archstudio.xadl3.implementation_3_0.ImplementationExtension;
import org.archstudio.xadl3.implementation_3_0.Implementation_3_0Package;
import org.archstudio.xadl3.lookupimplementation_3_0.LookupImplementation;
import org.archstudio.xadl3.lookupimplementation_3_0.Lookupimplementation_3_0Factory;
import org.archstudio.xadl3.lookupimplementation_3_0.Lookupimplementation_3_0Package;
import org.archstudio.xadl3.myxgen_3_0.MyxGen;
import org.archstudio.xadl3.myxgen_3_0.Myxgen_3_0Factory;
import org.archstudio.xadl3.myxgen_3_0.Myxgen_3_0Package;
import org.archstudio.xadl3.structure_3_0.Brick;
import org.archstudio.xadl3.structure_3_0.Direction;
import org.archstudio.xadl3.structure_3_0.Interface;
import org.archstudio.xadl3.structure_3_0.Structure_3_0Factory;
import org.archstudio.xadl3.structure_3_0.Structure_3_0Package;
import org.archstudio.xadlbna.things.IHasObjRef;
import org.archstudio.xarchadt.IXArchADT;
import org.archstudio.xarchadt.ObjRef;
import org.archstudio.xarchadt.core.XArchADTProxy;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;

public class StructureAssignMyxGenLogic extends AbstractThingLogic implements IBNAMenuListener {

	protected final IXArchADT xarch;

	public StructureAssignMyxGenLogic(IXArchADT xarch) {
		this.xarch = xarch;
	}

	@Override
	public void fillMenu(IBNAView view, List<IThing> things, ICoordinate location, IMenuManager menu) {
		IThing thing = SystemUtils.firstOrNull(things);
		if (thing != null) {
			final ObjRef objRef = thing.get(IHasObjRef.OBJREF_KEY);
			if (objRef != null) {
				if (XadlUtils.isBrick(xarch, objRef)) {
					MenuManager myxGenMenu = new MenuManager("Assign MyxGen Brick...");
					myxGenMenu.addMenuListener(new IMenuListener() {
						@Override
						public void menuAboutToShow(IMenuManager manager) {
							populateMenuWithMyxGenBricks(objRef, manager);
						}
					});
					myxGenMenu.add(new Action("[TBD]") {
						@Override
						public void run() {
						}
					});
					menu.add(myxGenMenu);
				}
			}
		}
	}

	protected void populateMenuWithMyxGenBricks(final ObjRef objRef, IMenuManager manager) {
		manager.removeAll();
		for (final IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			IMenuManager projectMenu = null;
			for (final MyxGenBrick myxGenBrick : MyxGenExtensions.getWorkspaceMyxGenBricks(project)) {
				if (projectMenu == null) {
					projectMenu = new MenuManager(project.getName());
					manager.add(projectMenu);
				}
				projectMenu.add(new Action(myxGenBrick.getName()) {
					@Override
					public void run() {
						assignMyxGenBrick(objRef, myxGenBrick);
					}
				});
			}
		}
	}

	protected void assignMyxGenBrick(ObjRef objRef, MyxGenBrick myxGenBrick) {
		Domain_3_0Factory domainFactory = XArchADTProxy.proxy(xarch, Domain_3_0Package.eINSTANCE.getNsURI());
		Lookupimplementation_3_0Factory lookupFactory = XArchADTProxy.proxy(xarch,
				Lookupimplementation_3_0Package.eINSTANCE.getNsURI());
		Myxgen_3_0Factory myxGenFactory = XArchADTProxy.proxy(xarch, Myxgen_3_0Package.eINSTANCE.getNsURI());
		Structure_3_0Factory structureFactory = XArchADTProxy.proxy(xarch, Structure_3_0Package.eINSTANCE.getNsURI());

		Brick brick = XArchADTProxy.proxy(xarch, objRef);
		brick.setSubStructure(null);

		{
			ImplementationExtension impl = XadlUtils.createExt(xarch, brick,
					Implementation_3_0Package.Literals.IMPLEMENTATION_EXTENSION);

			MyxGen myxGen = myxGenFactory.createMyxGen();
			myxGen.setId(UIDGenerator.generateUID());
			myxGen.setBrickID(myxGenBrick.getId());

			impl.getImplementation().clear();
			impl.getImplementation().add(myxGen);
		}

		brick.getInterface().clear();
		{
			for (MyxGenInterface mif : myxGenBrick.getInterfaces()) {

				Interface bif = structureFactory.createInterface();
				bif.setId(UIDGenerator.generateUID());
				bif.setName(mif.getName());
				bif.setDirection(Direction.getByName(mif.getDirection().name().toLowerCase()));

				ImplementationExtension impl = XadlUtils.createExt(xarch, bif,
						Implementation_3_0Package.Literals.IMPLEMENTATION_EXTENSION);

				LookupImplementation lookup = lookupFactory.createLookupImplementation();
				lookup.setId(UIDGenerator.generateUID());
				lookup.setLookup(mif.getId());

				impl.getImplementation().add(lookup);

				DomainExtension domainExt = XadlUtils
						.createExt(xarch, bif, Domain_3_0Package.Literals.DOMAIN_EXTENSION);
				Domain domain = domainFactory.createDomain();
				domain.setType(DomainType.getByName(mif.getDomain()));
				domainExt.setDomain(domain);

				brick.getInterface().add(bif);
			}
		}

	}
}
