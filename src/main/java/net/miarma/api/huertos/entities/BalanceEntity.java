package net.miarma.api.huertos.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.vertx.sqlclient.Row;
import net.miarma.api.common.annotations.Table;
import net.miarma.api.common.db.AbstractEntity;

@Table("huertos_balance")
public class BalanceEntity extends AbstractEntity {
	private Integer id;
	private BigDecimal initial_bank;
	private BigDecimal initial_cash;
	private LocalDateTime created_at;
	
	public BalanceEntity() {
		super();
	}
	
	public BalanceEntity(Row row) {
		super(row);
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public BigDecimal getInitial_bank() {
		return initial_bank;
	}
	public void setInitial_bank(BigDecimal initial_bank) {
		this.initial_bank = initial_bank;
	}
	public BigDecimal getInitial_cash() {
		return initial_cash;
	}
	public void setInitial_cash(BigDecimal initial_cash) {
		this.initial_cash = initial_cash;
	}
	public LocalDateTime getCreated_at() {
		return created_at;
	}
	public void setCreated_at(LocalDateTime created_at) {
		this.created_at = created_at;
	}
	
	
}
