<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form"   uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>boardMongo</title>
<link rel="stylesheet" href="<c:url value='/css/bootstrap.min.css'/>">
<script src="<c:url value='/js/jquery-3.7.1.min.js'/>"></script>
<script src="<c:url value='/js/bootstrap.min.js'/>"></script>
<script type="text/javaScript" language="javascript" defer="defer">
$( document ).ready(function() {
	//alert("okay")
	list();
});

function list(){
	$.ajax({
		url:"<c:url value='/list.do'/>",			// url을 이런식으로?
		processData: false,
		contentType: false,
		method: "GET",
		cache: false,
		data: ''
	})
	.done(function(data){
/* 		if(data.indexOf("") >= 0){
			return;
		}
		if(data != ""){
			$('#filename').val(data);
			document.forml.action = "<c:url value= '/mgmtAdd.do' />? mode=add";
			document.forml.submit();			
		} */
		
		//alert(data.list.length);
		$('#list').children().remove();
		for(var i = 0 ; i < data.list.length; i++){
			// var txt = "<tr onclick=\"detail('" + data.list[i].title  + "');\">";
		
// 			var title = data.list[i].title;
// 			var content =  data.list[i].content;	
			
			var title = data.list[i].title;
//  			var content =  escapeSpecialChars(data.list[i].content);						//
  			var content =  data.list[i].content;						//
  			var fname =  data.list[i].fname;
  			
			
			//alert( content);
			
// 			var txt = "<tr id = tr"+ i + " onclick= \"detail('" + data.list[i].id + "','" + title + "','" + content  + "')\">";
			var txt = "<tr id = tr"+ i + " onclick= \"detail2('" + data.list[i].id + "',`" + title + "`,`" + content  + "`,`" + fname + "`)\">";	// 백틱 이용 (줄바꿈등 자유로움)
			txt += "<td>" + data.list[i].title + "<span style=\"float:right\">" + data.list[i].date + "</span></td>";
			txt += "</tr>";
			$('#list').append(txt);
		}
		clearDetail();
		$('#tr0').trigger('click');
	})
	.fail(function(jqXHR, tetStatus, errorThrown){
		alert("오류:" + errorThrown);
	});
}

