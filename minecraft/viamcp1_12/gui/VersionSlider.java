package viamcp.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import viamcp.ViaMCP;
import viamcp.protocols.ProtocolCollection;

import java.util.Arrays;
import java.util.Collections;

public class VersionSlider extends GuiButton
{
    private final ProtocolCollection[] values;
    private float sliderValue;
    public boolean dragging;

    public VersionSlider(int buttonId, int x, int y , int widthIn, int heightIn)
    {
        super(buttonId, x, y, MathHelper.clamp_int(widthIn, 110, Integer.MAX_VALUE), heightIn, "");
        this.values = ProtocolCollection.values();
        Collections.reverse(Arrays.asList(values));
        this.sliderValue = GuiProtocolSelector.sliderDragValue != -1.0f ? GuiProtocolSelector.sliderDragValue : 0.0f;
        this.displayString = GuiProtocolSelector.sliderDragValue != -1.0f ? "Version: " + values[(int) (this.sliderValue * (values.length - 1))].getVersion().getName() : "Drag for Version";
    }

    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     */
    protected int getHoverState(boolean mouseOver)
    {
        return 0;
    }

    /**
     * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            if (this.dragging)
            {
                this.sliderValue = (float)(mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
                this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
                GuiProtocolSelector.sliderDragValue = sliderValue;
                this.displayString = "Version: " + values[(int) (this.sliderValue * (values.length - 1))].getVersion().getName();
                ViaMCP.getInstance().setVersion(values[(int) (this.sliderValue * (values.length - 1))].getVersion().getVersion());
            }

            mc.getTextureManager().bindTexture(buttonTextures);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)), this.yPosition, 0, 66, 4, 20);
            this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
        }
    }

    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
     * e).
     */
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
        if (super.mousePressed(mc, mouseX, mouseY))
        {
            this.sliderValue = (float)(mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
            this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
            GuiProtocolSelector.sliderDragValue = sliderValue;
            this.displayString = "Version: " + values[(int) (this.sliderValue * (values.length - 1))].getVersion().getName();
            ViaMCP.getInstance().setVersion(values[(int) (this.sliderValue * (values.length - 1))].getVersion().getVersion());
            this.dragging = true;
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
     */
    public void mouseReleased(int mouseX, int mouseY)
    {
        this.dragging = false;
    }
}
