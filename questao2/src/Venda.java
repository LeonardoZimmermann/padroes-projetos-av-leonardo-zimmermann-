import java.math.BigDecimal;
import java.util.Objects;
public final class Venda {
    private final String nomeLote;
    private final Licitante comprador;
    private final BigDecimal valor;
    private final BigDecimal comissao;
    Venda(String nomeLote, Licitante comprador, BigDecimal valor, BigDecimal comissao) {
        this.nomeLote = Objects.requireNonNull(nomeLote, "nomeLote");
        this.comprador = Objects.requireNonNull(comprador, "comprador");
        this.valor = Dinheiro.validar(valor);
        this.comissao = Dinheiro.validar(comissao);
    }
    public String getNomeLote() { return nomeLote; }
    public Licitante getComprador() { return comprador; }
    public BigDecimal getValor() { return valor; }
    public BigDecimal getComissao() { return comissao; }
}
