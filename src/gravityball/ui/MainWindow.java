package gravityball.ui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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

	private StartPanel start;
	private SelectPanel select;
	public GamePanel game;
	private PausePanel pause;
	public LosePanel lose;
	public WinPanel win;
	public WinFinalPanel winFinal;

	boolean isInitial;

	int totalScore;

	/** 程序设置 */
	private AppSettings settings;

	/** 程序场景 */
	public Scenes scenes;

	public Canvas c = null;

	public Integer gameLevel = 1;

	public MainWindow() {
		isInitial = true;
		totalScore = 0;
		this.setLayout(new GridBagLayout());
		// panel = new JPanel();
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
		settings.setResolution(800, 600);
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

	class StartPanel extends JPanel {
		private JButton select_level;
		private JTextArea description;
		private JTextArea title;

		// 设置背景
		protected void paintComponent(Graphics g) {
			ImageIcon icon = new ImageIcon("assets/Background/01.jpg");
			g.drawImage(icon.getImage(), 0, 0, getSize().width, getSize().height, this);// 图片会自动缩放
		}

		public StartPanel() {
			super();
			this.setLayout(new GridBagLayout());

			// 设置组件
			select_level = new JButton("SELECT LEVEL");
			select_level.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					try {
						start.setVisible(false);
						select.setVisible(true);
					} catch (Exception e) {
						//
					}
				}
			});

			String text = "Tilt the board by moving your mouse\n"
					+ "the board will tilt to the direction of the cursor.\n" + "Avoid the obstacles and\n"
					+ "make the ball roll to the chequered flag.\n";
			description = makeDescription(text, 20, Color.gray);

			text = "GRAVITY BALL";
			title = makeDescription(text, 50, Color.gray);

			// TODO
			this.add(select_level, new GBC(0, 2, 2, 1).setAnchor(GBC.CENTER).setWeight(100, 100));
			this.add(description, new GBC(0, 0).setAnchor(GBC.CENTER).setWeight(100, 100));
			this.add(title, new GBC(0, 0).setAnchor(GBC.PAGE_START).setWeight(100, 100));
		}
	}

	class SelectPanel extends JPanel {
		JButton level1;
		JButton level2;
		JButton level3;
		JButton level4;
		JButton level5;
		JTextArea title;
		JTextArea loading;

		JButton makeButton(Integer game_level) {
			JButton button = new JButton();
			String str = "LEVEL " + game_level.toString();
			button = new JButton(str);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					try {
						totalScore = 0;
						gameLevel = game_level;
						loading.setVisible(true);
						
						if (!isInitial)
							Program.mainWindow.remove(c);
						final JmeCanvasContext ctx = (JmeCanvasContext) scenes.getContext();
						c = ctx.getCanvas();
						Program.mainWindow.add(c,
								new GBC(0, 0).setAnchor(GBC.CENTER).setFill(GBC.BOTH).setWeight(100, 100));
						c.setVisible(false);
						isInitial = false;

						scenes.enqueue(new Runnable() {
							@Override
							public void run() {
								scenes.gameReset();
							}
						});

						scenes.enqueue(new Runnable() {
							@Override
							public void run() {
								String str_file = "/Maps/map" + game_level.toString() + ".json";
								scenes.loadFromFile(
										new JSONObject(readall(MainWindow.class.getResourceAsStream(str_file))));

								
								scenes.gameStart();
								loading.setVisible(false);
								select.setVisible(false);
								Program.mainWindow.c.setVisible(true);
								game.setVisible(true);
							}
						});

					} catch (Exception e) {
						// System.out.println(e.getMessage());
					}
				}
			});
			return button;
		}

		// 设置背景
		protected void paintComponent(Graphics g) {
			ImageIcon icon = new ImageIcon("assets/Background/01.jpg");
			g.drawImage(icon.getImage(), 0, 0, getSize().width, getSize().height, this);// 图片会自动缩放
		}

		public SelectPanel() {
			super();
			this.setLayout(new GridBagLayout());

			// 设置组件
			title = makeDescription("SELECT LEVEL", 35, Color.gray);
			loading = makeDescription("LOADING...", 20, Color.gray);
			level1 = makeButton(new Integer(1));
			level2 = makeButton(new Integer(2));
			level3 = makeButton(new Integer(3));
			level4 = makeButton(new Integer(4));
			level5 = makeButton(new Integer(5));
			JTextArea empty = makeDescription("", 100, Color.white);

			// TODO
			GridBagConstraints s = new GridBagConstraints();
			s.gridwidth = 0;
			s.anchor = GridBagConstraints.NORTH;
			this.add(title, s);
			loading.setVisible(false);
			this.add(empty, s);
			s.gridwidth = 1;
			s.anchor = GridBagConstraints.SOUTH;
			this.add(level1, s);
			this.add(level2, s);
			this.add(level3, s);
			this.add(level4, s);
			s.gridwidth = 0;
			this.add(level5, s);
			s.anchor = GridBagConstraints.NORTH;
			this.add(loading, s);
		}
	}

	public class GamePanel extends JPanel {
		JButton game_pause;
		JLabel game_score;

		GamePanel() {
			game_pause = new JButton("PAUSE");
			game_pause.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					try {
						scenes.enqueue(new Runnable() {
							@Override
							public void run() {
								if(scenes.getStatus() == ScenesStatus.PLAYING)
									scenes.gamePause();
							}
						});
						if (!isInitial)
							Program.mainWindow.remove(c);
						game.setVisible(false);
						pause.setVisible(true);
					} catch (Exception e) {
						//
					}
				}
			});

			game_score = new JLabel();

			// TODO
			this.add(game_pause, new GBC(0, 0).setAnchor(GBC.EAST).setWeight(100, 100));
			this.add(game_score, GBC.RELATIVE);
		}
	}

	class PausePanel extends JPanel {
		JButton game_retry;
		JButton game_resume;
		JButton select_level;

		// 设置背景
		protected void paintComponent(Graphics g) {
			ImageIcon icon = new ImageIcon("assets/Background/01.jpg");
			g.drawImage(icon.getImage(), 0, 0, getSize().width, getSize().height, this);// 图片会自动缩放
		}

		public PausePanel() {
			game_retry = new JButton("RETRY");
			game_retry.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					try {
						pause.setVisible(false);
						String tmpFileName = "/Maps/map" + gameLevel.toString() + ".json";
						scenes.enqueue(new Runnable() {
							@Override
							public void run() {
								scenes.gameReset();
							}
						});
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
						Program.mainWindow.add(c,
								new GBC(0, 0).setAnchor(GBC.CENTER).setFill(GBC.BOTH).setWeight(100, 100));
						isInitial = false;
						game.setVisible(true);
					} catch (Exception e) {
						//
					}
				}
			});

			game_resume = new JButton("RESUME");
			game_resume.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					try {
						pause.setVisible(false);
						scenes.enqueue(new Runnable() {
							@Override
							public void run() {
								scenes.gameResume();
							}
						});
						final JmeCanvasContext ctx = (JmeCanvasContext) scenes.getContext();
						c = ctx.getCanvas();
						Program.mainWindow.add(c,
								new GBC(0, 0).setAnchor(GBC.CENTER).setFill(GBC.BOTH).setWeight(100, 100));
						isInitial = false;
						game.setVisible(true);
					} catch (Exception e) {
						//
					}
				}
			});

			select_level = new JButton("SELECT LEVEL");
			select_level.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					try {
						pause.setVisible(false);
						select.setVisible(true);
					} catch (Exception e) {
						//
					}
				}
			});

			// TODO
			this.add(game_retry, new GBC(0, 0));
			this.add(game_resume, GBC.RELATIVE);
			this.add(select_level, GBC.RELATIVE);
		}
	}

	public class LosePanel extends JPanel {
		JButton game_retry;
		JButton select_level;

		// 设置背景
		protected void paintComponent(Graphics g) {
			ImageIcon icon = new ImageIcon("assets/Background/01.jpg");
			g.drawImage(icon.getImage(), 0, 0, getSize().width, getSize().height, this);// 图片会自动缩放
		}

		public LosePanel() {
			game_retry = new JButton("RETRY");
			game_retry.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					try {
						lose.setVisible(false);
						String tmpFileName = "/Maps/map" + gameLevel.toString() + ".json";
						scenes.enqueue(new Runnable() {
							@Override
							public void run() {
								scenes.gameReset();
							}
						});
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
						Program.mainWindow.add(c,
								new GBC(0, 0).setAnchor(GBC.CENTER).setFill(GBC.BOTH).setWeight(100, 100));
						isInitial = false;
						game.setVisible(true);
					} catch (Exception e) {
						//
					}
				}
			});

			select_level = new JButton("SELECT LEVEL");
			select_level.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					try {
						if (!isInitial)
							Program.mainWindow.remove(c);
						lose.setVisible(false);
						select.setVisible(true);
					} catch (Exception e) {
						//
					}
				}
			});

			// TODO
			this.add(game_retry, new GBC(0, 0));
			this.add(select_level, GBC.RELATIVE);
		}
	}

	public class WinPanel extends JPanel {
		JButton next_level;
		JButton select_level;

		// 设置背景
		protected void paintComponent(Graphics g) {
			ImageIcon icon = new ImageIcon("assets/Background/01.jpg");
			g.drawImage(icon.getImage(), 0, 0, getSize().width, getSize().height, this);// 图片会自动缩放
		}

		public WinPanel() {
			next_level = new JButton("NEXT LEVEL");
			next_level.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					try {
						totalScore += scenes.getScore();
						if (gameLevel < 5) {
							win.setVisible(false);
							gameLevel = gameLevel + 1;
							String tmpFileName = "/Maps/map" + gameLevel.toString() + ".json";
							scenes.enqueue(new Runnable() {
								@Override
								public void run() {
									scenes.gameReset();
								}
							});
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
							Program.mainWindow.add(c,
									new GBC(0, 0).setAnchor(GBC.CENTER).setFill(GBC.BOTH).setWeight(100, 100));
							isInitial = false;
							game.setVisible(true);
						}
					} catch (Exception e) {
						//
					}
				}
			});

			select_level = new JButton("SELECT LEVEL");
			select_level.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					try {
						if (!isInitial)
							Program.mainWindow.remove(c);
						win.setVisible(false);
						select.setVisible(true);
					} catch (Exception e) {
						//
					}
				}
			});

			// TODO
			this.add(next_level, new GBC(0, 0));
			this.add(select_level, GBC.RELATIVE);
		}
	}

	public class WinFinalPanel extends JPanel {
		JTextArea msg;

		// 设置背景
		protected void paintComponent(Graphics g) {
			ImageIcon icon = new ImageIcon("assets/Background/01.jpg");
			g.drawImage(icon.getImage(), 0, 0, getSize().width, getSize().height, this);// 图片会自动缩放
		}

		WinFinalPanel() {
			this.setLayout(new GridBagLayout());
			msg = makeDescription("YOU WIN", 50, Color.gray);

			this.add(msg, new GBC(0, 0));
		}
	}
}
