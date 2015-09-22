import com.datumbox.opensource.ai.*;
import com.datumbox.opensource.dataobjects.Direction;
import com.datumbox.opensource.game.Board;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import javax.swing.*;
import javax.swing.Timer;
import java.util.Random;

public class Canvas extends JPanel
{
	Random rand;
	int grid[][] = new int[4][4];
	int canvasSize = 400;
	int gridSize = 100;
	AIsolver aisolver = new AIsolver();
	Direction hintdir;

	public Canvas()
	{
		for(int i = 0; i < 4; i++)
		{
			for(int j = 0; j < 4; j++)
				grid[i][j] = 0;
		}
		rand = new Random();
		addSquare();
		setBackground(Color.YELLOW);
		setSize(new Dimension(400,400));
		hintdir = findBestMove();
		repaint();

		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e)
			{
				boolean moved = false;
				if(e.getKeyCode() == KeyEvent.VK_LEFT) /*Left*/
				{
					moved = moveLeft();
				}
				else if(e.getKeyCode() == KeyEvent.VK_UP) /*Up*/
				{
					moved = moveUp();
				}
				else if(e.getKeyCode() == KeyEvent.VK_RIGHT) /*Right*/
				{
					moved = moveRight();
				}
				else if(e.getKeyCode() == KeyEvent.VK_DOWN) /*Down*/
				{
					moved = moveDown();
				}
				else if(e.getKeyCode() == KeyEvent.VK_SPACE)
				{
					if(hintdir == Direction.LEFT)
						moved = moveLeft();
					else if(hintdir == Direction.RIGHT)
						moved = moveRight();
					else if(hintdir == Direction.UP)
						moved = moveUp();
					else
						moved = moveDown();
				}
				if(moved)
				{
					addSquare();
				}
				repaint();
				if(!canMove())
				{
					restart();
				}
				hintdir = findBestMove();
				repaint();
			}
		});
	}

	public void restart()
	{
		JOptionPane.showMessageDialog(null,"Restart");
		for(int i = 0; i < 4; i++)
		{
			for(int j = 0; j < 4; j++)
				grid[i][j] = 0;
		}
		addSquare();
	}

	private boolean addSquare()
	{
		ArrayList<Point> points = new ArrayList();
		for(int i = 0; i < 4; i++)
		{
			for(int j = 0; j < 4; j++)
			{
				if(grid[i][j] == 0)
					points.add(new Point(i,j));
			}
		}
		if(points.size() == 0) return false;
		int k = rand.nextInt(points.size());
		int x,y;
		x = points.get(k).x;
		y = points.get(k).y;
		int p = rand.nextInt(10);// Generate 2 with 0.9 probability 
		if(p > 0) grid[x][y] = 2;
		else grid[x][y] = 4;
		return true;
	}

	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		g.setColor(Color.YELLOW);
		g.fillRect(0,0,canvasSize,canvasSize);
		g.setColor(Color.DARK_GRAY);
		for(int i = 0; i <= 4; i++)
		{
			g.drawLine(0,i*gridSize,canvasSize,i*gridSize);
			g.drawLine(i*gridSize,0,i*gridSize,canvasSize);
		}
		g.setColor(Color.BLUE);
		for(int i = 0; i < 4; i++)
		{
			for(int j = 0; j < 4; j++)
			{
				if(grid[i][j] == 0) continue;
				int x = i*gridSize, y = j*gridSize;
				int pad = gridSize / 8;
				int side = gridSize - 2 * pad;
				g.setColor(Color.BLUE);
				g.fillRect(x+pad, y+pad, side, side);
				g.setColor(Color.WHITE);
				String s = Integer.toString(grid[i][j]);
				int fontSize = (int)(48.0 / Math.sqrt((double)s.length()));
				g.setFont(new Font("TimesRoman",Font.PLAIN, fontSize));
				g.drawString(s,x+gridSize/2-fontSize/3*s.length(),
							   y+gridSize/2+fontSize/2);
			}
		}
		if(hintdir == Direction.UP)    g2d.rotate(Math.toRadians(90),20,20);
		if(hintdir == Direction.DOWN)  g2d.rotate(Math.toRadians(-90),20,20);
		if(hintdir == Direction.RIGHT) g2d.rotate(Math.toRadians(180),20,20);
		g2d.setColor(Color.RED);
		int[] xpoints = {20,20,0};
		int[] ypoints = {0,40,20};
		g2d.fillPolygon(xpoints,ypoints,3);
		if(hintdir == Direction.UP)    g2d.rotate(-Math.toRadians(90),20,20);
		if(hintdir == Direction.DOWN)  g2d.rotate(-Math.toRadians(-90),20,20);
		if(hintdir == Direction.RIGHT) g2d.rotate(-Math.toRadians(180),20,20);

	}

	public boolean canMove()
	{
		boolean moved = false;
		moved |= canmoveLeft();
		moved |= canmoveRight();
		moved |= canmoveUp();
		moved |= canmoveDown();
		return moved;
	}
	public boolean canmoveLeft()
	{
		boolean moved = false;
		for(int j = 0; j < 4; j++)
		{
			int array[] = new int[4];
			for(int i = 0; i < 4; i++)
				array[i] = grid[i][j];
			moved |= fallDown(array);
		}
		return moved;
	}
	public boolean canmoveRight()
	{
		boolean moved = false;
		for(int j = 0; j < 4; j++)
		{
			int array[] = new int[4];
			for(int i = 0; i < 4; i++)
				array[i] = grid[3-i][j];
			moved |= fallDown(array);
		}
		return moved;
	}
	public boolean canmoveUp()
	{
		boolean moved = false;
		for(int i = 0; i < 4; i++)
		{
			int array[] = new int[4];
			for(int j = 0; j < 4; j++)
				array[j] = grid[i][j];
			moved |= fallDown(array);
		}
		return moved;
	}

	public boolean canmoveDown()
	{
		boolean moved = false;
		for(int i = 0; i < 4; i++)
		{
			int array[] = new int[4];
			for(int j = 0; j < 4; j++)
				array[j] = grid[i][3-j];
			moved |= fallDown(array);
		}
		return moved;
	}

	public boolean fallDown(int array[])
	{
		boolean moved = false;
		for(int i = 1; i < 4; i++)
		{
			if(array[i] == 0) continue;
			int k;
			for(k = i-1; k >= 0; k--)
			{
				if(array[k] > 0) break;
			}
			if(k >= 0)
			{
				if(array[k] == array[i])
				{
					array[i] = 0;
					array[k] *= 2;
					moved = true;
				}
				else
				{
					if(k < i-1)
					{
						array[k+1] = array[i];
						array[i] = 0;
						moved = true;
					}
				}
			}
			else
			{
				array[0] = array[i];
				array[i] = 0;
				moved = true;
			}
		}
		return moved;
	}

	public boolean moveLeft()
	{
		/*
		boolean moved = false;
		for(int j = 0; j < 4; j++)
		{
			int array[] = new int[4];
			for(int i = 0; i < 4; i++)
				array[i] = grid[i][j];
			moved |= fallDown(array);
			for(int i = 0; i < 4; i++)
				grid[i][j] = array[i];
		}
		return moved;
		*/
		try
		{
			Board board = new Board();
			for(int i = 0; i < 4; i++)
				for(int j = 0; j < 4; j++)
					board.setCell(i,j,grid[j][i]);
			Board nboard = (Board)board.clone();
			nboard.move(Direction.LEFT);
			int[][] boardArray = nboard.getBoardArray();
			for(int i = 0; i < 4; i++)
				for(int j = 0; j < 4; j++)
					grid[j][i] = boardArray[i][j];
			return !board.isEqual(board.getBoardArray(),nboard.getBoardArray());
		}
		catch(CloneNotSupportedException e)
		{
		}
		return false;
	}
	public boolean moveRight()
	{
		/*
		boolean moved = false;
		for(int j = 0; j < 4; j++)
		{
			int array[] = new int[4];
			for(int i = 0; i < 4; i++)
				array[i] = grid[3-i][j];
			moved |= fallDown(array);
			for(int i = 0; i < 4; i++)
				grid[i][j] = array[3-i];
		}
		return moved;
		*/
		try
		{
			Board board = new Board();
			for(int i = 0; i < 4; i++)
				for(int j = 0; j < 4; j++)
					board.setCell(i,j,grid[j][i]);
			Board nboard = (Board)board.clone();
			nboard.move(Direction.RIGHT);
			int[][] boardArray = nboard.getBoardArray();
			for(int i = 0; i < 4; i++)
				for(int j = 0; j < 4; j++)
					grid[j][i] = boardArray[i][j];
			return !board.isEqual(board.getBoardArray(),nboard.getBoardArray());
		}
		catch(CloneNotSupportedException e)
		{
		}
		return false;
	}
	public boolean moveUp()
	{
		/*
		boolean moved = false;
		for(int i = 0; i < 4; i++)
		{
			int array[] = new int[4];
			for(int j = 0; j < 4; j++)
				array[j] = grid[i][j];
			moved |= fallDown(array);
			for(int j = 0; j < 4; j++)
				grid[i][j] = array[j];
		}
		return moved;
		*/
		try
		{
			Board board = new Board();
			for(int i = 0; i < 4; i++)
				for(int j = 0; j < 4; j++)
					board.setCell(i,j,grid[j][i]);
			Board nboard = (Board)board.clone();
			nboard.move(Direction.UP);
			int[][] boardArray = nboard.getBoardArray();
			for(int i = 0; i < 4; i++)
				for(int j = 0; j < 4; j++)
					grid[j][i] = boardArray[i][j];
			return !board.isEqual(board.getBoardArray(),nboard.getBoardArray());
		}
		catch(CloneNotSupportedException e)
		{
		}
		return false;
	}

	public boolean moveDown()
	{
		/*
		boolean moved = false;
		for(int i = 0; i < 4; i++)
		{
			int array[] = new int[4];
			for(int j = 0; j < 4; j++)
				array[j] = grid[i][3-j];
			moved |= fallDown(array);
			for(int j = 0; j < 4; j++)
				grid[i][j] = array[3-j];
		}
		return moved;
		*/
		try
		{
			Board board = new Board();
			for(int i = 0; i < 4; i++)
				for(int j = 0; j < 4; j++)
					board.setCell(i,j,grid[j][i]);
			Board nboard = (Board)board.clone();
			nboard.move(Direction.DOWN);
			int[][] boardArray = nboard.getBoardArray();
			for(int i = 0; i < 4; i++)
				for(int j = 0; j < 4; j++)
					grid[j][i] = boardArray[i][j];
			return !board.isEqual(board.getBoardArray(),nboard.getBoardArray());
		}
		catch(CloneNotSupportedException e)
		{
		}
		return false;
	}

	public Direction findBestMove()
	{
		Board board = new Board();
		for(int i = 0; i < 4; i++)
			for(int j = 0; j < 4; j++)
				board.setCell(i,j,grid[j][i]);
		try
		{
			Direction dir = aisolver.findBestMove(board,8);
			return dir;
		}
		catch(CloneNotSupportedException e)
		{
		}
		return Direction.UP;
	}
}
