package com.group.exam.board.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group.exam.board.command.BoardlistCommand;
import com.group.exam.board.command.Criteria;
import com.group.exam.board.command.QuestionAdayCommand;
import com.group.exam.board.command.ReplyWriteCommand;
import com.group.exam.board.dao.BoardDao;
import com.group.exam.board.vo.BoardLikeVo;
import com.group.exam.board.vo.BoardVo;
import com.group.exam.board.vo.ReplyVo;

@Service
public class BoardServiceImpl implements BoardService{
	
	@Autowired
	private BoardDao boardDao;
	
	public BoardServiceImpl () {
		
	}
	

	@Override
	public void insertBoard(BoardVo boardVo) {
		// TODO Auto-generated method stub
	
		boardDao.insertBoard(boardVo);
		
	}

	@Override
	public List<BoardlistCommand> boardList(Criteria cri) {
		// TODO Auto-generated method stub
		
		return boardDao.boardList(cri);
	}


	@Override
	public List<BoardlistCommand> boardMyList(Criteria cri, int mSeq) {
		// TODO Auto-generated method stub
		HashMap<String, Object> map = new HashMap<String, Object>();
	
		map.put("mSeq", mSeq);
		map.put("rowStart", cri.getRowStart());
		map.put("rowEnd", cri.getRowEnd());
		return boardDao.boardMyList(map);
	}
	

	@Override
	public BoardlistCommand boardListDetail(int bSeq) {
		// TODO Auto-generated method stub
		return boardDao.boardListDetail(bSeq);
	}
	


	@Override
	public void deleteBoardOne(int bSeq, int mSeq) {
		// TODO Auto-generated method stub
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		
		map.put("bSeq", bSeq);
		map.put("mSeq", mSeq);
		boardDao.deleteBoardOne(map);
		
		
		
	}


	//게시물 조횟수 up
	@Override
	public void boardCountup(int bSeq) {
		// TODO Auto-generated method stub

		boardDao.boardCountup(bSeq);
		
	}


	@Override
	public int listCount() {
		// TODO Auto-generated method stub
		return boardDao.listCount();
	}


	@Override
	public void updateBoard(String bTitle, String bContent, int bSeq) {
		// TODO Auto-generated method stub
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("bTitle", bTitle);
		map.put("bContent", bContent);
		map.put("bSeq", bSeq);
		
		boardDao.updateBoard(map);
		
	}


	@Override
	public int mylistCount(int mSeq) {
		// TODO Auto-generated method stub
		return boardDao.boardMylistCount(mSeq);
	}


	@Override
	public void insertBoardLike(BoardLikeVo vo) {
		// TODO Auto-generated method stub
		boardDao.insertBoardLike(vo);
		boardDao.updateBoardLike(vo.getbSeq());
		
	}


	@Override
	public void deleteBoardLike(BoardLikeVo vo) {
		// TODO Auto-generated method stub
		boardDao.deleteBoardLike(vo);
		boardDao.updateBoardLike(vo.getbSeq());
		
	}


	@Override
	public int getBoardLike(BoardLikeVo vo) {
		// TODO Auto-generated method stub
		return boardDao.getBoardLike(vo);
	}


	@Override
	public String memberAuth(int mSeq) {
		// TODO Auto-generated method stub
		return boardDao.memberAuth(mSeq);
	}


	@Override
	public int memberLevelup(int mSeq, int mytotal, int mLevel) {
		// TODO Auto-generated method stub
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("mSeq", mSeq);
		map.put("mytotal", mytotal);
		map.put("mLevel", mLevel);
		
		return boardDao.memberLevelup(map);
		
	}


	@Override
	public QuestionAdayCommand questionselect(int num) {
		// TODO Auto-generated method stub
		return boardDao.questionselect(num);
	}


	@Override
	public int getSequence() {
		// TODO Auto-generated method stub
		return boardDao.getSequence();
	}


	@Override
	public int currentSequence() {
		// TODO Auto-generated method stub
		return boardDao.currentSequence();
	}


	//댓글 기능 관련
	@Override
	public List<ReplyVo> replySelect(int bSeq) {
		// TODO Auto-generated method stub
		return boardDao.replySelect(bSeq);
	}


	@Override
	public void replyInsert(ReplyVo replyVo) {
		// TODO Auto-generated method stub
		ReplyVo newreply = new ReplyVo();
		//newreply.setReplySeq(replywriteCommand.getReplySeq());
		newreply.setBoardSeq(replyVo.getBoardSeq());
		newreply.setMemberSeq(replyVo.getMemberSeq());
		newreply.setReplyContent(replyVo.getReplyContent());
		
		boardDao.replyInsert(newreply);
	}


	@Override
	public void replyUpdate(ReplyVo replyVo) {
		// TODO Auto-generated method stub
		ReplyVo updatereply = new ReplyVo();
		//updatereply.setReplySeq(replyVo.getReplySeq());
		//updatereply.setBoardSeq(replyVo.getBoardSeq());
		//updatereply.setMemberSeq(replyVo.getMemberSeq());
		updatereply.setReplyContent(replyVo.getReplyContent());
		
		boardDao.replyUpdate(updatereply);
	}


	@Override
	public void replyDelete(int replySeq) {
		// TODO Auto-generated method stub
		boardDao.replyDelete(replySeq);
	}
}