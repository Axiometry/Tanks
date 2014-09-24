package me.axiometry.tanks.util;

import javax.swing.SwingUtilities;

public class Util {
	private Util() {
	}

	public static void awtInvokeAndWait(Runnable runnable) {
		try {
			SwingUtilities.invokeAndWait(runnable);
		} catch(Exception exception) {}
	}

	public static void awtInvokeLater(Runnable runnable) {
		try {
			SwingUtilities.invokeLater(runnable);
		} catch(Exception exception) {}
	}
}
