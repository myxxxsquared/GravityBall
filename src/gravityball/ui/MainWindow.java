package gravityball.ui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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

	class StartPanel extends JPanel {
		private JButton select_level;
		private JTextArea description;
		private JTextArea title;

		// 设置背景
		protected void paintComponent(Graphics g1) {
			Graphics2D g = (Graphics2D) g1;
			super.paintComponent(g);
			int width = getWidth();
			int height = getHeight();

			GradientPaint paint = new GradientPaint(0, 0, Color.CYAN, 0, height, Color.MAGENTA);
			g.setPaint(paint);
			g.fillRect(0, 0, width, height);
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
			description = new JTextArea(100, 400);
			description.setText(
					"Tilt the board by moving your mouse\n" + "the board will tilt to the direction of the cursor.\n"
							+ "Avoid the obstacles and\n" + "make the ball roll to the chequered flag.\n");
			description.setEditable(false);
			description.setBackground(null);
			description.setOpaque(false);
			description.setFont(new Font("Serif", Font.BOLD, 20));

			title = new JTextArea(50, 100);
			title.setText("GRAVITY BALL");
			title.setEditable(false);
			title.setBackground(null);
			title.setOpaque(false);
			title.setFont(new Font("Serif", Font.BOLD, 50));

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

		
		public SelectPanel() {
			super();
			this.setLayout(new GridBagLayout());

			// 设置组件
			title = new JTextArea(50, 100);
			title.setText("SELECT LEVEL");
			title.setEditable(false);
			title.setBackground(null);
			title.setOpaque(false);
			title.setFont(new Font("Serif", Font.BOLD, 35));

			level1 = new JButton("LEVEL 1");
			level1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					try {
						totalScore = 0;
						gameLevel = 1;
						select.setVisible(false);
						
						if(!isInitial)
							Program.mainWindow.remove(c);
						final JmeCanvasContext ctx = (JmeCanvasContext) scenes.getContext();
						c = ctx.getCanvas();
						Program.mainWindow.add(c,
								new GBC(0, 0).setAnchor(GBC.CENTER).setFill(GBC.BOTH).setWeight(100, 100));
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
								scenes.loadFromFile(new JSONObject(
										readall(MainWindow.class.getResourceAsStream("/Maps/map1.json"))));
								scenes.gameStart();
							}
						});
						game.setVisible(true);

					} catch (Exception e) {
						// System.out.println(e.getMessage());
					}
				}
			});
			
			level2 = new JButton("LEVEL 2");
			level2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					try {
						totalScore = 0;
						gameLevel = 2;
						select.setVisible(false);
						if(!isInitial)
						Program.mainWindow.remove(c);
						final JmeCanvasContext ctx = (JmeCanvasContext) scenes.getContext();
						c = ctx.getCanvas();
						Program.mainWindow.add(c,
								new GBC(0, 0).setAnchor(GBC.CENTER).setFill(GBC.BOTH).setWeight(100, 100));
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
								scenes.loadFromFile(new JSONObject(
										readall(MainWindow.class.getResourceAsStream("/Maps/map2.json"))));
								scenes.gameStart();
							}
						});
						game.setVisible(true);

					} catch (Exception e) {
						// System.out.println(e.getMessage());
					}
				}
			});
			
			level3 = new JButton("LEVEL 3");
			level3.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					try {
						totalScore = 0;
						gameLevel = 3;
						select.setVisible(false);

						if(!isInitial)
						Program.mainWindow.remove(c);
						final JmeCanvasContext ctx = (JmeCanvasContext) scenes.getContext();
						c = ctx.getCanvas();
						Program.mainWindow.add(c,
								new GBC(0, 0).setAnchor(GBC.CENTER).setFill(GBC.BOTH).setWeight(100, 100));
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
								scenes.loadFromFile(new JSONObject(
										readall(MainWindow.class.getResourceAsStream("/Maps/map3.json"))));
								scenes.gameStart();
							}
						});
						game.setVisible(true);

					} catch (Exception e) {
						// System.out.println(e.getMessage());
					}
				}
			});
			
			level4 = new JButton("LEVEL 4");
			level4.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					try {
						totalScore = 0;
						gameLevel = 4;
						select.setVisible(false);

						if(!isInitial)
						Program.mainWindow.remove(c);
						final JmeCanvasContext ctx = (JmeCanvasContext) scenes.getContext();
						c = ctx.getCanvas();
						Program.mainWindow.add(c,
								new GBC(0, 0).setAnchor(GBC.CENTER).setFill(GBC.BOTH).setWeight(100, 100));
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
								scenes.loadFromFile(new JSONObject(
										readall(MainWindow.class.getResourceAsStream("/Maps/map4.json"))));
								scenes.gameStart();
							}
						});
						game.setVisible(true);

					} catch (Exception e) {
						// System.out.println(e.getMessage());
					}
				}
			});
			
			level5 = new JButton("LEVEL 5");
			level5.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					try {
						totalScore = 0;
						gameLevel = 5;
						select.setVisible(false);

						if(!isInitial)
						Program.mainWindow.remove(c);
						final JmeCanvasContext ctx = (JmeCanvasContext) scenes.getContext();
						c = ctx.getCanvas();
						Program.mainWindow.add(c,
								new GBC(0, 0).setAnchor(GBC.CENTER).setFill(GBC.BOTH).setWeight(100, 100));
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
								scenes.loadFromFile(new JSONObject(
										readall(MainWindow.class.getResourceAsStream("/Maps/map5.json"))));
								scenes.gameStart();
							}
						});
						game.setVisible(true);

					} catch (Exception e) {
						// System.out.println(e.getMessage());
					}
				}
			});

			// TODO
			this.add(title, new GBC(0, 0).setAnchor(GBC.NORTH).setWeight(100, 100));
			this.add(level1, GBC.RELATIVE);
			this.add(level2, GBC.RELATIVE);
			this.add(level3, GBC.RELATIVE);
			this.add(level4, GBC.RELATIVE);
			this.add(level5, GBC.RELATIVE);
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
								scenes.gamePause();
							}
						});
						if(!isInitial)
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
						if(!isInitial)
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
						if(!isInitial)
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
						if(!isInitial)
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
							if(!isInitial)
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
						if(!isInitial)
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
		
		WinFinalPanel() {
			this.setLayout(new GridBagLayout());
			msg = new JTextArea(50, 100);
			msg.setText("YOU WIN");
			msg.setEditable(false);
			msg.setBackground(null);
			msg.setOpaque(false);
			msg.setFont(new Font("Serif", Font.BOLD, 50));
			
			this.add(msg, new GBC(0, 0));
		}
	}
}
