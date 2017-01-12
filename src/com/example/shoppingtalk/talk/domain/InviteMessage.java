package com.example.shoppingtalk.talk.domain;

public class InviteMessage {
	private String from;
	//ʱ��
	private long time;
	//��������
	private String reason;
	
	//δ��֤����ͬ���״̬
	private InviteMesageStatus status;
	//Ⱥid
	private String groupId;
	//Ⱥ����
	private String groupName;
	//Ⱥ������
	private String groupInviter;
	

	private int id;
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}


	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public InviteMesageStatus getStatus() {
		return status;
	}

	public void setStatus(InviteMesageStatus status) {
		this.status = status;
	}

	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public void setGroupInviter(String inviter) {
	    groupInviter = inviter;
	}
	
	public String getGroupInviter() {
	    return groupInviter;	    
	}


	public enum InviteMesageStatus{
	    
	    //==����
		/**������*/
		BEINVITEED,
		/**���ܾ�*/
		BEREFUSED,
		/**�Է�ͬ��*/
		BEAGREED,
		
		//==Ⱥ��
		/**�Է��������Ⱥ*/
		BEAPPLYED,
		/**��ͬ���˶Է�������*/
		AGREED,
		/**�Ҿܾ��˶Է�������*/
		REFUSED,
		
		//==Ⱥ����
		/**�յ��Է���Ⱥ����**/
		GROUPINVITATION,
		/**�յ��Է�ͬ��Ⱥ�����֪ͨ**/
		GROUPINVITATION_ACCEPTED,
        /**�յ��Է��ܾ�Ⱥ�����֪ͨ**/
		GROUPINVITATION_DECLINED
	}
}