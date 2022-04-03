public class User {
	public Integer id;
	private Reserve[] reserve;
	private UserStore userStore;
	private Local local;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Reserve[] getReserve() {
		return reserve;
	}

	public void setReserve(Reserve[] reserve) {
		this.reserve = reserve;
	}

	public UserStore getUserStore() {
		return userStore;
	}

	public void setUserStore(UserStore userStore) {
		this.userStore = userStore;
	}

	public Local getLocal() {
		return local;
	}

	public void setLocal(Local local) {
		this.local = local;
	}

}
