package com.SmithsModding.SmithsCore.Client.GUI.Host;

import com.SmithsModding.SmithsCore.Client.GUI.Components.IGUIComponent;
import com.SmithsModding.SmithsCore.Client.GUI.Management.IGUIManager;
import com.SmithsModding.SmithsCore.Client.GUI.Management.IGUIManagerProvider;
import com.SmithsModding.SmithsCore.Util.Common.Postioning.Coordinate2D;
import net.minecraft.client.gui.Gui;

import java.util.HashMap;

/**
 * Created by Orion
 * Created on 01.12.2015
 * 18:13
 * <p/>
 * Copyrighted according to Project specific license
 */
public interface IGUIBasedComponentHost extends IGUIManagerProvider
{

    /**
     * Function used to register the sub components of this ComponentHost
     *
     * @param host This ComponentHosts host. For the Root GUIObject a reference to itself will be passed in..
     */
    void registerComponents(IGUIBasedComponentHost host);

    /**
     * Method to get the Root GUI Object that this Component is part of.
     *
     * @return The GUI that this component is part of.
     */
    Gui getRootGuiObject();

    /**
     * Method to get the GUI Roots Manager.
     *
     * @return The Manager that is at the root for the GUI Tree.
     */
    IGUIManager getRootManager();

    /**
     * Function to get all the Components registered to this Host.
     *
     * @return A ID to Component map that holds all the Components (but not their SubComponents) of this Host.
     */
    HashMap<String, IGUIComponent> getAllComponents();
}