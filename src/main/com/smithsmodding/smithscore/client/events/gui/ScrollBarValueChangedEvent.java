package com.smithsmodding.smithscore.client.events.gui;

import com.smithsmodding.smithscore.common.events.network.*;
import io.netty.buffer.*;

/**
 * Created by Marc on 10.02.2016.
 */
public class ScrollBarValueChangedEvent extends GuiInputEvent {

    int maximum;
    int minimum;

    public ScrollBarValueChangedEvent (String componentID, float value, int maximum, int minimum) {
        super(InputTypes.SCROLLED, componentID, String.valueOf(value));

        this.maximum = maximum;
        this.minimum = minimum;

    }

    public ScrollBarValueChangedEvent () {
    }


    /**
     * Function used by the instance created on the receiving side to reset its state from the sending side stored by it
     * in the Buffer before it is being fired on the NetworkRelayBus.
     *
     * @param pMessageBuffer The ByteBuffer from the IMessage used to Synchronize the implementing events.
     */
    @Override
    public void readFromMessageBuffer (ByteBuf pMessageBuffer) {
        super.readFromMessageBuffer(pMessageBuffer);

        this.maximum = pMessageBuffer.readInt();
        this.minimum = pMessageBuffer.readInt();
    }

    /**
     * Function used by the instance on the sending side to write its state top the Buffer before it is send to the
     * retrieving side.
     *
     * @param pMessageBuffer The buffer from the IMessage
     */
    @Override
    public void writeToMessageBuffer (ByteBuf pMessageBuffer) {
        super.writeToMessageBuffer(pMessageBuffer);

        pMessageBuffer.writeInt(maximum);
        pMessageBuffer.writeInt(minimum);
    }
}
