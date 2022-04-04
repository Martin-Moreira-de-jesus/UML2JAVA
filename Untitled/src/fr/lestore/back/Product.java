public class Product {
	public Integer id;
	private List<Reserve> reserve;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<Reserve> getReserve() {
		return reserve;
	}

	public void setReserve(List<Reserve> reserve) {
		this.reserve = reserve;
	}

}
