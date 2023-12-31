package org.dotvill.trade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class TraderDeals extends ArrayList<TraderDeal> {
    public TraderDeals() {}

    public TraderDeals(int capacity) {
        super(capacity);
    }

    public TraderDeals(CompoundTag nbt) {
        ListTag listTag = nbt.getList("Recipes", 10);

        for (int i = 0; i < listTag.size(); ++i) {
            this.add(new TraderDeal(listTag.getCompound(i)));
        }

    }
    @Nullable
    public TraderDeal getRecipeFor(ItemStack stackA, ItemStack stackB, ItemStack stackC, int index) {
        if (index > 0 && index < this.size()) {
            TraderDeal deal = this.get(index);
            return deal.isSatisfiedBy(stackA, stackB, stackC) ? deal : null;
        } else {
            for(int i = 0; i < this.size(); ++i) {
                TraderDeal deal = this.get(i);
                if (deal.isSatisfiedBy(stackA, stackB, stackC)) {
                    return deal;
                }
            }

            return null;
        }
    }

    public void writeToStream(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeCollection(this, (buffer, deal) -> {
            buffer.writeItem(deal.getInputA());
            buffer.writeItem(deal.getResult());
            if (deal.getInputB() == null || deal.getInputB().isEmpty()) {
                buffer.writeItem(ItemStack.EMPTY);
            } else {
                buffer.writeItem(deal.getInputB());
            }
            if (deal.getInputC() == null || deal.getInputC().isEmpty()) {
                buffer.writeItem(ItemStack.EMPTY);
            } else {
                buffer.writeItem(deal.getInputC());
            }
        });
    }

    public static TraderDeals createFromStream(FriendlyByteBuf friendlyByteBuf) {
        return friendlyByteBuf.readCollection(TraderDeals::new, (buffer) -> {
            ItemStack inputA = buffer.readItem();
            ItemStack result = buffer.readItem();
            ItemStack requiredB = buffer.readItem();
            ItemStack requiredC = buffer.readItem();
            TraderDeal deal = new TraderDeal.Builder(TraderDeal.DealType.PLAYER_BUYING, inputA, result).secondInput(requiredB).thirdInput(requiredC).build();
            return deal;
        });
    }

    public CompoundTag createTag() {
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();

        for(int i = 0; i < this.size(); ++i) {
            TraderDeal deal = this.get(i);
            listTag.add(deal.createTag());
        }

        tag.put("Recipes", listTag);
        return tag;
    }
}
