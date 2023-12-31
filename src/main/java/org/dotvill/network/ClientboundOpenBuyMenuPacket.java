package org.dotvill.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import org.dotvill.menu.BuyMenu;
import org.dotvill.trade.TraderDeals;

import java.util.function.Supplier;

public class ClientboundOpenBuyMenuPacket {
    private final int containerId;
    private final TraderDeals deals;

    public ClientboundOpenBuyMenuPacket(int pContainerId, TraderDeals deals) {
        this.containerId = pContainerId;
        this.deals = deals;
    }

    public ClientboundOpenBuyMenuPacket(FriendlyByteBuf pBuffer) {
        this.containerId = pBuffer.readVarInt();
        this.deals = TraderDeals.createFromStream(pBuffer);
    }

    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeVarInt(this.containerId);
        this.deals.writeToStream(pBuffer);
    }

    public int getContainerId() {
        return this.containerId;
    }

    public TraderDeals getDeals() {
        return this.deals;
    }


    public boolean handle(Supplier<NetworkEvent.Context> pSupplier) {
        NetworkEvent.Context context = pSupplier.get();
        context.enqueueWork(() -> {
            AbstractContainerMenu containerMenu = Minecraft.getInstance().player.containerMenu;
            if (getContainerId() == containerMenu.containerId && containerMenu instanceof BuyMenu buyMenu) {
                buyMenu.setDeals(new TraderDeals(getDeals().createTag()));
                //traderMenu.setXp(getVillagerXp());
                //traderMenu.setMerchantLevel(getVillagerLevel());
                //traderMenu.setShowProgressBar(showProgress());
                //traderMenu.setCanRestock(canRestock());
            }
        });
        return true;
    }
}
