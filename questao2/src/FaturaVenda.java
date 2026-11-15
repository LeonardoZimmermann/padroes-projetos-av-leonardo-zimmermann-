import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
public final class FaturaVenda implements Fatura {
    private final Venda venda;
    public FaturaVenda(Venda venda) { this.venda = Objects.requireNonNull(venda, "venda"); }
    public Venda getVenda() { return venda; }
    public List<ItemFatura> getItens() { return Collections.emptyList(); }
    public BigDecimal getTotal() { return venda.getValor(); }
}
