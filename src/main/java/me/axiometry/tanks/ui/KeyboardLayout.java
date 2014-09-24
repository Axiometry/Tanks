package me.axiometry.tanks.ui;

public final class KeyboardLayout {
	private final int keyLeft, keyRight, keyUp, keyDown, keyLeftBarrel,
			keyRightBarrel, keyFire, keyDropLandmine;

	public KeyboardLayout(Builder builder) {
		keyLeft = builder.keyLeft;
		keyRight = builder.keyRight;
		keyUp = builder.keyUp;
		keyDown = builder.keyDown;
		keyLeftBarrel = builder.keyLeftBarrel;
		keyRightBarrel = builder.keyRightBarrel;
		keyFire = builder.keyFire;
		keyDropLandmine = builder.keyDropLandmine;
	}

	public int getKeyLeft() {
		return keyLeft;
	}

	public int getKeyRight() {
		return keyRight;
	}

	public int getKeyUp() {
		return keyUp;
	}

	public int getKeyDown() {
		return keyDown;
	}

	public int getKeyLeftBarrel() {
		return keyLeftBarrel;
	}

	public int getKeyRightBarrel() {
		return keyRightBarrel;
	}

	public int getKeyFire() {
		return keyFire;
	}

	public int getKeyDropLandmine() {
		return keyDropLandmine;
	}

	public static final class Builder {
		private int keyLeft, keyRight, keyUp, keyDown, keyLeftBarrel,
				keyRightBarrel, keyFire, keyDropLandmine;

		public void setKeyLeft(int keyLeft) {
			this.keyLeft = keyLeft;
		}

		public void setKeyRight(int keyRight) {
			this.keyRight = keyRight;
		}

		public void setKeyUp(int keyUp) {
			this.keyUp = keyUp;
		}

		public void setKeyDown(int keyDown) {
			this.keyDown = keyDown;
		}

		public void setKeyLeftBarrel(int keyLeftBarrel) {
			this.keyLeftBarrel = keyLeftBarrel;
		}

		public void setKeyRightBarrel(int keyRightBarrel) {
			this.keyRightBarrel = keyRightBarrel;
		}

		public void setKeyFire(int keyFire) {
			this.keyFire = keyFire;
		}

		public void setKeyDropLandmine(int keyDropLandmine) {
			this.keyDropLandmine = keyDropLandmine;
		}
	}
}
