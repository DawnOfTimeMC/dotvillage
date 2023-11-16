package org.dawnoftimevillage.trade;

import net.minecraft.world.entity.player.Player;

public class TradingHandler {
    /** Trader object which is trading with the player. Could be an NPC, a block, a book...**/
    private TraderComponent trader;
    /** Player which is trading **/
    private Player tradingPlayer;
    private boolean trading;
    private TraderDeals traderDeals;

    public TradingHandler(TraderComponent trader) {
        this.trader = trader;
    }

    public void stopTrading() {
        this.setTradingPlayer(null);
    }

    public void setTradingPlayer(Player player) {
        this.tradingPlayer = player;
    }

    public boolean isTrading() {
        return this.tradingPlayer != null;
    }

    /*
    public MerchantOffers getOffers() {
        if (this.offers == null) {
            this.offers = new MerchantOffers();
            this.updateTrades();
        }
        return this.offers;
    }

    public boolean hasTrades() {
        return true;

    public void notifyTrade(MerchantOffer pOffer) {
        pOffer.increaseUses();
        //this.ambientSoundTime = -this.getAmbientSoundInterval();
        this.rewardTradeXp(pOffer);
        /*
        if (this.tradingPlayer instanceof ServerPlayer) {
            CriteriaTriggers.TRADE.trigger((ServerPlayer)this.tradingPlayer, this, pOffer.getResult());
        }
net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.TradeWithVillagerEvent(this.tradingPlayer, pOffer, this));



²protected abstract void rewardTradeXp(MerchantOffer pOffer);

    @Override
    public void notifyTradeUpdated(ItemStack pStack)

    @Override
    public void overrideXp(int pXp) {}

    protected void stopTrading() {
        this.setTradingPlayer((Player)null);
    }

    public boolean isTrading() {
        return this.tradingPlayer != null;
    }

    @Override
    public boolean isClientSide() {
        return this.level().isClientSide();
    }
}

    public void stopTrading() {
        this.tradingPlayer = null;
    }

    @Override
    protected void rewardTradeXp(MerchantOffer pOffer) {
        if (pOffer.shouldRewardExp()) {
            int i = 3 + this.random.nextInt(4);
            this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY() + 0.5D, this.getZ(), i));
        }
    }


    public void openTradingScreen(Player pPlayer, Component pDisplayName, int pLevel) {
        OptionalInt optionalint = pPlayer.openMenu(new SimpleMenuProvider((pContainerId, pPlayerInventory, player) -> {
            //return new MerchantMenu(pContainerId, pPlayerInventory, this);
            return new AovVillagerMenu(pContainerId, pPlayerInventory, this);
        }, pDisplayName));

        if (optionalint.isPresent()) {
            MerchantOffers merchantoffers = this.getOffers();
            if (!merchantoffers.isEmpty()) {
                if(pPlayer instanceof ServerPlayer player) {
                    ModPacketHandler.sendToPlayer(new TraderNpcClientOffersPacket(optionalint.getAsInt(), merchantoffers, pLevel, this.getVillagerXp(), this.showProgressBar(), this.canRestock()), player);
                    //pPlayer.sendMerchantOffers(optionalint.getAsInt(), merchantoffers, pLevel, this.getVillagerXp(), this.showProgressBar(), this.canRestock());
                }
            }
        }
    }

    protected void updateTrades() {
        VillagerTrades.ItemListing[] trades = {
                new AovVillagerTrades.ItemsForRubies(Items.IRON_HELMET, 4, 1, 5),
                new AovVillagerTrades.ItemsForRubies(Items.IRON_CHESTPLATE, 6, 1, 5),
                new AovVillagerTrades.ItemsForRubies(Items.IRON_LEGGINGS, 18, 1, 5),
                new AovVillagerTrades.ItemsForRubies(Items.IRON_BOOTS, 99, 1, 5),

                new AovVillagerTrades.ItemsForRubies(Items.GOLDEN_HELMET, 4, 1, 5),
                new AovVillagerTrades.ItemsForRubies(Items.GOLDEN_CHESTPLATE, 6, 1, 5),
                new AovVillagerTrades.ItemsForRubies(Items.GOLDEN_LEGGINGS, 5, 1, 5),
                new AovVillagerTrades.ItemsForRubies(Items.GOLDEN_BOOTS, 4, 1, 5),
                new AovVillagerTrades.ItemsForRubies(Items.GOLDEN_CARROT, 4, 1, 5),
                new AovVillagerTrades.ItemsForRubies(Items.BEACON, 22, 1, 5),
                new AovVillagerTrades.ItemsForRubies(Items.BEEF, 4, 1, 5),
                new AovVillagerTrades.ItemsForRubies(Items.COAL_BLOCK, 4, 1, 5),
                new AovVillagerTrades.ItemsForRubies(Items.LIME_STAINED_GLASS_PANE, 4, 1, 5),
                new AovVillagerTrades.ItemsForRubies(Items.RAW_GOLD, 4, 1, 5)
        };

        MerchantOffers offers = this.getOffers();
        for(int i = 0; i < trades.length; ++i) {
            offers.add(trades[i].getOffer(this, this.random));
        }


        VillagerTrades.ItemListing[] trades2 = AovVillagerTrades.AOV_VILLAGER_TRADES.get(2);

        if (trades != null && trades2 != null) {
            MerchantOffers merchantoffers = this.getOffers();
            this.addOffersFromItemListings(merchantoffers, trades, 5);

            int i = this.random.nextInt(trades2.length);
            VillagerTrades.ItemListing villagertrades$itemlisting = trades2[i];
            MerchantOffer merchantoffer = villagertrades$itemlisting.getOffer(this, this.random);
            if (merchantoffer != null) {
                merchantoffers.add(merchantoffer);
            }
        }
    }


    */
}
