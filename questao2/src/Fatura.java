import java.math.BigDecimal;
import java.util.List;
public interface Fatura {
    Venda getVenda();
    List<ItemFatura> getItens();
    BigDecimal getTotal();
}