function escapeSpecialChars(str) {
    return str
        .replace(/\n/g, '\\n')  // 줄 바꿈 문자
        .replace(/"/g, '\\"')   // 큰따옴표
        .replace(/'/g, "\\'")   // 작은따옴표
        .replace(/\\/g, '\\\\') // 백슬래시
        .replace(/\r/g, '\\r')  // 캐리지 리턴
        .replace(/\t/g, '\\t')  // 탭 문자
        .replace(/\f/g, '\\f'); // 폼 피드
}

function detail2(id,title, contents, fname){
	//var data = JSON.parse(item);
	
	$('#id').val(id);
	$('#fname').val(fname);
	$('#title').val(title);
	$('#contents').val(contents);
	$('#file').val('');
	
	setTimeout(getImg2(fname),500);
	
}

// image 조회할 때 
function getImg(fname){

	
	var formData = new FormData();
	formData.append('fname', fname);
	
	var url = "<c:url value='/img.do'/>";
	
// 	alert(url);
	$.ajax({
		url:url,			// url을 이런식으로?
		processData: false,
		contentType: false,
		method: "POST",
		cache: false,
		data: formData							// form 전체 데이타 전송
	})
	.done(function(data){
		if ( data == ""){
			$('#img').attr('src', "");
		} else {
			$('#img').attr('src', "data:image/jpeg;base64,"+data);
		}
		
	})
	.fail(function(jqXHR, tetStatus, errorThrown){
		alert("오류:" + errorThrown);
	});
}

// multi image 조회할 때 
function getImg2(fname){

	
	var formData = new FormData();
	formData.append('fname', fname);
	
	var url = "<c:url value='/img2.do'/>";
	
// 	alert(url);
	$.ajax({
		url:url,			// url을 이런식으로?
		processData: false,
		contentType: false,
		method: "POST",
		cache: false,
		data: formData							// form 전체 데이타 전송
	})
	.done(function(data){
		if ( data == ""){
			$('#imgDiv').html("");
		} else {
			$('#imgDiv').html(data);
		}
		
	})
	.fail(function(jqXHR, tetStatus, errorThrown){
		alert("오류:" + errorThrown);
	});
}

function clearDetail(){
	//var data = JSON.parse(item);
	
	$('#id').val('');
	$('#title').val('');
	$('#contents').val('');
	$('#file').val('');
	$('#img').val('');
	
}


//입력/ 수정
function save(){
	// alert(save');
	
	if ( !confirm('저장하시겠습니까?')){
		return;
	}
	
	var formData = new FormData();
	formData.append('id', $('#id').val());
	formData.append('title', $('#title').val());
	formData.append('contents', $('#contents').val());
	// 폼 전체를 넘기면 serialize 처리하면 한방에 처리되나. 개별로 설정해서 넘기는 경우는 아래처럼 처리해야함.
	for (var i=0; i < $('#file')[0].files.length; i++){
		formData.append('file', $('#file')[0].files[i]);	
	}
	
	var url = "<c:url value='/add.do'/>";
	if ($('#id').val() == "") { 
		url =  "<c:url value='/add.do'/>";
	} else {
		url =  "<c:url value='/mod2.do'/>";
	}
	
// 	alert(url);
	$.ajax({
		url:url,			// url을 이런식으로?
		processData: false,
		contentType: false,
		method: "POST",
		cache: false,
//		data: $('#form1').serialize()			// form 전체 데이타 전송
		data: formData							// form 전체 데이타 전송
	})
	.done(function(data){
		data.returnCode == 'success' ? list() : alert(data.returnDesc);
	})
	.fail(function(jqXHR, tetStatus, errorThrown){
		alert("오류:" + errorThrown);
	});
}

function cancel(){
	clearDetail();
}
function del(){
	// alert(save');
	
	if ( !confirm('삭제하시겠습니까?')){
		return;
	}
	
	if ( $('#id').val() == ''){
		$('#id').val('');
		$('#title').val('');
		$('#contents').val('');
		
		return;
	}
	
	var formData = new FormData();
	formData.append('id', $('#id').val());
	
	var url = "<c:url value='/del.do'/>";
	
// 	alert(url);
	$.ajax({
		url:url,			// url을 이런식으로?
		processData: false,
		contentType: false,
		method: "POST",
		cache: false,
//		data: $('#form1').serialize()			// form 전체 데이타 전송
		data: formData							// form 전체 데이타 전송
	})
	.done(function(data){
		data.returnCode == 'success' ? list() : alert(data.returnDesc);
	})
	.fail(function(jqXHR, tetStatus, errorThrown){
		alert("오류:" + errorThrown);
	});
}
function delimg(){

	
	if ( $('#id').val() == '' || $('#fname').val() == '' ){
		alert("삭제할 이미지가 없습니다.");
		return;
	} 	

	if ( !confirm('이미지를 삭제하시겠습니까?')){
		return;
	}
	
	var formData = new FormData();
	formData.append('id', $('#id').val());
	
	var url = "<c:url value='/delimg.do'/>";
	
// 	alert(url);
	$.ajax({
		url:url,			// url을 이런식으로?
		processData: false,
		contentType: false,
		method: "POST",
		cache: false,
//		data: $('#form1').serialize()			// form 전체 데이타 전송
		data: formData							// form 전체 데이타 전송
	})
	.done(function(data){
		data.returnCode == 'success' ? list() : alert(data.returnDesc);
	})
	.fail(function(jqXHR, tetStatus, errorThrown){
		alert("오류:" + errorThrown);
	});
}
</script>
</head>
<body>
<div class="card">
	<h1>jsp board</h1>
	<div class="card-header">
		<ul class="nav nav-tabs">
		  <li class="nav-item">
		    <a class="nav-link" href="<c:url value='/board.do'/>">싱글이미지 게시판</a>
		  </li>
		  <li class="nav-item">
		    <a class="nav-link active" href="<c:url value='/board2.do'/>">멀티이미지 게시판</a>
		  </li>
		</ul>
	</div>
    <div class="card-body">
    	<div class="row">
			<div class="col-lg-4">
				<div class="card" style="min-height:500px;max-height:1000px">
					<table class="table">
					    <thead>
					      <tr>
					        <th>게시물 리스트</th>
					      </tr>
					    </thead>
					    <tbody id="list">
					    </tbody>
					  </table>
				</div>
			</div>
			<div class="col-lg-5">
				<div class="card bg-light text-dark" style="min-height:500px;max-height:1000px">
					<form id="form1" name="form1" action="">
					  <div class="form-group">
					    <label class="control-label" for="title">제목:</label>
					    <div>
					      <input type="text" class="form-control" id="title" placeholder="제목을 입력하세요">
					    </div>
					  </div>
					  <div class="form-group">
					    <label class="control-label" for="contents">내용:</label>
					    <div> 
					      <textarea class="form-control" rows="10" id="contents"></textarea>
					    </div>
					  </div>
					  <div class="form-group">
							<label class="control-label">이미지첨부: jpg,gif,png</label>
							<div>
							    <input type="file" class="form-control" multiple id="file" name="file" style="width:90%" />
							</div>
					  </div>
					  <input type="hidden" id="id" name="id" />
					  <input type="hidden" id="fname" name="fname" />
					</form>
					<div style="text-align:center">
						<div class="btn-group">
						  <button type="button" class="btn btn-primary" onclick="save()">저장</button>
						  <button type="button" class="btn btn-secondary" onclick="cancel()">취소</button>
						  <button type="button" class="btn btn-danger" onclick="del()">삭제</button>
						  <button type="button" class="btn btn-info" onclick="delimg()">그림삭제</button>
						</div>
					</div>
				</div>
			</div>
			<div class="col-lg-3">
				<div id="imgDiv" class="card bg-light text-dark" style="min-height:500px;max-height:1000px">
				</div>
			</div>
		</div>
    </div>
    <div class="card-footer">SpringBoot + MongoDB + jquery + bootstrap4 게시판 만들기</div>
</div>
</body>
</html>