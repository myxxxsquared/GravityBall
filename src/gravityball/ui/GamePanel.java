package gravityball.ui;

import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;

import gravityball.game.*;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.FloatBuffer;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;

public class GamePanel extends GLJPanel implements GLEventListener{
	
	//构造
	public GamePanel(GLCapabilities caps) {
		super(caps);
		this.addGLEventListener(this);
		this.addKeyListener(new ThisKeyAdapter());
		ThisMouseAdapter thisMouseAdapter = new ThisMouseAdapter();
		this.addMouseListener(thisMouseAdapter);
		this.addMouseMotionListener(thisMouseAdapter);
		
		this.gameScenes = new Scenes();
	}
	
	private Scenes gameScenes;
	public Scenes getGameScenes() { return gameScenes; }
	public void setGameScenes(Scenes gameScenes) { this.gameScenes = gameScenes; }
	
	private GLU glu;
	private GLUT glut;
	
	@Override
	public void display(GLAutoDrawable arg0) {
		GL2 gl2 = arg0.getGL().getGL2();
		
		//摄像机位置
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();
		glu.gluPerspective(45.f, (double)getWidth()/(double)getHeight(), 0.1, 10);
		glu.gluLookAt(0., -1., 1., 0., 0., 0., 0., -1., 2.);
		
		//ZBuffer
		gl2.glEnable(GL2.GL_DEPTH_TEST);
		
		//灯光
		gl2.glEnable(GL2.GL_LIGHTING);
		gl2.glEnable(GL2.GL_LIGHT0);
		gl2.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, FloatBuffer.wrap(new float[]{0.f, 1.f, 1.f}));
		gl2.glEnable(GL2.GL_COLOR_MATERIAL);
		gl2.glLightModeli(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE);
		
		//清空
		gl2.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		
		gameScenes.paint(gl2);

		// TODO 这只是一个显示DEMO
//		double vertexs[][] = {
//				{1., 1., 1.},//0
//				{1., 1., -1.},//1
//				{1., -1., -1.},//2
//				{1., -1., 1.},//3
//				{-1., 1., 1.},//4
//				{-1., 1., -1.},//5
//				{-1., -1., -1.},//6
//				{-1., -1., 1.}//7
//		};
//		double colors[][] = {
//				{1., 0., 0.},
//				{0., 1., 0.},
//				{0., 0., 1.},
//				{1., 1., 0.},
//				{1., 0., 1.},
//				{0., 1., 1.},
//		};
//		
//		double normal[][] = {
//				{1., 0., 0.},
//				{-1., 0., 0.},
//				{0., 1., 0.},
//				{0., -1., 0.},
//				{0., 0., 1.},
//				{0., 0., -1.},
//		};
//		
//		int shape[][] = {
//				{0,1,2,3},
//				{4,5,6,7},
//				{0,1,5,4},
//				{3,2,6,7},
//				{0,3,7,4},
//				{1,5,6,2}
//		};
//		
//		
//		
//		
//		
//
//		gl2.glMatrixMode(GL2.GL_MODELVIEW);
//		gl2.glLoadIdentity();
//		
//		
//		gl2.glBegin(GL2.GL_QUADS);
//		int k = 0;
//		for (int[]s : shape) {
//			gl2.glNormal3d(normal[k][0], normal[k][1], normal[k][2]);
//			gl2.glColor3d(colors[k][0], colors[k][1], colors[k][2]);
//			k++;
//			for(int i1 : s)
//			{
//				gl2.glVertex3d(0.3*vertexs[i1][0], 0.3*vertexs[i1][1], 0.3*vertexs[i1][2]);
//			}
//		}
//		gl2.glEnd();
//		
//		gl2.glFlush();
	}

	@Override
	public void dispose(GLAutoDrawable arg0) { }

	@Override
	public void init(GLAutoDrawable arg0) {
		GL2 gl2 = arg0.getGL().getGL2();
		gl2.glEnable(GL2.GL_MULTISAMPLE);
		gl2.glEnable(GL2.GL_DEPTH_TEST);
		this.glu = new GLU();
		this.glut = new GLUT();
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) { }
	
	class ThisKeyAdapter extends KeyAdapter
	{
		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			super.keyTyped(e);
		}
	}
	
	class ThisMouseAdapter extends MouseAdapter
	{

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			super.mouseClicked(e);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			super.mousePressed(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			super.mouseReleased(e);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			super.mouseMoved(e);
		}
		
	}
}
