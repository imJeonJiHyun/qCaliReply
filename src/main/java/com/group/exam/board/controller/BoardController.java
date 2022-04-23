package com.group.exam.board.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.group.exam.board.command.BoardLikeCommand;
import com.group.exam.board.command.BoardPageCommand;
import com.group.exam.board.command.BoardlistCommand;
import com.group.exam.board.command.BoardreplyInsertCommand;
import com.group.exam.board.command.BoardupdateCommand;
import com.group.exam.board.command.Criteria;
import com.group.exam.board.command.QuestionAdayCommand;
import com.group.exam.board.service.BoardService;
import com.group.exam.board.vo.BoardLikeVo;
import com.group.exam.board.vo.BoardVo;
import com.group.exam.board.vo.ReplyVo;
import com.group.exam.member.command.LoginCommand;
import com.group.exam.member.service.MemberService;

@Controller
@RequestMapping("/board")
public class BoardController {

	private static final Logger logger = LoggerFactory.getLogger(BoardController.class);
	public static int num;

	private BoardService boardService;

	private MemberService memberService;

	@Autowired
	public BoardController(BoardService boardService, MemberService memberService) {
		this.boardService = boardService;

		this.memberService = memberService;

	}

	@GetMapping(value = "/write")
	public String insertBoard(@ModelAttribute("boardData") BoardVo boardVo, HttpSession session) {

		return "board/writeForm";
	}

	@PostMapping(value = "/write")
	public String insertBoard(@Valid @ModelAttribute("boardData") BoardVo boardVo, BindingResult bindingResult,
			Criteria cri, HttpSession session, Model model) {
		// not null 체크
		if (bindingResult.hasErrors()) {

			return "board/writeForm";
		}

		LoginCommand loginMember = (LoginCommand) session.getAttribute("memberLogin");

		boolean memberAuth = boardService.memberAuth(loginMember.getmSeq()).equals("F");
		if (memberAuth == true) {
			return "redirect: /exam/"; // 이메일 인증 x -> 예외 페이지

		}

		// 세션에서 멤버의 mSeq 를 boardVo에 셋팅
		boardVo.setmSeq(loginMember.getmSeq());

		// insert
		boardService.insertBoard(boardVo);

		// update

		int mytotal = boardService.mylistCount(loginMember.getmSeq());

		if (mytotal >= 10) {
			int memberLevel = boardService.memberLevelup(loginMember.getmSeq(), mytotal, loginMember.getmLevel());

			if (memberLevel == 1) {

				LoginCommand member = memberService.login(loginMember.getmId());

				LoginCommand login = member;

				session.setAttribute("memberLogin", login);

				model.addAttribute("level", login.getmLevel());
				model.addAttribute("id", login.getmId());

				return "/board/leverup";

			}
		}

		return "redirect:/board/list";
	}

	// 리스트 전체
	@GetMapping(value = "/list")
	public String boardListAll( Criteria cri, Model model, HttpSession session) {

		/*
		 * @RequestParam null 허용 방법 - (required = false) == true 가 기본 설정임 - @Nullable
		 * 어노테이션 추가
		 * 
		 * - int 형의 경우 (defaultValue="0")
		 * 
		 */

//		if (currentPage == 0) {
//			currentPage = 1;
//		}

		int total = boardService.listCount();

		if (total == 0) {
			total = 1;
		}
		/*
		 * 1 1,10 2 11, 20
		 */


		List<BoardlistCommand> list = boardService.boardList(cri);
		System.out.println("list " + list);
		model.addAttribute("list", list);

		//model.addAttribute("currentPage", currentPage);
		BoardPageCommand pageCommand = new BoardPageCommand();
		pageCommand.setCri(cri);
		pageCommand.setTotal(total);
		model.addAttribute("pageMaker", pageCommand);


		// 질문 출력 관련
		if (num == 0) {
			num = boardService.currentSequence();
			if(num == 0) {
				num = 1;
			}
		}
		logger.info("" + num);
		QuestionAdayCommand question = boardService.questionselect(num);

		System.out.println(question);
		System.out.println("num +" + num);
		model.addAttribute("question", question);

		return "board/list";
	}

	@Scheduled(cron = "0 0 12 1/1 * ?") // 하루마다 출력으로 표현식 바꿔야함
	public void getSequence() {
		logger.info(new Date() + "스케쥴러 실행");
		num = boardService.getSequence();
	}

	// 해당list 내 글 모아보기
	@GetMapping(value = "/mylist")
	public String boardListMy(@RequestParam("mSeq") int mSeq, Model model, Criteria cri,
			HttpSession session) {


		int total = boardService.mylistCount(mSeq);

		

		List<BoardlistCommand> list = boardService.boardMyList(cri, mSeq);
		model.addAttribute("list", list);

	
		BoardPageCommand pageCommand = new BoardPageCommand();
		pageCommand.setCri(cri);
		pageCommand.setTotal(total);
		model.addAttribute("pageMaker", pageCommand);

		return "board/mylist";
	}

