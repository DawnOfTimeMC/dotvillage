package org.dotvill.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.dotvill.DotVill;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE =  NetworkRegistry.newSimpleChannel(new ResourceLocation(DotVill.MOD_ID, "channel"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    private static int packetId = 0;

    private ModNetwork() {}

    private static int id() {
        return packetId++;
    }

    public static void init() {
        INSTANCE.registerMessage(id(),
                ServerboundSelectTraderDealPacket.class,
                ServerboundSelectTraderDealPacket::write,
                ServerboundSelectTraderDealPacket::new,
                ServerboundSelectTraderDealPacket::handle);

        INSTANCE.registerMessage(id(),
                ClientboundOpenBuyMenuPacket.class,
                ClientboundOpenBuyMenuPacket::write,
                ClientboundOpenBuyMenuPacket::new,
                ClientboundOpenBuyMenuPacket::handle);
        /* Other way using builder
        INSTANCE.messageBuilder(TraderNpcClientOffersPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(TraderNpcClientOffersPacket::write)
                .decoder(TraderNpcClientOffersPacket::new)
                .consumerMainThread(TraderNpcClientOffersPacket::handle)
                .add(); */
    };

    public static <MSG> void sendToServer(MSG pMessage) {
        INSTANCE.sendToServer(pMessage);
    }

    public static <MSG> void sendToPlayer(MSG pMessage, ServerPlayer pPlayer) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> pPlayer), pMessage);
    }

    public static <MSG> void sendToAllPlayers(MSG pMessage) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), pMessage);
    }
}
