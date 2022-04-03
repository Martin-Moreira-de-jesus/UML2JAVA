public class Product {
	public Integer id;
	private Reserve[] reserve;

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

}
