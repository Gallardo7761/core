package net.miarma.api.common.http;

import java.util.Optional;

import io.vertx.ext.web.RoutingContext;

public class QueryFilters {

    private Optional<String> sort = Optional.empty();
    private Optional<String> order = Optional.of("ASC");
    private Optional<Integer> limit = Optional.empty();
    private Optional<Integer> offset = Optional.empty();

    public QueryFilters() {}

    public QueryFilters(Optional<String> sort, Optional<String> order, Optional<Integer> limit, Optional<Integer> offset) {
        this.sort = sort;
        this.order = order;
        this.limit = limit;
        this.offset = offset;
    }

    public Optional<String> getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = Optional.ofNullable(sort);
    }

    public Optional<String> getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = Optional.ofNullable(order);
    }

    public Optional<Integer> getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = Optional.ofNullable(limit);
    }

    public Optional<Integer> getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = Optional.ofNullable(offset);
    }

    @Override
    public String toString() {
        return "QueryFilters{" +
                "sort=" + sort +
                ", order=" + order +
                ", limit=" + limit +
                ", offset=" + offset +
                '}';
    }

	public static QueryFilters from(RoutingContext ctx) {
		QueryFilters filters = new QueryFilters();
		filters.setSort(ctx.request().getParam("_sort"));
		filters.setOrder(ctx.request().getParam("_order"));
		filters.setLimit(ctx.request().getParam("_limit") != null ? Integer.parseInt(ctx.request().getParam("_limit")) : null);
		filters.setOffset(ctx.request().getParam("_offset") != null ? Integer.parseInt(ctx.request().getParam("_offset")) : null);
		return filters;
	}
}
