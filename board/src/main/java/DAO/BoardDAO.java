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
	public ArrayList<Board> getList() throws Exception {
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
	public Board getView(int board_no) throws Exception {
		Connection conn = open();
		Board b = new Board();
		
		String sql = "select board_no, title, user_id, to_char(reg_date, 'yyyy.mm.dd') reg_date, views, content, img from board where board_no = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, board_no);
		ResultSet rs = pstmt.executeQuery();	
		
		try(conn; pstmt; rs){
			while(rs.next()) {
				b.setBoard_no(rs.getInt("board_no"));
				b.setTitle(rs.getString("title"));
				b.setUser_id(rs.getString("user_id"));
				b.setReg_date(rs.getString("reg_date"));
				b.setViews(rs.getInt("views"));
				b.setContent(rs.getString("content"));
				b.setImg(rs.getString("img"));
			}
			return b;
		}
	}
	
	//조회수 증가(메소드)
	public void updateViews(int board_no) throws Exception {
		Connection conn = open();

		String sql =  "update board set views = (views + 1) where board_no = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		
		try(conn; pstmt){
			pstmt.setInt(1, board_no);   //매개변수를 int board_no 받아와서 바로 board_no 넣어줌
			pstmt.executeUpdate();   // executeUpdate(); 넣어줘야 실행됨
		}		
	}
	
	//게시글 등록(메소드)
	public void insertBoard(Board b) throws Exception {
		Connection conn = open();
		String sql = "insert into board(board_no, user_id, title, content, reg_date, views, img) values(BOARD_SEQ.nextval, ?, ?, ?, sysdate, 0, ?)";		
		PreparedStatement pstmt = conn.prepareStatement(sql);
		
		try(conn; pstmt){
			pstmt.setString(1, b.getUser_id());   //매개변수를 Board b 받아와서 b.get... 으로 넣어줌
			pstmt.setString(2, b.getTitle());
			pstmt.setString(3, b.getContent());
			pstmt.setString(4, b.getImg());
			
			pstmt.executeUpdate(); //실행구문
		}
		
	}
	
	//게시글 수정화면 보여주기(메소드)   getView 메소드와 같음 오류방지 때문에 나눠줌
	public Board getViewForEdit(int board_no) throws Exception {	
		Connection conn = open();
		Board b = new Board();
		
		String sql = "select board_no, title, user_id, to_char(reg_date, 'yyyy.mm.dd') reg_date, views, content, img from board where board_no = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, board_no);
		ResultSet rs = pstmt.executeQuery();
		
		try(conn; pstmt; rs){
			while(rs.next()) {
				b.setBoard_no(rs.getInt("board_no"));
				b.setTitle(rs.getString("title"));
				b.setUser_id(rs.getString("user_id"));
				b.setReg_date(rs.getString("reg_date"));
				b.setViews(rs.getInt("views"));
				b.setContent(rs.getString("content"));
				b.setImg(rs.getString("img"));				
			}
			return b;
		}		
	}
	
	//게시글 수정하기(메소드)
	public void updateBoard(Board b) throws Exception {
		Connection conn = open();
		String sql = "update board set title =?, user_id=?, content=?, img=? where board_no =?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		
		
		try(conn; pstmt){
			
			pstmt.setString(1, b.getTitle());
			pstmt.setString(2, b.getUser_id());
			pstmt.setString(3, b.getContent());
			pstmt.setString(4, b.getImg());
			pstmt.setInt(5, b.getBoard_no());
			
			
			//pstmt.executeUpdate(); 업데이트 실행구문이지만  board_no 찾지 못할 경우 수정이 안될 수 있음 수정이 안될 것을 방지 하기 위해 작성
			
			//수정된 글이 없을 경우
			if(pstmt.executeUpdate() != 1) {  
				 throw new Exception("수정된 글이 없습니다.");
			}
		}		
	}
	
	//게시글 삭제(메소드)
	public void deleteBoard(int board_no) throws Exception {
		Connection conn = open();
		String sql = "delete from board where board_no = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		
		try(conn; pstmt){
			pstmt.setInt(1, board_no);	
			
			//삭제된 글이 없을 경우
			if(pstmt.executeUpdate() != 1) {
				throw new Exception("삭제된 글이 없습니다.");
			}
		}
		
		
	}
	
	
}
