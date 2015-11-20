package com.SmithsModding.SmithsCore.Util.Client.Color;

import net.minecraft.util.ResourceLocation;

/**
 * Class used as a PlaceHolder for BlockIcons while the ColorSampling process is running.
 *
 * Created by Orion
 * Created on 15.06.2015
 * 13:11
 * <p/>
 * Copyrighted according to Project specific license
 */
public class BlockIconPlaceHolder extends IconPlaceHolder {

    public BlockIconPlaceHolder(ResourceLocation tLocation) {
        super(tLocation);

        iIconLocation = new ResourceLocation(tLocation.getResourceDomain(), "textures/blocks/" + tLocation.getResourcePath() + ".png");
    }
}