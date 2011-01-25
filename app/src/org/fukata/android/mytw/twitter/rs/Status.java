package org.fukata.android.mytw.twitter.rs;

public class Status {
	private String id;
	private String text;
	private String source;
	private String createdat;
	private User user;

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

	@Override
	public String toString() {
		return "User [id=" + id + ", text=" + text + ", source=" + source
				+ ", createdat=" + createdat + "]";
	}
}
