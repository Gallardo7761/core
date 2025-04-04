package net.miarma.api.huertos.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.vertx.sqlclient.Row;
import net.miarma.api.common.Constants.HuertosPaymentType;
import net.miarma.api.common.annotations.Table;
import net.miarma.api.common.db.AbstractEntity;

@Table("huertos_expenses")
public class ExpenseEntity extends AbstractEntity{
	private Integer expense_id;
	private String concept;
	private BigDecimal amount;
	private String supplier;
	private String invoice;
	private HuertosPaymentType type;
	private LocalDateTime created_at;
	
	public ExpenseEntity() {
		super();
	}
	
	public ExpenseEntity(Row row) {
		super(row);
	}
	
	public Integer getExpense_id() {
		return expense_id;
	}
	public void setExpense_id(Integer expense_id) {
		this.expense_id = expense_id;
	}
	public String getConcept() {
		return concept;
	}
	public void setConcept(String concept) {
		this.concept = concept;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	public String getInvoice() {
		return invoice;
	}
	public void setInvoice(String invoice) {
		this.invoice = invoice;
	}
	public HuertosPaymentType getType() {
		return type;
	}
	public void setType(HuertosPaymentType type) {
		this.type = type;
	}
	public LocalDateTime getCreated_at() {
		return created_at;
	}
	public void setCreated_at(LocalDateTime created_at) {
		this.created_at = created_at;
	}
	
	
}
