import java.math.BigDecimal;
import java.util.Objects;
public final class AcertoVendedor {
    private final Venda venda;
    public AcertoVendedor(Venda venda) { this.venda = Objects.requireNonNull(venda, "venda"); }
    public BigDecimal getLiquido() { return venda.getValor().subtract(venda.getComissao()); }
    public String gerar() {
        return "=== ACERTO DO VENDEDOR ===\nLote: " + venda.getNomeLote()
                + "\nValor de venda: " + Dinheiro.formatar(venda.getValor())
                + "\nComissao: " + Dinheiro.formatar(venda.getComissao())
                + "\nLiquido a repassar: " + Dinheiro.formatar(getLiquido());
    }
    public void imprimir() { System.out.println(gerar()); }
}
