package net.miarma.api.microservices.huertos.entities;

import io.vertx.sqlclient.Row;
import net.miarma.api.common.annotations.Table;
import net.miarma.api.common.db.AbstractEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("v_balance_with_totals")
public class ViewBalanceWithTotals extends AbstractEntity{
	private Integer id;
	private BigDecimal initial_bank;
	private BigDecimal initial_cash;
	private BigDecimal total_bank_expenses;
	private BigDecimal total_cash_expenses;
	private BigDecimal total_bank_incomes;
	private BigDecimal total_cash_incomes;
	private LocalDateTime created_at;
	
	public ViewBalanceWithTotals() {
		super();
	}
	
	public ViewBalanceWithTotals(Row row) {
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

	public BigDecimal getTotal_bank_expenses() {
		return total_bank_expenses;
	}

	public void setTotal_bank_expenses(BigDecimal total_bank_expenses) {
		this.total_bank_expenses = total_bank_expenses;
	}

	public BigDecimal getTotal_cash_expenses() {
		return total_cash_expenses;
	}

	public void setTotal_cash_expenses(BigDecimal total_cash_expenses) {
		this.total_cash_expenses = total_cash_expenses;
	}

	public BigDecimal getTotal_bank_incomes() {
		return total_bank_incomes;
	}

	public void setTotal_bank_incomes(BigDecimal total_bank_incomes) {
		this.total_bank_incomes = total_bank_incomes;
	}

	public BigDecimal getTotal_cash_incomes() {
		return total_cash_incomes;
	}

	public void setTotal_cash_incomes(BigDecimal total_cash_incomes) {
		this.total_cash_incomes = total_cash_incomes;
	}

	public LocalDateTime getCreated_at() {
		return created_at;
	}

	public void setCreated_at(LocalDateTime created_at) {
		this.created_at = created_at;
	}
	
	
	
}
