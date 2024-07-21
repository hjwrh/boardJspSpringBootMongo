package com.example.boardMongo.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Controller
public class BoardMongoController {
	
	@Autowired
	private Environment env ;   				// java에서 property 사용가능하게 해주는 객체

	@Autowired
	private MongoTemplate mongoTemplate ;   	// 몽고 db 사용 

	@Autowired
	private BoardRepository boardRepository ;   // 몽고 db 사용 

	
	@GetMapping("/")
	private String index() throws Exception{
		org.slf4j.Logger logger = LoggerFactory.getLogger(BoardMongoController.class);
		logger.info("[hjkim] / 호출  index 출력 ");
		return "index";
	}	

	@RequestMapping("/board.do")
	private String board() throws Exception{
		org.slf4j.Logger logger = LoggerFactory.getLogger(BoardMongoController.class);
		logger.info("board.do");
		System.out.println("board*************");
		return "board";
	}	

	@RequestMapping("/board2.do")
	private String board2() throws Exception{
		org.slf4j.Logger logger = LoggerFactory.getLogger(BoardMongoController.class);
		logger.info("board2.do");
		System.out.println("board2*************");
		return "board2";
	}	

	@RequestMapping("/board3.do")
	private String board3() throws Exception{
		org.slf4j.Logger logger = LoggerFactory.getLogger(BoardMongoController.class);
		logger.info("1111");
		return "board3";
	}	

	@RequestMapping("/list.do")
	@ResponseBody		// ajax로 받겠다. json으로 return.
	private Map<String, Object> list() throws Exception{
		System.out.println("list***************");
		Map<String, Object> map = new HashMap<>();
		List<Board> list = new ArrayList<Board>();
		
		list = boardRepository.findAll();
		
		map.put("list", list);
		
		return map;
	}
	
	@RequestMapping(value = "/add.do", method=RequestMethod.POST )		// post로 들어온 요청만 받겠다.
	@ResponseBody		
	private Map<String, Object> add(@RequestParam(value="title", required=true) String title,
			@RequestParam(value="contents", required=false, defaultValue = "") String contents,
			@RequestParam(value="file", required=false) MultipartFile file) throws Exception{
		System.out.println("add ***************");
		Map<String, Object> map = new HashMap<>();

		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:MM");		//오늘 날짜를 가져오기 위해
		Date time = new Date();
		String ymd = format1.format(time);			
		
		try {
			
			String repository = env.getProperty("user.file.upload");				// env : 전자정부 제공 객체. 설정정보 읽어옴
			
			String fname = "";
			if ( file!= null && file.getSize() > 0 ) {								// 파일이 넘어 왔다면
				fname = file.getOriginalFilename();									// 파일명을 얻고
				FileOutputStream fos = new FileOutputStream(new File(repository+ File.separator+fname));		// repository 밑에 파일을 쌓아라.
				IOUtils.copy(file.getInputStream(), fos);							// common io util (아파치기능), pom.xml에서 common io 설정을 해야함. 사용하면 편함.
				fos.close();
			}
			
			Query query = new Query();			// Mongo DB에서 Query하기 위해
			Board in = new Board();
			in.setTitle(title);
			in.setContent(contents);
			in.setDate(ymd);
			in.setFname(fname);
			
			boardRepository.insert(in);			// Mongo DB에 데이타 입력
			
			map.put("returnCode", "success");
			map.put("returnDesc", "");
		} catch(Exception e) {
			map.put("returnCode", "fail");
			map.put("returnDesc", "등록이 실패하였습니다.");
		}

		return map;
	}	
	
