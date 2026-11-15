import java.math.BigDecimal;
import java.util.Objects;
public final class ItemFatura {
    private final String descricao;
    private final BigDecimal custo;
    public ItemFatura(String descricao, BigDecimal custo) {
        this.descricao = Objects.requireNonNull(descricao, "descricao");
        this.custo = Dinheiro.validar(custo);
    }
    public String getDescricao() { return descricao; }
    public BigDecimal getCusto() { return custo; }
}
