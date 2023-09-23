/*
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 * 
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 * 
 * For more information, please refer to <http://unlicense.org/>
*/

package org.gwiz.wurmunlimited.mods;

import java.io.PrintWriter;
import java.util.Set;
import java.io.BufferedWriter;
import java.io.FileWriter;
import javassist.CtClass;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.Versioned;
import org.gotti.wurmunlimited.modloader.interfaces.WurmClientMod;
import org.gotti.wurmunlimited.modsupport.console.ConsoleListener;
import org.gotti.wurmunlimited.modsupport.console.ModConsole;
import org.gotti.wurmunlimited.modloader.callbacks.CallbackApi;
import com.wurmonline.client.console.ActionClass;
import com.wurmonline.client.options.Options;
import com.wurmonline.shared.util.MovementChecker;

public class Steer2Camera implements WurmClientMod, Initable, ConsoleListener, Versioned {

	private static final String version = "1.0";
	private byte lastTickMod = 0;
	private float accuracyMargin = 7.5f;
	private boolean s2cActive = true;

	@CallbackApi
	public Set<ActionClass> getAdjustedKeys(Set<ActionClass> keys, boolean autoRun, float xRotUsed,
			float xCarrierRotUsed) {
		if (s2cActive) {
			if (lastTickMod != 0) {
				if (lastTickMod == 1)
					keys.remove(ActionClass.MOVE_RIGHT);
				if (lastTickMod == 2)
					keys.remove(ActionClass.MOVE_LEFT);
				lastTickMod = 0;
				return keys;
			}
			if (keys.contains(ActionClass.MOVE_FORWARD) || keys.contains(ActionClass.MOVE_BACK))
				autoRun = false;
			boolean f = keys.contains(ActionClass.MOVE_FORWARD) || autoRun;
			boolean b = keys.contains(ActionClass.MOVE_BACK);
			boolean l = keys.contains(ActionClass.MOVE_LEFT)
					|| (keys.contains(ActionClass.STRAFE) && keys.contains(ActionClass.TURN_LEFT));
			boolean r = keys.contains(ActionClass.MOVE_RIGHT)
					|| (keys.contains(ActionClass.STRAFE) && keys.contains(ActionClass.TURN_RIGHT));
			byte bitmask = MovementChecker.buildBitmap(f, b, l, r);
			if ((bitmask == 1) || (bitmask == 2)) {
				if (Options.mountRotation.value()) {
					Options.mountRotation.set(!Options.mountRotation.value());
					System.out.println("[Steer2Camera] \"Rotate Player w/ Mount\" option disabled.");
				}
				xRotUsed = normalizeAngle(-xRotUsed + 180.0f);
				float diff = normalizeAngle(xCarrierRotUsed - xRotUsed);
				if (diff < 180.0f) {
					if (diff > accuracyMargin) {
						lastTickMod = 1;
						keys.add(ActionClass.MOVE_RIGHT);
					}
				} else {
					if (diff < (360.0f - accuracyMargin)) {
						lastTickMod = 2;
						keys.add(ActionClass.MOVE_LEFT);
					}
				}
			}
		}
		return keys;
	}

	@Override
	public void init() {
		try {
			CtClass ctPlayerObj = HookManager.getInstance().getClassPool()
					.getCtClass("com.wurmonline.client.game.PlayerObj");
			HookManager.getInstance().addCallback(ctPlayerObj, "steer2camera", this);
			ctPlayerObj.getDeclaredMethod("gametick")
					.insertBefore("if (this.carrierCreature != null) this.keys = this.steer2camera.getAdjustedKeys("
							+ "this.keys, this.autoRun, this.xRotUsed, this.xCarrierRotUsed);\n");
		} catch (NotFoundException | CannotCompileException e) {
			appendToFile(e);
			throw new HookException(e);
		}
		ModConsole.addConsoleListener(this);
	}

	// For anyone modding the client, this is seriously useful. It will write in
	// exception.txt if the code can't be injected in the client before it launches.
	public static void appendToFile(Exception e) {
		try {
			FileWriter fstream = new FileWriter("exception.txt", true);
			BufferedWriter out = new BufferedWriter(fstream);
			PrintWriter pWriter = new PrintWriter(out, true);
			e.printStackTrace(pWriter);
		} catch (Exception ie) {
			throw new RuntimeException("Could not write Exception to file", ie);
		}
	}

	public static float normalizeAngle(float angle) {
		angle -= (int) (angle / 360.0f) * 360;
		if (angle < 0.0f) {
			angle += 360.0f;
		}
		return angle;
	}

	@Override
	public boolean handleInput(String string, Boolean aBoolean) {
		if (string == null)
			return false;
		String[] args = string.split("\\s+");
		if (!args[0].equals("s2c"))
			return false;
		if (args.length > 1) {
			String command = args[1];
			switch (command) {
			case "on":
				s2cActive = true;
				System.out.println("]Steer2Camera] Enabled");
				return true;
			case "off":
				s2cActive = false;
				System.out.println("[Steer2Camera] Disabled");
				return true;
			case "toggle":
				s2cActive = !s2cActive;
				System.out.printf("[Steer2Camera] %s%n", s2cActive ? "Enabled" : "Disabled");
				return true;
			}
		}
		System.out.println("[Steer2Camera] Valid commands are: on, off, toggle");
		return true;
	}

	@Override
	public String getVersion() {
		return version;
	}
}
