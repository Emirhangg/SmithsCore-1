package com.smithsmodding.smithscore.common.structures;


import com.smithsmodding.smithscore.SmithsCore;
import com.smithsmodding.smithscore.common.pathfinding.IPathComponent;
import com.smithsmodding.smithscore.common.pathfinding.PathFinder;
import com.smithsmodding.smithscore.util.common.positioning.Coordinate3D;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Orion Created on 03.07.2015 12:49
 *
 * Copyrighted according to Project specific license
 */
public final class StructureManager {

    public static void joinSructure(IStructureComponent<?> pStructureMember, IStructureComponent<?> pNewComponent) {
        pNewComponent.initiateAsSlaveEntity(pStructureMember.getMasterLocation());
        try {
            pStructureMember.registerNewSlave(pNewComponent.getLocation());
        } catch (Exception IAEx) {
            SmithsCore.getLogger().error("Failed to register a TE on a remote master", IAEx);
        }

        return;
    }

    public static void mergeStructures(IStructureComponent<?> newStructureMaster, IStructureComponent<?> merginComponentMaster, IStructureComponent<?> combiningComponent) {
        //Add the individual slaves
        for (Coordinate3D slaveCoordinate : merginComponentMaster.getSlaveCoordinates()) {
            IStructureComponent slaveComponent = (IStructureComponent) ( (TileEntity) merginComponentMaster ).getWorld().getTileEntity(slaveCoordinate.toBlockPos());

            joinSructure(newStructureMaster, slaveComponent);
        }

        //Add the structures Master, destroying the old structures
        newStructureMaster.getStructureData().onDataMergeInto(merginComponentMaster.getStructureData());
        joinSructure(newStructureMaster, merginComponentMaster);
    }

    public static IStructureComponent splitStructure(IStructureComponent<?> pOldMasterStructure, ArrayList<IStructureComponent<?>> pSplittedComponents) {
        //Create the new structures master Entity
        IStructureComponent tNewMasterComponent = pSplittedComponents.remove(0);
        tNewMasterComponent.initiateAsMasterEntity();

        getWorld(tNewMasterComponent).setTileEntity(tNewMasterComponent.getLocation().toBlockPos(), (TileEntity) tNewMasterComponent);

        //Let all the Slaves join the new handlers
        for (IStructureComponent tNewSlave : pSplittedComponents) {
            joinSructure(tNewMasterComponent, tNewSlave);

            pOldMasterStructure.removeSlave(tNewSlave.getLocation());
        }

        return tNewMasterComponent;
    }

    public static void createStructureComponent(IStructureComponent<?> tNewComponent) {
        IStructureComponent tTargetStructure = null;

        tTargetStructure = checkNewComponentSide(tNewComponent.getLocation().moveCoordinate(EnumFacing.EAST, 1), tTargetStructure, tNewComponent);
        tTargetStructure = checkNewComponentSide(tNewComponent.getLocation().moveCoordinate(EnumFacing.WEST, 1), tTargetStructure, tNewComponent);
        tTargetStructure = checkNewComponentSide(tNewComponent.getLocation().moveCoordinate(EnumFacing.SOUTH, 1), tTargetStructure, tNewComponent);
        tTargetStructure = checkNewComponentSide(tNewComponent.getLocation().moveCoordinate(EnumFacing.NORTH, 1), tTargetStructure, tNewComponent);

        if (tTargetStructure == null) {
            tNewComponent.initiateAsMasterEntity();
        }
    }

    private static IStructureComponent checkNewComponentSide(Coordinate3D pTargetCoordinate, IStructureComponent<?> pTargetStructure, IStructureComponent<?> pNewComponent) {
        TileEntity tEntity = getWorld(pNewComponent).getTileEntity(pTargetCoordinate.toBlockPos());
        if (tEntity == null)
            return pTargetStructure;

        if (!( tEntity instanceof IStructureComponent ))
            return pTargetStructure;

        if (!( (IStructureComponent) tEntity ).getStructureTypeUniqueID().equals(pNewComponent.getStructureTypeUniqueID()))
            return pTargetStructure;

        if (pTargetStructure == null) {
            joinSructure((IStructureComponent) tEntity, pNewComponent);
            return (IStructureComponent) tEntity;
        }

        if (( (IStructureComponent) tEntity ).getMasterLocation().equals(pNewComponent.getMasterLocation()))
            return pTargetStructure;

        if (( (IStructureComponent) tEntity ).isSlaved()) {
            mergeStructures(pTargetStructure, (IStructureComponent) tEntity.getWorld().getTileEntity(( (IStructureComponent) tEntity ).getMasterLocation().toBlockPos()), pNewComponent);
        } else {
            mergeStructures(pTargetStructure, ( (IStructureComponent) tEntity ), pNewComponent);
        }

        return pTargetStructure;
    }

