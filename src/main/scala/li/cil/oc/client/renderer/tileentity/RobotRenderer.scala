package li.cil.oc.client.renderer.tileentity

import com.google.common.base.Strings
import li.cil.oc.OpenComputers
import li.cil.oc.Settings
import li.cil.oc.api.event.RobotRenderEvent
import li.cil.oc.client.Textures
import li.cil.oc.common.EventHandler
import li.cil.oc.common.tileentity
import li.cil.oc.util.RenderState
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GLAllocation
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType
import net.minecraft.client.renderer.entity.RendererLivingEntity
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.init.Items
import net.minecraft.item.ItemBlock
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumChatFormatting
import net.minecraft.util.EnumFacing
import net.minecraft.util.Vec3
import net.minecraftforge.client.IItemRenderer.ItemRenderType
import net.minecraftforge.client.MinecraftForgeClient
import net.minecraftforge.common.MinecraftForge
import org.lwjgl.opengl.GL11

object RobotRenderer extends TileEntitySpecialRenderer {
  private val displayList = GLAllocation.generateDisplayLists(2)

  private val mountPoints = Array.fill(7)(new RobotRenderEvent.MountPoint())

  private val gap = 1.0f / 28.0f
  private val gt = 0.5f + gap
  private val gb = 0.5f - gap

  private def normal(v: Vec3) {
    val n = v.normalize()
    GL11.glNormal3f(n.xCoord.toFloat, n.yCoord.toFloat, n.zCoord.toFloat)
  }

  def compileList() {
    val t = Tessellator.getInstance
    val r = t.getWorldRenderer

    val size = 0.4f
    val l = 0.5f - size
    val h = 0.5f + size

    GL11.glNewList(displayList, GL11.GL_COMPILE)

    GL11.glBegin(GL11.GL_TRIANGLE_FAN)
    GL11.glTexCoord2f(0.25f, 0.25f)
    GL11.glVertex3f(0.5f, 1, 0.5f)
    GL11.glTexCoord2f(0, 0.5f)
    GL11.glVertex3f(l, gt, h)
    normal(new Vec3(0, 0.2, 1))
    GL11.glTexCoord2f(0.5f, 0.5f)
    GL11.glVertex3f(h, gt, h)
    normal(new Vec3(1, 0.2, 0))
    GL11.glTexCoord2f(0.5f, 0)
    GL11.glVertex3f(h, gt, l)
    normal(new Vec3(0, 0.2, -1))
    GL11.glTexCoord2f(0, 0)
    GL11.glVertex3f(l, gt, l)
    normal(new Vec3(-1, 0.2, 0))
    GL11.glTexCoord2f(0, 0.5f)
    GL11.glVertex3f(l, gt, h)
    GL11.glEnd()

    r.startDrawingQuads()

    r.setNormal(0, -1, 0)
    r.addVertexWithUV(l, gt, h, 0, 1)
    r.addVertexWithUV(l, gt, l, 0, 0.5)
    r.addVertexWithUV(h, gt, l, 0.5, 0.5)
    r.addVertexWithUV(h, gt, h, 0.5, 1)

    t.draw()

    GL11.glEndList()

    GL11.glNewList(displayList + 1, GL11.GL_COMPILE)

    GL11.glBegin(GL11.GL_TRIANGLE_FAN)
    GL11.glTexCoord2f(0.75f, 0.25f)
    GL11.glVertex3f(0.5f, 0.03f, 0.5f)
    GL11.glTexCoord2f(0.5f, 0)
    GL11.glVertex3f(l, gb, l)
    normal(new Vec3(0, -0.2, 1))
    GL11.glTexCoord2f(1, 0)
    GL11.glVertex3f(h, gb, l)
    normal(new Vec3(1, -0.2, 0))
    GL11.glTexCoord2f(1, 0.5f)
    GL11.glVertex3f(h, gb, h)
    normal(new Vec3(0, -0.2, -1))
    GL11.glTexCoord2f(0.5f, 0.5f)
    GL11.glVertex3f(l, gb, h)
    normal(new Vec3(-1, -0.2, 0))
    GL11.glTexCoord2f(0.5f, 0)
    GL11.glVertex3f(l, gb, l)
    GL11.glEnd()

    r.startDrawingQuads()

    r.setNormal(0, 1, 0)
    r.addVertexWithUV(l, gb, l, 0, 0.5)
    r.addVertexWithUV(l, gb, h, 0, 1)
    r.addVertexWithUV(h, gb, h, 0.5, 1)
    r.addVertexWithUV(h, gb, l, 0.5, 0.5)

    t.draw()

    GL11.glEndList()
  }

  compileList()

