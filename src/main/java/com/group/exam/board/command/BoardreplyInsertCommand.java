package com.group.exam.board.command;

public class BoardreplyInsertCommand {

	
	private int boardSeq;
	private String replyContent;
	
	public int getBoardSeq() {
		return boardSeq;
	}
	public void setBoardSeq(int boardSeq) {
		this.boardSeq = boardSeq;
	}
	public String getReplyContent() {
		return replyContent;
	}
	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}
	@Override
	public String toString() {
		return "BoardreplyInsertCommand [boardSeq=" + boardSeq + ", replyContent=" + replyContent + "]";
	}

	
	
	
}
