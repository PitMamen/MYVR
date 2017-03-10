package com.kamino.store.entity;

public class APKEntity {
	private String belong;
	private int id;
	private String name;
	private int icon;
	private String size;
	private String besupported;
	private String introduction;
	private String downloadurl;
	private int thumbnail1;
	private int thumbnail2;
	private int thumbnail3;
	private int thumbnail4;
	private String packagename;
	private int TextureId = -1;
	private String iconurl;
	private String thumbnail1url;
	private String thumbnail2url;
	private String thumbnail3url;
	
	public String getBelong() {
		return belong;
	}
	public void setBelong(String belong) {
		this.belong = belong;
	}
	public String getIconurl() {
		return iconurl;
	}
	public void setIconurl(String iconurl) {
		this.iconurl = iconurl;
	}
	public String getThumbnail1url() {
		return thumbnail1url;
	}
	public void setThumbnail1url(String thumbnail1url) {
		this.thumbnail1url = thumbnail1url;
	}
	public String getThumbnail2url() {
		return thumbnail2url;
	}
	public void setThumbnail2url(String thumbnail2url) {
		this.thumbnail2url = thumbnail2url;
	}
	public String getThumbnail3url() {
		return thumbnail3url;
	}
	public void setThumbnail3url(String thumbnail3url) {
		this.thumbnail3url = thumbnail3url;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getThumbnail4url() {
		return thumbnail4url;
	}
	public void setThumbnail4url(String thumbnail4url) {
		this.thumbnail4url = thumbnail4url;
	}
	private String thumbnail4url;
	
	public APKEntity() {
		super();
	}
	public APKEntity(String name, int icon, String size, String besupported, String introduction, String downloadurl,
			int thumbnail1, int thumbnail2, int thumbnail3, int thumbnail4,String packagename) {
		super();
		this.name = name;
		this.icon = icon;
		this.size = size;
		this.besupported = besupported;
		this.introduction = introduction;
		this.downloadurl = downloadurl;
		this.thumbnail1 = thumbnail1;
		this.thumbnail2 = thumbnail2;
		this.thumbnail3 = thumbnail3;
		this.thumbnail4 = thumbnail4;
		this.packagename = packagename;
	}
	
	
	
	public APKEntity(String packagename,String belong,int id,String name, String size, String besupported, String introduction, String downloadurl,
			String iconurl, String thumbnail1url, String thumbnail2url, String thumbnail3url,
			String thumbnail4url) {
		super();

		this.packagename = packagename;
		this.belong =belong;
		this.id=id;
		this.name = name;
		this.size = size;
		this.besupported = besupported;
		this.introduction = introduction;
		this.downloadurl = downloadurl;
		this.iconurl = iconurl;
		this.thumbnail1url = thumbnail1url;
		this.thumbnail2url = thumbnail2url;
		this.thumbnail3url = thumbnail3url;
		this.thumbnail4url = thumbnail4url;
	}
	public int getTextureId() {
		return TextureId;
	}
	public void setTextureId(int textureId) {
		TextureId = textureId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIcon() {
		return icon;
	}
	public void setIcon(int icon) {
		this.icon = icon;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getBesupported() {
		return besupported;
	}
	public void setBesupported(String besupported) {
		this.besupported = besupported;
	}
	public String getIntroduction() {
		return introduction;
	}
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	public String getDownloadurl() {
		return downloadurl;
	}
	public void setDownloadurl(String downloadurl) {
		this.downloadurl = downloadurl;
	}
	public int getThumbnail1() {
		return thumbnail1;
	}
	public void setThumbnail1(int thumbnail1) {
		this.thumbnail1 = thumbnail1;
	}
	public int getThumbnail2() {
		return thumbnail2;
	}
	public void setThumbnail2(int thumbnail2) {
		this.thumbnail2 = thumbnail2;
	}
	public int getThumbnail3() {
		return thumbnail3;
	}
	public void setThumbnail3(int thumbnail3) {
		this.thumbnail3 = thumbnail3;
	}
	public int getThumbnail4() {
		return thumbnail4;
	}
	public void setThumbnail4(int thumbnail4) {
		this.thumbnail4 = thumbnail4;
	}
	public String getPackagename() {
		return packagename;
	}
	public void setPackagename(String packagename) {
		this.packagename = packagename;
	}
	
	
}
