package me.axiometry.tanks.rendering.ui;

import me.axiometry.tanks.Tanks;
import me.axiometry.tanks.entity.*;
import me.axiometry.tanks.rendering.Screen;
import me.axiometry.tanks.ui.KeyboardLayout;
import me.axiometry.tanks.util.Constants;
import me.axiometry.tanks.world.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class GameScreen implements Screen {
	private final Tanks game = Tanks.INSTANCE;

	private long lastUpdateTime, updateTime;

	@Override
	public void update() {
		lastUpdateTime = updateTime;
		updateTime = System.currentTimeMillis();
		if(lastUpdateTime == 0)
			lastUpdateTime = updateTime;
		World world = game.getWorld();
		if(world != null)
			world.update();
	}

	@Override
	public void onInputEvent(InputEvent inputEvent) {
		World world = game.getWorld();
		if(world == null || !world.isInitialized())
			return;
		for(Entity entity : world.getEntities())
			entity.onInputEvent(inputEvent);
	}

	@Override
	public void render(Graphics2D graphics, int width, int height) {
		long timeElapsed = System.currentTimeMillis() - updateTime;
		long lastTimeElapsed = updateTime - lastUpdateTime;
		double interpolationFactor = (double) timeElapsed
				/ (double) lastTimeElapsed;

		World world = game.getWorld();
		Entity[] entities = world.getEntities();
		for(Entity entity : entities)
			entity.prerender();
		if(world != null && world.isInitialized()) {
			int tileSize;
			Tile[][] tiles = world.getTiles();
			if(tiles.length > 0 && tiles[0].length > 0)
				tileSize = tiles[0][0].getSprite().getImage().getWidth(null);
			else
				tileSize = 0;
			List<CentralEntity> centralEntities = new ArrayList<CentralEntity>();
			for(Entity entity : entities)
				if(entity instanceof CentralEntity)
					centralEntities.add((CentralEntity) entity);
			int screens = centralEntities.size();
			if(screens == 0)
				return;
			double sqrt = Math.sqrt(screens);
			int columns = (int) Math.floor(sqrt);
			if(columns == 1 && columns != sqrt)
				columns += 1;
			int extendedBottomScreens = centralEntities.size() % columns;
			int rows = centralEntities.size() / columns
					+ (extendedBottomScreens > 0 ? 1 : 0);
			int regularColumnWidth = columns > 0 ? width / columns : 0;
			int extendedColumnWidth = extendedBottomScreens > 0 ? width
					/ extendedBottomScreens : 0;
			int rowHeight = rows > 0 ? height / rows : 0;
			int screenX = 0;
			int screenY = 0;
			for(int i = 0; i < centralEntities.size(); i++) {
				CentralEntity centralEntity = centralEntities.get(i);
				int columnWidth;
				int columnsInRow;
				if(i < screens - 1 || extendedBottomScreens == 0) {
					columnWidth = regularColumnWidth;
					columnsInRow = columns;
				} else {
					columnWidth = extendedColumnWidth;
					columnsInRow = extendedBottomScreens;
				}

				BufferedImage image = new BufferedImage(columnWidth, rowHeight,
						BufferedImage.TYPE_INT_ARGB);
				Graphics2D imageGraphics = image.createGraphics();
				Image spriteImage = centralEntity.getSprite().getImage();
				int spriteWidth = spriteImage.getWidth(null);
				int spriteHeight = spriteImage.getHeight(null);
				double centralEntityX = interpolate(centralEntity.getX(),
						centralEntity.getLastX(), interpolationFactor);
				double centralEntityY = interpolate(centralEntity.getY(),
						centralEntity.getLastY(), interpolationFactor);
				int centralEntityScreenX = (int) (centralEntityX * tileSize);
				int centralEntityScreenY = (int) (centralEntityY * tileSize);
				int minTileX = centralEntityScreenX - columnWidth / 2;
				int minTileY = centralEntityScreenY - rowHeight / 2;
				int maxTileX = minTileX + columnWidth;
				int maxTileY = minTileY + rowHeight;
				for(int x = 0; x < tiles.length; x++) {
					int tileScreenX = x * tileSize;
					if(tileScreenX + tileSize < minTileX
							|| tileScreenX > maxTileX)
						continue;
					for(int y = 0; y < tiles[x].length; y++) {
						int tileScreenY = y * tileSize;
						if(tileScreenY + tileSize < minTileY
								|| tileScreenY > maxTileY)
							continue;
						int dx = tileScreenX + columnWidth / 2
								- centralEntityScreenX;
						int dx2 = dx + tileSize;
						int sx = 0;
						int sx2 = tileSize;
						if(dx < 0) {
							if(dx2 < 0)
								continue;
							// sx += -dx;
							// dx = 0;
						}
						if(dx2 > columnWidth) {
							if(dx > columnWidth)
								continue;
							// sx2 += dx2 - columnWidth;
							// dx2 = columnWidth;
						}
						int dy = tileScreenY + rowHeight / 2
								- centralEntityScreenY;
						int dy2 = dy + tileSize;
						int sy = 0;
						int sy2 = tileSize;
						if(dy < 0) {
							if(dy2 < 0)
								continue;
							// sy += -dy;
							// dy = 0;
						}
						if(dy2 > rowHeight) {
							if(dy > rowHeight)
								continue;
							// sy2 += dy2 - rowHeight;
							// dy2 = rowHeight;
						}
						imageGraphics.drawImage(tiles[x][y].getSprite()
								.getImage(), dx, dy, dx2, dy2, sx, sy, sx2,
								sy2, Color.RED, null);
					}
				}
				for(Entity entity : entities) {
					if(entity.equals(centralEntity))
						continue;
					double entityX = interpolate(entity.getX(),
							entity.getLastX(), interpolationFactor);
					double entityY = interpolate(entity.getY(),
							entity.getLastY(), interpolationFactor);
					int entityScreenX = (int) (entityX * tileSize);
					int entityScreenY = (int) (entityY * tileSize);
					Image entitySpriteImage = entity.getSprite().getImage();
					int entitySpriteWidth = entitySpriteImage.getWidth(null);
					int entitySpriteHeight = entitySpriteImage.getHeight(null);
					BufferedImage entitySpriteImageRotated = new BufferedImage(
							entitySpriteWidth * 2, entitySpriteHeight * 2,
							BufferedImage.TYPE_INT_ARGB);
					Graphics2D entitySpriteGraphics = entitySpriteImageRotated
							.createGraphics();

					AffineTransform transform = new AffineTransform();
					transform.setToIdentity();
					transform.rotate(Math.toRadians(entity.getRotation()),
							entitySpriteWidth, entitySpriteHeight);
					entitySpriteGraphics.setRenderingHint(
							RenderingHints.KEY_INTERPOLATION,
							RenderingHints.VALUE_INTERPOLATION_BILINEAR);
					entitySpriteGraphics.setRenderingHint(
							RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
					entitySpriteGraphics.transform(transform);

					entitySpriteGraphics
							.drawImage(entitySpriteImage,
									entitySpriteWidth / 2,
									entitySpriteHeight / 2, null);
					int dx = entityScreenX - centralEntityScreenX
							+ (columnWidth / 2);
					int dx2 = dx + (entitySpriteWidth * 2);
					int sx = 0;
					int sx2 = entitySpriteWidth * 2;
					if(dx < 0) {
						if(dx2 < 0)
							continue;
					}
					if(dx2 > columnWidth) {
						if(dx - (entitySpriteWidth / 2) > columnWidth)
							continue;
					}
					int dy = entityScreenY + rowHeight / 2
							- centralEntityScreenY;
					int dy2 = dy + (entitySpriteHeight * 2);
					int sy = 0;
					int sy2 = entitySpriteHeight * 2;
					if(dy < 0) {
						if(dy2 < 0)
							continue;
					}
					if(dy2 > rowHeight) {
						if(dy - (entitySpriteHeight / 2) > rowHeight)
							continue;
					}
					imageGraphics.drawImage(entitySpriteImageRotated, dx
							- entitySpriteWidth, dy - entitySpriteHeight, dx2
							- entitySpriteWidth, dy2 - entitySpriteHeight, sx,
							sy, sx2, sy2, null);
					int nameOffset = 0;
					if(entity instanceof LivingEntity) {
						nameOffset = 10;
						LivingEntity livingEntity = (LivingEntity) entity;
						imageGraphics.setColor(Color.RED);
						imageGraphics.fillRoundRect(dx
								- (entitySpriteWidth / 2), dy
								- (entitySpriteHeight / 2) - 10,
								entitySpriteWidth, 5, 5, 5);
						imageGraphics.setColor(Color.GREEN);
						imageGraphics
								.fillRoundRect(
										dx - (entitySpriteWidth / 2),
										dy - (entitySpriteHeight / 2) - 10,
										(int) (entitySpriteWidth * (((double) livingEntity
												.getHealth()) / ((double) livingEntity
												.getMaxHealth()))), 5, 5, 5);
						imageGraphics.setColor(Color.BLACK);
						imageGraphics.drawRoundRect(dx
								- (entitySpriteWidth / 2), dy
								- (entitySpriteHeight / 2) - 10,
								entitySpriteWidth, 5, 5, 5);
					}
					if(entity instanceof NamedEntity) {
						imageGraphics.setColor(new Color(0, 0, 0, 100));
						Font font = imageGraphics.getFont();
						imageGraphics.setFont(Constants.NEUROPOL_X
								.deriveFont(15F));
						Object textAntialiasValue = imageGraphics
								.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
						imageGraphics.setRenderingHint(
								RenderingHints.KEY_TEXT_ANTIALIASING,
								RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
						FontMetrics metrics = imageGraphics.getFontMetrics();
						String name = ((NamedEntity) entity).getName();
						int x = dx - (metrics.stringWidth(name) / 2) - 2;
						int y = dy - (entitySpriteHeight / 2)
								- (metrics.getHeight() / 2) - 10 - nameOffset
								- 2;
						imageGraphics.fillRect(x, y,
								metrics.stringWidth(name) + 4,
								metrics.getHeight() + 4);
						imageGraphics.setColor(Color.GRAY);
						imageGraphics.drawString(name, x + 2,
								y + metrics.getHeight() + 2);
						imageGraphics.setFont(font);
						imageGraphics.setRenderingHint(
								RenderingHints.KEY_TEXT_ANTIALIASING,
								textAntialiasValue);
					}
				}

				BufferedImage spriteImageRotated = new BufferedImage(
						spriteWidth * 2, spriteHeight * 2,
						BufferedImage.TYPE_INT_ARGB);
				Graphics2D spriteGraphics = spriteImageRotated.createGraphics();

				AffineTransform transform = new AffineTransform();
				transform.setToIdentity();
				transform.rotate(Math.toRadians(centralEntity.getRotation()),
						spriteWidth, spriteHeight);
				spriteGraphics.setRenderingHint(
						RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				spriteGraphics.setRenderingHint(
						RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				spriteGraphics.transform(transform);

				spriteGraphics.drawImage(spriteImage, spriteWidth / 2,
						spriteHeight / 2, null);
				imageGraphics.drawImage(spriteImageRotated, columnWidth / 2
						- spriteWidth, rowHeight / 2 - spriteHeight, null);

				int nameOffset = 0;
				if(centralEntity instanceof LivingEntity) {
					nameOffset = 10;
					LivingEntity livingEntity = (LivingEntity) centralEntity;
					imageGraphics.setColor(Color.RED);
					imageGraphics.fillRoundRect((columnWidth / 2)
							- (spriteWidth / 2), (rowHeight / 2)
							- (spriteHeight / 2) - 10, spriteWidth, 5, 5, 5);
					imageGraphics.setColor(Color.GREEN);
					imageGraphics.fillRoundRect((columnWidth / 2)
							- (spriteWidth / 2), (rowHeight / 2)
							- (spriteHeight / 2) - 10,
							(int) (spriteWidth * (((double) livingEntity
									.getHealth()) / ((double) livingEntity
									.getMaxHealth()))), 5, 5, 5);
					imageGraphics.setColor(Color.BLACK);
					imageGraphics.drawRoundRect((columnWidth / 2)
							- (spriteWidth / 2), (rowHeight / 2)
							- (spriteHeight / 2) - 10, spriteWidth, 5, 5, 5);
				}
				if(centralEntity instanceof NamedEntity) {
					imageGraphics.setColor(new Color(0, 0, 0, 100));
					Font font = imageGraphics.getFont();
					imageGraphics.setFont(Constants.NEUROPOL_X.deriveFont(15F));
					Object textAntialiasValue = imageGraphics
							.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
					imageGraphics.setRenderingHint(
							RenderingHints.KEY_TEXT_ANTIALIASING,
							RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
					FontMetrics metrics = imageGraphics.getFontMetrics();
					String name = ((NamedEntity) centralEntity).getName();
					int x = (columnWidth / 2) - (metrics.stringWidth(name) / 2)
							- 2;
					int y = (rowHeight / 2) - (spriteHeight / 2)
							- (metrics.getHeight() / 2) - 10 - nameOffset - 2;
					imageGraphics.fillRect(x, y, metrics.stringWidth(name) + 4,
							metrics.getHeight() + 4);
					imageGraphics.setColor(Color.GRAY);
					imageGraphics.drawString(name, x + 2,
							y + metrics.getHeight() + 2);
					imageGraphics.setFont(font);
					imageGraphics.setRenderingHint(
							RenderingHints.KEY_TEXT_ANTIALIASING,
							textAntialiasValue);
				}
				imageGraphics.setFont(imageGraphics.getFont().deriveFont(15F));
				imageGraphics.setColor(Color.RED);
				imageGraphics.drawString("Location: (" + centralEntity.getX()
						+ ", " + centralEntity.getY() + ")", 30, 45);
				{
					KeyboardLayout layout = game.getKeyboardLayout();
					StringBuilder controls = new StringBuilder("Use ");
					controls.append(KeyEvent.getKeyText(layout.getKeyUp())
							.toLowerCase());
					controls.append(",");
					controls.append(KeyEvent.getKeyText(layout.getKeyDown())
							.toLowerCase());
					controls.append(",");
					controls.append(KeyEvent.getKeyText(layout.getKeyLeft())
							.toLowerCase());
					controls.append(",");
					controls.append(KeyEvent.getKeyText(layout.getKeyRight())
							.toLowerCase());
					controls.append(" to move. Press ");
					controls.append(KeyEvent.getKeyText(layout.getKeyFire())
							.toLowerCase());
					controls.append(" or left-click to fire.");

					imageGraphics.drawString(controls.toString(), 30, 60);

					imageGraphics.drawString(
							"Press "
									+ KeyEvent.getKeyText(
											layout.getKeyDropLandmine())
											.toLowerCase()
									+ " or right-click to drop charges.", 30,
							75);
				}
				graphics.drawImage(image, screenX, screenY, null);
				graphics.setColor(Color.BLACK);
				graphics.drawRect(0, 0, columnWidth - 1, rowHeight - 1);
				screenX += columnWidth;
				if(screenX > columnWidth * (columnsInRow - 1)) {
					screenX = 0;
					screenY += rowHeight;
					if(screenY > rowHeight * (rows - 1))
						break;
				}
			}
		}
	}

	private double interpolate(double current, double last, double factor) {
		return last + (current - last) * factor;
	}
}
