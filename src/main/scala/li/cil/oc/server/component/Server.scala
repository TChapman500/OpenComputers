package li.cil.oc.server.component

import li.cil.oc.Items
import li.cil.oc.api.driver.Processor
import li.cil.oc.api.machine.{Architecture, MachineHost}
import li.cil.oc.api.network.{Message, Node}
import li.cil.oc.api.{Driver, Machine, driver}
import li.cil.oc.common.inventory.{ComponentInventory, ServerInventory}
import li.cil.oc.common.{Slot, item, tileentity}
import li.cil.oc.util.ExtendedNBT._
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

class Server(val rack: tileentity.ServerRack, val number: Int) extends MachineHost {
  val machine = Machine.create(this)

  val inventory = new NetworkedInventory()

  def tier = Items.multi.subItem(rack.getStackInSlot(number)) match {
    case Some(server: item.Server) => server.tier
    case _ => 0
  }

  // ----------------------------------------------------------------------- //

  override def cpuArchitecture: Class[_ <: Architecture] = {
    for (i <- 0 until inventory.getSizeInventory if inventory.isComponentSlot(i)) Option(inventory.getStackInSlot(i)) match {
      case Some(s) => Option(Driver.driverFor(s, inventory.host)) match {
        case Some(driver: Processor) if driver.slot(s) == Slot.CPU => return driver.architecture(s)
        case _ =>
      }
      case _ =>
    }
    null
  }

  override def installedMemory = inventory.items.foldLeft(0)((sum, stack) => sum + (stack match {
    case Some(item) => Option(Driver.driverFor(item, inventory.host)) match {
      case Some(driver: driver.Memory) => driver.amount(item)
      case _ => 0
    }
    case _ => 0
  }))

  lazy val maxComponents = if (!hasCPU) 0
  else inventory.items.foldLeft(0)((sum, stack) => sum + (stack match {
    case Some(item) => Option(Driver.driverFor(item, inventory.host)) match {
      case Some(driver: driver.Processor) => driver.supportedComponents(item)
      case _ => 0
    }
    case _ => 0
  }))

  override def componentSlot(address: String) = inventory.components.indexWhere(_.exists(env => env.node != null && env.node.address == address))

  def hasCPU = inventory.items.exists {
    case Some(stack) => Option(Driver.driverFor(stack, inventory.host)) match {
      case Some(driver) => driver.slot(stack) == Slot.CPU
      case _ => false
    }
    case _ => false
  }

  override def xPosition = rack.x + 0.5

  override def yPosition = rack.y + 0.5

  override def zPosition = rack.z + 0.5

  override def world = rack.world

  override def markForSaving() = rack.markForSaving()

  override def markChanged() = rack.markChanged()

  // ----------------------------------------------------------------------- //

  override def onMachineConnect(node: Node) = inventory.onConnect(node)

  override def onMachineDisconnect(node: Node) = inventory.onDisconnect(node)

  def load(nbt: NBTTagCompound) {
    machine.load(nbt.getCompoundTag("machine"))
  }

  def save(nbt: NBTTagCompound) {
    nbt.setNewCompoundTag("machine", machine.save)
    inventory.saveComponents()
    inventory.markDirty()
  }

  // Required due to abstract overrides in component inventory.
  class NetworkedInventory extends ServerInventory with ComponentInventory {
    override def onConnect(node: Node) {
      if (node == this.node) {
        connectComponents()
      }
    }

    override def onDisconnect(node: Node) {
      if (node == this.node) {
        disconnectComponents()
      }
    }

    override def tier = Server.this.tier

    var containerOverride: ItemStack = _

    override def container = if (containerOverride != null) containerOverride else rack.getStackInSlot(number)

    override def node() = machine.node

    override def onMessage(message: Message) {}

    override def host = rack

    // Resolves conflict between ComponentInventory and ServerInventory.
    override def getInventoryStackLimit = 1
  }

}
