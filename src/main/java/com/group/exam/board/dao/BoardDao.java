package com.group.exam.board.dao;

import java.util.HashMap;
import java.util.List;

import com.group.exam.board.command.BoardlistCommand;
import com.group.exam.board.command.Criteria;
import com.group.exam.board.command.QuestionAdayCommand;
import com.group.exam.board.vo.BoardLikeVo;
import com.group.exam.board.vo.BoardVo;
import com.group.exam.board.vo.ReplyVo;

public interface BoardDao {
	
	public void insertBoard (BoardVo boardVo); //새 게시글 쓰기
	
	public void updateBoard (HashMap<String, Object> map); //게시글 수정
	
	public void deleteBoardOne (HashMap<String, Integer> map); //게시글 삭제
	
	public List<BoardlistCommand> boardList(Criteria cri); // 게시글 전체 리스트
	
	public List<BoardlistCommand> boardMyList (HashMap<String, Object> map); // 내가 쓴 글 모아보기
	
	public BoardlistCommand boardListDetail (int bSeq); // 특정 게시글 디테일
	
	public int listCount (); // board 테이블 전체 글 수
	
	public int boardMylistCount (int mSeq); // 내가 쓴 글 수 
	
	public void boardCountup (int bSeq); // 해당 게시글 카운트 업
	
	public String memberAuth (int mSeq); //멤버 Auth 상태 체크
	
	public int memberLevelup (HashMap<String, Object> map); //멤버 level up 기능
	
	//질문 하루마다 출력 기능 관련
	public QuestionAdayCommand questionselect(int num);
	
	public int getSequence();
	
	public int currentSequence();
	
	//좋아요 기능 관련
    public int getBoardLike(BoardLikeVo vo);

    public void insertBoardLike(BoardLikeVo vo);

    public void deleteBoardLike(BoardLikeVo vo);

    public void updateBoardLike(int bSeq);
    
	//댓글 기능 관련
	public List<ReplyVo> replySelect(ReplyVo replyVo); // 게시글에 맞춰서 댓글 리스트 띄우기
	
	public int replyCount(); //댓글 갯수 카운트
	
	public void replyInsert(ReplyVo replyVo); //댓글 쓰기
	
	public void replyUpdate(ReplyVo replyVo); //댓글 수정
	
	public void replyDelete(int replySeq); //댓글 삭제
}
