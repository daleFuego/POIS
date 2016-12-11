package utils;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Toast extends JFrame {

	public Toast(String iconPath, String msg) {
		setResizable(false);
		setTitle("BuildFarm");
		setSize(566, 158);

		setBackground(Color.WHITE);
		getContentPane().setLayout(null);
		setVisible(true);

		ImageIcon icon = new ImageIcon(iconPath);
		JLabel lblIcon = new JLabel("BuildFarm");
		lblIcon.setBounds(10, 15, 100, 100);

		JLabel lblMessage = new JLabel("BuildFarm");
		lblMessage.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
		lblMessage.setBounds(120, 48, 430, 33);

		getContentPane().add(lblMessage);
		getContentPane().add(lblIcon);
	}
}
