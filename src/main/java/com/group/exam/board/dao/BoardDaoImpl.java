package com.group.exam.board.dao;

import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.group.exam.board.command.BoardlistCommand;
import com.group.exam.board.command.Criteria;
import com.group.exam.board.command.QuestionAdayCommand;
import com.group.exam.board.vo.BoardLikeVo;
import com.group.exam.board.vo.BoardVo;
import com.group.exam.board.vo.ReplyVo;

@Repository
public class BoardDaoImpl implements BoardDao{

	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;

	public BoardDaoImpl (SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}
	
	
	@Override
	public void insertBoard(BoardVo boardVo) {
		// TODO Auto-generated method stub
		sqlSessionTemplate.insert("insertBoard", boardVo);
		
	}

	@Override
	public void updateBoard(HashMap<String, Object> map) {
		// TODO Auto-generated method stub
		// 게시글 제목과 내용 수정 
		sqlSessionTemplate.update("updateBoard", map);
		
	}

	@Override
	public void deleteBoardOne(HashMap<String, Integer> map) {
		// TODO Auto-generated method stub
	
		sqlSessionTemplate.delete("deleteBoardOne", map);
		
	}


	@Override
	public List<BoardlistCommand> boardMyList(HashMap<String, Object> map) {
		// TODO Auto-generated method stub

		return sqlSessionTemplate.selectList("boardMylist", map);
	}

	@Override
	public BoardlistCommand boardListDetail(int bSeq) {
		// TODO Auto-generated method stub
		return sqlSessionTemplate.selectOne("boardlistDetail", bSeq);
	}

	@Override
	public int listCount() {
		// TODO Auto-generated method stub
		return sqlSessionTemplate.selectOne("boardlistCount");
	}



	@Override
	public List<BoardlistCommand> boardList(Criteria cri) {
		// TODO Auto-generated method stub

		return sqlSessionTemplate.selectList("boardlist", cri);
	}


	@Override
	public void boardCountup(int bSeq) {
		// TODO Auto-generated method stub
		
		sqlSessionTemplate.update("boardCountup", bSeq);
		
	}


	@Override
	public int boardMylistCount(int mSeq) {
		// TODO Auto-generated method stub

		return sqlSessionTemplate.selectOne("boardMylistCount", mSeq);
	}
	
	
	//좋아요 기능 관련
	@Override
	public int getBoardLike(BoardLikeVo vo) {
		// TODO Auto-generated method stub
		return sqlSessionTemplate.selectOne("getLike", vo);
	}

	@Override
	public void insertBoardLike(BoardLikeVo vo) {
		// TODO Auto-generated method stub
		sqlSessionTemplate.insert("createLike", vo);
		
	}

	@Override
	public void deleteBoardLike(BoardLikeVo vo) {
		// TODO Auto-generated method stub
		sqlSessionTemplate.delete("deleteLike", vo);
		
	}

	@Override
	public void updateBoardLike(int bSeq) {
		// TODO Auto-generated method stub
		sqlSessionTemplate.update("updateLike", bSeq);
		
	}

	
	@Override
	public String memberAuth(int mSeq) {
		// TODO Auto-generated method stub
		return sqlSessionTemplate.selectOne("memberAuth", mSeq);
	}


	//멤버 레벨 관련
	@Override
	public int memberLevelup(HashMap<String, Object> map) {
		// TODO Auto-generated method stub
		return sqlSessionTemplate.update("memberLevelup", map);
		
	}


	@Override
	public QuestionAdayCommand questionselect(int num) {
		// TODO Auto-generated method stub
		return sqlSessionTemplate.selectOne("questionselect", num);
	}


	@Override
	public int getSequence() {
		// TODO Auto-generated method stub
		return sqlSessionTemplate.selectOne("getSequence");
	}


	@Override
	public int currentSequence() {
		// TODO Auto-generated method stub
		return sqlSessionTemplate.selectOne("currentSequence");
	}
	

	//댓글 관련
	@Override
	public List<ReplyVo> replySelect(int boardSeq) {
		return sqlSessionTemplate.selectList("replySelect", boardSeq);
		// 게시글에 맞춰서 댓글 리스트 띄우기
	}
	
	@Override
	public int replyCount() {
		return sqlSessionTemplate.selectOne("replyCount");
		//댓글 갯수 카운트
	}

	@Override
	public void replyInsert(ReplyVo replyVo) {
		sqlSessionTemplate.insert("replyInsert", replyVo);	
		//댓글 쓰기
	}

	@Override
	public void replyUpdate(ReplyVo replyVo) {
		sqlSessionTemplate.update("replyUpdate", replyVo);
		//댓글 수정
	}

	@Override
	public void replyDelete(int replySeq) {
		sqlSessionTemplate.delete("replyDelete", replySeq);
		//댓글 삭제
	}
}
