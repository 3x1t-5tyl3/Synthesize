package com.github.sieves.content.io.battery

import com.github.sieves.api.ApiScreen
import com.github.sieves.api.gui.ConfigWidget
import com.github.sieves.api.gui.UpgradesWidget
import com.github.sieves.api.ApiConfig
import com.github.sieves.dsl.res
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.minecraftforge.energy.CapabilityEnergy
import java.text.NumberFormat

@Suppress("DIFFERENT_NAMES_FOR_THE_SAME_PARAMETER_IN_SUPERTYPES")
class BatteryScreen(
    val container: BatteryContainer, playerInv: Inventory
) : ApiScreen<BatteryContainer, BatteryTile>(container, playerInv), ConfigWidget<BatteryTile>,
    UpgradesWidget<BatteryTile> {
    override val texture: ResourceLocation = "textures/gui/battery_gui.png".res
    override fun renderMain(stack: PoseStack, mouseX: Double, mouseY: Double) {
        blit(stack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight)
    }

    override fun renderOverlayWidgets(stack: PoseStack, mouseX: Double, mouseY: Double) {
        renderCharger(stack, mouseX, mouseY)
        val power = (tile().getStoredPower() / tile().getTotalPower().toFloat()) * 68
        blit(stack, this.leftPos + 165, this.topPos + 8, 0, 114, 3, power.toInt())
    }

    override fun renderToolTips(stack: PoseStack, mouseX: Double, mouseY: Double) {
        if (isHovered(83, 56, 9, 11, mouseX, mouseY)) {
            val item = tile().items.getStackInSlot(0)
            if (item.isEmpty) return
            val cap = item.getCapability(CapabilityEnergy.ENERGY)
            if (cap.isPresent) {
                val energy = cap.resolve().get()
                drawTooltip(
                    stack,
                    "power: §6${
                        NumberFormat.getIntegerInstance().format(energy.energyStored)
                    }FE/ ${NumberFormat.getIntegerInstance().format(energy.maxEnergyStored)}FE",
                    mouseX,
                    mouseY
                )
            }
        }

        if (isHovered(163, 8, 5, 67, mouseX, mouseY)) {
            drawTooltip(
                stack,
                "power: §6${
                    NumberFormat.getIntegerInstance().format(tile().getStoredPower())
                }FE/${NumberFormat.getIntegerInstance().format(tile().getTotalPower())}",
                mouseX,
                mouseY
            )
        }
    }


    private fun renderCharger(stack: PoseStack, mouseX: Double, mouseY: Double) {
        val item = tile().items.getStackInSlot(0)
        if (item.isEmpty) return
        val cap = item.getCapability(CapabilityEnergy.ENERGY)
        if (cap.isPresent) {
            val energy = cap.resolve().get()
            val percent = (energy.energyStored / energy.maxEnergyStored.toFloat()) * 11f
            blit(stack, this.leftPos + 83, this.topPos + 56, 59, 0, 9, percent.toInt())
        }
    }


    //ConfigWidget
    override val config: ApiConfig get() = container.tile.getConfig()
    override var configWidth: Float = 0f
    override var configure: Boolean = false

    /**
     * This is called by the basecontainer screen
     */
    override fun preRender(stack: PoseStack, mouseX: Double, mouseY: Double) {
        super<ConfigWidget>.preRender(stack, mouseX, mouseY)
            .also { super<UpgradesWidget>.preRender(stack, mouseX, mouseY) }
    }


    /**
     * This is called by the basecontainer screen
     */
    override fun postRender(stack: PoseStack, mouseX: Double, mouseY: Double) =
        super<ConfigWidget>.postRender(stack, mouseX, mouseY)
            .also { super<UpgradesWidget>.postRender(stack, mouseX, mouseY) }

    override var y: Float = 0f

    /**
     * Pass a reference to our tile
     */
    override val tile: () -> BatteryTile
        get() = { container.tile }
}
