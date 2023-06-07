package DAO;

import java.sql.*;
import java.util.ArrayList;

import DTO.Board;

public class BoardDAO {
	PreparedStatement pstmt;  //쿼리문 실행
	
	final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
	final String JDBC_URL = "jdbc:oracle:thin:@localhost:1521:xe";
	
	public Connection open() {
		Connection conn = null;  //데이터베이스 연결 담당
		
		try {
			Class.forName(JDBC_DRIVER);	//드라이버로드
			conn = DriverManager.getConnection(JDBC_URL, "test", "test1234"); //DB연결
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return conn;
	}
	
	
	//게시판 리스트 가져오기(메소드)
	public ArrayList<Board> getList() throws SQLException {
		//open(); 소스 작성
		Connection conn = open();

		//Board 객체를 저장할 ArrayList
		ArrayList<Board> boardList = new ArrayList<>();

		String sql = "select board_no, title, user_id, to_char(reg_date, 'yyyy.mm.dd') reg_date, views from board";
		PreparedStatement pstmt = conn.prepareStatement(sql); //쿼리문 등록
		ResultSet rs = pstmt.executeQuery(); //쿼리문 실행
		
		   // ↓ try/catch 문에서 한번에 close(); 실행 : 자동리소스
		try(conn; pstmt; rs) {
			while(rs.next()) {  // 1라인 씩 데이터를 읽어 온다.
				Board b = new Board();
				
				b.setBoard_no(rs.getInt("board_no"));
				b.setTitle(rs.getString("title"));
				b.setUser_id(rs.getString("user_id"));
				b.setReg_date(rs.getString("reg_date"));
				b.setViews(rs.getInt("views"));
				
				boardList.add(b);
			}
			
			return boardList;
		} 
		
		
	}	

	//게시물 내용 가져오기(메소드)
	//public Board getView(int board_no) {	}
	
	//조회수 증가(메소드)
	public void updateViews(int board_no) {
		
	}
	
	//게시글 등록(메소드)
	public void insertBoard(Board b) {
		
	}
	
	//게시글 수정화면 보여주기(메소드)
	//public Board getViewForEdit(int board_no) {	}
	
	//게시글 수정하기(메소드)
	public void updateBoard(Board b) {
		
	}
	
	//게시글 삭제(메소드)
	public void deleteBoard(int board_no) {
		
	}
	
	
}
