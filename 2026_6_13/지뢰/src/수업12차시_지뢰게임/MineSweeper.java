package 수업12차시_지뢰게임;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

class MineSweeper extends JFrame {
	JPanel jp;
	JTextField txtSize;
	ButtonCell[][] cells; 
	int size, bombCnt;
	JButton btnRun;
	JPanel cp;
	
	boolean isOver;
	
	public MineSweeper() {
		setTitle("\uC9C0\uB8B0\uAC8C\uC784");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 626, 583);
		jp = new JPanel();
		jp.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(jp);
		jp.setLayout(new BorderLayout(0, 0));
		
		JPanel np = new JPanel();
		jp.add(np, BorderLayout.NORTH);
		
		txtSize = new JTextField();
		txtSize.setHorizontalAlignment(SwingConstants.CENTER);
		np.add(txtSize);
		txtSize.setColumns(10);
		
		btnRun = new JButton("\uC2E4\uD589");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cp.removeAll();
				
				size = Integer.parseInt(txtSize.getText());
				cells = new ButtonCell[size][size];
				cp.setLayout(new GridLayout(0, size));
				
				//지뢰판 생성
				for (int i = 0; i < size; i++) {
					for (int j = 0; j < size; j++) {
						cells[i][j] = new ButtonCell(i,j);
						cp.add(cells[i][j]);
					}
				}
				
				cp.repaint();
				cp.revalidate();
				
				//지뢰 설치: 갯수나 위치는 무작위 설치
				bombCnt = (int)(size*size*0.1);
				
				boolean[] nums = new boolean[size*size];
				for (int i = 0; i < bombCnt; i++) {
					nums[i] = true;
				}

				Random rnd = new Random();
				int a, b;
				boolean c;
				for (int i = 0; i < size*size*size; i++) {
					a=rnd.nextInt(size*size);
					b=rnd.nextInt(size*size);
					c=nums[a]; nums[a]=nums[b]; nums[b]=c;
				}
			
				for (int i = 0; i < size*size; i++) {
					cells[i/size][i%size].isBomb = nums[i];
				}
				
				//셀마다 주변 8방의 지뢰 갯수 카운트 
			}
		});
		np.add(btnRun);
		
		cp = new JPanel();
		jp.add(cp, BorderLayout.CENTER);
		
		setVisible(true);
	}

	class ButtonCell extends JButton {
		int r, c;
		boolean isBomb, isClicked, isRightClicked;
		
		public ButtonCell(int i, int j) {
			r=i; c=j;
			
			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if(e.getButton()==MouseEvent.BUTTON1)
						isClicked = true;
					else if(e.getButton()==MouseEvent.BUTTON3)
						isRightClicked = !isRightClicked;
						
					repaint();
				}
			});
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			if(isClicked==false && isRightClicked==false) return;
			
			//오른쪽 버튼 클릭함: 깃발을 꽂거나 제거한다. 토글 기능
			if(isRightClicked) {
				this.setBorderPainted(true);
				this.setContentAreaFilled(true);
				this.setEnabled(true);
				
				g.setColor(Color.RED);
				g.setFont(new Font("Wingdings", 0, 30));
				g.drawString("\uf050", 15, 35);
			} else {
				this.setBorderPainted(true);
				this.setContentAreaFilled(true);
				this.setEnabled(true);
				
				g.drawString("", 15, 35);
			}
			
			//클릭함. 지뢰가 없음.
			if(isClicked==true && isBomb==false) {
				this.setBorderPainted(false);
				this.setContentAreaFilled(false);
				this.setEnabled(false);
				
				g.drawString("", 15, 35);
			}
			
			//클릭함. 지뢰가 있음. 게임 끝!
			if(isClicked==true && isBomb==true) {
				this.setBorderPainted(false);
				this.setContentAreaFilled(false);
				this.setEnabled(false);
				
				g.setColor(Color.RED);
				g.setFont(new Font("Wingdings", 0, 30));
				g.drawString("\uf04d", 15, 35);
				
				JOptionPane.showMessageDialog(null, "지뢰를 밟았어요. 게임 끝!");
			}
		}
	}
}
