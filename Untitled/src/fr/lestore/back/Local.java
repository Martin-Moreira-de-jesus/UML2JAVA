public class Local {
	private UserStore userStore;
	private List<User> user;
	private ReserveStore reserveStore;
	private ProductStore productStore;

	public void reserve(Integer idUser, Integer idProd) {
		//TODO
	}

	public UserStore getUserStore() {
		return userStore;
	}

	public void setUserStore(UserStore userStore) {
		this.userStore = userStore;
	}

	public List<User> getUser() {
		return user;
	}

	public void setUser(List<User> user) {
		this.user = user;
	}

	public ReserveStore getReserveStore() {
		return reserveStore;
	}

	public void setReserveStore(ReserveStore reserveStore) {
		this.reserveStore = reserveStore;
	}

	public ProductStore getProductStore() {
		return productStore;
	}

	public void setProductStore(ProductStore productStore) {
		this.productStore = productStore;
	}

}