    public static void destroyStructureComponent(IStructureComponent<?> tToBeDestroyedComponent) {
        IStructureComponent<?> tMasterComponent = null;

        if (tToBeDestroyedComponent.isSlaved()) {
            ( (IStructureComponent) ( (TileEntity) tToBeDestroyedComponent ).getWorld().getTileEntity(tToBeDestroyedComponent.getMasterLocation().toBlockPos()) ).removeSlave(tToBeDestroyedComponent.getLocation());

            tMasterComponent = (IStructureComponent) ( (TileEntity) tToBeDestroyedComponent ).getWorld().getTileEntity(tToBeDestroyedComponent.getMasterLocation().toBlockPos());
        } else {
            if (tToBeDestroyedComponent.getSlaveCoordinates().size() == 1) {
                tMasterComponent = (IStructureComponent) ((TileEntity) tToBeDestroyedComponent).getWorld().getTileEntity(tToBeDestroyedComponent.getSlaveCoordinates().iterator().next().toBlockPos());
                tMasterComponent.initiateAsMasterEntity();
                tMasterComponent.getStructureData().onDataMergeInto(tToBeDestroyedComponent.getStructureData());
            } else if (tToBeDestroyedComponent.getSlaveCoordinates().size() > 1) {
                Iterator<Coordinate3D> iterator = tToBeDestroyedComponent.getSlaveCoordinates().iterator();

                tMasterComponent = (IStructureComponent) ((TileEntity) tToBeDestroyedComponent).getWorld().getTileEntity(iterator.next().toBlockPos());
                tMasterComponent.initiateAsMasterEntity();
                tMasterComponent.getStructureData().onDataMergeInto(tToBeDestroyedComponent.getStructureData());

                while (iterator.hasNext()) {
                    joinSructure(tMasterComponent, (IStructureComponent) ((TileEntity) tToBeDestroyedComponent).getWorld().getTileEntity(iterator.next().toBlockPos()));
                }
            }
        }

        if (tMasterComponent != null) {
            ArrayList<IStructureComponent<?>> tNotConnectedComponents = validateStructureIntegrity(tMasterComponent, tToBeDestroyedComponent);
            while (!tNotConnectedComponents.isEmpty()) {
                tNotConnectedComponents = validateStructureIntegrity(splitStructure(tMasterComponent, tNotConnectedComponents), tToBeDestroyedComponent);
            }
        }
    }

    public static ArrayList<IStructureComponent<?>> validateStructureIntegrity(IStructureComponent<?> pMasterComponent, IPathComponent pSeperatingComponent) {
        ArrayList<IStructureComponent<?>> tNotConnectedComponents = new ArrayList<>();

        for (Coordinate3D slaveCoordinate : pMasterComponent.getSlaveCoordinates()) {
            IStructureComponent slaveComponent = (IStructureComponent) ( (TileEntity) pMasterComponent ).getWorld().getTileEntity(slaveCoordinate.toBlockPos());

            if (!checkIfComponentStillConnected(pMasterComponent, slaveComponent, pSeperatingComponent))
                tNotConnectedComponents.add(slaveComponent);
        }

        for (IStructureComponent tSlave : tNotConnectedComponents) {
            SmithsCore.getLogger().info("Removing " + tSlave.getLocation().toString() + " from structure.");
            pMasterComponent.removeSlave(tSlave.getLocation());
        }

        return tNotConnectedComponents;
    }

    private static World getWorld(IStructureComponent<?> pComponent) {
        return ( (TileEntity) pComponent ).getWorld();
    }

    private static boolean checkIfComponentStillConnected(IStructureComponent<?> pMasterComponent, IStructureComponent<?> pTargetComponent, IPathComponent pSplittingComponent) {
        SmithsCore.getLogger().info("Starting connection search between: " + pMasterComponent.getLocation().toString() + " to " + pTargetComponent.getLocation().toString());

        PathFinder tConnectionChecker = new PathFinder(pMasterComponent, pTargetComponent, pSplittingComponent);
        return tConnectionChecker.isConnected();
    }


}
