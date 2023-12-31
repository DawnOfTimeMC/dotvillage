package org.dotvill.trade;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public interface TradingModule {

    @Nullable
    Player getTradingPlayer();

    void setTradingPlayer(@Nullable Player tradingPlayer);

    TraderDeals getDeals();

    default LivingEntity getOwner() {
        return null;
    }

    void notifyDeal(TraderDeal deal);

    SoundEvent getDealSound();

    void openTradingScreen(Player player, Component displayName);

    boolean isClientSide();
}
