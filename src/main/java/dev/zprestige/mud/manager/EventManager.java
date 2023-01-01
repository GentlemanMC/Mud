package dev.zprestige.mud.manager;

import dev.zprestige.mud.Mud;
import dev.zprestige.mud.events.impl.player.CameraSetupEvent;
import dev.zprestige.mud.events.impl.render.*;
import dev.zprestige.mud.events.impl.system.ConnectEvent;
import dev.zprestige.mud.events.impl.system.DisconnectEvent;
import dev.zprestige.mud.util.MC;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class EventManager implements MC {
    public static long lastFrame, deltaTime;

    public EventManager() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (!nullCheck()) {
            mc.profiler.startSection("mud");
            Render3DPreEvent render3dPreEvent = new Render3DPreEvent(event.getPartialTicks());
            Mud.eventBus.invoke(render3dPreEvent);

            Render3DEvent render3dEvent = new Render3DEvent(event.getPartialTicks());
            Mud.eventBus.invoke(render3dEvent);

            deltaTime = System.currentTimeMillis() - lastFrame;
            lastFrame = System.currentTimeMillis();

            mc.profiler.endSection();
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlayEvent(final RenderGameOverlayEvent.Text event) {
        if (!nullCheck()) {
            final Render2DEvent render2DEvent = new Render2DEvent(event.getPartialTicks(), event.getResolution());
            Mud.eventBus.invoke(render2DEvent);

            final Render2DPostEvent render2DPostEvent = new Render2DPostEvent(event.getPartialTicks(), event.getResolution());
            Mud.eventBus.invoke(render2DPostEvent);
        }
    }

    @SubscribeEvent
    public void onClientConnect(FMLNetworkEvent.ClientConnectedToServerEvent ignoredEvent) {
        ConnectEvent event = new ConnectEvent();
        Mud.eventBus.invoke(event);
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent ignoredEvent) {
        DisconnectEvent event = new DisconnectEvent();
        Mud.eventBus.invoke(event);
    }

    @SubscribeEvent
    public void onRenderGameOverlay(final RenderGameOverlayEvent event) {
        if (!nullCheck()) {
            RenderOverlayEvent renderOverlayEvent = new RenderOverlayEvent(event.getType(), event.getResolution());
            Mud.eventBus.invoke(renderOverlayEvent);
            if (renderOverlayEvent.isCancelled()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onRenderBlockOverlay(RenderBlockOverlayEvent event) {
        if (!nullCheck()) {
            BlockOverlayEvent blockOverlayEvent = new BlockOverlayEvent();
            Mud.eventBus.invoke(blockOverlayEvent);
            if (blockOverlayEvent.isCancelled()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onCameraSetup(final EntityViewRenderEvent.CameraSetup event) {
        CameraSetupEvent cameraSetupEvent = new CameraSetupEvent(event.getYaw(), event.getPitch());
        Mud.eventBus.invoke(cameraSetupEvent);
        event.setYaw(cameraSetupEvent.getYaw());
        event.setPitch(cameraSetupEvent.getPitch());
    }

    @SubscribeEvent
    public void onInputUpdate(InputUpdateEvent event) {
        dev.zprestige.mud.events.impl.player.InputUpdateEvent itemInputUpdateEvent = new dev.zprestige.mud.events.impl.player.InputUpdateEvent(event.getMovementInput());
        Mud.eventBus.invoke(itemInputUpdateEvent);
    }
    public static boolean nullCheck() {
        return mc.player == null || mc.world == null;
    }

    public static float getDeltaTime() {
        return deltaTime * 0.01f;
    }
}
