package org.dotvill.client.screen.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;

public class ClientTradeItemTooltip implements ClientTooltipComponent {
    private final ItemStack a;
    private final ItemStack b;
    private final ItemStack c;


    public ClientTradeItemTooltip(TradeItemTooltip tooltip) {
        this.a = tooltip.getA();
        this.b = tooltip.getB();
        this.c = tooltip.getC();
    }

    @Override
    public void renderImage(Font pFont, int pX, int pY, GuiGraphics pGuiGraphics) {
        pGuiGraphics.renderItem(this.a, pX , pY + 1, 1);
        pGuiGraphics.renderItemDecorations(pFont, this.a, pX , pY + 1);
        if (!this.b.isEmpty()) {
            pGuiGraphics.renderItem(this.b, pX + 18, pY + 1, 2);
            pGuiGraphics.renderItemDecorations(pFont, this.b, pX + 18, pY + 1);
        }
        if (!this.c.isEmpty()) {
            pGuiGraphics.renderItem(this.c, pX + 18 + 18, pY + 1, 3);
            pGuiGraphics.renderItemDecorations(pFont, this.c, pX + 18 + 18, pY + 1);
        }
    }

    @Override
    public int getHeight() {
        return 21;
    }

    @Override
    public int getWidth(Font pFont) {
        int i = 0;
        if (!this.a.isEmpty()) {
            i+= 18;
        }
        if (!this.b.isEmpty()) {
            i+= 18;
        }
        if (!this.c.isEmpty()) {
            i+= 18;
        }
        return i + 1;
    }
}
