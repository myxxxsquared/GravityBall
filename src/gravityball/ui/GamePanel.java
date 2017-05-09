package gravityball.ui;

import com.jogamp.opengl.awt.GLJPanel;

import gravityball.game.*;

import com.jogamp.newt.Window;
import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseAdapter;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;

public class GamePanel extends GLJPanel implements GLEventListener{
	
	private Scenes gameScenes;
	
	public Scenes getGameScenes() {
		return gameScenes;
	}

	public void setGameScenes(Scenes gameScenes) {
		this.gameScenes = gameScenes;
	}

	public GamePanel(GLCapabilities caps) {
		super(caps);
		this.addGLEventListener(this);
	}
	
	@Override
	public void display(GLAutoDrawable arg0) {
		// TODO 绘制
			
		// TODO 这只是一个显示DEMO
		double vertexs[][] = {
				{1., 1., 1.},//0
				{1., 1., -1.},//1
				{1., -1., -1.},//2
				{1., -1., 1.},//3
				{-1., 1., 1.},//4
				{-1., 1., -1.},//5
				{-1., -1., -1.},//6
				{-1., -1., 1.}//7
		};
		double colors[][] = {
				{1., 0., 0.},
				{0., 1., 0.},
				{0., 0., 1.},
				{0., 0., 0.},
				{1., 1., 0.},
				{1., 0., 1.},
				{0., 1., 1.},
				{1., 1., 1.}
		};
		
		int shape[][] = {
				{0,1,2,3},
				{4,5,6,7},
				{0,1,5,4},
				{3,2,6,7},
				{0,3,7,4},
				{1,5,6,2}
		};
		
		GL2 gl2 = arg0.getGL().getGL2();
		
		gl2.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl2.glEnable(GL2.GL_DEPTH_TEST);
		

		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glLoadIdentity();
		
		
		gl2.glBegin(GL2.GL_QUADS);
		for (int[]s : shape) {
			for(int i1 : s)
			{
				gl2.glColor3d(colors[i1][0], colors[i1][1], colors[i1][2]);
				gl2.glVertex3d(0.3*vertexs[i1][0], 0.3*vertexs[i1][1], 0.3*vertexs[i1][2]);
			}
		}
		gl2.glEnd();
		
		gl2.glFlush();
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable arg0) {
		GL2 gl2 = arg0.getGL().getGL2();
		gl2.glEnable(GL2.GL_MULTISAMPLE);
		gl2.glEnable(GL2.GL_DEPTH_TEST);
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		
	}
	
	class ThisKeyAdapter extends KeyAdapter
	{

		@Override
		public void keyPressed(KeyEvent arg0) {
			// TODO Auto-generated method stub
		}
		
	}
	
	class ThisMouseAdapter extends MouseAdapter
	{

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseMoved(MouseEvent arg0) {
			// TODO Auto-generated method stub
		}
		
	}
}
