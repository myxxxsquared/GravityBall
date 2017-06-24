package gravityball.ui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;

import org.json.JSONObject;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

import gravityball.Program;
import gravityball.game.Scenes;
import gravityball.game.Scenes.ScenesStatus;

/** 主窗口 */
public class MainWindow extends JFrame {

	// 各种窗口
	public StartPanel start;
	public SelectPanel select;
	public GamePanel game;
	public PausePanel pause;
	public LosePanel lose;
	public WinPanel win;
	public WinFinalPanel winFinal;

	/** 第一次加载之前为true */
	boolean isInitial;

	/** 保存当前关卡之前的分数之和 */
	public static int totalScore;

	/** 保存当前解锁的最高关卡 */
	public Integer historyLevel = new Integer(1);

	/** 程序设置 */
	private AppSettings settings;

	/** 游戏场景 */
	public Scenes scenes;

	/** 游戏画布 */
	public Canvas c = null;

	/** 保存当前游戏等级 */
	public Integer gameLevel = 1;

	public MainWindow() {
		isInitial = true;
		gameLevel = 1;
		totalScore = 0;
		this.setLayout(new GridBagLayout());

		// 添加各种窗口，初始时只显示start窗口
		start = new StartPanel();
		select = new SelectPanel();
		game = new GamePanel();
		pause = new PausePanel();
		lose = new LosePanel();
		win = new WinPanel();
		winFinal = new WinFinalPanel();

		this.add(start, new GBC(0, 0).setFill(GBC.BOTH).setWeight(100, 100));
		this.add(select, new GBC(0, 0).setFill(GBC.BOTH).setWeight(100, 100));
		select.setVisible(false);

		this.add(game, new GBC(0, 1));
		game.setVisible(false);

		this.add(pause, new GBC(0, 0).setFill(GBC.BOTH).setWeight(100, 100));
		pause.setVisible(false);

		this.add(lose, new GBC(0, 0).setFill(GBC.BOTH).setWeight(100, 100));
		lose.setVisible(false);

		this.add(win, new GBC(0, 0).setFill(GBC.BOTH).setWeight(100, 100));
		win.setVisible(false);

		this.add(winFinal, new GBC(0, 0).setFill(GBC.BOTH).setWeight(100, 100));
		winFinal.setVisible(false);

		// 建立和修改设置
		settings = new AppSettings(true);
		settings.setResolution(640, 600);
		settings.setSamples(Program.NUMBER_SAMPLES);
		settings.setEmulateMouse(false);
		settings.setFrameRate(Program.FRAME_RATE);
		settings.setUseInput(false);

		// 新建场景，修改设置
		scenes = new Scenes();
		scenes.setSettings(settings);
		scenes.createCanvas();
		scenes.setDisplayStatView(false);
		scenes.setDisplayFps(false);

		// 刷新分数
		Timer timeAction = new Timer(100, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Integer score = new Integer(totalScore + scenes.getScore());
				game.game_score.setText("score: " + score.toString());
			}
		});
		timeAction.start();
	}

	// 临时测试变量
	boolean thisenabled = false;
	public static JLabel jLabel;

	// 临时测试函数
	static String readall(InputStream input) {
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		try {
			for (int n; (n = input.read(b)) != -1;) {
				out.append(new String(b, 0, n, "UTF-8"));
			}
		} catch (IOException e) {
		}
		return out.toString();
	}

	/** 创建一个透明背景的文本区域 */
	public JTextArea makeDescription(String text, int font_size, Color font_color) {
		JTextArea text_area = new JTextArea();
		text_area.setText(text);
		text_area.setEditable(false);
		text_area.setBackground(null);
		text_area.setOpaque(false);
		text_area.setFont(new Font("Serif", Font.BOLD, font_size));
		text_area.setForeground(font_color);
		return text_area;
	}
	
	/** 重写了paint方法，用于设置按钮的背景 */
	class ButtonPanel extends JPanel {
		String text;

		public ButtonPanel(String s) {
			text = s;
		}

		// 重写paint方法
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			int li_beginx = (int) g2.getClipBounds().getX();
			int li_beginy = (int) g2.getClipBounds().getY();
			int li_width = this.getWidth();
			int li_height = this.getHeight();

			g2.setColor(Color.black);
			g2.drawRect(li_beginx, li_beginy, li_width - 1, li_height - 1);
			Color bgColor = this.getBackground();
			int[] li_fromcolor = new int[] { bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue() }; // 传入的背景颜色的RGB的值
			int[][] li_delcolors = getDelColors(li_fromcolor, li_height - 2); // 根据高度，计算出每帧高度的渐变颜色
			for (int i = 0; i < li_delcolors.length; i++) { // 循环画出每一帧
				int li_r = li_fromcolor[0] + li_delcolors[i][0]; // R
				int li_g = li_fromcolor[1] + li_delcolors[i][1]; // G
				int li_b = li_fromcolor[2] + li_delcolors[i][2]; // B
				if (li_r > 255) { // 如果溢出255，则当255
					li_r = 255;
				}
				if (li_g > 255) {
					li_g = 255;
				}
				if (li_b > 255) {
					li_b = 255;
				}
				g2.setColor(new Color(li_r, li_g, li_b)); // 设置颜色
				g2.fillRect(0 + 1, i + 1, li_width - 2, 1);
			}

			// 设置文本区域的颜色和字体
			g2.setColor(Color.WHITE);
			g2.setFont(new Font("Serif", Font.BOLD, 12));
			g2.drawString(text, 30, 17);
		}

		// 颜色递减!!! 即要有一个算法,呈某种递减速度就会出现不同效果!!!
		// 出现光感效果的原理是上半部
		private int[][] getDelColors(int[] _fromColor, int _height) {
			int[][] delColor = new int[_height][3]; //
			int li_half = _height / 2;
			if (li_half == 0) {
				li_half = 1;
			}
			int li_d1 = 100 / li_half; // 设置递减的值
			if (li_d1 == 0) {
				li_d1 = 1;
			}
			int li_prefix = 57; // 有个前辍，可以随便设个值，取决于想变成黑色还是白色
			for (int i = li_half - 1; i >= 0; i--) { //
				delColor[i][0] = li_prefix + (li_half - 1 - i) * li_d1;
				delColor[i][1] = li_prefix + (li_half - 1 - i) * li_d1;
				delColor[i][2] = li_prefix + (li_half - 1 - i) * li_d1;
			}
			int li_d2 = (int) ((100 / li_half) * 0.7); // 这里有个系数变化，会有光感效果
			if (li_d2 == 0) {
				li_d2 = 1;
			}
			for (int i = li_half; i < _height; i++) {
				delColor[i][0] = (i - li_half) * li_d2;
				delColor[i][1] = (i - li_half) * li_d2;
				delColor[i][2] = (i - li_half) * li_d2;
			}
			return delColor;
		}
	}

	/** 初始界面的画布 */
	class StartPanel extends JPanel {
		/** 选关按钮 */
		private ButtonPanel select_level;
		
		/** 游戏描述 */
		private JTextArea description;
		
		/** 游戏名字 */
		private JTextArea title;

		/** 设置背景 */
		protected void paintComponent(Graphics g) {
			ImageIcon icon = new ImageIcon(this.getClass().getResource("/Background/01.jpg"));
			g.drawImage(icon.getImage(), 0, 0, getSize().width, getSize().height, this);// 图片会自动缩放
		}

		public StartPanel() {
			this.setLayout(new GridBagLayout()); // 设置布局格式为GridBagLayout
			
			// 设置选关按钮
			select_level = new ButtonPanel("SELECT LEVEL");
			select_level.setBackground(new Color(75, 75, 49)); // 背景颜色，有渐变效果
			select_level.setPreferredSize(new Dimension(150, 30)); // 大小
			select_level.addMouseListener(new MouseAdapter() { // 设置鼠标点击的监听
				public void mouseClicked(MouseEvent e) {
					try {
						start.setVisible(false); // 当前界面隐藏
						select.setVisible(true); // 进入选关界面
					} catch (Exception ee) {
					}
				}
			});
			
			// 设置游戏描述
			String text = "Tilt the board by moving your mouse\n"
					+ "the board will tilt to the direction of the cursor.\n" + "Avoid the obstacles and\n"
					+ "make the ball roll to the chequered flag.\n";
			description = makeDescription(text, 20, Color.gray);

			// 设置游戏名字
			text = "GRAVITY BALL";
			title = makeDescription(text, 50, Color.gray);
			
			// 空行，用于形成画布中不同组件之间的间隔
			JTextArea empty = makeDescription("", 50, Color.BLACK);
			JTextArea empty2 = makeDescription("", 30, Color.BLACK);

			GridBagConstraints s = new GridBagConstraints();
			s.gridwidth = 0;
			s.anchor = GridBagConstraints.NORTH;
			this.add(title, s);
			this.add(empty, s);
			this.add(description, s);
			this.add(empty2, s);
			this.add(select_level, s);
		}
	}

	/** 选择关卡界面的画布 */
	class SelectPanel extends JPanel {
		/** 关卡按钮 */
		ButtonPanel level1;
		ButtonPanel level2;
		ButtonPanel level3;
		ButtonPanel level4;
		ButtonPanel level5;
		
		/** 画布标题 */
		JTextArea title;
		
		/** 加载提示 */
		JTextArea loading;

		/** 创建一个指定关卡和颜色的按钮 */
		public ButtonPanel makeButton(Integer game_level, Color color) {
			ButtonPanel button = new ButtonPanel("LEVEL " + game_level.toString()); // 设置按钮文本
			button.setBackground(color); // 设置按钮颜色
			button.setPreferredSize(new Dimension(150, 30)); // 设置按钮大小
			button.addMouseListener(new MouseAdapter() { // 添加鼠标点击事件
				public void mouseClicked(MouseEvent e) {
					try {
						if (historyLevel >= game_level) { // 检测是否解锁了当前关卡
							totalScore = 0; // 每次重新选关都会清空当前分数
							gameLevel = game_level;
							loading.setVisible(true);

							if (!isInitial) // 第一次加载前不能移除游戏画布
								Program.mainWindow.remove(c);
							final JmeCanvasContext ctx = (JmeCanvasContext) scenes.getContext();
							c = ctx.getCanvas();
							Program.mainWindow.add(c,
									new GBC(0, 0).setAnchor(GBC.CENTER).setFill(GBC.BOTH).setWeight(100, 100));
							c.setVisible(false);
							isInitial = false;

							// 游戏重置
							scenes.enqueue(new Runnable() {
								@Override
								public void run() {
									scenes.gameReset();
								}
							});

							// 游戏加载并开始
							scenes.enqueue(new Runnable() {
								@Override
								public void run() {
									// 游戏加载
									String str_file = "/Maps/map" + game_level.toString() + ".json";
									scenes.loadFromFile(
											new JSONObject(readall(MainWindow.class.getResourceAsStream(str_file))));

									scenes.gameStart(); // 游戏开始
									loading.setVisible(false); // 加载提示隐藏
									select.setVisible(false); // 当前界面隐藏
									Program.mainWindow.c.setVisible(true); // 加载完成，进入游戏界面
									game.setVisible(true);
								}
							});
						}
					} catch (Exception ee) {

					}
				}
			});
			return button;
		}

		/** 设置画布的背景 */
		protected void paintComponent(Graphics g) {
			ImageIcon icon = new ImageIcon(this.getClass().getResource("/Background/01.jpg"));
			g.drawImage(icon.getImage(), 0, 0, getSize().width, getSize().height, this);// 图片会自动缩放
		}

		public SelectPanel() {
			this.setLayout(new GridBagLayout());

			// 设置组件
			title = makeDescription("SELECT LEVEL", 35, Color.gray);
			loading = makeDescription("LOADING...", 20, Color.gray);
			level1 = makeButton(new Integer(1), new Color(144, 29, 67));
			level2 = makeButton(new Integer(2), new Color(126, 125, 76));
			level3 = makeButton(new Integer(3), new Color(75, 93, 75));
			level4 = makeButton(new Integer(4), new Color(35, 65, 105));
			level5 = makeButton(new Integer(5), new Color(75, 48, 132));
			JTextArea empty = makeDescription("", 100, Color.white); // 用于形成标题和按钮间的空白

			// 添加各组件到画布中
			GridBagConstraints s = new GridBagConstraints();
			s.gridwidth = 0;
			s.anchor = GridBagConstraints.NORTH;
			this.add(title, s);
			this.add(empty, s);
			s.anchor = GridBagConstraints.SOUTH;
			this.add(level1, s);
			this.add(level2, s);
			this.add(level3, s);
			this.add(level4, s);
			s.gridwidth = 0;
			this.add(level5, s);
			s.anchor = GridBagConstraints.NORTH;
			this.add(loading, s);
			loading.setVisible(false);
		}
	}

	/** 游戏界面菜单的画布 */
	public class GamePanel extends JPanel {
		ButtonPanel game_pause;
		JTextArea game_score;

		GamePanel() {
			this.setLayout(new FlowLayout());
			
			// 设置组件
			game_score = makeDescription("score: 0", 25, Color.BLACK);
			game_pause = new ButtonPanel("       PAUSE");
			game_pause.setBackground(new Color(75, 75, 49));
			game_pause.setPreferredSize(new Dimension(150, 30));
			game_pause.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					try {
						scenes.enqueue(new Runnable() {
							@Override
							public void run() {
								if (scenes.getStatus() == ScenesStatus.PLAYING)
									scenes.gamePause(); // 游戏暂停
							}
						});
						if (!isInitial)
							Program.mainWindow.remove(c);
						game.setVisible(false); // 当前界面隐藏
						pause.setVisible(true); // 进入暂停界面
					} catch (Exception ee) {
						
					}
				}
			});
			
			// 添加各组件到画布中
			this.add(game_pause);
			this.add(game_score);
		}
	}

	/** 暂停界面的画布 */
	class PausePanel extends JPanel {
		ButtonPanel game_retry;
		ButtonPanel game_resume;
		ButtonPanel select_level;
		JTextArea title;

		/** 设置画布背景 */
		protected void paintComponent(Graphics g) {
			ImageIcon icon = new ImageIcon(this.getClass().getResource("/Background/01.jpg"));
			g.drawImage(icon.getImage(), 0, 0, getSize().width, getSize().height, this);// 图片会自动缩放
		}

		public PausePanel() {
			this.setLayout(new GridBagLayout());
			
			// 设置各组件
			title = makeDescription("PAUSE", 35, Color.gray);
			JTextArea empty = makeDescription("", 100, Color.BLACK);
			game_retry = new ButtonPanel("       RETRY");
			game_retry.setBackground(new Color(75, 75, 49));
			game_retry.setPreferredSize(new Dimension(150, 30));
			game_retry.addMouseListener(new MouseAdapter() { // 重新加载当前关卡并进入游戏
				public void mouseClicked(MouseEvent e) {
					try {
						pause.setVisible(false);
						
						// 重新加载
						String tmpFileName = "/Maps/map" + gameLevel.toString() + ".json";
						scenes.enqueue(new Runnable() {
							@Override
							public void run() {
								scenes.gameReset();
							}
						});
						
						// 进入游戏
						scenes.enqueue(new Runnable() {
							@Override
							public void run() {
								scenes.loadFromFile(
										new JSONObject(readall(MainWindow.class.getResourceAsStream(tmpFileName))));
								scenes.gameStart();
							}
						});
						if (!isInitial)
							Program.mainWindow.remove(c);
						final JmeCanvasContext ctx = (JmeCanvasContext) scenes.getContext();
						c = ctx.getCanvas();
						
						// 恢复游戏界面
						Program.mainWindow.add(c,
								new GBC(0, 0).setAnchor(GBC.CENTER).setFill(GBC.BOTH).setWeight(100, 100));
						isInitial = false;
						game.setVisible(true);
					} catch (Exception ee) {
						
					}
				}
			});

			game_resume = new ButtonPanel("      RESUME");
			game_resume.setBackground(new Color(75, 75, 49));
			game_resume.setPreferredSize(new Dimension(150, 30));
			game_resume.addMouseListener(new MouseAdapter() { // 恢复游戏
				public void mouseClicked(MouseEvent e) {
					try {
						pause.setVisible(false);
						scenes.enqueue(new Runnable() {
							@Override
							public void run() {
								scenes.gameResume(); // 恢复游戏
							}
						});
						final JmeCanvasContext ctx = (JmeCanvasContext) scenes.getContext();
						c = ctx.getCanvas();
						
						// 恢复游戏界面
						Program.mainWindow.add(c,
								new GBC(0, 0).setAnchor(GBC.CENTER).setFill(GBC.BOTH).setWeight(100, 100));
						isInitial = false;
						game.setVisible(true);
					} catch (Exception ee) {
						
					}
				}
			});

			select_level = new ButtonPanel("SELECT_LEVEL");
			select_level.setBackground(new Color(75, 75, 49));
			select_level.setPreferredSize(new Dimension(150, 30));
			select_level.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					try {
						pause.setVisible(false);
						select.setVisible(true); // 进入选关界面
					} catch (Exception ee) {

					}
				}
			});

			// 添加各组件到画布中
			GridBagConstraints s = new GridBagConstraints();
			s.gridwidth = 0;
			s.anchor = GridBagConstraints.CENTER;
			this.add(title, s);
			this.add(empty, s);
			this.add(game_retry, s);
			this.add(game_resume, s);
			this.add(select_level, s);
		}
	}

	/** 失败界面的画布 */
	public class LosePanel extends JPanel {
		ButtonPanel game_retry;
		ButtonPanel select_level;
		JTextArea title;
		public JTextArea description;

		// 设置背景
		protected void paintComponent(Graphics g) {
			ImageIcon icon = new ImageIcon(this.getClass().getResource("/Background/01.jpg"));
			g.drawImage(icon.getImage(), 0, 0, getSize().width, getSize().height, this);// 图片会自动缩放
		}

		public LosePanel() {
			this.setLayout(new GridBagLayout());
			
			// 设置组件
			title = makeDescription("LOSE!!", 35, Color.gray);
			JTextArea empty1 = makeDescription("", 35, Color.GRAY);
			description = makeDescription("", 25,
					Color.GRAY);
			JTextArea empty2 = makeDescription("", 50, Color.GRAY);
			game_retry = new ButtonPanel("      RETRY");
			game_retry.setBackground(new Color(75, 75, 49));
			game_retry.setPreferredSize(new Dimension(150, 30));
			game_retry.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					try {
						lose.setVisible(false);
						
						// 重新加载
						String tmpFileName = "/Maps/map" + gameLevel.toString() + ".json";
						scenes.enqueue(new Runnable() {
							@Override
							public void run() {
								scenes.gameReset();
							}
						});
						
						// 进入游戏
						scenes.enqueue(new Runnable() {
							@Override
							public void run() {
								scenes.loadFromFile(
										new JSONObject(readall(MainWindow.class.getResourceAsStream(tmpFileName))));
								scenes.gameStart();
							}
						});
						if (!isInitial)
							Program.mainWindow.remove(c);
						final JmeCanvasContext ctx = (JmeCanvasContext) scenes.getContext();
						
						// 恢复游戏界面
						c = ctx.getCanvas();
						Program.mainWindow.add(c,
								new GBC(0, 0).setFill(GBC.BOTH).setWeight(100, 100));
						isInitial = false;
						game.setVisible(true);
					} catch (Exception ee) {
						
					}
				}
			});

			select_level = new ButtonPanel("SELECT_LEVEL");
			select_level.setBackground(new Color(75, 75, 49));
			select_level.setPreferredSize(new Dimension(150, 30));
			select_level.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					try {
						if (!isInitial)
							Program.mainWindow.remove(c);
						lose.setVisible(false);
						select.setVisible(true); // 进入选关界面
					} catch (Exception ee) {
						
					}
				}
			});

			// 添加各组件到画布中
			GridBagConstraints s = new GridBagConstraints();
			s.gridwidth = 0;
			s.anchor = GridBagConstraints.CENTER;
			this.add(title, s);
			this.add(empty1, s);
			this.add(description, s);
			this.add(empty2, s);
			s.gridwidth = 1;
			this.add(game_retry, s);
			this.add(select_level, s);
		}
	}

	/** 通关界面的画布 */
	public class WinPanel extends JPanel {
		JTextArea title;
		public JTextArea description;
		ButtonPanel next_level;
		ButtonPanel select_level;

		// 设置背景
		protected void paintComponent(Graphics g) {
			ImageIcon icon = new ImageIcon(this.getClass().getResource("/Background/01.jpg"));
			g.drawImage(icon.getImage(), 0, 0, getSize().width, getSize().height, this);// 图片会自动缩放
		}

		public WinPanel() {
			this.setLayout(new GridBagLayout());
			
			// 设置组件
			title = makeDescription("YOU WIN!!!", 35, Color.GRAY);
			JTextArea empty1 = makeDescription("", 35, Color.GRAY);
			description = makeDescription("", 25,
					Color.GRAY);
			JTextArea empty2 = makeDescription("", 50, Color.GRAY);
			next_level = new ButtonPanel("NEXT LEVEL");
			next_level.setBackground(new Color(75, 75, 49));
			next_level.setPreferredSize(new Dimension(150, 30));
			next_level.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					try {
						totalScore += scenes.getScore();
						if (gameLevel < 5) {
							win.setVisible(false);
							
							// 加载下一关卡
							gameLevel = gameLevel + 1;
							String tmpFileName = "/Maps/map" + gameLevel.toString() + ".json";
							scenes.enqueue(new Runnable() {
								@Override
								public void run() {
									scenes.gameReset();
								}
							});
							
							// 进入游戏
							scenes.enqueue(new Runnable() {
								@Override
								public void run() {
									scenes.loadFromFile(
											new JSONObject(readall(MainWindow.class.getResourceAsStream(tmpFileName))));
									scenes.gameStart();
								}
							});
							if (!isInitial)
								Program.mainWindow.remove(c);
							final JmeCanvasContext ctx = (JmeCanvasContext) scenes.getContext();
							
							// 恢复游戏界面
							c = ctx.getCanvas();
							Program.mainWindow.add(c,
									new GBC(0, 0).setAnchor(GBC.CENTER).setFill(GBC.BOTH).setWeight(100, 100));
							isInitial = false;
							game.setVisible(true);
						}
					} catch (Exception ee) {
						
					}
				}
			});
			
			select_level = new ButtonPanel("SELECT LEVEL");
			select_level.setBackground(new Color(75, 75, 49));
			select_level.setPreferredSize(new Dimension(150, 30));
			select_level.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					try {
						if (!isInitial)
							Program.mainWindow.remove(c);
						win.setVisible(false);
						select.setVisible(true); // 进入选关界面
					} catch (Exception ee) {

					}
				}
			});

			// 添加各组件到画布中
			GridBagConstraints s = new GridBagConstraints();
			s.gridwidth = 0;
			s.anchor = GridBagConstraints.CENTER;
			this.add(title, s);
			this.add(empty1, s);
			this.add(description, s);
			this.add(empty2, s);
			s.gridwidth = 1;
			this.add(next_level, s);
			this.add(select_level, s);
		}
	}

	/** 游戏全部通关的界面 */
	public class WinFinalPanel extends JPanel {
		JTextArea msg;
		public JTextArea description;

		// 设置背景
		protected void paintComponent(Graphics g) {
			ImageIcon icon = new ImageIcon(this.getClass().getResource("/Background/01.jpg"));
			g.drawImage(icon.getImage(), 0, 0, getSize().width, getSize().height, this);// 图片会自动缩放
		}

		WinFinalPanel() {
			this.setLayout(new GridBagLayout());
			
			// 设置组件
			msg = makeDescription("YOU WIN", 50, Color.gray);
			JTextArea empty = makeDescription("", 40, Color.GRAY);
			description = makeDescription("", 25,
					Color.GRAY);
			
			// 添加各组件到画布中
			GridBagConstraints s = new GridBagConstraints();
			s.gridwidth = 0;
			s.anchor = GridBagConstraints.CENTER;
			this.add(msg, s);
			this.add(empty, s);
			this.add(description, s);
		}
	}
}
