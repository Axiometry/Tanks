package me.axiometry.tanks.rendering;

import java.awt.event.*;

public interface InputManager {
	public MouseEvent nextMouseEvent();

	public KeyEvent nextKeyEvent();

	public void destroy();
}
