package me.axiometry.tanks.rendering.components;

import me.axiometry.tanks.rendering.AbstractScreenComponent;
import me.axiometry.tanks.util.Constants;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.IOException;

public class TextField extends AbstractScreenComponent {
	private volatile String text = "";
	private volatile int blinkTimer = 0;
	private volatile int caretPosition = 0;
	private volatile boolean focused = false;

	public TextField(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	@Override
	public void update() {
		if(++blinkTimer > 30)
			blinkTimer = 0;
	}

	@Override
	public void render(Graphics2D graphics) {
		graphics.setColor(Color.GRAY);
		graphics.fillRect(area.x, area.y, area.width, area.height);
		graphics.setColor(Color.BLACK);
		graphics.fillRect(area.x + 2, area.y + 2, area.width - 4,
				area.height - 4);
		graphics.setFont(Constants.NEUROPOL_X.deriveFont(25F));
		FontMetrics metrics = graphics.getFontMetrics();
		graphics.setColor(Color.GRAY);
		graphics.drawString(text, area.x + 10, area.y
				+ ((area.height / 2) + (metrics.getHeight() / 2)));
		if(focused && blinkTimer - 15 < 0) {
			graphics.fillRect(
					area.x
							+ 12
							+ metrics.stringWidth(text.substring(0,
									caretPosition)) - 2, area.y
							+ ((area.height / 2) - (metrics.getHeight() / 2)),
					1, metrics.getHeight());
		}
	}

	@Override
	protected void onKeyTyped(char key, int keyCode, int modifiers) {
		if(!focused)
			return;
		if(modifiers == 0 || modifiers == KeyEvent.SHIFT_MASK) {
			if(key == KeyEvent.VK_BACK_SPACE) {
				if(text.length() > 0 && caretPosition > 0) {
					text = text.substring(0, caretPosition - 1)
							+ text.substring(caretPosition);
					caretPosition--;
					blinkTimer = 0;
				}
			} else {
				text = text.substring(0, caretPosition) + key
						+ text.substring(caretPosition);
				caretPosition++;
				blinkTimer = 0;
			}
		}
	}

	private String getClipboard() {
		Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard()
				.getContents(null);

		try {
			if(t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				String text = (String) t
						.getTransferData(DataFlavor.stringFlavor);
				return text;
			}
		} catch(UnsupportedFlavorException e) {} catch(IOException e) {}
		return null;
	}

	@Override
	protected void onKeyPressed(char key, int keyCode, int modifiers) {
		if(!focused)
			return;
		if(modifiers == KeyEvent.CTRL_MASK && keyCode == KeyEvent.VK_V) {
			String clipboard = getClipboard();
			if(clipboard == null)
				return;
			text = text.substring(0, caretPosition) + clipboard
					+ text.substring(caretPosition);
			caretPosition += clipboard.length();
			return;
		}
		if(modifiers == 0) {
			if(keyCode == KeyEvent.VK_LEFT) {
				if(caretPosition > 0) {
					caretPosition--;
					blinkTimer = 0;
				}
			} else if(keyCode == KeyEvent.VK_RIGHT) {
				if(caretPosition < text.length()) {
					caretPosition++;
					blinkTimer = 0;
				}
			}
		}
	}

	@Override
	public void onInputEvent(InputEvent event) {
		super.onInputEvent(event);
		if(event instanceof MouseEvent
				&& ((MouseEvent) event).getID() == MouseEvent.MOUSE_PRESSED) {
			int x = ((MouseEvent) event).getX();
			int y = ((MouseEvent) event).getY();
			if(!area.contains(x, y))
				onMousePress(x, y, ((MouseEvent) event).getButton());
		}
	}

	@Override
	protected void onMousePress(int x, int y, int button) {
		if(area.contains(x, y) && button == 1) {
			focused = true;
			blinkTimer = 0;
		} else if(!area.contains(x, y)) {
			focused = false;
			blinkTimer = 0;
		}
	}

	public String getText() {
		return text;
	}

	public boolean isFocused() {
		return focused;
	}

	public void setFocused(boolean focused) {
		this.focused = focused;
	}
}
