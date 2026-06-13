package 지뢰찾기;

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
	int size, bombCnt; //bomCnt는 지뢰를 매설할 개수임
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
				cells = new ButtonCell[size][size]; //모든 버튼을 제어하는 변수
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
			
				// 1차적으로 모든 셀에 지뢰 상태를 먼저 주입합니다.
				for (int i = 0; i < size*size; i++) {
					cells[i/size][i%size].isBomb = nums[i];
				}
				
				// 검증 및 수정 완료: 개별 try-catch 구조를 통해 8방향을 독립적으로 완벽히 카운트한다.
				for (int i = 0; i < size*size; i++) {
					int row = i/size , col = i % size;
					
					// 내가 지뢰가 아닐 경우에만 주변 지뢰 개수를 센다.
					if (cells[row][col].isBomb == false) { 
						
						// 1. 북서 (위 왼쪽)
						try {
							if (row - 1 >= 0 && col - 1 >= 0 && cells[row-1][col-1].isBomb) cells[row][col].surBomCnt++;
						} catch (Exception ex) {}

						// 2. 북 (위)
						try {
							if (row - 1 >= 0 && cells[row-1][col].isBomb) cells[row][col].surBomCnt++;
						} catch (Exception ex) {}

						// 3. 북동 (위 오른쪽)
						try {
							if (row - 1 >= 0 && col + 1 < size && cells[row-1][col+1].isBomb) cells[row][col].surBomCnt++;
						} catch (Exception ex) {}

						// 4. 서 (왼쪽)
						try {
							if (col - 1 >= 0 && cells[row][col-1].isBomb) cells[row][col].surBomCnt++;
						} catch (Exception ex) {}

						// 5. 동 (오른쪽)
						try {
							if (col + 1 < size && cells[row][col+1].isBomb) cells[row][col].surBomCnt++;
						} catch (Exception ex) {}

						// 6. 남서 (아래 왼쪽)
						try {
							if (row + 1 < size && col - 1 >= 0 && cells[row+1][col-1].isBomb) cells[row][col].surBomCnt++;
						} catch (Exception ex) {}

						// 7. 남 (아래)
						try {
							if (row + 1 < size && cells[row+1][col].isBomb) cells[row][col].surBomCnt++;
						} catch (Exception ex) {}

						// 8. 남동 (아래 오른쪽)
						try {
							if (row + 1 < size && col + 1 < size && cells[row+1][col+1].isBomb) cells[row][col].surBomCnt++;
						} catch (Exception ex) {}
					}
				}
			}
		});
		np.add(btnRun);
		
		cp = new JPanel();
		jp.add(cp, BorderLayout.CENTER);
		
		setVisible(true);
	}

	class ButtonCell extends JButton {
		int surBomCnt; //주변에 폭탄이 몇 개인지 표시하는 변수
		int r, c;
		boolean isBomb, isClicked, isRightClicked;
		
		public ButtonCell(int i, int j) {
			r=i; c=j;
			
			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					// 깃발(우클릭)이 꽂혀있지 않고, 이미 클릭된 상태가 아닐 때만 좌클릭 작동
					if(e.getButton()==MouseEvent.BUTTON1 && !isRightClicked && !isClicked) {
						isClicked = true;
						
						// 무한 루프 방지를 위해 paintComponent에 있던 버튼 상태 변경 코드를 이쪽으로 이동
						setBorderPainted(false);
						setContentAreaFilled(false);
						setEnabled(false);
						
						if (isBomb == true) { 
							repaint(); // 지뢰 아이콘을 그리도록 즉시 리페인트 호출
							JOptionPane.showMessageDialog(null, "지뢰를 밟았어요. 게임 끝!");
							isOver = true;
							return;
						}
					}
					else if(e.getButton()==MouseEvent.BUTTON3) {
						if(!isClicked) {
							isRightClicked = !isRightClicked;
							
							// 우클릭 상태에 따른 버튼 속성을 여기서 한 번만 제어 (무한 루프 방지)
							if(isRightClicked) {
								setBorderPainted(true);
								setContentAreaFilled(true);
							}
						}
					}
						
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
				g.setColor(Color.RED);
				g.setFont(new Font("Wingdings", 0, 30));
				g.drawString("\uf050", 15, 35);
			} 
			
			// ★ 수정: 지뢰가 없는 칸을 클릭했을 때 주변 지뢰 개수가 1개 이상이면 파란색 숫자로 선명하게 그림
			if(isClicked==true && isBomb==false) {
				if (surBomCnt > 0) {
					g.setColor(Color.BLUE);
					g.setFont(new Font("맑은 고딕", Font.BOLD, 18));
					g.drawString(String.valueOf(surBomCnt), 20, 35);
				} else {
					g.drawString("", 15, 35);
				}
			}
			
			//클릭함. 지뢰가 있음. 게임 끝!
			if(isClicked==true && isBomb==true) {
				g.setColor(Color.RED);
				g.setFont(new Font("Wingdings", 0, 30));
				g.drawString("\uf04d", 15, 35);
			}
		}
	}
}