	@RequestMapping(value = "/mod.do", method=RequestMethod.POST )		// post로 들어온 요청만 받겠다.
	@ResponseBody		
	private Map<String, Object> mod(@RequestParam(value="id", required=true) String id,
			@RequestParam(value="title", required=true) String title,
			@RequestParam(value="contents", required=false, defaultValue = "") String contents,
			@RequestParam(value="file", required=false) MultipartFile file) throws Exception{
		System.out.println("mod ***************");
		Map<String, Object> map = new HashMap<>();
				
		try {
			String repository = env.getProperty("user.file.upload");				// env : 전자정부 제공 객체. 설정정보 읽어옴
			
			String fname = "";
			if ( file!= null && file.getSize() > 0 ) {								// 파일에 파일 이름, 파일 내용등이 객체로 넘오옴.
				fname = file.getOriginalFilename();									// 파일명을 얻고
				FileOutputStream fos = new FileOutputStream(new File(repository+ File.separator+fname));		// repository 밑에 파일을 쌓아라.
				IOUtils.copy(file.getInputStream(), fos);							// common io util (아파치기능), pom.xml에서 common io 설정을 해야함. 사용하면 편함.
				fos.close();
			}
			
			Query query = new Query();			// Mongo DB에서 Query하기 위해
			Criteria activityCriteria = Criteria.where("id").is(id);						// where 조건을 넣는것
			query.addCriteria(activityCriteria);
	
			List<Board> out = mongoTemplate.find(query,Board.class);
			
			if (out.size()>0) {
				Board in = out.get(0);
				in.setTitle(title);
				in.setContent(contents);
				in.setFname(fname);
				boardRepository.save(in);	
			}
				
			map.put("returnCode", "success");
			map.put("returnDesc", "수정하였습니다. ");
		} catch(Exception e) {
			map.put("returnCode", "fail");
			map.put("returnDesc", "수정이 실패하였습니다.");
		}
		
		return map;
	}	
	// multi 이미지 처리용.
	@RequestMapping(value = "/mod2.do", method=RequestMethod.POST )		// post로 들어온 요청만 받겠다.
	@ResponseBody		
	private Map<String, Object> mod2(@RequestParam(value="id", required=true) String id,
			@RequestParam(value="title", required=true) String title,
			@RequestParam(value="contents", required=false, defaultValue = "") String contents,
			@RequestParam(value="file", required=false) List<MultipartFile> files) throws Exception{			// MultipartFile을 list형대톨 받음
		System.out.println("mod2 ***************");
		Map<String, Object> map = new HashMap<>();
		
		try {
			String repository = env.getProperty("user.file.upload");				// env : 전자정부 제공 객체. 설정정보 읽어옴
			String fname = "";
			String fnames = "";
			
			for (MultipartFile file : files) {
				if ( file!= null && file.getSize() > 0 ) {								// 파일에 파일 이름, 파일 내용등이 객체로 넘오옴.
					fname = file.getOriginalFilename();									// 파일명을 얻고
					FileOutputStream fos = new FileOutputStream(new File(repository+ File.separator+fname));		// repository 밑에 파일을 쌓아라.
					IOUtils.copy(file.getInputStream(), fos);							// common io util (아파치기능), pom.xml에서 common io 설정을 해야함. 사용하면 편함.
					fos.close();
				}
				if (fname.equals("")) {
					fnames = fname;
				} else {
					fnames += "," + fname;
				}
			}
			
			Query query = new Query();			// Mongo DB에서 Query하기 위해
			Criteria activityCriteria = Criteria.where("id").is(id);						// where 조건을 넣는것
			query.addCriteria(activityCriteria);
			
			List<Board> out = mongoTemplate.find(query,Board.class);
			
			if (out.size()>0) {
				Board in = out.get(0);
				in.setTitle(title);
				in.setContent(contents);
				in.setFname(fnames);
				boardRepository.save(in);	
			}
			
			map.put("returnCode", "success");
			map.put("returnDesc", "수정하였습니다. ");
		} catch(Exception e) {
			map.put("returnCode", "fail");
			map.put("returnDesc", "수정이 실패하였습니다.");
		}
		
		return map;
	}	
	
	@RequestMapping(value = "/del.do", method=RequestMethod.POST )		// post로 들어온 요청만 받겠다.
	@ResponseBody		
	private Map<String, Object> del(@RequestParam(value="id", required=true) String id) throws Exception{
		System.out.println("del ***************");
		Map<String, Object> map = new HashMap<>();
		
		try {
			Query query = new Query();			// Mongo DB에서 Query하기 위해
			Criteria activityCriteria = Criteria.where("id").is(id);						// where 조건을 넣는것
			query.addCriteria(activityCriteria);
			
			List<Board> out = mongoTemplate.find(query,Board.class);
			
			if (out.size()>0) {
				Board in = out.get(0);
				boardRepository.delete(in);	
			}
			
			map.put("returnCode", "success");
			map.put("returnDesc", "삭제하였습니다. ");
		} catch(Exception e) {
			map.put("returnCode", "fail");
			map.put("returnDesc", "삭제 실패하였습니다.");
		}
		
		return map;
	}	
	
