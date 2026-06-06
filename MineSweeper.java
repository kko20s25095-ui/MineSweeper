package 지뢰게임;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
				
				// [수정] 줄 끝에 있던 유령 특수문자 공백을 제거했습니다.
				cells = new buttonCell[size][size]; 
				cp.removeAll(); //새로 지뢰찾기를 만들 때 예전 지뢰가 남지 않도록 해서 새로우 지뢰를 다시 만들도록 함
				
				
				
				//지뢰판 생성
				for (int i = 0; i < size; i++) {
					for (int j = 0; j < size; j++) {
						cells[i][j] = new buttonCell(i, j);
						cp.add(cells[i][j]);
						
						//모든 셀 이벤트
						cells[i][j].addActionListener(new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								// TODO Auto-generated method stub
								int r,c;
								buttonCell jb = (buttonCell)e.getSource();
								r = jb.r;
								c = jb.c;
								
								JOptionPane.showMessageDialog(null, "[" + (r+1) + "행, " + (c+1) + "열] " + "클릭");
							}
						});
					}
				}
				cp.repaint();
				cp.revalidate();
				
				//지뢰 설치 : 난수 => 갯수나 위치는 무작위 설치
				bomCnt = (int)(size*size*0.1);
				boolean[] nums = new boolean[size*size];
				for (int i = 0; i < bomCnt; i++) {
					// [수정] boolean 배열에는 숫자 1 대신 true를 대입해야 합니다.
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
					if (nums[i] == true) {
						cells[i/size][i%size].setText("지뢰");
					}
				}
				
				
				
				//셀마다 주변 8방의 지뢰 갯수 카운트
				
				
			}
		});
		panel.add(btnRun);
		
		cp = new JPanel();
		jp.add(cp, BorderLayout.CENTER);
		
		// [수정] 줄 끝에 있던 유령 특수문자 공백을 제거했습니다.
		setVisible(true); 
	}
	
	class buttonCell extends JButton {
		// [수정] 변수명 뒤 유령 공백 제거 및 외부에서 접근할 지뢰 여부 변수(isBomb)를 클래스 멤버 변수로 올바르게 선언했습니다.
		int r,c;
		boolean isBomb; 
		
		public buttonCell(int i, int j) { //버튼에 대한 정보를 입력받는 생성자
			// [수정] 줄 끝 유령 공백 제거
			r = i; 
			c = j;
		}
	}

}