  def resetMountPoints(running: Boolean) {
    val offset = if (running) 0 else -0.06f

    // Back.
    mountPoints(0).offset.setX(0)
    mountPoints(0).offset.setY(-0.2f)
    mountPoints(0).offset.setZ(0.24f)
    mountPoints(0).rotation.setX(0)
    mountPoints(0).rotation.setY(1)
    mountPoints(0).rotation.setZ(0)
    mountPoints(0).rotation.setW(180)

    mountPoints(1).offset.setX(0)
    mountPoints(1).offset.setY(0.2f + offset)
    mountPoints(1).offset.setZ(0.24f)
    mountPoints(1).rotation.setX(0)
    mountPoints(1).rotation.setY(1)
    mountPoints(1).rotation.setZ(0)
    mountPoints(1).rotation.setW(180)

    // Front.
    mountPoints(2).offset.setX(0)
    mountPoints(2).offset.setY(-0.2f)
    mountPoints(2).offset.setZ(0.24f)
    mountPoints(2).rotation.setX(0)
    mountPoints(2).rotation.setY(1)
    mountPoints(2).rotation.setZ(0)
    mountPoints(2).rotation.setW(0)

    // Left.
    mountPoints(3).offset.setX(0)
    mountPoints(3).offset.setY(-0.2f)
    mountPoints(3).offset.setZ(0.24f)
    mountPoints(3).rotation.setX(0)
    mountPoints(3).rotation.setY(1)
    mountPoints(3).rotation.setZ(0)
    mountPoints(3).rotation.setW(90)

    mountPoints(4).offset.setX(0)
    mountPoints(4).offset.setY(0.2f + offset)
    mountPoints(4).offset.setZ(0.24f)
    mountPoints(4).rotation.setX(0)
    mountPoints(4).rotation.setY(1)
    mountPoints(4).rotation.setZ(0)
    mountPoints(4).rotation.setW(90)

    // Right.
    mountPoints(5).offset.setX(0)
    mountPoints(5).offset.setY(-0.2f)
    mountPoints(5).offset.setZ(0.24f)
    mountPoints(5).rotation.setX(0)
    mountPoints(5).rotation.setY(1)
    mountPoints(5).rotation.setZ(0)
    mountPoints(5).rotation.setW(-90)

    mountPoints(6).offset.setX(0)
    mountPoints(6).offset.setY(0.2f + offset)
    mountPoints(6).offset.setZ(0.24f)
    mountPoints(6).rotation.setX(0)
    mountPoints(6).rotation.setY(1)
    mountPoints(6).rotation.setZ(0)
    mountPoints(6).rotation.setW(-90)
  }

  def renderChassis(robot: tileentity.Robot = null, offset: Double = 0, isRunningOverride: Boolean = false) {
    val isRunning = if (robot == null) isRunningOverride else robot.isRunning

    val size = 0.3f
    val l = 0.5f - size
    val h = 0.5f + size
    val vStep = 1.0f / 32.0f

    val offsetV = ((offset - offset.toInt) * 16).toInt * vStep
    val (u0, u1, v0, v1) = {
      if (isRunning)
        (0.5f, 1f, 0.5f + offsetV, 0.5f + vStep + offsetV)
      else
        (0.25f - vStep, 0.25f + vStep, 0.75f - vStep, 0.75f + vStep)
    }

    resetMountPoints(robot != null && robot.isRunning)
    val event = new RobotRenderEvent(robot, mountPoints)
    MinecraftForge.EVENT_BUS.post(event)
    if (!event.isCanceled) {
      bindTexture(Textures.Model.Robot)
      if (!isRunning) {
        GL11.glTranslatef(0, -2 * gap, 0)
      }
      GL11.glCallList(displayList + 1)
      if (!isRunning) {
        GL11.glTranslatef(0, -2 * gap, 0)
      }
      GL11.glCallList(displayList)
      GL11.glColor3f(1, 1, 1)

      if (isRunning) {
        if (MinecraftForgeClient.getRenderPass == 0) {
          RenderState.disableEntityLighting()
        }

        {
          // Additive blending for the light.
          RenderState.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
          // Light color.
          val lightColor = if (robot != null && robot.info != null) robot.info.lightColor else 0xF23030
          val r = ((lightColor >>> 16) & 0xFF).toByte
          val g = ((lightColor >>> 8) & 0xFF).toByte
          val b = ((lightColor >>> 0) & 0xFF).toByte
          GL11.glColor3ub(r, g, b)
        }

        val t = Tessellator.getInstance
        val r = t.getWorldRenderer
        r.startDrawingQuads()
        r.addVertexWithUV(l, gt, l, u0, v0)
        r.addVertexWithUV(l, gb, l, u0, v1)
        r.addVertexWithUV(l, gb, h, u1, v1)
        r.addVertexWithUV(l, gt, h, u1, v0)

        r.addVertexWithUV(l, gt, h, u0, v0)
        r.addVertexWithUV(l, gb, h, u0, v1)
        r.addVertexWithUV(h, gb, h, u1, v1)
        r.addVertexWithUV(h, gt, h, u1, v0)

        r.addVertexWithUV(h, gt, h, u0, v0)
        r.addVertexWithUV(h, gb, h, u0, v1)
        r.addVertexWithUV(h, gb, l, u1, v1)
        r.addVertexWithUV(h, gt, l, u1, v0)

        r.addVertexWithUV(h, gt, l, u0, v0)
        r.addVertexWithUV(h, gb, l, u0, v1)
        r.addVertexWithUV(l, gb, l, u1, v1)
        r.addVertexWithUV(l, gt, l, u1, v0)
        t.draw()

        if (MinecraftForgeClient.getRenderPass == 0) {
          RenderState.enableEntityLighting()
        }
        RenderState.color(1, 1, 1, 1)
      }
    }
  }

