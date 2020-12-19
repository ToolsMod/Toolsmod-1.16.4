package de.whiletrue.toolsmod.mod;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.whiletrue.toolsmod.gui.inject.GuiInjectionHandler;
import de.whiletrue.toolsmod.gui.modules.GuiQAModules;
import de.whiletrue.toolsmod.module.defined.Module;
import de.whiletrue.toolsmod.network.Networkmanager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Events {

	// Reference to the game
	private Minecraft mc = Minecraft.getInstance();

	// Reference to the mod
	private Toolsmod mod = Toolsmod.getInstance();

	@SubscribeEvent
	void onChat(ClientChatEvent evt) {
		//Gets the message reference
		String msg = evt.getMessage();
		
		// Checks if the message is a command
		if (msg.charAt(0) == Toolsmod.COMMAND_INDICATOR) {
			
			//Checks if the message is special (Should send with the command indicator)
			if(msg.length()>1 && msg.charAt(1) == Toolsmod.COMMAND_INDICATOR) {
				//Updates the message
				evt.setMessage(evt.getMessage().substring(1));
				return;
			}
			
			// Executes the command
			this.mod.getCommandManager().execute(evt.getMessage());
			// Cancels the event
			evt.setCanceled(true);
			// Adds the send command to the history
			this.mc.ingameGUI.getChatGUI().addToSentMessages(evt.getMessage());
		}
	}

	@SubscribeEvent
	void onLogout(ClientPlayerNetworkEvent.LoggedOutEvent evt) {
		this.mod.getModuleManager().executeEvent(i -> i.onDisconnect(evt));
	}

	@SubscribeEvent
	void onLogin(ClientPlayerNetworkEvent.LoggedInEvent evt) {
		// Intercepts the traffic between the client/server
		Networkmanager.getInstance().handleLogin();
	}

	@SubscribeEvent
	void onWorldLoad(WorldEvent.Load evt) {
		this.mod.getModuleManager().executeEvent(i -> i.onWorldLoad(evt));
	}

	@SubscribeEvent
	void onPlace(EntityPlaceEvent evt) {
		// Checks if the entity is the player
		if (!evt.getEntity().equals(this.mc.player))
			return;

		// Executes the place event for every module
		this.mod.getModuleManager().executeEvent(i -> i.onPlace(evt));
	}

	@SubscribeEvent
	void onRender2D(RenderGameOverlayEvent.Pre evt) {		
		if (this.mc.world == null)
			return;

		// Checks if the tablist is rendered
		boolean tablist = this.mc.gameSettings.keyBindPlayerList.isKeyDown();

		GL11.glPushMatrix();
		render:{
			// Executes the event on every module
			this.mod.getModuleManager().executeEvent(i -> i.onRender2D(tablist, evt));
			
			// Checks if the tab-list or the GUI is rendered
			if (tablist || Minecraft.getInstance().gameSettings.showDebugInfo)
				break render;
			
			// Executes the render event for the module stat's
			this.mod.getModuleManager().handleRender(evt.getMatrixStack());
			
			//Reset
			RenderSystem.enableBlend();
			Minecraft.getInstance().getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
		}
		GL11.glPopMatrix();

	}

	@SubscribeEvent
	void onKey(KeyInputEvent evt) {
		if (this.mc.world == null || this.mc.currentScreen != null || evt.getAction() != GLFW.GLFW_PRESS)
			return;

		//Checks if the quick access gui should open
		if (ModSettings.quickAccessKeybind.value.isKeycodeMatching(evt.getKey())) {
			GuiQAModules.MODULES_GUIS.open();
			return;
		}

		// Executes the event on every module
		this.mod.getModuleManager().executeEvent(i -> i.onKeyToggled(evt));

		// Checks if the event got canceled
		if (evt.isCanceled())
			return;

		// Gets every module
		this.mod.getModuleManager().getModules().stream()
				// Checks if the bind matches and if the mod is allowed
				.filter(i -> i.isAllowed() && i.getKeyBind() == evt.getKey())
				// Toggles it
				.forEach(Module::toggle);
	}

	@SubscribeEvent
	void onTick(TickEvent.ClientTickEvent evt) {
		if (this.mc.world != null)
			// Executes the event on every activated module
			this.mod.getModuleManager().executeEvent(i -> i.onTick(evt));
	}

	@SubscribeEvent
	void onRender3D(RenderWorldLastEvent evt) {

		// Prepares the rendering
		final GameRenderer gameRenderer = this.mc.gameRenderer;
		final ActiveRenderInfo activeRenderInfo = gameRenderer.getActiveRenderInfo();
		final float partialTicks = this.mc.getRenderPartialTicks();

		MatrixStack stack = new MatrixStack();
		stack.getLast().getMatrix().mul(gameRenderer.getProjectionMatrix(activeRenderInfo, partialTicks, true));

		GL11.glPushMatrix();
		{

			// Prepares the graphics
			RenderSystem.multMatrix(evt.getMatrixStack().getLast().getMatrix());
			RenderSystem.disableTexture();
			RenderSystem.enableBlend();
			RenderSystem.disableAlphaTest();
			RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
			RenderSystem.shadeModel(GL11.GL_SMOOTH);
			RenderSystem.disableDepthTest();
			RenderSystem.lineWidth(1.f);

			// Executes the event on every active module
			this.mod.getModuleManager().executeEvent(i -> i.onRender3D(evt));

			// Resets the graphics
			RenderSystem.lineWidth(1.f);
			RenderSystem.shadeModel(GL11.GL_FLAT);
			RenderSystem.disableBlend();
			RenderSystem.enableAlphaTest();
			RenderSystem.enableTexture();
			RenderSystem.enableDepthTest();
			RenderSystem.enableCull();
		}

		GL11.glPopMatrix();
	}

	@SubscribeEvent
	void onMouse(MouseScrollEvent evt) {
		if (this.mc.world != null)
			// Executes the event on every active module
			this.mod.getModuleManager().executeEvent(i -> i.onScrollMouse(evt));
	}
	
	
	// =================
	// ==Gui Injection==
	// =================
	
	@SubscribeEvent
	void onGuiInitPre(GuiScreenEvent.InitGuiEvent.Pre evt) {
		//Executes the pre init injection
		GuiInjectionHandler.handlePreInit(evt);
	}
	
	@SubscribeEvent
	void onGuiInitPost(GuiScreenEvent.InitGuiEvent.Post evt) {
		//Executes the post init injection
		GuiInjectionHandler.handlePostInit(evt);
	}
}
