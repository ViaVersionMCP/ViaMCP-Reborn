package viamcp.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;
import viamcp.ViaMCP;
import viamcp.protocols.ProtocolCollection;

import java.io.IOException;

public class GuiProtocolSelector extends GuiScreen
{
    private GuiScreen parent;
    public SlotList list;

    public GuiProtocolSelector(GuiScreen parent)
    {
        this.parent = parent;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        buttonList.add(new GuiButton(1, width / 2 - 100, height - 27, 200, 20, "Back"));
        list = new SlotList(mc, width, height, 32, height - 32, 10);
    }

    @Override
    protected void actionPerformed(GuiButton p_actionPerformed_1_) throws IOException
    {
        list.actionPerformed(p_actionPerformed_1_);

        if (p_actionPerformed_1_.id == 1)
        {
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        list.handleMouseInput();
        super.handleMouseInput();
    }

    @Override
    public void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_)
    {
        list.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
        GL11.glPushMatrix();
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        this.drawCenteredString(this.fontRendererObj, TextFormatting.BOLD + "ViaMCP Reborn", this.width / 4, 6, 16777215); // You can use either ChatFormatting (Realms) or TextFormatting (Default), They both work the same.
        GL11.glPopMatrix();
        drawString(this.fontRendererObj, "Maintained by Hideri (1.12.2 Version)", 3, 3, -1);
        drawString(this.fontRendererObj, "Discord: Hideri#9003", 3, 13, -1);
        drawString(this.fontRendererObj, "Credits", 3, this.height - 30, -1);
        drawString(this.fontRendererObj, "ViaForge: https://github.com/FlorianMichael/ViaForge", 3, this.height - 20, -1);
        drawString(this.fontRendererObj, "Original ViaMCP: https://github.com/LaVache-FR/ViaMCP", 3, this.height - 10, -1);
        super.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
    }

    class SlotList extends GuiSlot
    {
        public SlotList(Minecraft p_i1052_1_, int p_i1052_2_, int p_i1052_3_, int p_i1052_4_, int p_i1052_5_, int p_i1052_6_)
        {
            super(p_i1052_1_, p_i1052_2_, p_i1052_3_, p_i1052_4_, p_i1052_5_, p_i1052_6_);
        }

        @Override
        protected int getSize()
        {
            return ProtocolCollection.values().length;
        }

        @Override
        protected void elementClicked(int i, boolean b, int i1, int i2)
        {
            ViaMCP.getInstance().setVersion(ProtocolCollection.values()[i].getVersion().getVersion());
        }

        @Override
        protected boolean isSelected(int i)
        {
            return false;
        }

        @Override
        protected void drawBackground()
        {
            drawDefaultBackground();
        }

        @Override
        protected void func_192637_a(int p_192637_1_, int p_192637_2_, int p_192637_3_, int p_192637_4_, int p_192637_5_, int p_192637_6_, float p_192637_7_)
        {
            drawCenteredString(mc.fontRendererObj,(ViaMCP.getInstance().getVersion() == ProtocolCollection.values()[p_192637_1_].getVersion().getVersion() ? TextFormatting.GREEN.toString() + TextFormatting.BOLD : TextFormatting.GRAY.toString()) + ProtocolCollection.getProtocolById(ProtocolCollection.values()[p_192637_1_].getVersion().getVersion()).getName(), width / 2, p_192637_3_ + 2, -1); // Same here. You can use either ChatFormatting (Realms) or TextFormatting (Default), They both work the same.
        }
    }
}
