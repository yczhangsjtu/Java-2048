import java.awt.*;
import javax.swing.*;

public class MainClass extends JFrame
{
	Canvas canvas;
	public static void main(String[] args)
	{
		new MainClass("2048");
	}

	public MainClass(String s)
	{
		super(s);
		canvas = new Canvas();
		canvas.setFocusable(true);
		
		add(canvas);
		setLayout(new BorderLayout());
		setLocation(400, 200);
		setSize(400,400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
	}
}