  override def renderTileEntityAt(tileEntity: TileEntity, x: Double, y: Double, z: Double, f: Float, damage: Int) {
    RenderState.checkError(getClass.getName + ".renderTileEntityAt: entering (aka: wasntme)")

    val proxy = tileEntity.asInstanceOf[tileentity.RobotProxy]
    val robot = proxy.robot
    val worldTime = EventHandler.totalWorldTicks + f

    RenderState.pushMatrix()
    GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5)

    // If the move started while we were rendering and we have a reference to
    // the *old* proxy the robot would be rendered at the wrong position, so we
    // correct for the offset.
    if (robot.proxy != proxy) {
      GL11.glTranslated(robot.proxy.x - proxy.x, robot.proxy.y - proxy.y, robot.proxy.z - proxy.z)
    }

    if (robot.isAnimatingMove) {
      val remaining = (robot.animationTicksLeft - f) / robot.animationTicksTotal.toDouble
      val delta = robot.moveFrom.get.subtract(robot.getPos)
      GL11.glTranslated(delta.getX * remaining, delta.getY * remaining, delta.getZ * remaining)
    }

    val timeJitter = robot.hashCode ^ 0xFF
    val hover =
      if (robot.isRunning) (Math.sin(timeJitter + worldTime / 20.0) * 0.03).toFloat
      else -0.03f
    GL11.glTranslatef(0, hover, 0)

    RenderState.pushMatrix()

    RenderState.enableDepthMask()
    RenderState.enableEntityLighting()
    RenderState.disableBlend()

    if (robot.isAnimatingTurn) {
      val remaining = (robot.animationTicksLeft - f) / robot.animationTicksTotal.toDouble
      GL11.glRotated(90 * remaining, 0, robot.turnAxis, 0)
    }

    robot.yaw match {
      case EnumFacing.WEST => GL11.glRotatef(-90, 0, 1, 0)
      case EnumFacing.NORTH => GL11.glRotatef(180, 0, 1, 0)
      case EnumFacing.EAST => GL11.glRotatef(90, 0, 1, 0)
      case _ => // No yaw.
    }

    GL11.glTranslatef(-0.5f, -0.5f, -0.5f)

    if (MinecraftForgeClient.getRenderPass == 0) {
      val offset = timeJitter + worldTime / 20.0
      renderChassis(robot, offset)
    }

