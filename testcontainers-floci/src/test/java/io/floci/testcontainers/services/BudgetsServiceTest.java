package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.budgets.BudgetsClient;
import software.amazon.awssdk.services.budgets.model.Budget;
import software.amazon.awssdk.services.budgets.model.BudgetType;
import software.amazon.awssdk.services.budgets.model.NotFoundException;
import software.amazon.awssdk.services.budgets.model.TimeUnit;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestMethodOrder(OrderAnnotation.class)
class BudgetsServiceTest extends AbstractServiceTest {

    private static final String BUDGET_NAME = "floci-tc-budget";

    static BudgetsClient budgets;

    @BeforeAll
    static void setUp() {
        budgets = client(BudgetsClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateBudget() {
        budgets.createBudget(b -> b
                .accountId(floci.getDefaultAccountId())
                .budget(Budget.builder()
                        .budgetName(BUDGET_NAME)
                        .budgetType(BudgetType.COST)
                        .timeUnit(TimeUnit.MONTHLY)
                        .budgetLimit(s -> s.amount(new BigDecimal("100.00")).unit("USD"))
                        .build()));

        Budget budget = budgets.describeBudget(b -> b.accountId(floci.getDefaultAccountId()).budgetName(BUDGET_NAME)).budget();
        assertThat(budget.budgetName()).isEqualTo(BUDGET_NAME);
        assertThat(budget.budgetLimit().amount()).isEqualByComparingTo("100");
    }

    @Test
    @Order(2)
    void shouldListBudgets() {
        assertThat(budgets.describeBudgets(b -> b.accountId(floci.getDefaultAccountId())).budgets())
                .extracting(Budget::budgetName)
                .contains(BUDGET_NAME);
    }

    @Test
    @Order(3)
    void shouldDeleteBudget() {
        budgets.deleteBudget(b -> b.accountId(floci.getDefaultAccountId()).budgetName(BUDGET_NAME));

        assertThatThrownBy(() -> budgets.describeBudget(b -> b.accountId(floci.getDefaultAccountId()).budgetName(BUDGET_NAME)))
                .isInstanceOf(NotFoundException.class);
    }
}