	// 게시글 디테일
	@GetMapping(value = "/detail")
	public String boardListDetail(@RequestParam int bSeq, Model model, HttpSession session) {

		boardService.boardCountup(bSeq);

		BoardlistCommand list = boardService.boardListDetail(bSeq);

		// 세션 값 loginMember에 저장

		LoginCommand loginMember = (LoginCommand) session.getAttribute("memberLogin");
		
		

		if (loginMember != null) {
			// 세션에서 멤버의 mSeq 를 boardVo에 셋팅
			int mSeq = loginMember.getmSeq();
			// 세션에 저장된 mSeq와 게시글의 mSeq를 비교하여 내 글이면 수정 삭제 버튼이 뜨게
			if (mSeq == list.getmSeq()) {
				boolean my = true;
				model.addAttribute("my", my);
			}
		}


		model.addAttribute("list", list);
		model.addAttribute("bSeq", bSeq);

		BoardLikeVo likeVo = new BoardLikeVo();

		likeVo.setbSeq(bSeq);
		likeVo.setmSeq(loginMember.getmSeq());

		int boardlike = boardService.getBoardLike(likeVo);

		model.addAttribute("heart", boardlike);
		
		//댓글 리스트
		List<ReplyVo> replySelect = boardService.replySelect(bSeq);
		model.addAttribute("replySelect", replySelect);
		
		return "board/listDetail";
	}

	@PostMapping(value = "/heart", produces = "application/json")
	@ResponseBody
	public int boardLike(@RequestBody BoardLikeCommand command, HttpSession session) {

		LoginCommand loginMember = (LoginCommand) session.getAttribute("memberLogin");

		BoardLikeVo likeVo = new BoardLikeVo();

		likeVo.setbSeq(command.getbSeq());
		likeVo.setmSeq(loginMember.getmSeq());

		if (command.getHeart() >= 1) {
			boardService.deleteBoardLike(likeVo);
			command.setHeart(0);
		} else {

			boardService.insertBoardLike(likeVo);
			command.setHeart(1);
		}

		return command.getHeart();

	}
	
	//댓글 insert ajax
	@PostMapping(value = "/reply", produces = "application/json")
	@ResponseBody
	public List<ReplyVo> boardReply(@RequestBody BoardreplyInsertCommand command, HttpSession session, Model model) {
		
		LoginCommand loginMember = (LoginCommand) session.getAttribute("memberLogin");
		
		//댓글 입력 값이 있을 떄 (클릭 시)
		if (command.getReplyContent() != null) {
		ReplyVo replyVo = new ReplyVo();

		replyVo.setBoardSeq(command.getBoardSeq());
		replyVo.setMemberSeq(loginMember.getmSeq());
		replyVo.setMemberNickname(loginMember.getmNickname());
		replyVo.setReplyRegDay(replyVo.getReplyRegDay());
		replyVo.setReplyContent(command.getReplyContent());
		
		boardService.replyInsert(replyVo);
		}
		
		List<ReplyVo> replySelect = boardService.replySelect(bSeq);
		model.addAttribute("replySelect", replySelect);
		System.out.println("댓글 리스트 : " + replySelect);
		
		return replySelect;
	}

	
	
	
	// 게시글 수정
	@GetMapping(value = "/edit")
	public String boardEdit(@ModelAttribute("boardEditData") BoardVo boardVo, HttpSession session, Model model) {

		return "board/editForm";
	}

	// 게시글 수정
	@PostMapping(value = "/edit")
	public String boardEdit(@Valid @ModelAttribute("boardEditData") BoardupdateCommand updateCommand,
			BindingResult bindingResult, Model model, HttpSession session) {

		if (bindingResult.hasErrors()) {

			return "board/editForm";
		}

		// 세션 값 loginMember에 저장
		LoginCommand loginMember = (LoginCommand) session.getAttribute("memberLogin");

		if (loginMember != null) {
			// 세션에서 멤버의 mSeq 를 boardVo에 셋팅
			int mSeq = loginMember.getmSeq();
			int bSeq = updateCommand.getbSeq();

			BoardlistCommand list = boardService.boardListDetail(bSeq);

			model.addAttribute("list", list);
			boardService.updateBoard(updateCommand.getbTitle(), updateCommand.getbContent(), bSeq);
			System.out.println(" 수정 성공");
		} else {
			System.out.println("수정 실패");
			// return errors - 삭제 실패
		}

		return "redirect:/board/list";
	}

	// 게시글 삭제
	@GetMapping(value = "/delete")
	public String boardDelect(@RequestParam int bSeq, Model model, HttpSession session, Criteria cri) {

		// 세션 값 loginMember에 저장
		LoginCommand loginMember = (LoginCommand) session.getAttribute("memberLogin");

		if (loginMember != null) {
			// 세션에서 멤버의 mSeq 를 boardVo에 셋팅
			int mSeq = loginMember.getmSeq();
			boardService.deleteBoardOne(bSeq, mSeq);
			System.out.println("삭제 성공");
		} else {
			System.out.println("삭제 실패");
			// return errors - 삭제 실패
		}

		return "redirect:/board/list";
	}

}
