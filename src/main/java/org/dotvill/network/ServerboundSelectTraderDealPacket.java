package org.dotvill.network;

import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import org.dotvill.menu.BuyMenu;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class ServerboundSelectTraderDealPacket {
    private final int item;

    public ServerboundSelectTraderDealPacket(int pItem) {
        this.item = pItem;
    }

    public ServerboundSelectTraderDealPacket(FriendlyByteBuf pBuffer) {
        this.item = pBuffer.readVarInt();
    }

    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeVarInt(this.item);
    }

    public int getItem() {
        return this.item;
    }

    public boolean handle(Supplier<NetworkEvent.Context> pSupplier) {
        NetworkEvent.Context context = pSupplier.get();
        context.enqueueWork(() -> {
            int item = this.getItem();
            AbstractContainerMenu containerMenu = context.getSender().containerMenu;
            if (containerMenu instanceof BuyMenu buyMenu) {
                if (!buyMenu.stillValid( context.getSender())) {
                    Logger LOGGER = LogUtils.getLogger();
                    LOGGER.debug("Player {} interacted with invalid menu {}", context.getSender(), buyMenu);
                }
                else {
                    buyMenu.setSelectionHint(item);
                    buyMenu.tryMoveItems(item);
                }
            }
        });
        return true;
    }
}