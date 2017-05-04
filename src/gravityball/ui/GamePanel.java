package gravityball.ui;

import com.jogamp.opengl.awt.GLJPanel;

import gravityball.game.*;

import com.jogamp.newt.Window;
import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseAdapter;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

public class GamePanel extends GLJPanel implements GLEventListener{
	
	private Scenes gameScenes;
	
	public Scenes getGameScenes() {
		return gameScenes;
	}

	public void setGameScenes(Scenes gameScenes) {
		this.gameScenes = gameScenes;
	}

	public GamePanel() {
		this.addGLEventListener(this);
	}
	
	@Override
	public void display(GLAutoDrawable arg0) {
		// TODO 绘制
		
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable arg0) {
		Window window = (Window)arg0;
		window.addMouseListener(new ThisMouseAdapter());
		window.addKeyListener(new ThisKeyAdapter());
		
		// TODO Auto-generated method stub
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
