import java.math.BigDecimal;
public final class EntregaExpressa extends ServicoAdicional {
    public EntregaExpressa(Fatura fatura) { super(fatura); }
    protected ItemFatura criarItem() { return new ItemFatura("Entrega expressa", new BigDecimal("450.00")); }
}
