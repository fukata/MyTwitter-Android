package org.fukata.android.mytw.twitter.rs;

public class User {
	private String id;
	private String name;
	private String screenname;
	private String location;
	private String description;
	private String profileimageurl;
	private String url;
	private Integer followerscount;
	private Integer friendscount;
	private String following;
	private String createdat;
	private Integer favouritescount;
	private String utcoffset;
	private String timezone;
	private Integer statusescount;
	private String lang;

	public String getFollowing() {
		return following;
	}

	public void setFollowing(String following) {
		this.following = following;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScreenname() {
		return screenname;
	}

	public void setScreenname(String screenname) {
		this.screenname = screenname;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProfileimageurl() {
		return profileimageurl;
	}

	public void setProfileimageurl(String profileimageurl) {
		this.profileimageurl = profileimageurl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getFollowerscount() {
		return followerscount;
	}

	public void setFollowerscount(Integer followerscount) {
		this.followerscount = followerscount;
	}

	public Integer getFriendscount() {
		return friendscount;
	}

	public void setFriendscount(Integer friendscount) {
		this.friendscount = friendscount;
	}

	public String getCreatedat() {
		return createdat;
	}

	public void setCreatedat(String createdat) {
		this.createdat = createdat;
	}

	public Integer getFavouritescount() {
		return favouritescount;
	}

	public void setFavouritescount(Integer favouritescount) {
		this.favouritescount = favouritescount;
	}

	public String getUtcoffset() {
		return utcoffset;
	}

	public void setUtcoffset(String utcoffset) {
		this.utcoffset = utcoffset;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public Integer getStatusescount() {
		return statusescount;
	}

	public void setStatusescount(Integer statusescount) {
		this.statusescount = statusescount;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", screenname="
				+ screenname + ", location=" + location + ", description="
				+ description + ", profileimageurl=" + profileimageurl
				+ ", url=" + url + ", followerscount=" + followerscount
				+ ", friendscount=" + friendscount + ", following=" + following
				+ ", createdat=" + createdat + ", favouritescount="
				+ favouritescount + ", utcoffset=" + utcoffset + ", timezone="
				+ timezone + ", statusescount=" + statusescount + ", lang="
				+ lang + "]";
	}

}
