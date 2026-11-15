import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
public abstract class ServicoAdicional implements Fatura {
    private final Fatura fatura;
    protected ServicoAdicional(Fatura fatura) { this.fatura = Objects.requireNonNull(fatura, "fatura"); }
    public final Venda getVenda() { return fatura.getVenda(); }
    protected abstract ItemFatura criarItem();
    public final List<ItemFatura> getItens() {
        List<ItemFatura> itens = new ArrayList<>(fatura.getItens());
        itens.add(criarItem());
        return Collections.unmodifiableList(itens);
    }
    public final BigDecimal getTotal() { return fatura.getTotal().add(criarItem().getCusto()); }
}
