package me.axiometry.tanks;

import me.axiometry.tanks.rendering.*;
import me.axiometry.tanks.rendering.ui.MainMenuScreen;
import me.axiometry.tanks.server.TanksServer;
import me.axiometry.tanks.ui.KeyboardLayout;
import me.axiometry.tanks.util.Timer;
import me.axiometry.tanks.world.World;
import me.axiometry.tanks.world.multiplayer.MemoryNetworkManager;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.event.*;

import javax.swing.JFrame;

public final class Tanks extends Applet implements Runnable {
	public static Tanks INSTANCE;

	private static final long serialVersionUID = -303323756633698486L;

	private MainMenuScreen mainMenu;
	private Timer timer;
	private GameRenderer renderer;
	private Thread thread;
	private Screen screen;
	private SpriteMap sprites;
	private World world;
	private KeyboardLayout keyboardLayout;
	private TanksServer server;

	public Tanks() {
		INSTANCE = this;
		timer = new Timer(20F, 60);
	}

	@Override
	public void init() {
		setSize(640, 480);
		setLayout(new BorderLayout());
		renderer = new CanvasRenderer();
		setVisible(true);
	}

	@Override
	public void start() {
	}

	public void begin() {
		if(thread != null)
			return;
		thread = new Thread(this, "Tanks Main Thread");
		thread.start();
	}

	private void startGame() throws Exception {
		sprites = new SpriteMapImpl(getClass().getResourceAsStream(
				"/sprites.png"));
		KeyboardLayout.Builder layoutBuilder = new KeyboardLayout.Builder();
		layoutBuilder = new KeyboardLayout.Builder();
		layoutBuilder.setKeyUp(KeyEvent.VK_W);
		layoutBuilder.setKeyDown(KeyEvent.VK_S);
		layoutBuilder.setKeyLeft(KeyEvent.VK_A);
		layoutBuilder.setKeyRight(KeyEvent.VK_D);
		layoutBuilder.setKeyLeftBarrel(KeyEvent.VK_C);
		layoutBuilder.setKeyRightBarrel(KeyEvent.VK_V);
		layoutBuilder.setKeyFire(KeyEvent.VK_SPACE);
		layoutBuilder.setKeyDropLandmine(KeyEvent.VK_B);
		keyboardLayout = new KeyboardLayout(layoutBuilder);
		screen = mainMenu = new MainMenuScreen();
	}

	@Override
	public void run() {
		System.out.println("Applet started successfully!");
		try {
			startGame();
		} catch(Throwable exception) {
			exception.printStackTrace();
			System.err.println("Unable to start game!");
			System.exit(-1);
		}
		try {
			while(isEnabled()) {
				timer.update();
				for(int i = 0; i < timer.getElapsedTicks(); i++) {
					try {
						runTick();
					} catch(Throwable exception) {
						exception.printStackTrace();
					}
				}
				renderer.render();
				if(timer.getFPSCoolDown() > 0)
					try {
						Thread.sleep(timer.getFPSCoolDown());
					} catch(Exception exception) {}
			}
		} catch(Throwable exception) {
			exception.printStackTrace();
		}
	}

	private void runTick() {
		if(screen != null)
			screen.update();
		InputEvent event;
		InputManager inputManager = renderer.getInputManager();
		while((event = inputManager.nextMouseEvent()) != null
				|| (event = inputManager.nextKeyEvent()) != null)
			if(event instanceof InputEvent)
				handleInputEvent(event);
		renderer.update();
	}

	@Override
	public void stop() {
		setEnabled(false);
		System.exit(0);
	}

	private void handleInputEvent(InputEvent event) {
		if(screen != null)
			screen.onInputEvent(event);
	}

	public Timer getTimer() {
		return timer;
	}

	public GameRenderer getRenderer() {
		return renderer;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public SpriteMap getSprites() {
		return sprites;
	}

	public Screen getScreen() {
		return screen;
	}

	public void setScreen(Screen screen) {
		this.screen = screen;
	}

	public KeyboardLayout getKeyboardLayout() {
		return keyboardLayout;
	}

	public static void main(String[] args) {
		final JFrame frame = new JFrame("Tanks");
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setSize(640, 480);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new BorderLayout());
		frame.setResizable(false);
		final Tanks game = new Tanks();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				game.stop();
			}
		});
		game.init();
		frame.add(game);
		game.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentHidden(ComponentEvent e) {
				frame.setVisible(false);
			}
		});
		frame.setVisible(true);
	}

	public void showMainMenu() {
		screen = mainMenu;
	}

	public synchronized TanksServer getServer() {
		return server;
	}

	public synchronized void startServer(MemoryNetworkManager networkManager)
			throws Exception {
		if(server != null)
			return;
		TanksServer server = new TanksServer(networkManager.getConnection());
		server.start();
		this.server = server;
	}

	public synchronized void stopServer() {
		if(server == null)
			return;
		server.stop();
		server = null;
	}
}
