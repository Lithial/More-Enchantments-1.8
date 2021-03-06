package com.lithial.me.handlers.controls;


import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.Type;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * Borrowed From Mdiyo to make cloud work
 */
public abstract class TKeyHandler
{
    public KeyBinding[] keyBindings;
    public KeyBinding[] vKeyBindings;
    public boolean[] keyDown;
    public boolean[] repeatings;
    public boolean[] vRepeatings;
    public boolean isDummy;

    /**
     * Pass an array of keybindings and a repeat flag for each one
     *
     * @param keyBindings
     * @param repeatings
     */
    public TKeyHandler(KeyBinding[] keyBindings, boolean[] repeatings, KeyBinding[] vanillaKeys, boolean[] vanillaRepeatings)
    {
        assert keyBindings.length == repeatings.length : "You need to pass two arrays of identical length";
        assert vanillaKeys.length == vanillaRepeatings.length : "You need to pass two arrays of identical length";
        this.keyBindings = keyBindings;
        this.repeatings = repeatings;
        this.vKeyBindings = vanillaKeys;
        this.vRepeatings = vanillaRepeatings;
        this.keyDown = new boolean[keyBindings.length + vanillaKeys.length];
    }

    /**
     * Register the keys into the system. You will do your own keyboard
     * management elsewhere. No events will fire if you use this method
     *
     * @param keyBindings
     */
    public TKeyHandler(KeyBinding[] keyBindings)
    {
        this.keyBindings = keyBindings;
        this.isDummy = true;
    }

    public KeyBinding[] getKeyBindings ()
    {
        return this.keyBindings;
    }

    @SubscribeEvent
    public void onTick (ClientTickEvent event)
    {
        if (event.side == Side.CLIENT)
        {
            if (event.phase == Phase.START)
                keyTick(event.type, false);
            else if (event.phase == Phase.END)
                keyTick(event.type, true);
        }

    }

    public void keyTick (Type type, boolean tickEnd)
    {
        for (int i = 0; i < keyBindings.length; i++)
        {
            KeyBinding keyBinding = keyBindings[i];
            int keyCode = keyBinding.getKeyCode();
            boolean state = (keyCode < 0 ? Mouse.isButtonDown(keyCode + 100) : Keyboard.isKeyDown(keyCode));
            if (state != keyDown[i] || (state && repeatings[i]))
            {
                if (state)
                {
                    keyDown(type, keyBinding, tickEnd, state != keyDown[i]);
                }
                else
                {
                    keyUp(type, keyBinding, tickEnd);
                }
                if (tickEnd)
                {
                    keyDown[i] = state;
                }
            }
        }
        for (int i = 0; i < vKeyBindings.length; i++)
        {
            KeyBinding keyBinding = vKeyBindings[i];
            int keyCode = keyBinding.getKeyCode();
            boolean state = (keyCode < 0 ? Mouse.isButtonDown(keyCode + 100) : Keyboard.isKeyDown(keyCode));
            if (state != keyDown[i + keyBindings.length] || (state && vRepeatings[i]))
            {
                if (state)
                {
                    keyDown(type, keyBinding, tickEnd, state != keyDown[i + keyBindings.length]);
                }
                else
                {
                    keyUp(type, keyBinding, tickEnd);
                }
                if (tickEnd)
                {
                    keyDown[i + keyBindings.length] = state;
                }
            }
        }
    }

    public abstract void keyDown (Type types, KeyBinding kb, boolean tickEnd, boolean isRepeat);

    public abstract void keyUp (Type types, KeyBinding kb, boolean tickEnd);

}