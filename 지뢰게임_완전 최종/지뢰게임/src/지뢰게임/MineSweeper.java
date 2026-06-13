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
import java.util.LinkedList;
import java.util.Queue;
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
	
	// ★ 알고리즘 반영: 큐(Queue)와 연결리스트(LinkedList)를 활용한 주변 빈칸 BFS 자동 확장 함수
	public void openCellsBFS(int startR, int startC) {
		Queue<int[]> queue = new LinkedList<>();
		queue.add(new int[]{startR, startC});
		
		while (!queue.isEmpty()) {
			int[] curr = queue.poll();
			int r = curr[0];
			int c = curr[1];
			
			int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
			int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};
			
			for (int d = 0; d < 8; d++) {
				int nr = r + dr[d];
				int nc = c + dc[d];
				
				if (nr >= 0 && nr < size && nc >= 0 && nc < size) {
					ButtonCell neighbor = cells[nr][nc];
					
					if (!neighbor.isClicked && !neighbor.isRightClicked && !neighbor.isBomb) {
						neighbor.isClicked = true;
						neighbor.setBorderPainted(false);
						neighbor.setContentAreaFilled(false);
						neighbor.setEnabled(false);
						
						if (neighbor.surBomCnt == 0) {
							queue.add(new int[]{nr, nc});
						}
					}
				}
			}
		}
		cp.repaint();
	}
	
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
			
				for (int i = 0; i < size*size; i++) {
					cells[i/size][i%size].isBomb = nums[i];
				}
				
				//셀마다 주변 8방의 지뢰 갯수 카운트 
				for (int i = 0; i < size*size; i++) {
					int row = i/size , col = i % size;
					
					// 내가 지뢰가 아닐 경우에만 주변 지뢰 개수를 센다.
					if (cells[row][col].isBomb == false) { 
						
						try {
							// 1. 북서 (위 왼쪽)
							if (row - 1 >= 0 && col - 1 >= 0 && cells[row-1][col-1].isBomb) cells[row][col].surBomCnt++;

							// 2. 북 (위)
							if (row - 1 >= 0 && cells[row-1][col].isBomb) cells[row][col].surBomCnt++;

							// 3. 북동 (위 오른쪽)
							if (row - 1 >= 0 && col + 1 < size && cells[row-1][col+1].isBomb) cells[row][col].surBomCnt++;

							// 4. 서 (왼쪽)
							if (col - 1 >= 0 && cells[row][col-1].isBomb) cells[row][col].surBomCnt++;

							// 5. 동 (오른쪽)
							if (col + 1 < size && cells[row][col+1].isBomb) cells[row][col].surBomCnt++;

							// 6. 남서 (아래 왼쪽)
							if (row + 1 < size && col - 1 >= 0 && cells[row+1][col-1].isBomb) cells[row][col].surBomCnt++;

							// 7. 남 (아래)
							if (row + 1 < size && cells[row+1][col].isBomb) cells[row][col].surBomCnt++;

							// 8. 남동 (아래 오른쪽)
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
					if(e.getButton()==MouseEvent.BUTTON1 && !isRightClicked && !isClicked) {
						isClicked = true;
						
						setBorderPainted(false);
						setContentAreaFilled(false);
						setEnabled(false);
						
						// ★ 알고리즘 반영: 지뢰 클릭 시 2차원 배열 선형 탐색을 돌며 전체 칸 일제히 오픈
						if (isBomb == true) { 
							for (int r = 0; r < size; r++) {
								for (int c = 0; c < size; c++) {
									MineSweeper.this.cells[r][c].isClicked = true;
									MineSweeper.this.cells[r][c].setBorderPainted(false);
									MineSweeper.this.cells[r][c].setContentAreaFilled(false);
									MineSweeper.this.cells[r][c].setEnabled(false);
								}
							}
							
							MineSweeper.this.cp.repaint(); 
							
							JOptionPane.showMessageDialog(null, "지뢰를 밟았어요. 게임 끝!");
							isOver = true;
							MineSweeper.this.dispose(); 
							return;
						}
						// ★ 알고리즘 반영: 클릭한 칸의 주변 지뢰가 0개인 경우 BFS 탐색 연쇄 확장 가동
						else if (surBomCnt == 0) {
							MineSweeper.this.openCellsBFS(r, c);
						}
					}
					else if(e.getButton()==MouseEvent.BUTTON3) {
						if(!isClicked) {
							isRightClicked = !isRightClicked;
							
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
			
			//클릭함. 지뢰가 없음. (상태 변경 코드 제거, 단순 그리기 영역으로 격하)
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