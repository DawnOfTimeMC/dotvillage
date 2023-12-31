package org.dotvill.trade;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.network.NetworkHooks;
import org.dotvill.entity.Dillager;
import org.dotvill.menu.BuyMenu;
import org.dotvill.menu.SellMenu;
import org.dotvill.util.ModLogger;
import org.jetbrains.annotations.Nullable;

public class VillagerTradingModule implements TradingModule {
    private Dillager owner;
    private TraderDeals deals;
    private Player tradingPlayer;
    private String name = "Forgeron";

    public VillagerTradingModule(Dillager owner) {
        this.owner = owner;
    }

    @Override
    public void openTradingScreen(Player player, Component displayName) {
        if (false && player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((containerID, playerInventory, p) -> new BuyMenu(containerID, playerInventory, this), displayName), buffer -> {
                getDeals().writeToStream(buffer);
                buffer.writeInt(owner.getId());

                ModLogger.info("EXTRA DATA WRITTEN : " + buffer.readableBytes());
            });
        }

        if (player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((containerID, playerInventory, p) -> new SellMenu(containerID, playerInventory, this), displayName), buffer -> {
                getDeals().writeToStream(buffer);
                buffer.writeInt(owner.getId());

                ModLogger.info("EXTRA DATA WRITTEN : " + buffer.readableBytes());
            });
        }
    }

    public void startTradingWithPlayer(Player player) {
        this.tradingPlayer = player;
        openTradingScreen(tradingPlayer, Component.literal("Sell"));
    }

    @Override
    public LivingEntity getOwner() {
        return this.owner;
    }

    @Nullable
    @Override
    public Player getTradingPlayer() {
        return this.tradingPlayer;
    }

    @Override
    public void setTradingPlayer(@Nullable Player tradingPlayer) {
        this.tradingPlayer = tradingPlayer;
    }
    @Override
    public TraderDeals getDeals() {
        if (this.deals == null) {
            TraderDeals deals = new TraderDeals();
            deals.add(buyDeal(Items.EMERALD, Items.BROWN_MUSHROOM));
            deals.add(buyDeal(Items.EMERALD, Items.RED_MUSHROOM));
            deals.add(buyDeal(Items.EMERALD, Items.MANGROVE_PROPAGULE));
            deals.add(buyDeal(Items.EMERALD, Items.CHERRY_SAPLING));
            deals.add(buyDeal(Items.EMERALD, Items.AZALEA));
            deals.add(buyDeal(Items.EMERALD, Items.FERN));
            deals.add(buyDeal(Items.EMERALD, Items.DANDELION));
            deals.add(buyDeal(Items.EMERALD, Items.POPPY));
            deals.add(buyDeal(Items.EMERALD, Items.BLUE_ORCHID));
            deals.add(buyDeal(Items.EMERALD, Items.ALLIUM));
            deals.add(buyDeal(Items.EMERALD, Items.CORNFLOWER));
            deals.add(buyDeal(Items.EMERALD, Items.LILY_OF_THE_VALLEY));
            deals.add(buyDeal(Items.EMERALD, Items.PINK_PETALS));
            deals.add(buyDeal(Items.EMERALD, Items.SUNFLOWER));







            return deals;
        } else {
            return this.deals;
        }
    }


    private TraderDeal buyDeal(Item a, Item result) {
        return new TraderDeal.Builder(TraderDeal.DealType.PLAYER_BUYING, new ItemStack(a,3), new ItemStack(result,1)).build();
    }

    private TraderDeal buyDeal(Item a, Item b, Item c, Item result) {
        return new TraderDeal.Builder(TraderDeal.DealType.PLAYER_BUYING, new ItemStack(a,1), new ItemStack(result,2))
                .secondInput(new ItemStack(b,1)).thirdInput(new ItemStack(c,1)).build();
    }

    private TraderDeal buyDeal(Item a, Item b, Item result) {
        return new TraderDeal.Builder(TraderDeal.DealType.PLAYER_BUYING, new ItemStack(a,3), new ItemStack(result,1))
                .secondInput(new ItemStack(b,3)).build();
    }


    @Override
    public void notifyDeal(TraderDeal deal) {

    }

    @Override
    public SoundEvent getDealSound() {
        return SoundEvents.VILLAGER_TRADE;
    }

    @Override
    public boolean isClientSide() {
        return false;
    }

}
