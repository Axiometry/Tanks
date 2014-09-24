package me.axiometry.tanks.rendering;

import java.awt.Canvas;
import java.awt.event.*;
import java.util.*;

import me.axiometry.tanks.util.Util;

public class CanvasInputManager implements InputManager, MouseListener,
		MouseMotionListener, MouseWheelListener, KeyListener {
	private final CanvasRenderer renderer;

	private Deque<MouseEvent> mouseQueue;
	private Deque<KeyEvent> keyQueue;

	CanvasInputManager(CanvasRenderer renderer) {
		this.renderer = renderer;
		mouseQueue = new ArrayDeque<MouseEvent>();
		keyQueue = new ArrayDeque<KeyEvent>();
		registerListeners();
	}

	private void registerListeners() {
		Util.awtInvokeAndWait(new Runnable() {
			@Override
			public void run() {
				try {
					Canvas canvas = renderer.getCanvas();
					canvas.addMouseListener(CanvasInputManager.this);
					canvas.addMouseMotionListener(CanvasInputManager.this);
					canvas.addMouseWheelListener(CanvasInputManager.this);
					canvas.addKeyListener(CanvasInputManager.this);
				} catch(Exception exception) {
					exception.printStackTrace();
				}
			}
		});
	}

	public MouseEvent nextMouseEvent() {
		synchronized(mouseQueue) {
			if(mouseQueue.size() > 0)
				return mouseQueue.remove();
			return null;
		}
	}

	public KeyEvent nextKeyEvent() {
		synchronized(keyQueue) {
			if(keyQueue.size() > 0)
				return keyQueue.remove();
			return null;
		}
	}

	public void destroy() {
		Util.awtInvokeAndWait(new Runnable() {
			@Override
			public void run() {
				Canvas canvas = renderer.getCanvas();
				canvas.removeMouseListener(CanvasInputManager.this);
				canvas.removeMouseMotionListener(CanvasInputManager.this);
				canvas.removeMouseWheelListener(CanvasInputManager.this);
				canvas.removeKeyListener(CanvasInputManager.this);
			}
		});
		synchronized(mouseQueue) {
			mouseQueue = null;
		}
		synchronized(keyQueue) {
			keyQueue = null;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		appendMouseEvent(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		appendMouseEvent(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		appendMouseEvent(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		appendMouseEvent(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		appendMouseEvent(e);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		appendMouseEvent(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		appendMouseEvent(e);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		appendMouseEvent(e);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		appendKeyEvent(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		appendKeyEvent(e);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		appendKeyEvent(e);
	}

	private void appendMouseEvent(MouseEvent event) {
		synchronized(mouseQueue) {
			mouseQueue.add(event);
		}
	}

	private void appendKeyEvent(KeyEvent event) {
		synchronized(keyQueue) {
			keyQueue.add(event);
		}
	}
}
