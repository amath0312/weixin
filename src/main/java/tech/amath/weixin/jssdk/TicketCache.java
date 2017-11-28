package tech.amath.weixin.jssdk;

public class TicketCache {
	String ticket;
	long cacheTime;
	long expiresTime;

	public long getCacheTime() {
		return cacheTime;
	}

	public void setCacheTime(long cacheTime) {
		this.cacheTime = cacheTime;
	}

	public long getExpiresTime() {
		return expiresTime;
	}

	public void setExpiresTime(long expiresTime) {
		this.expiresTime = expiresTime;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public boolean isExpired() {
		long now = System.currentTimeMillis();
		return (now - cacheTime) >= expiresTime;
	}
}
