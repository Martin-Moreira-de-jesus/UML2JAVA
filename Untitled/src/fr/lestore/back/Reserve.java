public class Reserve {
	private Integer id;
	private Date reserveDate;
	private Date alarmDate;
	private Integer state;
	private User user;
	private ReserveStore reserveStore;
	private Product product;

	public Reserve new(User u, Product p) {
		//TODO
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getReserveDate() {
		return reserveDate;
	}

	public void setReserveDate(Date reserveDate) {
		this.reserveDate = reserveDate;
	}

	public Date getAlarmDate() {
		return alarmDate;
	}

	public void setAlarmDate(Date alarmDate) {
		this.alarmDate = alarmDate;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ReserveStore getReserveStore() {
		return reserveStore;
	}

	public void setReserveStore(ReserveStore reserveStore) {
		this.reserveStore = reserveStore;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

}
