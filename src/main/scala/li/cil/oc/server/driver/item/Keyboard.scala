package li.cil.oc.server.driver.item

import li.cil.oc.api
import li.cil.oc.api.driver.EnvironmentHost
import li.cil.oc.common.Slot
import li.cil.oc.server.component
import net.minecraft.item.ItemStack

object Keyboard extends Item {
  override def worksWith(stack: ItemStack) =
    isOneOf(stack, api.Items.get("keyboard"))

  override def createEnvironment(stack: ItemStack, host: EnvironmentHost) = new component.Keyboard(host)

  override def slot(stack: ItemStack) = Slot.Upgrade
}
