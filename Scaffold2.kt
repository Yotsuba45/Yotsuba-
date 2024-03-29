package net.ccbluex.liquidbounce.features.module.modules.world

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render2DEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.client.SoundModule
import net.ccbluex.liquidbounce.features.value.BoolValue
import net.ccbluex.liquidbounce.features.value.ListValue
import net.ccbluex.liquidbounce.utils.InventoryUtil
import net.ccbluex.liquidbounce.utils.InventoryUtils
import net.ccbluex.liquidbounce.utils.extensions.drawCenteredString
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.block.BlockBush
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import java.awt.Color

@ModuleInfo(
    name = "Scaffold2",
    description = "by SZ四叶" ,
    category = ModuleCategory.WORLD
)
class Scaffold2 : Module() {
    val auto = BoolValue("Auto", false)
    val scaffold2 = BoolValue("scaffold", true)
    val Sound = BoolValue("SoundModule", false)
    val toggleSoundValue = ListValue("ToggleSound", arrayOf("None", "Custom", "Custom2"), "Custom").displayable { Sound.get() }
    private val slowValue = BoolValue("slowValue", false)
    private val counterDisplayValue = BoolValue("Counter", true)
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val scaffold = LiquidBounce.moduleManager.getModule(Scaffold::class.java) as Scaffold
        val soundModule = LiquidBounce.moduleManager.getModule(SoundModule::class.java) as SoundModule
        if (auto.get()) {
            if (!mc.gameSettings.keyBindJump.pressed) {
                this.scaffold2.set(false)
                if (slowValue.get()) {
                    scaffold.slowValue.set(true)
                }
            } else {
                if (slowValue.get()) {
                    scaffold.slowValue.set(false)
                }
                this.scaffold2.set(true)
            }
        }
        if (scaffold2.get()) {
            if (mc.player!!.onGround) {
                if (Sound.get()) {
                    if (soundModule.toggleSoundValue.get() != "none") soundModule.toggleSoundValue.set("none")
                }
                LiquidBounce.moduleManager[Scaffold::class.java].state = false
            } else {
                LiquidBounce.moduleManager[Scaffold::class.java].state = true
                if (Sound.get()) {
                    if (soundModule.toggleSoundValue.get() != toggleSoundValue.get()) soundModule.toggleSoundValue.set(toggleSoundValue.get())
                }
            }
        }
    }
    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        if (counterDisplayValue.get()) {
            GlStateManager.pushMatrix()
            val info = blocksAmount.toString()
            val barrier = ItemStack(Item.getItemById(166), 0, 0)
            val slot = InventoryUtils.findAutoBlockBlock()
            val scaledResolution = ScaledResolution(mc)
            val height = scaledResolution.scaledHeight
            val width = scaledResolution.scaledWidth
            val w2=(mc.fontRenderer.getStringWidth(info))
            RenderUtils.drawRoundedCornerRect(
                (width - w2 - 20) / 2f,
                height * 0.8f - 24f,
                (width + w2 + 18) / 2f,
                height * 0.8f + 12f,
                5f,
                Color(20, 20, 20, 100).rgb
            )
            var stack = barrier
            if (slot != -1) {
                if (mc.player.inventory.getCurrentItem() != null) {
                    val handItem = mc.player.inventory.getCurrentItem().item
                    if (handItem is ItemBlock && InventoryUtil.canPlaceBlock(handItem.block)) {
                        stack = mc.player.inventory.getCurrentItem()
                    }
                }
                if (stack == barrier) {
                    stack = mc.player.inventory.getStackInSlot(InventoryUtils.findAutoBlockBlock() - 36)
                    if (stack == null) {
                        stack = barrier
                    }
                }
            }

            RenderHelper.enableGUIStandardItemLighting()
            mc.renderItem.renderItemIntoGUI(stack, width / 2 - 9, (height * 0.8 - 20).toInt())
            RenderHelper.disableStandardItemLighting()
            mc.fontRenderer.drawCenteredString(info, width / 2f, height * 0.8f, Color.WHITE.rgb, false)
            GlStateManager.popMatrix()
        }
    }
    val blocksAmount: Int
        get() {
            var amount = 0
            for (i in 36..44) {
                val itemStack = mc.player!!.inventoryContainer.getSlot(i).stack
                if (itemStack != null && itemStack.item is ItemBlock) {
                    val block = (itemStack.item as ItemBlock).block
                    val heldItem = mc.player!!.heldItemMainhand
                    if (heldItem != null && heldItem == itemStack || !InventoryUtils.BLOCK_BLACKLIST.contains(block) && block !is BlockBush
                    ) amount += itemStack.stackSize
                }
            }
            return amount
        }
}
