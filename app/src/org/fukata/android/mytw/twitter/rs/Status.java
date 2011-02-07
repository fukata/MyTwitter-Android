package org.fukata.android.mytw.twitter.rs;

public class Status {
	private String id;
	private String text;
	private String source;
	private String createdat;
	private User user;
	
	// mentions
	private String inreplytostatusid;
	private String inreplytouserid;
	private String favorited;
	private String inreplytoscreenname;
	
	// direct_message
	private String recipientid;
	private String recipientscreenname;
	private String senderid;
	private String senderscreenname;
	
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getCreatedat() {
		return createdat;
	}

	public void setCreatedat(String createdat) {
		this.createdat = createdat;
	}
	

	public String getInreplytostatusid() {
		return inreplytostatusid;
	}

	public void setInreplytostatusid(String inreplytostatusid) {
		this.inreplytostatusid = inreplytostatusid;
	}

	public String getInreplytouserid() {
		return inreplytouserid;
	}

	public void setInreplytouserid(String inreplytouserid) {
		this.inreplytouserid = inreplytouserid;
	}

	public String getFavorited() {
		return favorited;
	}

	public void setFavorited(String favorited) {
		this.favorited = favorited;
	}

	public String getInreplytoscreenname() {
		return inreplytoscreenname;
	}

	public void setInreplytoscreenname(String inreplytoscreenname) {
		this.inreplytoscreenname = inreplytoscreenname;
	}

	
	
	public String getRecipientid() {
		return recipientid;
	}

	public void setRecipientid(String recipientid) {
		this.recipientid = recipientid;
	}

	public String getRecipientscreenname() {
		return recipientscreenname;
	}

	public void setRecipientscreenname(String recipientscreenname) {
		this.recipientscreenname = recipientscreenname;
	}

	public String getSenderid() {
		return senderid;
	}

	public void setSenderid(String senderid) {
		this.senderid = senderid;
	}

	public String getSenderscreenname() {
		return senderscreenname;
	}

	public void setSenderscreenname(String senderscreenname) {
		this.senderscreenname = senderscreenname;
	}

	@Override
	public String toString() {
		return "Status [id=" + id + ", text=" + text + ", source=" + source
				+ ", createdat=" + createdat + ", user=" + user
				+ ", inreplytostatusid=" + inreplytostatusid
				+ ", inreplytouserid=" + inreplytouserid + ", favorited="
				+ favorited + ", inreplytoscreenname=" + inreplytoscreenname
				+ ", recipientid=" + recipientid + ", recipientscreenname="
				+ recipientscreenname + ", senderid=" + senderid
				+ ", senderscreenname=" + senderscreenname + "]";
	}
}
