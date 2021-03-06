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
		// not null ??????
		if (bindingResult.hasErrors()) {

			return "board/writeForm";
		}

		LoginCommand loginMember = (LoginCommand) session.getAttribute("memberLogin");

		boolean memberAuth = boardService.memberAuth(loginMember.getmSeq()).equals("F");
		if (memberAuth == true) {
			return "redirect: /exam/"; // ????????? ?????? x -> ?????? ?????????

		}

		// ???????????? ????????? mSeq ??? boardVo??? ??????
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

	// ????????? ??????
	@GetMapping(value = "/list")
	public String boardListAll( Criteria cri, Model model, HttpSession session) {

		/*
		 * @RequestParam null ?????? ?????? - (required = false) == true ??? ?????? ????????? - @Nullable
		 * ??????????????? ??????
		 * 
		 * - int ?????? ?????? (defaultValue="0")
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


		// ?????? ?????? ??????
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

	@Scheduled(cron = "0 0 12 1/1 * ?") // ???????????? ???????????? ????????? ????????????
	public void getSequence() {
		logger.info(new Date() + "???????????? ??????");
		num = boardService.getSequence();
	}

	// ??????list ??? ??? ????????????
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

	// ????????? ?????????
	@GetMapping(value = "/detail")
	public String boardListDetail(@RequestParam int bSeq, Model model, HttpSession session) {

		boardService.boardCountup(bSeq);

		BoardlistCommand list = boardService.boardListDetail(bSeq);

		// ?????? ??? loginMember??? ??????

		LoginCommand loginMember = (LoginCommand) session.getAttribute("memberLogin");
		
		

		if (loginMember != null) {
			// ???????????? ????????? mSeq ??? boardVo??? ??????
			int mSeq = loginMember.getmSeq();
			// ????????? ????????? mSeq??? ???????????? mSeq??? ???????????? ??? ????????? ?????? ?????? ????????? ??????
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
		
		//?????? ?????????
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
	
	//?????? insert ajax
	@PostMapping(value = "/reply", produces = "application/json")
	@ResponseBody
	public List<ReplyVo> boardReply(@RequestBody BoardreplyInsertCommand command, HttpSession session, Model model) {
		
		LoginCommand loginMember = (LoginCommand) session.getAttribute("memberLogin");
		
		//?????? ?????? ?????? ?????? ??? (?????? ???)
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
		System.out.println("?????? ????????? : " + replySelect);
		
		return replySelect;
	}

	
	
	
	// ????????? ??????
	@GetMapping(value = "/edit")
	public String boardEdit(@ModelAttribute("boardEditData") BoardVo boardVo, HttpSession session, Model model) {

		return "board/editForm";
	}

	// ????????? ??????
	@PostMapping(value = "/edit")
	public String boardEdit(@Valid @ModelAttribute("boardEditData") BoardupdateCommand updateCommand,
			BindingResult bindingResult, Model model, HttpSession session) {

		if (bindingResult.hasErrors()) {

			return "board/editForm";
		}

		// ?????? ??? loginMember??? ??????
		LoginCommand loginMember = (LoginCommand) session.getAttribute("memberLogin");

		if (loginMember != null) {
			// ???????????? ????????? mSeq ??? boardVo??? ??????
			int mSeq = loginMember.getmSeq();
			int bSeq = updateCommand.getbSeq();

			BoardlistCommand list = boardService.boardListDetail(bSeq);

			model.addAttribute("list", list);
			boardService.updateBoard(updateCommand.getbTitle(), updateCommand.getbContent(), bSeq);
			System.out.println(" ?????? ??????");
		} else {
			System.out.println("?????? ??????");
			// return errors - ?????? ??????
		}

		return "redirect:/board/list";
	}

	// ????????? ??????
	@GetMapping(value = "/delete")
	public String boardDelect(@RequestParam int bSeq, Model model, HttpSession session, Criteria cri) {

		// ?????? ??? loginMember??? ??????
		LoginCommand loginMember = (LoginCommand) session.getAttribute("memberLogin");

		if (loginMember != null) {
			// ???????????? ????????? mSeq ??? boardVo??? ??????
			int mSeq = loginMember.getmSeq();
			boardService.deleteBoardOne(bSeq, mSeq);
			System.out.println("?????? ??????");
		} else {
			System.out.println("?????? ??????");
			// return errors - ?????? ??????
		}

		return "redirect:/board/list";
	}

}
