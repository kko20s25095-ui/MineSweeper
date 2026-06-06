package 지뢰게임;

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


public class MineSweeper extends JFrame {

	private JPanel contentPane;
	private JTextField txtSize;
	buttonCell[][] cells; //셀로 나누어서 지뢰찾기 버튼을 만드는 것(배열)
	int size, bomCnt; //폭탄의 개수 : bomCnt
	private JButton btnRun;
	private JPanel cp;
	

	public MineSweeper() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600, 600);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel jp = new JPanel();
		contentPane.add(jp);
		jp.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		jp.add(panel, BorderLayout.NORTH);
		
		txtSize = new JTextField();
		txtSize.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(txtSize);
		txtSize.setColumns(10);
		
		btnRun = new JButton("\uC2E4\uD589");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				size = Integer.parseInt(txtSize.getText()); //문자열은 객체이기 때문에 강제 형변환이 안된다.고로 Integer클래스의 파스를 가져옴
				JOptionPane.showMessageDialog(null, size + " x " + size + "지뢰 찾기 생성 완료"); //나중에 지울 것
				
				cp.setLayout(new GridLayout(0, size));
				
				cells = new buttonCell[size][size]; 
				cp.removeAll(); //새로 지뢰찾기를 만들 때 예전 지뢰가 남지 않도록 해서 새로우 지뢰를 다시 만들도록 함
				
				
				
				//지뢰판 생성
				for (int i = 0; i < size; i++) {
					for (int j = 0; j < size; j++) {
						cells[i][j] = new buttonCell(i, j);
						cp.add(cells[i][j]);
					}
				}
				cp.repaint();
				cp.revalidate();
				
				//지뢰 설치 : 난수 => 갯수나 위치는 무작위 설치
				bomCnt = (int)(size*size*0.1);
				boolean[] nums = new boolean[size*size];
				for (int i = 0; i < bomCnt; i++) {
					// [수정] 줄 끝 유령 공백 제거
					nums[i] = true; 
				}
				
				//셔플 부분
				Random rnd = new Random();
				int a,b;
				boolean c;
				
				for (int i = 0; i < size*size*size; i++) {
					a = rnd.nextInt(size * size);
					b = rnd.nextInt(size * size);
					
					c = nums[a]; nums[a] = nums[b]; nums[b] = c;
				}
				
				for (int i = 0; i < size*size; i++) {
					cells[i/size][i%size].isBomb = nums[i];
//					if (nums[i] == true) {
//						cells[i/size][i%size].setText("\u004d"); //체크용
//					}
				}
				
				
				
				//셀마다 주변 8방의 지뢰 갯수 카운트
				
				
			}
		});
		panel.add(btnRun);
		
		cp = new JPanel();
		jp.add(cp, BorderLayout.CENTER);
		
		setVisible(true); 
	}
	
	class buttonCell extends JButton {
		int r,c;
		boolean isBomb; 
		boolean isClicked = false;
		boolean isRightClicked = false;
		boolean isOval = false;
		
		public buttonCell(int i, int j) { //버튼에 대한 정보를 입력받는 생성자
			r = i; 
			c = j;
			
			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					super.mousePressed(e);
					
					// 이미 클릭되어 열린 셀이거나 게임 오버 상태면 무시
					if (isClicked) return;
					
					if (e.getButton() == MouseEvent.BUTTON1) { // 좌클릭
						if (isRightClicked) return; 
						
						isClicked = true;
						repaint();
						
						if (isBomb) {
							JOptionPane.showMessageDialog(null, "지뢰를 밟으셨습니다. 게임 종료!");
							System.exit(0);
						}
					}
					else if (e.getButton() == MouseEvent.BUTTON3) { // 우클릭
						isRightClicked = !isRightClicked;
						repaint();
					}
				}
			});
		}
		
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			if (isClicked == false && isRightClicked == false) {
				return;
			}
			
			// 오른쪽 버튼을 누르면 깃발을 꽂는다. (회수하면 알아서 안 그려짐)
			if (isRightClicked) {
				this.setBorderPainted(true);
				this.setContentAreaFilled(true);
				this.setEnabled(true);
				
				g.setColor(Color.RED);
				g.setFont(new Font("Wingdings", 0, 30));
				g.drawString("\uf50d", 15, 35);
			}
			
			// 폭탄이 터졌을 경우 (좌클릭 시 작동)
			if (isClicked == true && isBomb == true) {
				
				this.setBorderPainted(false);
				this.setContentAreaFilled(false);
				this.setEnabled(false);
				isOval = true;
				
				g.setColor(Color.BLACK);
				g.setFont(new Font("Wingdings", 0, 30));
				g.drawString("\uf04d", 15, 35);
				
			}
			
			
		}
	}

}