function chkForm(){
	var f = document.frm;    //form 태그 가져오기 =>frm => form 태그의 name
	
	if(frm.title.value == ''){   // title => 제목 input의 name
		alert("제목을 입력해주세요.");
		return false;
	}
	
	if(frm.user_id.value == ''){  //form 태그의 user_id 의 값이 비어있으면 알림창이 뜬다.
		alert("아이디를 입력해주세요.");
		return false;
	}
	
	if(frm.content.value == ''){  
		alert("글내용을 입력해주세요.");
		return false;
	}
	
	f.submit();     //form 태그 전송  => write.jsp 에서 전송태그 이용하지 않고
					//자바스크립트에서 공백확인 후 DB로 전송하기 위해 자바에서 submit 실행					
}

function chkDelete(board_no){   //url 에서 board_no 을 받아와야되기 때문에 매개변수를 줌
	const result = confirm("삭제하시겠습니까?");   /*confirm => 확인, 취소 누르는 차이*/
	
	if(result){
		
		//↓ http://localhost:8081/board/delete?board_no=1
		const url = location.origin;    //location.origin => 주소의 앞부분 출력
		location.href = url + "/board/delete?board_no=" + board_no;
	} else {
		false;
	}
	
	
	
	
	
	
	
}