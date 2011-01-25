package org.fukata.android.mytw;

import java.util.Date;

public class TimelineItem {
	private String statusId;
	private String status;
	private String username;
	private String userId;
	private String source;
	private Date createdAt;

	public String getStatusId() {
		return statusId;
	}

	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "TimelineItem [statusId=" + statusId + ", status=" + status
				+ ", username=" + username + ", userId=" + userId + ", source="
				+ source + ", createdAt=" + createdAt + "]";
	}

}
