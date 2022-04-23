package com.group.exam.board.service;

import java.util.List;

import com.group.exam.board.command.BoardlistCommand;
import com.group.exam.board.command.Criteria;
import com.group.exam.board.command.QuestionAdayCommand;
import com.group.exam.board.command.ReplyWriteCommand;
import com.group.exam.board.vo.BoardLikeVo;
import com.group.exam.board.vo.BoardVo;
import com.group.exam.board.vo.ReplyVo;


public interface BoardService {
	
	public void insertBoard(BoardVo boardVo); // 새 게시글 쓰기
	
	public List<BoardlistCommand> boardList(Criteria cri); // 게시글 전체 리스트
	
	public List<BoardlistCommand> boardMyList(Criteria cri,int mSeq); // 내가 쓴 글 불러오기
	
	public BoardlistCommand boardListDetail(int bSeq); // 해당 게시글 디테일
	
	public void boardCountup(int bSeq); // 해당 게시글 카운트 업
	
	public void deleteBoardOne(int bSeq, int mSeq); // 해당 게시글 삭제
	
	public int listCount(); // board 테이블 전체 글 수
	
	public int mylistCount(int mSeq); // 게시글 쓴 글 수
	
	public void updateBoard(String bTitle, String bContent, int bSeq); //게시글 수정
	
	public String memberAuth (int mSeq); //멤버 Auth 상태 확인 
	
	public int memberLevelup (int mSeq, int mytotal, int mLevel);
	
	//하루마다 질문 출력
	
	public QuestionAdayCommand questionselect(int num);
	
	public int getSequence();
	
	public int currentSequence();
	
	
	//좋아요 기능 관련
	public void insertBoardLike(BoardLikeVo vo);
	
	public void deleteBoardLike(BoardLikeVo vo);
	
	public int getBoardLike(BoardLikeVo vo);
	

	//댓글 기능 관련
	public List<ReplyVo> replySelect(int bSeq); // 게시글에 맞춰서 댓글 리스트 띄우기
	
	public void replyInsert(ReplyVo replyVo); //댓글 쓰기
	
	public void replyUpdate(ReplyVo replyVo); //댓글 수정
	
	public void replyDelete(int replySeq); //댓글 삭제
}