	@RequestMapping(value = "/img.do")
	@ResponseBody	
	private String img(@RequestParam(value="fname", required=true, defaultValue="") String fname,
			HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		System.out.println("img ***************");
		
		String repository = env.getProperty("user.file.upload");
		String base64Encoded = "";
		fname = repository + File.separator+fname;
		System.out.println("idr:" + fname);								// 받은 파일명을 실제 경로와 결합
		File file = new File(fname);									// 실제 파일을 찾음
		if (file.exists() && file.isFile()) {
			InputStream in =  new FileInputStream(fname);				// input stream으로 받아서
			byte[] bytes = IOUtils.toByteArray(in);
			byte[] enCodeBase64 = Base64.getEncoder().encode(bytes);	// 64비트 encoding 시킴
			base64Encoded = new String(enCodeBase64,"UTF-8");			// 64비트 인코딩 문자열로 바꿈
			in.close();
		}
		
		return base64Encoded;											// 64비트 인코딩 문자열을 화면으로 리턴
	}
	
	@RequestMapping(value = "/img2.do")									//multi image를 가져온다.
	@ResponseBody	
	private String img2(@RequestParam(value="fname", required=true, defaultValue="") String fnames,
			HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		System.out.println("img2 ***************");
		
		String repository = env.getProperty("user.file.upload");
		String base64Encoded = "";
		String[] fnameArray = fnames.split(",");
		String rtnTag = "";
		
		for (String fname : fnameArray) {
			fname = repository + File.separator+fname;
			
			System.out.println("idr:" + fname);								// 받은 파일명을 실제 경로와 결합
			File file = new File(fname);									// 실제 파일을 찾음
			if (file.exists() && file.isFile()) {
				InputStream in =  new FileInputStream(fname);				// input stream으로 받아서
				byte[] bytes = IOUtils.toByteArray(in);
				byte[] enCodeBase64 = Base64.getEncoder().encode(bytes);	// 64비트 encoding 시킴
				base64Encoded = new String(enCodeBase64,"UTF-8");			// 64비트 인코딩 문자열로 바꿈
				in.close();
				
				rtnTag += "<img src=\"data:image/jpeg;base64," + base64Encoded + "\"/><br/><br/>";
			}
		}
		
		
		return rtnTag;											// 64비트 인코딩 문자열을 화면으로 리턴
	}	

	@RequestMapping(value = "/delimg.do", method=RequestMethod.POST )		// post로 들어온 요청만 받겠다.
	@ResponseBody		
	private Map<String, Object> delimg(@RequestParam(value="id", required=true) String id) throws Exception{
		System.out.println("delimg ***************");
		Map<String, Object> map = new HashMap<>();
		
		try {
			Query query = new Query();			// Mongo DB에서 Query하기 위해
			Criteria activityCriteria = Criteria.where("id").is(id);						// where 조건을 넣는것
			query.addCriteria(activityCriteria);
			
			List<Board> out = mongoTemplate.find(query,Board.class);
			
			if (out.size()>0) {
				Board in = out.get(0);
				
				// 실제 파일 삭제
				String repository = env.getProperty("user.file.upload");
				String fname = repository+File.separator+in.getFname();
				File delFile = new File(fname);
				if (delFile.exists()) {
					System.out.println("del dir:" + fname);
					delFile.delete();
				}
				
				// db 파일 정보 삭제
				in.setFname("");
				boardRepository.save(in);
			}
			
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																				map.put("returnCode", "success");
			map.put("returnDesc", "이미지를 삭제하였습니다. ");
		} catch(Exception e) {
			map.put("returnCode", "fail");
			map.put("returnDesc", "이미지 삭제에 실패하였습니다.");
		}
		
		return map;
	}	
	
	
}