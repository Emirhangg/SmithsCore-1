package com.smithsmodding.smithscore.client.gui.scissoring;

import com.smithsmodding.smithscore.client.gui.hosts.*;
import com.smithsmodding.smithscore.util.common.positioning.*;

/**
 * Created by Marc on 10.01.2016.
 */
public interface IScissoredGuiComponent extends IGUIBasedComponentHost {

    boolean shouldScissor ();

    Plane getGlobalScissorLocation ();
}