    if (!robot.renderingErrored && x * x + y * y + z * z < 24 * 24) {
      val itemRenderer = Minecraft.getMinecraft.getItemRenderer
      Option(robot.getStackInSlot(0)) match {
        case Some(stack) =>

          RenderState.pushMatrix()
          try {
            // Copy-paste from player render code, with minor adjustments for
            // robot scale.

            RenderState.disableCullFace()
            RenderState.enableRescaleNormal()

            GL11.glScalef(1, -1, -1)
            GL11.glTranslatef(0, -8 * 0.0625F - 0.0078125F, -0.5F)

            if (robot.isAnimatingSwing) {
              val remaining = (robot.animationTicksLeft - f) / robot.animationTicksTotal.toDouble
              GL11.glRotatef((Math.sin(remaining * Math.PI) * 45).toFloat, 1, 0, 0)
            }

            val item = stack.getItem
            val minecraft = Minecraft.getMinecraft

            if (item.isInstanceOf[ItemBlock] && minecraft.getBlockRendererDispatcher.isRenderTypeChest(Block.getBlockFromItem(item), stack.getMetadata)) {
              GlStateManager.translate(0.0F, 0.1875F, -0.3125F)
              GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F)
              GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F)
              val scale = 0.375F
              GlStateManager.scale(scale, -scale, scale)
            }
            else if (item eq Items.bow) {
              GlStateManager.translate(-0.1F, -0.125F, -0.1f)
              val scale = 0.625F
              GlStateManager.scale(scale, -scale, scale)
              GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F)
              GlStateManager.rotate(10.0F, 0.0F, 1.0F, 0.0F)
            }
            else if (item.isFull3D) {
              if (item.shouldRotateAroundWhenRendering) {
                GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F)
                GlStateManager.translate(0.0F, -0.125F, 0.0F)
              }
              GlStateManager.translate(0.0F, 0.1F, 0.0F)
              val scale = 0.625F
              GlStateManager.scale(scale, -scale, scale)
              GlStateManager.rotate(-2.0F, 0.0F, 1.0F, 0.0F)
              GlStateManager.rotate(-5.0F, 0.0F, 0.0F, 1.0F)
            }

            itemRenderer.renderItem(Minecraft.getMinecraft.thePlayer, stack, TransformType.THIRD_PERSON)
          }
          catch {
            case e: Throwable =>
              OpenComputers.log.warn("Failed rendering equipped item.", e)
              robot.renderingErrored = true
          }
          RenderState.enableCullFace()
          RenderState.disableRescaleNormal()
          RenderState.popMatrix()
        case _ =>
      }

      val stacks = (robot.componentSlots ++ robot.containerSlots).map(robot.getStackInSlot).filter(stack => stack != null && MinecraftForgeClient.getItemRenderer(stack, ItemRenderType.EQUIPPED) != null).padTo(mountPoints.length, null).take(mountPoints.length)
      for ((stack, mountPoint) <- stacks.zip(mountPoints)) {
        try {
          if (stack != null /* && (stack.getItem.requiresMultipleRenderPasses() || MinecraftForgeClient.getRenderPass == 0) TODO remove? */ ) {
            val tint = stack.getItem.getColorFromItemStack(stack, MinecraftForgeClient.getRenderPass)
            val r = ((tint >> 16) & 0xFF) / 255f
            val g = ((tint >> 8) & 0xFF) / 255f
            val b = ((tint >> 0) & 0xFF) / 255f
            RenderState.color(r, g, b, 1)
            RenderState.pushMatrix()
            GL11.glTranslatef(0.5f, 0.5f, 0.5f)
            GL11.glRotatef(mountPoint.rotation.getW, mountPoint.rotation.getX, mountPoint.rotation.getY, mountPoint.rotation.getZ)
            GL11.glTranslatef(mountPoint.offset.getX, mountPoint.offset.getY, mountPoint.offset.getZ)
            itemRenderer.renderItem(Minecraft.getMinecraft.thePlayer, stack, TransformType.NONE)
            RenderState.popMatrix()
          }
        }
        catch {
          case e: Throwable =>
            OpenComputers.log.warn("Failed rendering equipped upgrade.", e)
            robot.renderingErrored = true
        }
      }
    }
    RenderState.popMatrix()

    val name = robot.name
    if (Settings.get.robotLabels && !Strings.isNullOrEmpty(name) && x * x + y * y + z * z < RendererLivingEntity.NAME_TAG_RANGE) {
      RenderState.pushMatrix()

      // This is pretty much copy-pasta from the entity's label renderer.
      val t = Tessellator.getInstance
      val r = t.getWorldRenderer
      val f = getFontRenderer
      val scale = 1.6f / 60f
      val width = f.getStringWidth(name)
      val halfWidth = width / 2

      GL11.glTranslated(0, 0.8, 0)
      GL11.glNormal3f(0, 1, 0)
      GL11.glColor3f(1, 1, 1)

      GL11.glRotatef(-rendererDispatcher.entityYaw, 0, 1, 0)
      GL11.glRotatef(rendererDispatcher.entityPitch, 1, 0, 0)
      GL11.glScalef(-scale, -scale, scale)

      RenderState.makeItBlend()
      RenderState.disableDepthMask()
      RenderState.disableLighting()
      GL11.glDisable(GL11.GL_TEXTURE_2D)

      r.startDrawingQuads()
      r.setColorRGBA_F(0, 0, 0, 0.25f)
      r.addVertex(-halfWidth - 1, -1, 0)
      r.addVertex(-halfWidth - 1, 8, 0)
      r.addVertex(halfWidth + 1, 8, 0)
      r.addVertex(halfWidth + 1, -1, 0)
      t.draw

      GL11.glEnable(GL11.GL_TEXTURE_2D) // For the font.
      f.drawString((if (EventHandler.isItTime) EnumChatFormatting.OBFUSCATED.toString else "") + name, -halfWidth, 0, 0xFFFFFFFF)

      RenderState.enableDepthMask()
      RenderState.enableLighting()
      RenderState.disableBlend()

      RenderState.popMatrix()
    }

    RenderState.popMatrix()

    RenderState.checkError(getClass.getName + ".renderTileEntityAt: leaving")
  }
}
