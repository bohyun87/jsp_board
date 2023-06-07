<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>Insert title here</title>
<link rel="stylesheet" href="./css/style.css" />
</head>
<body>
	<!-- index.jsp 에서 시작하게 만든 과정 -->
	<!-- 기본페이지가 여기서 부터 시작 -->
	<c:if test="${boardList == null}">   <!-- boardList: controller 에서 만든 list -->
		<jsp:forward page="list" />      <!-- forward : 페이지 이동, 주소변화 없음 -->
	</c:if>
	
	<div class="wrap">
		<table class="board_list">
			<caption>
				<h1>자유게시판</h1>
			</caption>
			<thead>
				<tr>
					<th>번호</th>
					<th>제목</th>
					<th>글쓴이</th>
					<th>작성일</th>
					<th>조회수</th>
				</tr>
			</thead>
			<tbody>
				<!--  for (Board board : boardList) -->
				<c:forEach var="board" items="${boardList}" varStatus="status">
				<!-- boardList 에 담아서 가져왔기 때문에 하나씩 풀어줘야 한다. ↓ -->
				<tr>
					<td>${board.board_no}</td>
					<td class="title">${board.title}</td>
					<td>${board.user_id}</td>
					<td>${board.reg_date}</td>
					<td>${board.views}</td>
				</tr>
				</c:forEach>
			</tbody>
		</table>
		<div class="bt_wrap bt_list">
			<a href="write">글쓰기</a>
		</div>
		<div class="board_page">
			<a href="#" class="bt first">&lt;&lt;</a> <a href="#" class="bt prev">&lt;</a>
			<a href="#" class="num on">1</a> <a href="#" class="num">2</a> <a
				href="#" class="num">3</a> <a href="#" class="num">4</a> <a href="#"
				class="num">5</a> <a href="#" class="bt next">&gt;</a> <a href="#"
				class="bt last">&gt;&gt;</a>
		</div>
	</div>
	


</body>
</html>