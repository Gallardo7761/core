package net.miarma.api.huertos.entities;
import net.miarma.api.common.annotations.Table;
import net.miarma.api.common.db.AbstractEntity;

@Table("v_incomes_with_full_names")
public class ViewIncomesWithFullNames extends AbstractEntity {
	private Integer income_id;
	private Integer member_number;
	private String display_name;
	private String concept;
	private Double amount;
	private String type;
	private Long created_at;
	
	public Integer getIncome_id() { return income_id; }
	public void setIncome_id(Integer income_id) { this.income_id = income_id; }
	public Integer getMember_number() { return member_number; }
	public void setMember_number(Integer member_number) { this.member_number = member_number; }
	public String getDisplay_name() { return display_name; }
	public void setDisplay_name(String display_name) { this.display_name = display_name; }
	public String getConcept() { return concept; }
	public void setConcept(String concept) { this.concept = concept; }
	public Double getAmount() { return amount; }
	public void setAmount(Double amount) { this.amount = amount; }
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	public Long getCreated_at() { return created_at; }
	public void setCreated_at(Long created_at) { this.created_at = created_at; }
}
