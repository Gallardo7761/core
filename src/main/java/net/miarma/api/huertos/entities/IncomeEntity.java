package net.miarma.api.huertos.entities;

import io.vertx.sqlclient.Row;
import net.miarma.api.common.Constants.HuertosPaymentFrequency;
import net.miarma.api.common.Constants.HuertosPaymentType;
import net.miarma.api.common.annotations.Table;
import net.miarma.api.common.db.AbstractEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Table("huertos_incomes")
public class IncomeEntity extends AbstractEntity {
	private Integer income_id;
	private Integer member_number;
	private String concept;
	private BigDecimal amount;
	private HuertosPaymentType type;
	private HuertosPaymentFrequency frequency;
	private LocalDateTime created_at;
	
	public IncomeEntity() {
		super();
	}
	
	public IncomeEntity(Row row) {
		super(row);
	}	
	
	public Integer getIncome_id() {
		return income_id;
	}
	public void setIncome_id(Integer income_id) {
		this.income_id = income_id;
	}
	public Integer getMember_number() {
		return member_number;
	}
	public void setMember_number(Integer member_number) {
		this.member_number = member_number;
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
	public HuertosPaymentType getType() {
		return type;
	}
	public void setType(HuertosPaymentType type) {
		this.type = type;
	}
	public HuertosPaymentFrequency getFrequency() {
		return frequency;
	}
	public void setFrequency(HuertosPaymentFrequency frequency) {
		this.frequency = frequency;
	}
	public LocalDateTime getCreated_at() {
		return created_at;
	}
	public void setCreated_at(LocalDateTime created_at) {
		this.created_at = created_at;
	}
	
	public boolean isPaid() {
		if(frequency == HuertosPaymentFrequency.BIYEARLY) {
			return (System.currentTimeMillis() - created_at.toEpochSecond(ZoneOffset.UTC)) > 6L * 30 * 24 * 60 * 60 * 1000;
		} else if(frequency == HuertosPaymentFrequency.YEARLY) {
			return (System.currentTimeMillis() - created_at.toEpochSecond(ZoneOffset.UTC)) > 12L * 30 * 24 * 60 * 60 * 1000;
		} else {
			return false;
		}
	}
	
	
}
