package org.dotvill.trade;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.dotvill.util.ModLogger;
import org.jetbrains.annotations.Nullable;

public class ClientSideTradingModule implements TradingModule {
    private LivingEntity owner;
    private Player source;
    private TraderDeals deals;

    public ClientSideTradingModule(Player player, TraderDeals deals, LivingEntity owner) {
        this.source = player;
        ModLogger.info("INSIDE CLIENT SIDE MODULE CONSTRUCTOR");
        ModLogger.info("Deals size :"+deals.size());
        deals.forEach((deal -> {
            ModLogger.info(deal.getResult().toString());
        }));;
        ModLogger.info("End of item list");
        this.deals = deals;
        this.owner = owner;
    }

    @Nullable
    @Override
    public Player getTradingPlayer() {
        return this.source;
    }

    @Override
    public LivingEntity getOwner() {
        return this.owner;
    }

    @Override
    public void setTradingPlayer(@Nullable Player tradingPlayer) {
        this.source = tradingPlayer;
    }

    @Override
    public TraderDeals getDeals() {
        return this.deals;
    }

    @Override
    public void notifyDeal(TraderDeal deal) {

    }

    @Override
    public SoundEvent getDealSound() {
        return SoundEvents.DECORATED_POT_BREAK;
    }

    @Override
    public void openTradingScreen(Player player, Component displayName) {

    }

    @Override
    public boolean isClientSide() {
        return true;
    }
}
