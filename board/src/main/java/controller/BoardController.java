 package controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.security.auth.message.callback.PrivateKeyCallback.Request;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.beanutils.BeanUtils;

import DAO.BoardDAO;
import DTO.Board;


@WebServlet("/")   //업로드할 파일의 최대 크기            // 파일 업로드할 위치
@MultipartConfig(maxFileSize=1024*1024*2, location="c:/Temp/img")
public class BoardController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BoardDAO dao;  //model
    private ServletContext ctx;  //자원관리하는 역할, 페이지 이동하고 forward 하기 위해 사용한다. 
  
    public BoardController() {
        super();
    }

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		dao = new BoardDAO();
		ctx = getServletContext();  //ServletContext: 웹 어플리케이션 자원관리
	}

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");  //한글깨짐 방지
		String command = request.getServletPath(); //경로를 가지고 온다.
		String site = null;
		
		
		System.out.println("command" + command);
		
		// 1. 경로를 정해준다.(라우팅)
		switch (command) {
			case "/list" : site = getList(request); break;	// => getList 메소드 만들기
			case "/view" : site = getView(request); break;
			case "/write" : site = "write.jsp"; break;		//단순하게 글쓰는 화면 보여줌
			case "/insert" : site = insertBoard(request); break;    //실질적으로 글등록 해주는 코드 / insert => write.jsp <form> 태그의 action 
			case "/edit" : site = getViewForEdit(request); break;     //수정화면을 보여줌
			case "/update" : site = updateBoard(request); break;      //수정화면을 넘겨주기 => edit.jsp 에서 경로를 update 로 함
			case "/delete" : site = deleteBoard(request); break;	  //글 삭제하기
		}
		
		/* 
		 * redirect vs forward
		 * 둘다 새로운 페이지로 이동
		 * redirect : 데이터(response, request 객체)를 가지고 이동하지 X, 주소가 변한다 O
		 * DB에 변화가 생기는 요청(글쓰기, 회원가입,....)
		 * insert, update, delete...
		 * post 방식일 때는 데이터를 가지고 이동하지 않기 때문에 데이터를 잃을 수 있음
		 		  
		 * forward : 데이터(response, request 객체)를 가지고 이동 O, 주소가 변하지 X
		 * 단순조회(상세페이지 보기, 리스트 보기, 검색....)
		 * select...
		 * 데이터를 가지고 이동하기 때문에 post 방식일 때 데이터 잃을 염려가 없음

		 */
		
		
		//return redirect  때문에 나누기 
		if(site.startsWith("redirect:/")) {  //redirect
			
			//redirect 경로만 잘라온다.
			String rview = site.substring("redirect:/".length());
			System.out.println("rview: " + rview);  // "list" 를 출력할 것이다.
			
			response.sendRedirect(rview);  //페이지 이동(단순한 페이지 이동, 주소가 변함) != forward => 경로가 list 들어옴 => 다시 list 페이지로 돌아감
			
			 
		} else { //forward 
			ctx.getRequestDispatcher("/" + site).forward(request, response);   //위에서 getServletContext() 선언했기 때문에 ctx로 			
		}
		
	}

	
	public String getList(HttpServletRequest request) {
		ArrayList<Board> list;
		
		try {  //BoardDAO.java 에서 throws 했기 때문에 불러온 곳에서 다시 예외발생함 => try/catch문으로 받음
			list = dao.getList();
			request.setAttribute("boardList", list);   //(key, value)
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return "index.jsp";
	}
	
	public String getView(HttpServletRequest request) {
		int board_no = Integer.parseInt(request.getParameter("board_no"));
		
		try {
			dao.updateViews(board_no);   //DAO 의 업데이트뷰스 메소드 가져오기
			Board b = dao.getView(board_no);
			request.setAttribute("board", b);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return "view.jsp";		
	}
	
	public String insertBoard(HttpServletRequest request) {
		Board b = new Board();      //model 에 넘겨주기 위해 Board객체에 담아서 DAO에 넘겨줌
		
		/*
		b.setUser_id(request.getParameter("user_id"));
		b.setUser_id(request.getParameter(""));
		b.setUser_id(request.getParameter(""));
		b.setUser_id(request.getParameter(""));
		b.setUser_id(request.getParameter(""));
		*/
		
		// 위에서 처럼 파라메터를 하나하나 입력하기 번거롭기 때문에
		// BeanUtils 를 이용해 request에 담겨있는 파라메터를 Board 객체와 mapping 해서 가져온다.
		
		try {
			BeanUtils.populate(b, request.getParameterMap());
			// 1. 이미지 파일 자체를 "서버 컴퓨터"에 저장
			Part part = request.getPart("file");  //이미지파일 받기 ("file") => write 의 이미지 input 의 name
			String fileName = getFileName(part);  //파일 이름 구하기
			
			if(fileName != null && !fileName.isEmpty()) { // fileName 이 null 값이 아니고 비어있지 않으면 함수실행
				part.write(fileName);  //파일을 컴퓨터에 저장한다.
							
				//@MultipartConfig(maxFileSize=1024*1024*2, location="c:/Temp/img")  
				
			}			
			
			//2. 이미지 파일 이름에  "/img/" 경로를 붙여서 "board 객체"에 저장
			b.setImg("/img/" + fileName);   //파일명 구하면 확장자도 자동으로 구한다.
			
			
			dao.insertBoard(b);   //board 객체로 넘겨준다.
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return "redirect:/list";
	}
	
	//파일 이름 추출
	public String getFileName(Part part) {  //header 에서 파일이름을 추출할 수 있다.
		String fileName = null;
		String header = part.getHeader("content-disposition");
		System.out.println("header =>" + header);
		
		int start = header.indexOf("filename=");  //indexOf (fileName=) 로 시작하는 
		fileName = header.substring(start + 10, header.length() - 1);  //substring => 글자를 잘라줌
		System.out.println("파일명: " + fileName);	
		
		return fileName;
	}
	
	public String getViewForEdit(HttpServletRequest request) {
		int board_no = Integer.parseInt(request.getParameter("board_no"));  //view.jsp 에 board_no 을 넣어줬으므로 객체 만들기
		
		try {
			Board b = dao.getViewForEdit(board_no);   //getViewForEdit 에서 데이터 가져오기
			request.setAttribute("board", b);   //request 에 담아주기
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
		return "edit.jsp";
	}
	
	public String updateBoard(HttpServletRequest request) {
		Board b = new Board();
		
		try {
			BeanUtils.populate(b, request.getParameterMap());
			// 1. 이미지 파일 자체를 "서버 컴퓨터"에 저장
			Part part = request.getPart("file");  //이미지파일 받기 ("file") => write 의 이미지 input 의 name
			String fileName = getFileName(part);  //파일 이름 구하기
						
			if(fileName != null && !fileName.isEmpty()) { // fileName 이 null 값이 아니고 비어있지 않으면 함수실행
				part.write(fileName);  //파일을 컴퓨터에 저장한다.
			}			
						
			//2. 이미지 파일 이름에  "/img/" 경로를 붙여서 "board 객체"에 저장
			b.setImg("/img/" + fileName);   //파일명 구하면 확장자도 자동으로 구한다.
			
			dao.updateBoard(b);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "redirect:/view?board_no=" + b.getBoard_no();  
				
	}
	
	public String deleteBoard(HttpServletRequest request) {
		int board_no = Integer.parseInt(request.getParameter("board_no"));
		
		try {
			dao.deleteBoard(board_no);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "redirect:/list";
	}
	
	
	
	
	
	
	
	
	
}
